package com.brahvim.androidgamecontroller.render;

import org.jetbrains.annotations.NotNull;

import com.brahvim.androidgamecontroller.serial.configs.TouchpadConfig;
import com.brahvim.androidgamecontroller.serial.states.TouchpadState;

import processing.core.PConstants;
import processing.core.PGraphics;

public class TouchpadRendererBase {
    public TouchpadConfig config;
    public TouchpadState state;

    public TouchpadRendererBase(@NotNull TouchpadConfig p_config) {
        this.config = p_config;
        this.state = new TouchpadState();
        this.state.controlNumber = this.config.controlNumber;
    }

    public void draw(@NotNull PGraphics p_graphics) {
        p_graphics.pushMatrix();
        p_graphics.pushStyle();

        p_graphics.fill(230, this.state.pressed ? 100 : 50);
        p_graphics.noStroke();

        // region Drawing the touchpad.
        p_graphics.pushMatrix();

        p_graphics.translate(this.config.transform.x,
                this.config.transform.y);
        // Touchpads ain't got any rotation! (Yet!...)
        p_graphics.scale(this.config.scale.x, this.config.scale.x);
        p_graphics.rectMode(PConstants.CENTER);
        p_graphics.rect(0, 0, 1.2f, 0.55f,
                0.1f, 0.1f, 0.1f, 0.1f);

        p_graphics.popMatrix();
        // endregion

        // region Drawing the touch on the touchpad!
        p_graphics.pushMatrix();
        p_graphics.scale(this.state.mouse.z);
        p_graphics.ellipse(this.state.mouse.x, this.state.mouse.y, 1, 1);
        p_graphics.popMatrix();
        // endregion

        p_graphics.popMatrix();
        p_graphics.popStyle();
    }

}
