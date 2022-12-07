package com.brahvim.androidgamecontroller.server.server_render;

import java.awt.Robot;

import org.jetbrains.annotations.NotNull;

import com.brahvim.androidgamecontroller.render.ThumbstickRendererBase;
import com.brahvim.androidgamecontroller.serial.configs.ThumbstickConfig;
import com.brahvim.androidgamecontroller.server.Sketch;

import processing.core.PGraphics;

public class ThumbstickRendererForServer extends ThumbstickRendererBase implements ServerRenderer {
    @SuppressWarnings("unused")
    private Robot robot;
    @SuppressWarnings("unused")
    private Sketch parentSketch;

    public ThumbstickRendererForServer(Sketch p_parentSketch, @NotNull ThumbstickConfig p_config, Robot p_robot) {
        super(p_config);
        this.robot = p_robot;
        this.parentSketch = p_parentSketch;
        ServerRenderer.all.add(this);
    }

    @Override
    public void draw(@NotNull PGraphics p_graphics) {
        // TODO Auto-generated method stub!
        super.draw(p_graphics);
    }

}
