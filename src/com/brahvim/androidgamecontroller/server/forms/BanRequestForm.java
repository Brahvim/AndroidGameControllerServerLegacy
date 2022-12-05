package com.brahvim.androidgamecontroller.server.forms;

import com.brahvim.androidgamecontroller.server.AgcClient;
import com.brahvim.androidgamecontroller.server.AgcServerSocket;
import com.brahvim.androidgamecontroller.server.Sketch;
import com.brahvim.androidgamecontroller.server.StringTable;

public class BanRequestForm extends AgcForm {
    BanRequestForm(AgcClient p_client, NewConnectionForm p_newConForm, Runnable p_onBan) {
        super(Sketch.UI.createForm(StringTable.getString("RejectConnection.winTitle"))
                .addLabel(
                        StringTable.getString("RejectConnection.message")
                                .replace("<name>", p_client.getDeviceName()))
                .addButton(StringTable.getString("RejectConnection.yes"), new Runnable() {
                    @Override
                    public void run() {
                        AgcServerSocket.getInstance().banClient(p_client);
                    }
                })
                .addButton(StringTable.getString("RejectConnection.no"), new Runnable() {
                    @Override
                    public void run() {
                        p_onBan.run();
                    }
                }));
    }
}
