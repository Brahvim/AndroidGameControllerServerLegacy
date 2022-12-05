package com.brahvim.androidgamecontroller.server.forms;

import java.util.ArrayList;

import com.brahvim.androidgamecontroller.server.AgcClient;
import com.brahvim.androidgamecontroller.server.AgcServerSocket;
import com.brahvim.androidgamecontroller.server.Sketch;
import com.brahvim.androidgamecontroller.server.StringTable;

public class NewConnectionForm extends AgcForm {
    public static ArrayList<NewConnectionForm> INSTANCES = new ArrayList<>();

    public NewConnectionForm(AgcClient p_client) {
        super(Sketch.UI.createForm(StringTable.getString("ConfirmConnection.title!"))
                .addLabel(StringTable.getString("ConfirmConnection.message"))
                .addButton(StringTable.getString("ConfirmConnection.yes"), new Runnable() {
                    public void run() {
                        if (!AgcServerSocket.getInstance().isClientBanned(p_client)) {
                            AgcServerSocket.getInstance().addClientIfAbsent(p_client);
                        }
                    }
                })
                .addButton(StringTable.getString("ConfirmConnection.no"), new Runnable() {
                    public void run() {
                        if (!AgcServerSocket.getInstance().isClientBanned(p_client)) {
                            AgcServerSocket.getInstance().addClientIfAbsent(p_client);
                        }
                    };
                }));
        NewConnectionForm.INSTANCES.add(this);
    }

    public static void closeAllInstances() {
        for (NewConnectionForm f : NewConnectionForm.INSTANCES)
            f.close();
    }

}
