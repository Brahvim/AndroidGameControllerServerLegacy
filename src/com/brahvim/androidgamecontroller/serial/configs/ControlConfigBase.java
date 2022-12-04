package com.brahvim.androidgamecontroller.serial.configs;

import java.io.Serializable;

/**
 * Base class for all controller elements' configuration objects.
 */
public class ControlConfigBase implements Serializable {
    public final static long serialVersionUID = 8587363312969447326L;
    public static int totalConfigs = 0;

    public int controlNumber;

    // This constructor helps NOT force extending classes from always giving the
    // data this class needs. *"Might"* not be a good idea.
    public ControlConfigBase() {
        ControlConfigBase.totalConfigs++;
        this.controlNumber = ControlConfigBase.totalConfigs;
    }
}
