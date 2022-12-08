package com.brahvim.androidgamecontroller.server.forms;

import com.brahvim.androidgamecontroller.server.AgcClient;
import com.brahvim.androidgamecontroller.server.AgcServerSocket;
import com.brahvim.androidgamecontroller.server.StringTable;

import uibooster.model.Form;

public class BanRequestForm extends AgcForm {
    public BanRequestForm(AgcClient p_client) {
        final BanRequestForm THIS = this;
        super.build = AgcForm.UI.createForm(
                StringTable.getString("RejectConnection.winTitle"))
                .addLabel(StringTable.getString("RejectConnection.message")
                        .replace("<name>", p_client.getDeviceName()))
                .addButton(StringTable.getString("RejectConnection.yes"), new Runnable() {
                    @Override
                    public void run() {
                        AgcServerSocket.getInstance().banClient(p_client);
                        THIS.close();
                    }
                })
                .addButton(StringTable.getString("RejectConnection.no"), new Runnable() {
                    @Override
                    public void run() {
                        THIS.close();
                    }
                });
    }

    // ...yeah, I'll actually use this callback instead of inlining.
    // Don't worry about optimization!
    @Override
    protected void onClose(Form p_form) {
        NewConnectionForm.noMorePings = false;
    }
}
