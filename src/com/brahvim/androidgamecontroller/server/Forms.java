package com.brahvim.androidgamecontroller.server;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JDialog;

import uibooster.model.Form;
import uibooster.model.FormBuilder;

public enum Forms {
    SETTINGS(Sketch.UI.createForm(StringTable.getString("SettingsForm.winTitle"))
            .addButton(StringTable.getString("SettingsForm.exitButton"), new Runnable() {
                public void run() {
                    System.out.println("Exit button pressed!");
                    Sketch.agcExit();
                };
            })),

    NEW_CONNECTION(Sketch.UI.createForm(StringTable.getString("ConfirmConnection.title!"))
            .addLabel(StringTable.getString("ConfirmConnection.message"))
            .addButton(StringTable.getString("ConfirmConnection.yes"), new Runnable() {
                public void run() {
                    if (!AgcServerSocket.INSTANCE.isClientBanned(Forms.Data.lastClient)) {
                        AgcServerSocket.INSTANCE
                                .addClientIfAbsent(Forms.Data.lastSketch.client = Forms.Data.lastClient);
                    }
                };
            }));
    // BAN(Forms.UI.createForm("")),
    // UNBAN(Forms.UI.createForm(""));

    // TODO: Create this class for EACH form type. Communicate via the constructor:
    public class Example {
        private AgcForm form;
        private AgcClient lastClient;
        private Sketch lastSketch;

        public Example(AgcClient p_client, Sketch p_sketch) {
            // Initialize...
        }

        // These have been commented out due to errors, but must be written:
        // public static Form show(AgcClient p_client) {
        // Example ref = new Example(null, null);
        // Form ret = ref.form.show();
        // return ret;
        // }

        // public static Form showBlocking(Data p_data) {
        // Example ref = new Example(null, null);
        // return ref.form.showBlocking();
        // }
    }

    public class Data {
        public static AgcClient lastClient;
        public static Sketch lastSketch;
    }

    protected FormBuilder build;
    protected Form form;

    // Apparently these are `private` by default.
    // /* private */ Forms(Function<Void, FormBuilder> p_initializer) {
    // /* private */ Forms(Forms.Builder p_initializer) {
    /* private */ Forms(FormBuilder p_build) {
        this.build = p_build;
    }

    public boolean isFormOpen() {
        return this.form == null ? false : this.form.isClosedByUser() ? false : true;
    }

    public boolean isFormClosed() {
        return this.form == null ? true : this.form.isClosedByUser();
    }

    public Form show() {
        if (this.form != null)
            if (!this.form.isClosedByUser())
                this.form.close();

        this.form = this.build.run();
        this.becomeRich();

        return this.form;
    }

    public Form showBlocking() {
        if (this.form != null)
            if (!this.form.isClosedByUser())
                this.form.close();

        this.form = this.build.show();
        this.becomeRich();

        return this.form;
    }

    public void closeForm() {
        if (this.form != null)
            this.form.close();
    }

    private void becomeRich() {
        final Form FORM = this.form;
        final JDialog WIN = this.form.getWindow();

        WIN.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent p_keyEvent) {
                if (p_keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    FORM.close();
                }
            }
        });
    }

}
