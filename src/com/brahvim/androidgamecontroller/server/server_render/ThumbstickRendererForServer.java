package com.brahvim.androidgamecontroller.server.server_render;

import java.awt.Robot;

import org.jetbrains.annotations.NotNull;

import com.brahvim.androidgamecontroller.render.ThumbstickRendererBase;
import com.brahvim.androidgamecontroller.serial.configs.ThumbstickConfig;

import processing.core.PGraphics;

public class ThumbstickRendererForServer extends ThumbstickRendererBase implements ServerRenderer {
    @SuppressWarnings("unused")

    private Robot robot;

    public ThumbstickRendererForServer(@NotNull ThumbstickConfig p_config, Robot p_robot) {
        super(p_config);
    }

    @Override
    public void draw(@NotNull PGraphics p_graphics) {
        // TODO Auto-generated method stub!
        super.draw(p_graphics);
    }

}
