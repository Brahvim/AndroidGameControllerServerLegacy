package com.brahvim.androidgamecontroller.server;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.brahvim.androidgamecontroller.Scene;

import processing.awt.PSurfaceAWT;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

public class Sketch extends PApplet {
    public final static int AGC_WIDTH = 400, AGC_HEIGHT = 200;
    public final static ArrayList<Sketch> SKETCHES = new ArrayList<>(1);
    public final static int REFRESH_RATE = GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getScreenDevices()[0].getDisplayMode().getRefreshRate();

    // #region Fields.
    public final Color javaBgColor = new Color(0, 0, 0, 1);
    public final Sketch SKETCH = this;
    public int frameStartTime, pframeTime, frameTime;
    public Scene currentScene;

    // #region Window coordinates and window states.
    public int bgColor = super.color(0, 150);
    public PVector minExtent, maxExtent;

    // #region Ma'h boilerplate :D
    public float cx, cy, qx, qy, q3x, q3y;
    public int pwidth, pheight;

    public JFrame sketchFrame; // We do not rely on the Processing 3 'dummy' variable!

    public boolean mouseInWin;
    public int pwinMouseX, pwinMouseY;
    public int winMouseX, winMouseY;
    public int surfaceX, surfaceY; // Used to constrain the position of the overlay.
    public int pmousePressX, pmousePressY; // Where was the mouse when it was last clicked?
    // #endregion
    // #endregion

    // #region `private` Fields.
    private static PImage AGC_ICON;
    private PGraphics gr;
    // #endregion
    // #endregion Fields.

    public static void main(String[] p_args) {
        Sketch constructedSketch = new Sketch();
        String[] args = new String[] { constructedSketch.getClass().getName() };

        if (p_args == null || p_args.length == 0)
            PApplet.runSketch(args, constructedSketch);
        else
            PApplet.runSketch(PApplet.concat(p_args, args), constructedSketch);
    }

    public void settings() {
        super.size(Sketch.AGC_WIDTH, Sketch.AGC_HEIGHT);
    }

    @Override
    public void setup() {
        System.out.printf("");

        super.registerMethod("pre", this);
        super.registerMethod("post", this);

        super.surface.setTitle(StringTable.getString("Meta.winTitle"));
        super.surface.setIcon(
                Sketch.AGC_ICON = Sketch.AGC_ICON == null
                        ? this.loadImage(StringTable.getString("Meta.iconPath"))
                        : Sketch.AGC_ICON);

        this.minExtent = new PVector(0, 0);
        this.maxExtent = new PVector(displayWidth - width, displayHeight - height);

        super.frameRate(Sketch.REFRESH_RATE);

        this.sketchFrame = this.createSketchPanel(new Runnable() {
            @Override
            public void run() {
                // Don't fear the `SKETCH`! It's not bad code!
                SKETCH.agcExit();
            }
        }, this, this.gr = createGraphics(width, height));
    }

    public void pre() {
    }

    @Override
    public void draw() {
        frameStartTime = millis(); // Timestamp.
        frameTime = frameStartTime - pframeTime;
        pframeTime = frameStartTime;

        // #region Window dragging logic.
        pwinMouseX = winMouseX;
        pwinMouseY = winMouseY;

        winMouseX = MouseInfo.getPointerInfo().getLocation().x;
        winMouseY = MouseInfo.getPointerInfo().getLocation().y;

        if (mousePressed) {
            surfaceX = winMouseX - pmousePressX;
            surfaceY = winMouseY - pmousePressY;

            if (surfaceX < minExtent.x)
                surfaceX = (int) minExtent.x;

            if (surfaceY < minExtent.y)
                surfaceY = (int) minExtent.y;

            if (surfaceX > maxExtent.x)
                surfaceX = (int) maxExtent.x;

            if (surfaceY > maxExtent.y)
                surfaceY = (int) maxExtent.y;

            surface.setLocation(surfaceX, surfaceY);
        }

        mouseInWin = winMouseX > sketchFrame.getX() &&
                winMouseX < sketchFrame.getX() + width &&
                winMouseY > sketchFrame.getY() &&
                winMouseY < sketchFrame.getY() + height;
        // #endregion

        this.sketchFrame.setBackground(this.javaBgColor);

        this.gr.beginDraw();
        this.gr.background(bgColor);

        if (this.currentScene != null)
            if (this.currentScene != null)
                this.currentScene.draw();

        this.gr.endDraw();
    }

    public void post() {
    }

    public void agcExit() {
        System.out.println("AGC exits!");
    }

    public void onReceive(byte[] p_data, String p_ip, int p_port) {
    }

    // #region Processing's keyboard event callbacks.
    @Override
    public void keyPressed() {
        if (this.currentScene != null)
            this.currentScene.keyPressed();
    }

    @Override
    public void keyReleased() {
        if (this.currentScene != null)
            this.currentScene.keyReleased();
    }

    @Override
    public void keyTyped() {
        if (this.currentScene != null)
            this.currentScene.keyTyped();
    }
    // #endregion

    // #region Processing's mouse event callbacks.
    @Override
    public void mouseMoved() {
        if (this.currentScene != null)
            this.currentScene.mouseMoved();
    }

    @Override
    public void mouseWheel(processing.event.MouseEvent p_mouseEvent) {
        if (this.currentScene != null)
            this.currentScene.mouseWheel(p_mouseEvent);
    }

    @Override
    public void mouseClicked() {
        if (this.currentScene != null)
            this.currentScene.mouseClicked();
    }

    @Override
    public void mouseDragged() {
        if (this.currentScene != null)
            this.currentScene.mouseDragged();
    }

    @Override
    public void mouseExited() {
        if (this.currentScene != null)
            this.currentScene.mouseExited();
    }

    @Override
    public void mouseEntered() {
        if (this.currentScene != null)
            this.currentScene.mouseEntered();
    }

    @Override
    public void mouseReleased() {
        if (this.currentScene != null)
            if (this.currentScene != null)
                this.currentScene.mouseReleased();
    }

    @Override
    public void mousePressed() {
        pmousePressX = mouseX;
        pmousePressY = mouseY;

        if (this.currentScene != null)
            if (this.currentScene != null)
                this.currentScene.mousePressed();
    }
    // #endregion

    // #region Extras.
    public JFrame createSketchPanel(Runnable p_exitTask,
            Sketch p_sketch, PGraphics p_sketchGraphics) {
        // This is the dummy variable from Processing.
        JFrame ret = (JFrame) ((PSurfaceAWT.SmoothCanvas) p_sketch
                .getSurface().getNative()).getFrame();
        ret.removeNotify();
        ret.setUndecorated(true);
        ret.setLayout(null);
        ret.addNotify();

        ret.addWindowListener(new WindowListener() {
            @Override
            public void windowClosing(WindowEvent p_event) {
                System.out.println("Window closing...");
                p_sketch.agcExit();
            }

            // #region Unused...
            // Never called!:
            @Override
            public void windowClosed(WindowEvent p_event) {
                // System.out.println("Window CLOSED.");
                // Sketch.agcExit();
            }

            @Override
            public void windowOpened(WindowEvent p_event) {
            }

            @Override
            public void windowIconified(WindowEvent p_event) {
            }

            @Override
            public void windowDeiconified(WindowEvent p_event) {
            }

            @Override
            public void windowActivated(WindowEvent p_event) {
            }

            @Override
            public void windowDeactivated(WindowEvent p_event) {
            }
            // #endregion
        });

        // #region The `JPanel`:
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics p_javaGaphics) {
                if (p_javaGaphics instanceof Graphics2D) {
                    ((Graphics2D) p_javaGaphics).drawImage(
                            p_sketchGraphics.image, 0, 0, null);
                }
            }
        };

        // Let the `JFrame` be visible and request for `OS` permissions:
        ((JFrame) ret).setContentPane(panel); // This is the dummy variable from Processing.
        panel.setFocusable(true);
        panel.setFocusTraversalKeysEnabled(false);
        panel.requestFocus();
        panel.requestFocusInWindow();

        // Listeners for handling events :+1::
        panel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent p_mouseEvent) {
                p_sketch.mousePressed = true;
                p_sketch.mouseButton = p_mouseEvent.getButton();
                p_sketch.mousePressed();
            }

            public void mouseReleased(MouseEvent p_mouseEvent) {
                p_sketch.mousePressed = false;
                p_sketch.mouseReleased();
            }
        });

        // Listeners for `mouseDragged()` and `mouseMoved()`:
        panel.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent p_mouseEvent) {
                p_sketch.mouseX = MouseInfo.getPointerInfo().getLocation().x - ret.getLocation().x;
                p_sketch.mouseY = MouseInfo.getPointerInfo().getLocation().y - ret.getLocation().y;
                p_sketch.mouseDragged();
            }

            public void mouseMoved(MouseEvent p_mouseEvent) {
                p_sketch.mouseX = MouseInfo.getPointerInfo().getLocation().x - ret.getLocation().x;
                p_sketch.mouseY = MouseInfo.getPointerInfo().getLocation().y - ret.getLocation().y;
                p_sketch.mouseMoved();
            }
        });

        // For `keyPressed()`, `keyReleased()` and `keyTyped()`:
        panel.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent p_keyEvent) {
                p_sketch.key = p_keyEvent.getKeyChar();
                p_sketch.keyCode = p_keyEvent.getKeyCode();
                p_sketch.keyTyped();
            }

            @Override
            public void keyPressed(KeyEvent p_keyEvent) {
                p_sketch.key = p_keyEvent.getKeyChar();
                p_sketch.keyCode = p_keyEvent.getKeyCode();
                // System.out.println("Heard a keypress!");
                p_sketch.keyPressed();
            }

            @Override
            public void keyReleased(KeyEvent p_keyEvent) {
                p_sketch.key = p_keyEvent.getKeyChar();
                p_sketch.keyCode = p_keyEvent.getKeyCode();
                p_sketch.keyReleased();
            }
        });

        // Handle `Alt + F4` closes ourselves!:
        // It is kinda 'stupid' to use another listener for optimization, but the reason
        // why multiple listeners are allowed anyway is to let outer code access events
        // and also give you convenience :P

        // PS Notice how this uses `KeyAdapter` instead for
        panel.addKeyListener(new KeyAdapter() {
            boolean exited;

            public void keyPressed(KeyEvent p_keyEvent) {
                if (this.exited)
                    return;

                if (KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, InputEvent.ALT_DOWN_MASK) != null
                        && p_keyEvent.getKeyCode() == KeyEvent.VK_F4) {
                    // Apparently this wasn't the cause of an error I was trying to rectify.
                    // However, it *still is a good practice!*
                    if (!p_sketch.exitCalled()) {
                        p_exitTask.run();
                        this.exited = true;
                        p_keyEvent.consume();
                    }
                }
            }
        });
        // #endregion
        return ret;
    }
    // #endregion

}
