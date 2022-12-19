package com.brahvim.androidgamecontroller.serial.configs;

import processing.core.PVector;

public class TouchpadConfig extends ControlConfigBase {
    public final static long serialVersionUID = 7084394767346371323L;

    public PVector scale;
    public PVector transform;
    public float sensitivity = 0.5f;

    public TrackingPolicy trackingPolicy = TrackingPolicy.FIRST;
    public ReplicationPolicy replicationPolicy = ReplicationPolicy.VELOCITY;

    // region "Policy" enumerators.
    // Determines which touch the touchpad tracks when there are multiple on it!
    // Might be a redundant feature!
    public enum TrackingPolicy {
        FIRST(), // The touch that touched the touchpad before the others is tracked.
        LAST(), // The latest touch that touched the touchpad is tracked.
        MID(); // The midpoint of all touches is the position the touchpad tracks.
    }

    // Determines how the mouse pointer responds to touches on the touchpad.
    // *Idr est,* how the touch's movement is replicated by the mouse pointer.
    public enum ReplicationPolicy {
        POSITION(), // Set the pointer's position as the touch is moved around,
        VELOCITY(); // Add a velocity to the pointer as the touch is pressed.
    }
    // endregion

    public TouchpadConfig() {
        super();
    }

    public TouchpadConfig(PVector p_scale, PVector p_transform) {
        this.scale = p_scale;
        this.transform = p_transform;
        // No use for the `z`, again...
        // (It stores "touch pressure", but Android keeps that VERY inconsistent!)
    }

    public TouchpadConfig(PVector p_scale, PVector p_transform, float p_sensitivity) {
        this.scale = p_scale;
        this.transform = p_transform;
        this.sensitivity = p_sensitivity;
    }

    public TouchpadConfig(PVector p_scale, PVector p_transform, float p_sensitivity,
                          TrackingPolicy p_trackingPolicy, ReplicationPolicy p_replicationPolicy) {
        this.scale = p_scale;
        this.transform = p_transform;
        this.sensitivity = p_sensitivity;
        this.trackingPolicy = p_trackingPolicy;
        this.replicationPolicy = p_replicationPolicy;
    }
}
