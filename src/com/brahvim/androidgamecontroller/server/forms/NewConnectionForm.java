package com.brahvim.androidgamecontroller.server.forms;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import com.brahvim.androidgamecontroller.server.AgcClient;
import com.brahvim.androidgamecontroller.server.AgcServerSocket;
import com.brahvim.androidgamecontroller.server.StringTable;

public class NewConnectionForm extends AgcForm {
    public static ArrayList<NewConnectionForm> INSTANCES = new ArrayList<>();
    public static volatile boolean noMorePings = false;

    public NewConnectionForm(AgcClient p_client) {
        super(AgcForm.UI.createForm(StringTable.getString("ConfirmConnection.winTitle"))
                .addLabel(
                        StringTable.getString("ConfirmConnection.message")
                                .replace("<name>", p_client.getDeviceName())
                                .replace("<address>", p_client.getIp()))
                .addButton(StringTable.getString("ConfirmConnection.yes"), new Runnable() {
                    public void run() {
                        AgcServerSocket.getInstance().addClientIfAbsent(p_client);
                    }
                })
                .addButton(StringTable.getString("ConfirmConnection.no"), new Runnable() {
                    public void run() {
                    };
                })
        // End of `super()` constructor call:
        );
        NewConnectionForm.INSTANCES.add(this);
    }

    @Override
    protected void onVisible() {
        super.form.getWindow().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent p_windowEvent) {
                NewConnectionForm.noMorePings = false;
            }
        });
    }

    @Override
    protected void onClose() {
        NewConnectionForm.noMorePings = false;
    }

    public static void closeAllInstances() {
        for (NewConnectionForm f : NewConnectionForm.INSTANCES)
            f.close();
    }

}
