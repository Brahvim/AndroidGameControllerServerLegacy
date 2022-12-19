package com.brahvim.androidgamecontroller.server.server_render;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;

import org.jetbrains.annotations.NotNull;

import com.brahvim.androidgamecontroller.render.TouchpadRendererBase;
import com.brahvim.androidgamecontroller.serial.configs.TouchpadConfig;
import com.brahvim.androidgamecontroller.server.SineWave;
import com.brahvim.androidgamecontroller.server.Sketch;
import com.brahvim.androidgamecontroller.server.StringTable;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

public class TouchpadRendererForServer extends TouchpadRendererBase implements ServerRenderer {
    private Robot robot;
    private Sketch parentSketch;
    private PVector screenDimensions, center, touchStart, touchDelta, ptouchPos;
    private SineWave tapFadeWave, numTapsFadeWave;

    public TouchpadRendererForServer(Sketch p_parentSketch, @NotNull TouchpadConfig p_config, Robot p_robot) {
        super(p_config);
        this.robot = p_robot;
        this.robot.setAutoWaitForIdle(true);
        this.parentSketch = p_parentSketch;
        this.tapFadeWave = new SineWave(p_parentSketch, 0.025f);
        this.numTapsFadeWave = new SineWave(p_parentSketch, 0.001f);
        this.screenDimensions = this.parentSketch.client.getConfig().screenDimensions;
        this.center = PVector.add(super.config.transform, PVector.mult(super.config.scale, 0.5f));
        ServerRenderer.all.add(this);
    }

    @Override
    public void draw(@NotNull PGraphics p_graphics) {
        // #region Robot logic.
        switch (super.config.replicationPolicy) {
            case VELOCITY:
                // Unnecessary and "dangerous". I know:
                if (!super.state.pressed && super.state.ppressed)
                    this.touchStart = null;

                if (super.state.pressed && !super.state.ppressed)
                    this.touchStart = super.state.mouse;

                if (super.state.pressed) {
                    Point mouseLocation = MouseInfo.getPointerInfo().getLocation();

                    if (this.touchStart == null)
                        this.touchStart = new PVector(mouseLocation.x, mouseLocation.y);

                    this.touchDelta = PVector.sub(super.state.mouse, this.touchStart);

                    this.touchDelta.set(
                            PApplet.map(this.touchDelta.x, 0, this.screenDimensions.x, 0, this.parentSketch.width),
                            PApplet.map(this.touchDelta.y, 0, this.screenDimensions.y, 0, this.parentSketch.height));

                    this.touchDelta.mult(super.config.sensitivity);

                    this.robot.mouseMove(
                            mouseLocation.x + (int) this.touchDelta.x,
                            mouseLocation.y + (int) this.touchDelta.y);
                }
                break;

            case POSITION:
                // Unnecessary and "dangerous". I know:
                if (!super.state.pressed && super.state.ppressed)
                    this.touchStart = null;

                if (super.state.pressed && !super.state.ppressed)
                    this.touchStart = super.state.mouse;

                if (super.state.pressed && !this.ptouchPos.equals(super.state.mouse)) {
                    System.out.println("Touch moved!" + this.parentSketch.frameCount);
                    Point mouseLocation = MouseInfo.getPointerInfo().getLocation();

                    if (this.touchStart == null)
                        this.touchStart = new PVector(mouseLocation.x, mouseLocation.y);

                    this.touchDelta = PVector.sub(super.state.mouse, this.touchStart);

                    this.touchDelta.set(
                            PApplet.map(this.touchDelta.x, 0, this.screenDimensions.x, 0, this.parentSketch.width),
                            PApplet.map(this.touchDelta.y, 0, this.screenDimensions.y, 0, this.parentSketch.height));

                    this.touchDelta.mult(super.config.sensitivity);

                    this.robot.mouseMove(
                            mouseLocation.x + (int) this.touchDelta.x,
                            mouseLocation.y + (int) this.touchDelta.y);
                }

                this.ptouchPos = super.state.mouse;
                break;

            default:
        }
        // #endregion

        if (super.state.ppressed && !super.state.pressed) {
            this.tapFadeWave.endWhenAngleIs(90);
            this.tapFadeWave.start();
        }

        p_graphics.pushMatrix();
        p_graphics.pushStyle();

        // #region Drawing the touchpad.
        p_graphics.pushMatrix();

        p_graphics.translate(this.config.transform.x, this.config.transform.y);

        p_graphics.fill(230, this.state.pressed ? 100 : 50);
        p_graphics.noStroke();

        // Touchpads ain't got any rotation! (Yet!...)
        p_graphics.scale(this.config.scale.x, this.config.scale.x);
        p_graphics.rectMode(PConstants.CENTER);
        p_graphics.rect(0, 0, 1.2f, 0.55f,
                0.1f, 0.1f, 0.1f, 0.1f);

        p_graphics.popMatrix();
        // #endregion

        // Drawing the touch!:
        if (super.state.pressed && !super.state.doubleTapped) {
            p_graphics.pushMatrix();

            float tapFade = PApplet.abs(this.tapFadeWave.get());

            p_graphics.translate(
                    PApplet.map(super.state.mouse.x,
                            0, this.screenDimensions.x,
                            0, this.parentSketch.width),
                    PApplet.map(super.state.mouse.y,
                            0, this.screenDimensions.y,
                            0, this.parentSketch.height));
            p_graphics.scale(super.state.mouse.z * 20 * tapFade);

            p_graphics.fill(0, tapFade * 150);
            p_graphics.strokeWeight(tapFade * 0.05f);
            p_graphics.stroke(255, tapFade);

            p_graphics.ellipse(0, 0, 1, 1);
            p_graphics.popMatrix();
        }

        // #region Double tap indicator:
        if (super.state.doubleTapped) {
            if (!super.state.pdoubleTapped) {
                System.out.println("Double tap!");
                this.numTapsFadeWave.endWhenAngleIs(180);
                this.numTapsFadeWave.start();
            }

            float numTapsFade = PApplet.abs(this.numTapsFadeWave.get());

            p_graphics.pushMatrix();
            p_graphics.pushStyle();

            p_graphics.fill(0, numTapsFade * 255);
            p_graphics.textSize(numTapsFade * 8);
            p_graphics.text(StringTable.getString("TouchpadRenderer.doubleTap"),
                    this.center.x, this.center.y);

            p_graphics.popStyle();
            p_graphics.popMatrix();
        }
        // #endregion

        p_graphics.popMatrix();
        p_graphics.popStyle();
    }

}
