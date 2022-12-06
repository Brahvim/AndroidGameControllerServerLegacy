package com.brahvim.androidgamecontroller.server.forms;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;

import com.brahvim.androidgamecontroller.RequestCode;
import com.brahvim.androidgamecontroller.server.AgcClient;
import com.brahvim.androidgamecontroller.server.AgcServerSocket;
import com.brahvim.androidgamecontroller.server.StringTable;

import uibooster.model.Form;
import uibooster.model.FormElement;
import uibooster.model.FormElementChangeListener;

public class NewConnectionForm extends AgcForm {
    public static volatile boolean noMorePings = false;

    public BanRequestForm banRequestForm = null;

    private static NewConnectionForm INSTANCE = null;

    private NewConnectionForm(AgcClient p_client) {
        super(AgcForm.UI.createForm(StringTable.getString("ConfirmConnection.winTitle"))
                .addLabel(
                        StringTable.getString("ConfirmConnection.message")
                                .replace("<name>", p_client.getDeviceName())
                                .replace("<address>", p_client.getIp()))
                .addButton(StringTable.getString("ConfirmConnection.yes"), new Runnable() {
                    @Override
                    public void run() {
                        AgcServerSocket.getInstance().sendCode(RequestCode.CLIENT_WAS_REGISTERED, p_client);
                        AgcServerSocket.getInstance().addClientIfAbsent(p_client);
                    }
                }).setID("btn_yes")
                .addButton(StringTable.getString("ConfirmConnection.no"), new Runnable() {
                    @Override
                    public void run() {
                        BanRequestForm banForm = new BanRequestForm(
                                p_client, NewConnectionForm.INSTANCE);
                        banForm.show();
                    }
                }).setID("btn_no")
                .setChangeListener(new FormElementChangeListener() {
                    @Override
                    public void onChange(FormElement p_elt, Object p_value, Form p_form) {
                        switch (p_elt.getId()) {
                            case "btn_yes":
                            case "btn_no":
                                p_form.close();
                                break;
                        }
                    }
                })
        // End of `super()` constructor call:
        );
        NewConnectionForm.INSTANCE = this;
    }

    public static NewConnectionForm build(AgcClient p_client) {
        while (NewConnectionForm.INSTANCE != null)
            ;
        NewConnectionForm.noMorePings = true;
        return NewConnectionForm.INSTANCE = new NewConnectionForm(p_client);
    }

    @Override
    protected void onShow(Form p_form) {
        final NewConnectionForm AGC_FORM = this;
        final JDialog WIN = p_form.getWindow();

        WIN.setFocusable(true);
        WIN.requestFocus();
        WIN.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent p_WindowEvent) {
                // System.out.println("NewConnectionForm.onShow(...).new WindowAdapter() {...}.windowClosing()");
                // AGC_FORM.onClose();
            }
        });
    }

    @Override
    protected void onClose() {
        System.out.println("NewConnectionForm.onClose()");
        NewConnectionForm.noMorePings = false;
        NewConnectionForm.INSTANCE = null;
    }

}
