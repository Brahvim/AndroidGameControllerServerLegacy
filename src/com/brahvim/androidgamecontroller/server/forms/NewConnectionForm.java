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
    private volatile static NewConnectionForm lastInstance = null;
    private final static ArrayList<NewConnectionForm> INSTANCES = new ArrayList<>();

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
                        THIS.close();

                        new Thread() {
                            @Override
                            public void run() {
                                while (THIS != null)
                                    ;

                                BanRequestForm banForm = new BanRequestForm(
                                        p_client, NewConnectionForm.lastInstance);
                                NewConnectionForm.destroyAllInstances();
                                banForm.show();
                            }
                        }.start();
                    }
                });
    }

    public static NewConnectionForm build(AgcClient p_client) {
        while (!(NewConnectionForm.lastInstance == null &&
                NewConnectionForm.INSTANCES.size() == 0))
            ;

        NewConnectionForm.noMorePings = true;
        return NewConnectionForm.lastInstance = new NewConnectionForm(p_client);
    }

    public static synchronized void destroyAllInstances() {
        for (int i = 0; i < NewConnectionForm.INSTANCES.size(); i++) {
            NewConnectionForm.INSTANCES.get(i).close();
        }

        NewConnectionForm.INSTANCES.clear();
    }

    @Override
    protected void onShow(Form p_form) {
        NewConnectionForm.lastInstance = this;

        final JDialog WIN = p_form.getWindow();
        WIN.setFocusable(true);
        WIN.requestFocus();
    }

    @Override
    protected void onClose() {
        NewConnectionForm.noMorePings = false;
        NewConnectionForm.lastInstance = null;
    }

}