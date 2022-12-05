package com.brahvim.androidgamecontroller.server.forms;

import com.brahvim.androidgamecontroller.server.AgcClient;
import com.brahvim.androidgamecontroller.server.AgcServerSocket;
import com.brahvim.androidgamecontroller.server.StringTable;

import uibooster.model.Form;
import uibooster.model.FormElement;
import uibooster.model.FormElementChangeListener;

public class BanRequestForm extends AgcForm {
    public BanRequestForm(AgcClient p_client, NewConnectionForm p_conForm) {
        super(AgcForm.UI.createForm(StringTable.getString("RejectConnection.winTitle"))
                .addLabel(
                        StringTable.getString("RejectConnection.message")
                                .replace("<name>", p_client.getDeviceName()))
                .addButton(StringTable.getString("RejectConnection.yes"), new Runnable() {
                    @Override
                    public void run() {
                        AgcServerSocket.getInstance().banClient(p_client);
                    }
                }).setID("btn_yes")
                .addButton(StringTable.getString("RejectConnection.no"), new Runnable() {
                    @Override
                    public void run() {
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
                }));
    }
}
