package com.brahvim.androidgamecontroller.render;

import com.brahvim.androidgamecontroller.serial.configs.ButtonConfig;
import com.brahvim.androidgamecontroller.serial.states.ButtonState;

import org.jetbrains.annotations.NotNull;

import processing.core.PConstants;
import processing.core.PGraphics;

public class ButtonRendererBase {
    public ButtonConfig config;
    public ButtonState state;

    public ButtonRendererBase(@NotNull ButtonConfig p_config) {
        this.config = p_config;
        this.state = new ButtonState();
    }

    public void draw(@NotNull PGraphics p_graphics) {
        // this.state.ppressed = this.state.pressed; // Nope! The impl. handles this!

        p_graphics.pushMatrix();
        p_graphics.pushStyle();

        p_graphics.translate(this.config.transform.x,
          this.config.transform.y);
        p_graphics.scale(this.config.scale.x, this.config.scale.x);
        p_graphics.rotate(this.config.transform.z);

        p_graphics.fill(230, this.state.pressed? 100 : 50);
        p_graphics.noStroke();

        switch (this.config.shape) {
            case ROUND:
                p_graphics.ellipse(0, 0, 1, 1);
                break;

            case RECTANGLE:
                // p_graphics.rectMode(PConstants.CENTER);
                p_graphics.rect(0, 0, 1.2f, 0.55f,
                  0.1f, 0.1f, 0.1f, 0.1f);
                break;

            default:
                break;
        }

        p_graphics.textSize(0.4f);
        p_graphics.textAlign(PConstants.CENTER, PConstants.CENTER);
        p_graphics.text(this.config.text, 0, 0);

        p_graphics.popMatrix();
        p_graphics.popStyle();
    }

    public boolean wasPressed() {
        return this.state.ppressed;
    }

    public boolean isPressed() {
        return this.state.pressed;
    }
}
