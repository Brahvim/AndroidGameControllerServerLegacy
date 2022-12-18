package com.brahvim.androidgamecontroller.serial.states;

import processing.core.PVector;

public class TouchpadState extends StateBase {
    // NO idea why I would need to use abstraction like this here...
    // Better do it anyway! This is software development!

    public PVector mouse; // The `z` holds pressure or area. Probably area.
    public boolean doubleTapped, pdoubleTapped;

    public TouchpadState() {
        this.mouse = new PVector();
    }

}
