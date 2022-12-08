package com.brahvim.androidgamecontroller.server.forms;

import javax.swing.JDialog;

import com.brahvim.androidgamecontroller.RequestCode;
import com.brahvim.androidgamecontroller.server.AgcClient;
import com.brahvim.androidgamecontroller.server.AgcServerSocket;
import com.brahvim.androidgamecontroller.server.StringTable;

import uibooster.model.Form;

public class NewConnectionForm extends AgcForm {
    public volatile static boolean noMorePings = false;

    private boolean startedBanForm = false;

    private NewConnectionForm(AgcClient p_client) {
        final NewConnectionForm THIS = this;
        super.build = AgcForm.UI.createForm(
                StringTable.getString("ConfirmConnection.winTitle"))
                .addLabel(StringTable.getString("ConfirmConnection.message")
                        .replace("<name>", p_client.getDeviceName())
                        .replace("<address>", p_client.getIp()))
                .addButton(StringTable.getString("ConfirmConnection.yes"), new Runnable() {
                    @Override
                    public void run() {
                        THIS.close();
                        // THIS.noOptionSelected = false;
                        AgcServerSocket.getInstance().sendCode(
                                RequestCode.CLIENT_WAS_REGISTERED, p_client);
                        AgcServerSocket.getInstance().addClientIfAbsent(p_client);
                    }
                })
                .addButton(StringTable.getString("ConfirmConnection.no"), new Runnable() {
                    @Override
                    public void run() {
                        THIS.startedBanForm = true;
                        THIS.close();

                        // ...do this async or you'll break my app!:
                        new Thread() {
                            // ...I have no idea why!
                            @Override
                            public void run() {
                                BanRequestForm banForm = new BanRequestForm(p_client);
                                banForm.show();
                            }
                        }.start();
                    }
                });
    }

    public static NewConnectionForm build(AgcClient p_client) {
        while (NewConnectionForm.noMorePings)
            ;

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
    protected void onClose(Form p_form) {
        if (!this.startedBanForm)
            NewConnectionForm.noMorePings = false;
    }

}