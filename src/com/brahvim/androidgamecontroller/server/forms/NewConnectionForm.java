package com.brahvim.androidgamecontroller.server.forms;

import java.util.ArrayList;

import javax.swing.JDialog;

import com.brahvim.androidgamecontroller.RequestCode;
import com.brahvim.androidgamecontroller.server.AgcClient;
import com.brahvim.androidgamecontroller.server.AgcServerSocket;
import com.brahvim.androidgamecontroller.server.StringTable;

import uibooster.model.Form;

public class NewConnectionForm extends AgcForm {
    public volatile static boolean noMorePings = false;
    private volatile static NewConnectionForm instance = null;
    private final static ArrayList<NewConnectionForm> INSTANCES = new ArrayList<>();

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

                        new Thread() {
                            @Override
                            public void run() {
                                BanRequestForm banForm = new BanRequestForm(p_client, THIS);
                                // NewConnectionForm.destroyAllInstances();
                                banForm.show();
                            }
                        }.start();
                    }
                });
    }

    public static void build(AgcClient p_client) {
        // NewConnectionForm.destroyAllInstances();
        // while (NewConnectionForm.noMorePings)
        // ;

        while (NewConnectionForm.noMorePings) {
            System.out.println("Instance is not yet `null`...");
        }

        NewConnectionForm.noMorePings = true;
        NewConnectionForm.instance = new NewConnectionForm(p_client);
    }

    public static NewConnectionForm getInstance() {
        return NewConnectionForm.instance;
    }

    public static synchronized void destroyAllInstances() {
        for (NewConnectionForm f : NewConnectionForm.INSTANCES) {
            if (f != null)
                f.close();
        }

        NewConnectionForm.INSTANCES.clear();
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
        // NewConnectionForm.destroyAllInstances();
    }

}