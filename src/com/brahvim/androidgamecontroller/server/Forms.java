package com.brahvim.androidgamecontroller.server;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JDialog;

import uibooster.model.Form;
import uibooster.model.FormBuilder;

public enum Forms {
    SETTINGS(Sketch.UI.createForm(StringTable.getString("SettingsForm.title"))
            .addButton(StringTable.getString("SettingsForm.exitButton"), new Runnable() {
                public void run() {
                    System.out.println("Exit button pressed!");
                    Sketch.agcExit();
                };
            }));

    // NEW_CONECTION(Forms.UI.createForm("")),
    // BAN(Forms.UI.createForm("")),
    // UNBAN(Forms.UI.createForm(""));

    protected FormBuilder build;
    protected Form form;

    // Apparently these are `private` by default.
    // /* private */ Forms(Function<Void, FormBuilder> p_initializer) {
    // /* private */ Forms(Forms.Builder p_initializer) {
    /* private */ Forms(FormBuilder p_build) {
        this.build = p_build;
    }

    public Form get() {
        return this.form;
    }

    public boolean isFormOpen() {
        return this.form == null ? false : this.form.isClosedByUser() ? false : true;
    }

    public boolean isFormClosed() {
        return this.form == null ? true : this.form.isClosedByUser();
    }

    public void show() {
        this.form = this.build.run();
        this.becomeRich();
    }

    public void showBlocking() {
        this.form = this.build.show();
        this.becomeRich();
    }

    public void closeForm() {
        if (this.form != null)
            this.form.close();
    }

    public Form showForm() {
        if (this.form != null)
            if (!this.form.isClosedByUser())
                this.form.close();

        this.form = this.build.run();
        return this.form;
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
