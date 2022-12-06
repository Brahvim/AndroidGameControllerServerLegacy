package com.brahvim.androidgamecontroller.server.forms;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JDialog;

import uibooster.UiBooster;
import uibooster.model.Form;
import uibooster.model.FormBuilder;
import uibooster.model.UiBoosterOptions;

// Wrapper classes providing `AgcForm` builders may or not be singletons.
// Those, which are not, MUST provide a method to close all instances.
public class AgcForm {
    public final static UiBooster UI = new UiBooster(UiBoosterOptions.Theme.DARK_THEME);
    public final static ArrayList<AgcForm> FORMS = new ArrayList<>();
    public final static ArrayList<AgcForm> VISIBLE_FORMS = new ArrayList<>(4);

    // #region Fields and constructors.
    protected FormBuilder build;
    private Form form;

    protected AgcForm() {
    }

    public AgcForm(FormBuilder p_build) {
        this.build = p_build;
        AgcForm.FORMS.add(this);
    }
    // #endregion

    public final Form show() {
        if (this.form != null)
            if (!this.form.isClosedByUser())
                this.form.close();

        this.form = this.build.run();
        this.makeFormRich();

        AgcForm.VISIBLE_FORMS.add(this);
        this.onShow(this.form);
        this.onVisible(this.form);
        return this.form;
    }

    public final Form showBlocking() {
        if (this.form != null)
            if (!this.form.isClosedByUser())
                this.form.close();

        this.form = this.build.show();
        this.makeFormRich();

        AgcForm.VISIBLE_FORMS.remove(this);
        this.onBlockingShow(this.form);
        this.onVisible(this.form);
        return this.form;
    }

    // Can't be overriden since it's `private`!
    private void makeFormRich() {
        final AgcForm AGC_FORM = this;
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

        WIN.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent p_windowEvent) {
                AGC_FORM.onClose();
            }
        });
    }

    public final boolean isOpen() {
        return this.form == null ? false : this.form.isClosedByUser() ? false : true;
    }

    public final boolean isClosed() {
        return this.form == null ? true : this.form.isClosedByUser();
    }

    public final void close() {
        if (this.form != null)
            this.form.close();
        this.onClose();
    }

    // #region Callbacks!~
    protected void onVisible(Form p_form) {
    }

    protected void onShow(Form p_form) {
    }

    protected void onBlockingShow(Form p_form) {
    }

    protected void onClose() {
    }
    // #endregion

    public static void closeAllAgcForms() {
        for (AgcForm f : AgcForm.VISIBLE_FORMS)
            f.close();
    }

}
