package com.brahvim.androidgamecontroller.serial.states;

public class KeyboardState extends StateBase {
    public final static long serialVersionUID = -3605879719869452598L;

    public KeyboardState() {
    }

    public char keyPressed;
    public int keyCode;
    public int lastKeyMillis; // This should use `System.currentTimeMillis()`, by the way.
    // ...even though the server is aware of each client's start-up time.
}
