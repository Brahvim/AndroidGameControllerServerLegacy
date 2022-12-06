package com.brahvim.androidgamecontroller.server.forms;

import com.brahvim.androidgamecontroller.server.AgcClient;
import com.brahvim.androidgamecontroller.server.AgcServerSocket;
import com.brahvim.androidgamecontroller.server.StringTable;

public class BanRequestForm extends AgcForm {
    public BanRequestForm(AgcClient p_client, NewConnectionForm p_conForm) {
        final BanRequestForm THIS = this;
        super.build = AgcForm.UI.createForm(
                StringTable.getString("RejectConnection.winTitle"))
                .addLabel(
                        StringTable.getString("RejectConnection.message")
                                .replace("<name>", p_client.getDeviceName()))
                .addButton(StringTable.getString("RejectConnection.yes"), new Runnable() {
                    @Override
                    public void run() {
                        AgcServerSocket.getInstance().banClient(p_client);
                        NewConnectionForm.noMorePings = false;
                        THIS.close();
                    }
                })
                .addButton(StringTable.getString("RejectConnection.no"), new Runnable() {
                    @Override
                    public void run() {
                        NewConnectionForm.noMorePings = false;
                        THIS.close();
                    }
                });
    }

    @Override
    protected void onClose() {
        NewConnectionForm.noMorePings = false;
    }
}
