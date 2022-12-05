package com.brahvim.androidgamecontroller.server;

import java.util.ArrayList;

public class Scene {
    public final static ArrayList<Scene> SCENES = new ArrayList<Scene>(3);
    // private final Scene SCENE = this;

    public Scene() {
        Scene.SCENES.add(this);
    }

    // #region Application callback structure.
    public void setup() {
    }

    public void draw() {
    }

    public void pre() {
    }

    public void post() {
    }

    public void onReceive(byte[] p_data, AgcClient p_client) {
    }
    // #endregion

    // #region Mouse events.
    public void mousePressed() {
    }

    public void mouseMoved() {
    }

    public void mouseWheel(processing.event.MouseEvent p_mouseEvent) {
    }

    public void mouseClicked() {
    }

    public void mouseDragged() {
    }

    public void mouseReleased() {
    }

    public void mouseExited() {
    }

    public void mouseEntered() {
    }
    // endregion

    // #region Keyboard events.
    public void keyPressed() {
    }

    public void keyTyped() {
    }

    public void keyReleased() {
    }
    // #endregion

}
