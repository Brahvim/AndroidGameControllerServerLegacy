package com.brahvim.androidgamecontroller.server.forms;

import com.brahvim.androidgamecontroller.server.Sketch;
import com.brahvim.androidgamecontroller.server.StringTable;

public class SettingsForm extends AgcForm {
    public static SettingsForm INSTANCE;

    static {
        SettingsForm.INSTANCE = new SettingsForm();
    }

    public SettingsForm() {
        super(AgcForm.UI.createForm(
                StringTable.getString("SettingsForm.winTitle"))
                .addButton(StringTable.getString("SettingsForm.exitButton"),
                        new Runnable() {
                            public void run() {
                                System.out.println("Exit button pressed!");
                                Sketch.agcExit();
                            };
                        }));
    }

}
