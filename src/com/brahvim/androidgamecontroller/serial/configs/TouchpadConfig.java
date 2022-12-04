package com.brahvim.androidgamecontroller.serial.configs;

import processing.core.PVector;

public class TouchpadConfig extends ControlConfigBase {
    public final static long serialVersionUID = 7084394767346371323L;

    public PVector scale;
    public PVector transform;

    public TouchpadConfig() {
        super();
    }

    public TouchpadConfig(PVector p_scale, PVector p_transform) {
        this.scale = p_scale;
        this.transform = p_transform;
        // No use for the `z`, again...
    }
}
