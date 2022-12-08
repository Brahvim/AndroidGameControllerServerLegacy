package com.brahvim.androidgamecontroller.server;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.brahvim.androidgamecontroller.RequestCode;
import com.brahvim.androidgamecontroller.serial.ByteSerial;
import com.brahvim.androidgamecontroller.serial.configs.AgcConfigurationPacket;
import com.brahvim.androidgamecontroller.serial.configs.ButtonConfig;
import com.brahvim.androidgamecontroller.serial.configs.DpadButtonConfig;
import com.brahvim.androidgamecontroller.serial.configs.ThumbstickConfig;
import com.brahvim.androidgamecontroller.serial.configs.TouchpadConfig;
import com.brahvim.androidgamecontroller.serial.states.ButtonState;
import com.brahvim.androidgamecontroller.serial.states.DpadButtonState;
import com.brahvim.androidgamecontroller.serial.states.KeyboardState;
import com.brahvim.androidgamecontroller.serial.states.ThumbstickState;
import com.brahvim.androidgamecontroller.serial.states.TouchpadState;
import com.brahvim.androidgamecontroller.server.forms.AgcForm;
import com.brahvim.androidgamecontroller.server.forms.NewConnectionForm;
import com.brahvim.androidgamecontroller.server.forms.SettingsForm;
import com.brahvim.androidgamecontroller.server.server_render.ButtonRendererForServer;
import com.brahvim.androidgamecontroller.server.server_render.DpadButtonRendererForServer;
import com.brahvim.androidgamecontroller.server.server_render.ServerRenderer;
import com.brahvim.androidgamecontroller.server.server_render.ThumbstickRendererForServer;
import com.brahvim.androidgamecontroller.server.server_render.TouchpadRendererForServer;

import processing.awt.PSurfaceAWT;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

public class Sketch extends PApplet {
    // #region Fields.
    public final static ArrayList<Sketch> SKETCHES = new ArrayList<>(1);

    public final static int REFRESH_RATE = GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getScreenDevices()[0].getDisplayMode().getRefreshRate();

    public final static File ROOT_DIR = new File("");
    public final static File DATA_DIR = new File("data");
    public final static File[] DATA_DIR_FILES = Sketch.DATA_DIR.listFiles();

    public static PImage agcIcon;

    // #region Instance fields.
    public final Color javaBgColor = new Color(0, 0, 0, 1);
    public final Sketch SKETCH = this;
    public AgcClient client;
    public int frameStartTime, pframeTime, frameTime;

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
    // #endregion Ma'h boilerplate :D
    // #endregion Window coordinates and window states.

    // #region `private` and `protected` fields.
    protected Scene currentScene;
    protected PGraphics gr;
    protected boolean agcExitCalled;
    // #endregion `private` and `protected` fields.
    // #endregion Instance fields.
    // #endregion Fields.

    // #region Scenes.
    // #region Scene methods.
    public Scene getScene() {
        return this.currentScene;
    }

    public void setScene(Scene p_scene) {
        p_scene.setup();
        this.currentScene = p_scene;
    }
    // #endregion

    public final Scene awaitingConnectionsScene, workScene, exitScene;

    // #region Scene methods.
    private void scenes_settingsMenuMsCheck() {
        if (mouseButton == MouseEvent.BUTTON3) {
            if (!SettingsForm.INSTANCE.isOpen())
                SettingsForm.INSTANCE.show();
        }
    }

    private void scenes_settingsMenuKbCheck() {
        // `525` is the context menu key / "right-click key" *at least* on my keyboard.
        if (keyCode == KeyEvent.VK_SPACE || keyCode == 525)
            if (!SettingsForm.INSTANCE.isOpen())
                SettingsForm.INSTANCE.show();
    }
    // #endregion

    {

        awaitingConnectionsScene = new Scene() {
            @Override
            public void draw() {
                gr.textAlign(PConstants.CENTER);
                gr.textSize(28);
                gr.text(StringTable.getString("AwaitingConnectionsScene.text"),
                        cx + sin(millis() * 0.001f) * 25, cy);
            }

            @Override
            public void mouseClicked() {
                scenes_settingsMenuMsCheck();
            }

            @Override
            public void keyPressed() {
                scenes_settingsMenuKbCheck();
            }
        };

        exitScene = new Scene() {
            SineWave fadeWave;

            @Override
            public void setup() {
                this.fadeWave = new SineWave(SKETCH, 0.0008f);
                this.fadeWave.zeroWhenInactive = true;
                this.fadeWave.endWhenAngleIs(90);
                this.fadeWave.start();

                // AgcServerSocket.tellAllClients(RequestCode.SERVER_CLOSE);
                AgcServerSocket.getInstance().close();
            }

            @Override
            public void draw() {
                if (this.fadeWave != null) {
                    float wave = this.fadeWave.get();
                    if (wave == 0) {
                        this.fadeWave.end();
                        delay(100);
                        exit();
                    } else {
                        bgColor = color(0, abs(1 - wave) * 150);
                    }
                }

                gr.textAlign(CENTER);
                gr.textSize(28);
                gr.fill(255, alpha(bgColor));
                gr.text(StringTable.getString("ExitScene.text"), cx, qy);
            }
        };

        workScene = new Scene() {
            // #region Fields.
            AgcConfigurationPacket config;

            @SuppressWarnings("unused")
            KeyboardState keyboardState;

            ArrayList<ButtonRendererForServer> buttonRenderers;
            ArrayList<DpadButtonRendererForServer> dpadButtonRenderers;
            ArrayList<ThumbstickRendererForServer> thumbstickRenderers;
            ArrayList<TouchpadRendererForServer> touchpadRenderers;
            // #endregion

            @Override
            public void setup() {
                System.out.println("CONGRATULATIIIIIIONS! You made it to the WORK scene!");

                this.config = client.getConfig();
                // Renderer initialization:
                this.buttonRenderers = new ArrayList<>(config.buttons.size());
                this.dpadButtonRenderers = new ArrayList<>(config.dpadButtons.size());
                this.thumbstickRenderers = new ArrayList<>(config.thumbsticks.size());
                this.touchpadRenderers = new ArrayList<>(config.touchpads.size());

                // #region Map them all over!:
                for (int i = 0; i < config.buttons.size(); i++) {
                    ButtonConfig c = config.buttons.get(i);
                    c.scale.set(
                            map(c.scale.x, 0, config.screenDimensions.x, 0, SKETCH.width),
                            map(c.scale.y, 0, config.screenDimensions.y, 0, SKETCH.height));
                    c.transform.set(
                            map(c.transform.x, 0, config.screenDimensions.x, 0, SKETCH.width),
                            map(c.transform.y, 0, config.screenDimensions.y, 0, SKETCH.height));
                    try {
                        ButtonRendererForServer toAdd = new ButtonRendererForServer(
                                SKETCH, c, new Robot());
                        toAdd.state.controlNumber = c.controlNumber;
                        buttonRenderers.add(toAdd);
                    } catch (AWTException e) {
                        e.printStackTrace();
                    }
                }

                for (int i = 0; i < config.dpadButtons.size(); i++) {
                    DpadButtonConfig c = config.dpadButtons.get(i);
                    c.scale.set(
                            map(c.scale.x, 0, config.screenDimensions.x, 0, SKETCH.width),
                            map(c.scale.y, 0, config.screenDimensions.y, 0, SKETCH.height));
                    c.transform.set(
                            map(c.transform.x, 0, config.screenDimensions.x, 0, SKETCH.width),
                            map(c.transform.y, 0, config.screenDimensions.y, 0, SKETCH.height));

                    try {
                        DpadButtonRendererForServer toAdd = new DpadButtonRendererForServer(
                                SKETCH, c, new Robot());
                        toAdd.state.controlNumber = c.controlNumber;
                        dpadButtonRenderers.add(toAdd);
                    } catch (AWTException e) {
                    }
                }

                for (int i = 0; i < config.thumbsticks.size(); i++) {
                    ThumbstickConfig c = config.thumbsticks.get(i);
                    c.scale.set(
                            map(c.scale.x, 0, config.screenDimensions.x, 0, SKETCH.width),
                            map(c.scale.y, 0, config.screenDimensions.y, 0, SKETCH.height));
                    c.transform.set(
                            map(c.transform.x, 0, config.screenDimensions.x, 0, SKETCH.width),
                            map(c.transform.y, 0, config.screenDimensions.y, 0, SKETCH.height));

                    try {
                        ThumbstickRendererForServer toAdd = new ThumbstickRendererForServer(
                                SKETCH, c, new Robot());
                        toAdd.state.controlNumber = c.controlNumber;
                        thumbstickRenderers.add(toAdd);
                    } catch (AWTException e) {
                    }
                }

                for (int i = 0; i < config.touchpads.size(); i++) {
                    TouchpadConfig c = config.touchpads.get(i);
                    c.scale.set(
                            map(c.scale.x, 0, config.screenDimensions.x, 0, SKETCH.width),
                            map(c.scale.y, 0, config.screenDimensions.y, 0, SKETCH.height));
                    c.transform.set(
                            map(c.transform.x, 0, config.screenDimensions.x, 0, SKETCH.width),
                            map(c.transform.y, 0, config.screenDimensions.y, 0, SKETCH.height));

                    try {
                        TouchpadRendererForServer toAdd = new TouchpadRendererForServer(
                                SKETCH, c, new Robot());
                        toAdd.state.controlNumber = c.controlNumber;
                        touchpadRenderers.add(toAdd);
                    } catch (AWTException e) {
                    }
                }
                // #endregion

            }

            @Override
            public void draw() {
                for (ServerRenderer r : ServerRenderer.all) {
                    r.draw(gr);
                }
            }

            @Override
            public void onReceive(byte[] p_data, AgcClient p_client) {
                if (RequestCode.packetHasCode(p_data)) {
                    switch (RequestCode.fromReceivedPacket(p_data)) {
                        case CLIENT_CLOSE:
                            AgcServerSocket.getInstance().removeClient(p_client);
                            if (Sketch.SKETCHES.size() == 1)
                                setScene(awaitingConnectionsScene);
                            else
                                exit();
                            break;

                        default:
                            break;
                    }
                } else { // Deserialize, compare using `controlNumber`, set!
                    Object deserialized = ByteSerial.decode(p_data);
                    boolean isButtonRecognized = updateRendererState(deserialized);
                    if (!isButtonRecognized)
                        System.out.printf("%s was not assigned.\n",
                                deserialized.getClass().getSimpleName());
                    // System.out.printf("It's ID was `%d`.\n",
                    // ((StateBase) deserialized).controlNumber);
                    // #endregion
                }

            }

            /**
             * @return Indication of whether or not some renderer made use of this update.
             */
            private boolean updateRendererState(Object p_updatedState) {
                // #region `instanceof` checks...
                if (p_updatedState instanceof ButtonState newState) {
                    for (int i = 0; i < this.buttonRenderers.size(); i++) {
                        ButtonState s = buttonRenderers.get(i).state;
                        if (s.controlNumber == newState.controlNumber) {
                            buttonRenderers.get(i).state = newState;
                            return true;
                        }
                    }
                } else if (p_updatedState instanceof DpadButtonState newState) {
                    for (int i = 0; i < this.dpadButtonRenderers.size(); i++) {
                        DpadButtonState s = this.dpadButtonRenderers.get(i).state;
                        if (s.controlNumber == newState.controlNumber) {
                            this.dpadButtonRenderers.get(i).state = newState;
                            return true;
                        }
                    }
                } else if (p_updatedState instanceof ThumbstickState newState) {
                    for (int i = 0; i < this.thumbstickRenderers.size(); i++) {
                        ThumbstickState s = this.thumbstickRenderers.get(i).state;
                        if (s.controlNumber == newState.controlNumber) {
                            this.thumbstickRenderers.get(i).state = newState;
                            return true;
                        }
                    }
                } else if (p_updatedState instanceof TouchpadState newState) {
                    for (int i = 0; i < this.touchpadRenderers.size(); i++) {
                        TouchpadState s = this.touchpadRenderers.get(i).state;
                        if (s.controlNumber == newState.controlNumber) {
                            this.touchpadRenderers.get(i).state = newState;
                            return true;
                        }
                    }
                } else if (p_updatedState instanceof KeyboardState newState) {
                    this.keyboardState = newState;
                    return true;
                }

                // Nothing listed here used it! return `false`!:
                return false;
            }

            @Override
            public void mouseClicked() {
                scenes_settingsMenuMsCheck();
            }

            @Override
            public void keyPressed() {
                scenes_settingsMenuKbCheck();
            }
        };
    }
    // #endregion

    // #region Constructors, `main()`, `settings()`...
    public Sketch() {
        Sketch.SKETCHES.add(this);
    }

    public Sketch(AgcClient p_client) {
        this();
        this.client = p_client;
    }

    public static void main(String[] p_args) {
        Sketch constructedSketch = new Sketch();
        String[] args = new String[] { constructedSketch.getClass().getName() };

        if (p_args == null || p_args.length == 0)
            PApplet.runSketch(args, constructedSketch);
        else
            PApplet.runSketch(PApplet.concat(p_args, args), constructedSketch);

        AgcServerSocket.init(); // No socket without this!

        System.out.printf("Welcome to AndroidGameController `%s`!\n",
                AgcSettings.getSetting("version"));
    }

    @Override
    public void settings() {
        super.size(
                Integer.parseInt(AgcSettings.getSetting("defaultWidth")),
                Integer.parseInt(AgcSettings.getSetting("defaultHeight")));
    }
    // #endregion

    @Override
    public void setup() {
        this.updateRatios();
        this.sketchFrame = this.createSketchPanel(new Runnable() {
            @Override
            public void run() {
                Sketch.agcExit();
            }
        }, this, this.gr = createGraphics(width, height));

        super.registerMethod("pre", this);
        super.registerMethod("post", this);

        super.surface.setTitle(StringTable.getString("Meta.winTitle"));
        super.surface.setLocation(
                displayWidth / 2 - (int) cx,
                displayHeight / 2 + (int) cy);
        super.surface.setIcon(Sketch.agcIcon = Sketch.agcIcon == null
                ? this.loadImage(StringTable.getString("Meta.iconPath"))
                : Sketch.agcIcon);

        this.minExtent = new PVector(0, 0);
        this.maxExtent = new PVector(displayWidth - width, displayHeight - height);

        super.frameRate(Sketch.REFRESH_RATE);
        super.surface.setAlwaysOnTop(true);

        this.setScene(Sketch.SKETCHES.size() == 1
                ? this.awaitingConnectionsScene
                : this.workScene);
    }

    public void pre() {
        if (!(this.pwidth == super.width || this.pheight == super.height))
            this.updateRatios();
    }

    @Override
    public void draw() {
        this.frameStartTime = super.millis(); // Timestamp.
        this.frameTime = this.frameStartTime - this.pframeTime;
        this.pframeTime = this.frameStartTime;

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
        this.gr.background(this.bgColor);

        super.pushMatrix();
        super.pushStyle();

        this.gr.pushMatrix();
        this.gr.pushStyle();

        if (this.currentScene != null)
            if (this.currentScene != null)
                this.currentScene.draw();

        this.gr.popMatrix();
        this.gr.popStyle();

        super.popMatrix();
        super.popStyle();

        this.gr.endDraw();
    }

    public void post() {
        this.pwidth = super.width;
        this.pheight = super.height;
    }

    private void myExit() {
        if (this.agcExitCalled)
            return;
        this.agcExitCalled = true;

        System.out.println("AGC exits!");
        this.setScene(this.exitScene);
    }

    public static void agcExit() {
        AgcForm.closeAllAgcForms();
        AgcServerSocket.getInstance().tellAllClients(RequestCode.SERVER_CLOSE);

        for (Sketch s : Sketch.SKETCHES) {
            s.myExit();
        }
    }

    public static Sketch createNewInstance(AgcClient p_client) {
        Sketch sketch = new Sketch(p_client);
        return sketch;
    }

    public void onReceive(byte[] p_data, String p_ip, int p_port) {
        AgcClient sender = null;

        for (AgcClient c : AgcServerSocket.getInstance().getClients()) {
            if (c.getIp().equals(p_ip)) {
                sender = c;
            }
        }

        if (sender == null) {
            sender = new AgcClient(p_ip, p_port, new String(
                    RequestCode.getPacketExtras(p_data)));
            System.out.printf("%s wants to connect!\n", sender.getDeviceName());
        } else
            System.out.printf("%s sent a message.\n", sender.getDeviceName());

        if (this.currentScene != null)
            if (currentScene == awaitingConnectionsScene)
                ; // this.currentScene.onReceive(p_data, sender); return;
            else if (currentScene == workScene) {
                this.currentScene.onReceive(p_data, sender);
                return;
            } else if (currentScene == exitScene)
                ;

        if (RequestCode.packetHasCode(p_data)) {
            switch (RequestCode.fromReceivedPacket(p_data)) {
                case ADD_ME:
                    final AgcClient toAdd = sender;
                    if (AgcServerSocket.getInstance().isClientBanned(toAdd))
                        return;

                    // If the client isn't already in our list,
                    if (!AgcServerSocket.getInstance().hasClient(toAdd))
                        // Yes, yes, `NewConnectionsForm.build()` will also check this,
                        // but the app breaks if you remove this check ¯\_(ツ)_/¯
                        if (!NewConnectionForm.noMorePings) {
                            NewConnectionForm.build(toAdd).show();
                        } else
                            return;
                    break;

                case CLIENT_SENDS_CONFIG:
                    if (AgcServerSocket.getInstance().getClients().size() == 1) {
                        Sketch sketch = Sketch.SKETCHES.get(0);
                        sender.setConfig((AgcConfigurationPacket) ByteSerial.decode(
                                RequestCode.getPacketExtras(p_data)));
                        sketch.client = sender;
                        sketch.setScene(sketch.workScene);
                        return;
                    }

                    Sketch.createNewInstance(sender);
                    break;

                default:
                    System.out.println(new String(p_data));
                    break;
            }
        }

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
    public void updateRatios() {
        cx = width * 0.5f;
        cy = height * 0.5f;
        qx = cx * 0.5f;
        qy = cy * 0.5f;
        q3x = cx + qx;
        q3y = cy + qy;
    }

    /*
     * public void myDefaultStyleSettings(PGraphics p_gr) {
     * p_gr.fill(255);
     * p_gr.stroke(0);
     * 
     * p_gr.strokeWeight(1);
     * p_gr.strokeCap(PConstants.PROJECT);
     * p_gr.strokeJoin(PConstants.ROUND);
     * 
     * // p_gr.textAlign(PConstants., ALT);
     * }
     */

    public JFrame createSketchPanel(Runnable p_exitTask,
            Sketch p_sketch, PGraphics p_sketchGraphics) {
        // This is the dummy variable from Processing.
        JFrame ret = (JFrame) ((PSurfaceAWT.SmoothCanvas) p_sketch
                .getSurface().getNative()).getFrame();
        ret.removeNotify();
        ret.setUndecorated(true);
        ret.setLayout(null);
        ret.addNotify();

        ret.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent p_event) {
                System.out.println("Window closing...");
                Sketch.agcExit();
            }
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
            @Override
            public void mousePressed(MouseEvent p_mouseEvent) {
                p_sketch.mousePressed = true;
                p_sketch.mouseButton = p_mouseEvent.getButton();
                p_sketch.mousePressed();
            }

            @Override
            public void mouseReleased(MouseEvent p_mouseEvent) {
                p_sketch.mousePressed = false;
                p_sketch.mouseReleased();
            }

            @Override
            public void mouseClicked(MouseEvent p_mouseEvent) {
                p_sketch.mouseButton = p_mouseEvent.getButton();
                p_sketch.mouseClicked();
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
        panel.addKeyListener(new KeyAdapter() {
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

                if (KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, InputEvent.ALT_DOWN_MASK) != null
                        && p_keyEvent.getKeyCode() == KeyEvent.VK_F4) {
                    // Apparently this wasn't the cause of an error I was trying to rectify.
                    // However, it *still is a good practice!*
                    if (!p_sketch.exitCalled()) {
                        if (!this.exited)
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
