package com.brahvim.androidgamecontroller.server;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JDialog;

import uibooster.model.Form;
import uibooster.model.FormBuilder;

public class AgcForm {
    protected FormBuilder build;
    protected Form form;

    public AgcForm(FormBuilder p_build) {
        this.build = p_build;
    }

    protected Form show() {
        if (this.form != null)
            if (!this.form.isClosedByUser())
                this.form.close();

        this.form = this.build.run();
        this.makeFormRich();

        return this.form;
    }

    protected Form showBlocking() {
        if (this.form != null)
            if (!this.form.isClosedByUser())
                this.form.close();

        this.form = this.build.show();
        this.makeFormRich();

        return this.form;
    }

    public void makeFormRich() {
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

    public boolean isFormOpen() {
        return this.form == null ? false : this.form.isClosedByUser() ? false : true;
    }

    public boolean isFormClosed() {
        return this.form == null ? true : this.form.isClosedByUser();
    }

    public void closeForm() {
        if (this.form != null)
            this.form.close();
    }

}
