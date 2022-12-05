package com.brahvim.androidgamecontroller.server.forms;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JDialog;

import uibooster.model.Form;
import uibooster.model.FormBuilder;

// Wrapper classes providing `AgcForm` builders may or not be singletons.
// Those, which are not, MUST provide a method to close all instances.
public class AgcForm {
    public final static ArrayList<AgcForm> FORMS = new ArrayList<>();
    public final static ArrayList<AgcForm> VISIBLE_FORMS = new ArrayList<>(4);

    private FormBuilder build;
    private Form form;

    public AgcForm(FormBuilder p_build) {
        this.build = p_build;
        AgcForm.FORMS.add(this);
    }

    public final Form show() {
        if (this.form != null)
            if (!this.form.isClosedByUser())
                this.form.close();

        this.form = this.build.run();
        this.makeFormRich();

        AgcForm.VISIBLE_FORMS.add(this);
        return this.form;
    }

    public final Form showBlocking() {
        if (this.form != null)
            if (!this.form.isClosedByUser())
                this.form.close();

        this.form = this.build.show();
        this.makeFormRich();

        AgcForm.VISIBLE_FORMS.remove(this);
        return this.form;
    }

    private void makeFormRich() {
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

    public void close() {
        if (this.form != null)
            this.form.close();
    }

    public static void closeAllAgcForms() {
        for (AgcForm f : AgcForm.VISIBLE_FORMS)
            f.close();
    }

}
