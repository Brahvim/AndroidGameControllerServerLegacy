package com.brahvim.androidgamecontroller.server.forms;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import com.brahvim.androidgamecontroller.server.AgcSettings;
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
                        })
                .addButton(StringTable.getString("SettingsForm.restartButton"), new Runnable() {
                    @Override
                    public void run() {
                        Process restarter = null;
                        String jarPath = null;

                        try {
                            jarPath = Sketch.class
                                    .getProtectionDomain()
                                    .getCodeSource()
                                    .getLocation()
                                    .toURI()
                                    .getPath();
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }

                        System.out.println(jarPath);

                        // #region Restart AGC.
                        try {
                            restarter = new ProcessBuilder(
                                    "cmd", "/c",
                                    "java", "jar", jarPath)
                                    .start();
                            AgcForm.UI.showInfoDialog(jarPath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // #endregion

                        if (restarter != null) {
                            AgcForm.UI.showInfoDialog("" + restarter.exitValue());
                        }

                        Sketch.agcExit();
                    }
                })
        // End of super-constructor call:
        );
    }

}
