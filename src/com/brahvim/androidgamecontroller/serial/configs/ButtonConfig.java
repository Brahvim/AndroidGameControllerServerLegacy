package com.brahvim.androidgamecontroller.serial.configs;

import com.brahvim.androidgamecontroller.serial.ButtonShape;

import processing.core.PVector;

public class ButtonConfig extends ControlConfigBase {
    public final static long serialVersionUID = 3448431857528425173L;

    public String text;
    public PVector scale;
    public PVector transform; // The `z` is rotation.
    public ButtonShape shape;

    public ButtonConfig() {
        super();
    }

    /**
     * Create a button at the desired position with text.
     */
    public ButtonConfig(float p_x, float p_y, String p_text) {
        this.text = p_text;
        this.shape = ButtonShape.RECTANGLE;
        this.scale = new PVector(400, 400);
        this.transform = new PVector(p_x, p_y, 0);
    }

    /**
     * Create a button with the desired position and size with text.
     */
    public ButtonConfig(PVector p_transform, PVector p_scale, String p_text) {
        this.text = p_text;
        this.scale = p_scale;
        this.transform = p_transform;
        this.shape = ButtonShape.RECTANGLE;
    }

    /**
     * Create a button at the desired position, with the desired shape and size,
     * with text.
     */
    public ButtonConfig(PVector p_transform, PVector p_scale, String p_text, ButtonShape p_shape) {
        this.text = p_text;
        this.scale = p_scale;
        this.transform = p_transform;
        this.shape = p_shape;
    }

    /**
     * Create a circular button at the desired position with the desired radius and
     * text.
     */
    public ButtonConfig(PVector p_transform, float p_radius, String p_text) {
        this.text = p_text;
        this.scale = new PVector(p_radius, p_radius);
        this.transform = p_transform;
        this.shape = ButtonShape.ROUND;
    }

    /*
     * // Hopefully, we can also have STYLE information in the future! :D
     * // AndroidGameController themes! 😍
     * int fillColor, strokeColor;
     * int strokeWeight, strokeCap, strokeJoin;
     */

}
