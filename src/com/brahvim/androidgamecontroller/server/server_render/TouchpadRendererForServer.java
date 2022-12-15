package com.brahvim.androidgamecontroller.server.server_render;

import java.awt.Robot;

import org.jetbrains.annotations.NotNull;

import com.brahvim.androidgamecontroller.render.TouchpadRendererBase;
import com.brahvim.androidgamecontroller.serial.configs.TouchpadConfig;
import com.brahvim.androidgamecontroller.server.Sketch;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

public class TouchpadRendererForServer extends TouchpadRendererBase implements ServerRenderer {
    @SuppressWarnings("unused")
    private Robot robot;
    private Sketch parentSketch;

    public TouchpadRendererForServer(Sketch p_parentSketch, @NotNull TouchpadConfig p_config, Robot p_robot) {
        super(p_config);
        this.robot = p_robot;
        this.parentSketch = p_parentSketch;
        ServerRenderer.all.add(this);
    }

    @Override
    public void draw(@NotNull PGraphics p_graphics) {
        p_graphics.pushMatrix();
        p_graphics.pushStyle();

        p_graphics.translate(this.config.transform.x, this.config.transform.y);

        p_graphics.fill(230, this.state.pressed ? 100 : 50);
        p_graphics.noStroke();

        // #region Drawing the touchpad.
        p_graphics.pushMatrix();

        // Touchpads ain't got any rotation! (Yet!...)
        p_graphics.scale(this.config.scale.x, this.config.scale.x);
        p_graphics.rectMode(PConstants.CENTER);
        p_graphics.rect(0, 0, 1.2f, 0.55f,
                0.1f, 0.1f, 0.1f, 0.1f);

        p_graphics.popMatrix();
        // #endregion

        // Drawing the touch!:
        if (super.state.pressed) {
            p_graphics.pushMatrix();

            p_graphics.translate(
                    PApplet.map(super.state.mouse.x, 0,
                            this.parentSketch.client.getConfig().screenDimensions.x,
                            0, this.parentSketch.width),
                    PApplet.map(super.state.mouse.y, 0,
                            this.parentSketch.client.getConfig().screenDimensions.y,
                            0, this.parentSketch.height));

            System.out.println(super.state.mouse);

            p_graphics.scale(20);
            p_graphics.fill(0, 255, 0);
            p_graphics.ellipse(0, 0, 1, 1);
            p_graphics.popMatrix();
        }

        p_graphics.popMatrix();
        p_graphics.popStyle();
    }
}
