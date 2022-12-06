package com.brahvim.androidgamecontroller.server.forms;

import javax.swing.JDialog;

import com.brahvim.androidgamecontroller.RequestCode;
import com.brahvim.androidgamecontroller.server.AgcClient;
import com.brahvim.androidgamecontroller.server.AgcServerSocket;
import com.brahvim.androidgamecontroller.server.StringTable;

import uibooster.model.Form;

public class NewConnectionForm extends AgcForm {
    public volatile static boolean noMorePings = false;

    private static NewConnectionForm lastInstance = null;
    private volatile boolean clientWasRejected, noOptionSelected;

    private NewConnectionForm(AgcClient p_client) {
        final NewConnectionForm THIS = this;

        super.build = AgcForm.UI.createForm(StringTable.getString("ConfirmConnection.winTitle"))
                .addLabel(
                        StringTable.getString("ConfirmConnection.message")
                                .replace("<name>", p_client.getDeviceName())
                                .replace("<address>", p_client.getIp()))
                .addButton(StringTable.getString("ConfirmConnection.yes"), new Runnable() {
                    @Override
                    public void run() {
                        THIS.noOptionSelected = false;
                        AgcServerSocket.getInstance().sendCode(
                                RequestCode.CLIENT_WAS_REGISTERED, p_client);
                        AgcServerSocket.getInstance().addClientIfAbsent(p_client);
                        THIS.close();
                    }
                })
                .addButton(StringTable.getString("ConfirmConnection.no"), new Runnable() {
                    @Override
                    public void run() {
                        THIS.noOptionSelected = false;
                        THIS.clientWasRejected = true;
                        BanRequestForm banForm = new BanRequestForm(
                                p_client, THIS);

                        THIS.close();
                        while (THIS.isOpen())
                            ;
                        banForm.show();
                    }
                });
    }

    public static NewConnectionForm build(AgcClient p_client) {
        // If the previous instance ain't done yet, don't make a new one!
        if (NewConnectionForm.lastInstance != null)
            return null;

        NewConnectionForm.noMorePings = true;
        return new NewConnectionForm(p_client);
    }

    @Override
    protected void onShow(Form p_form) {
        final JDialog WIN = p_form.getWindow();
        WIN.setFocusable(true);
        WIN.requestFocus();
    }

    @Override
    protected void onClose() {
        if (!this.clientWasRejected || this.noOptionSelected)
            NewConnectionForm.noMorePings = false;
        NewConnectionForm.lastInstance = null;
    }

}
