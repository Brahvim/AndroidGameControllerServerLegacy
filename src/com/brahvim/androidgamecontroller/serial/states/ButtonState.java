package com.brahvim.androidgamecontroller.serial.states;

public class ButtonState extends StateBase {
    public final static long serialVersionUID = -8131929056944084215L;
    public boolean pressed, ppressed;

    public ButtonState() {
    }

    // Nope! No OOP here :)

    /*
     * // NO idea why I would need to use abstraction like this here...
     * // Better do it anyway! This is software development!
     *
     * private boolean pressed, ppressed;
     *
     * public boolean wasPressed() {
     * return this.ppressed;
     * }
     *
     * public boolean isPressed() {
     * return this.pressed;
     * }
     */
}
