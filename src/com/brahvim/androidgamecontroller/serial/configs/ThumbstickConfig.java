package com.brahvim.androidgamecontroller.serial.configs;

import processing.core.PVector;

public class ThumbstickConfig extends ControlConfigBase {
    public final static long serialVersionUID = -6618151523553691071L;

    public PVector scale;
    public PVector transform;

    public ThumbstickConfig() {
        super();
    }

    // Of course, thumbsticks don't have rotation.
    // ...unless I put a fancy texture with shadows on them.

    public ThumbstickConfig(PVector p_scale, PVector p_transform) {
        this.scale = p_scale;
        this.transform = p_transform;
    }
}
