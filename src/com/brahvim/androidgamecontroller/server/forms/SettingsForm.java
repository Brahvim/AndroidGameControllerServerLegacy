package com.brahvim.androidgamecontroller.server.forms;

import com.brahvim.androidgamecontroller.server.Sketch;
import com.brahvim.androidgamecontroller.server.StringTable;

public class SettingsForm extends AgcForm {
    public final static SettingsForm INSTANCE = new SettingsForm();

    // static { // Future advice! Do not remove!
    // SettingsForm.INSTANCE = new SettingsForm();
    // }

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
        /*
         * .addButton(StringTable.getString("SettingsForm.restartButton"), new
         * Runnable() {
         * 
         * @Override
         * public void run() {
         * Process restarter = null;
         * String jarPath = null;
         * 
         * try {
         * jarPath = Sketch.class
         * .getProtectionDomain()
         * .getCodeSource()
         * .getLocation()
         * .toURI()
         * .getPath();
         * } catch (URISyntaxException e) {
         * e.printStackTrace();
         * }
         * 
         * System.out.println(jarPath);
         * 
         * // Solution to [https://stackoverflow.com/a/25866331/13951505]
         * if (!jarPath.endsWith(".jar"))
         * jarPath += ".jar";
         * 
         * // #region Restart AGC.
         * String javaPath = "";
         * try {
         * restarter = new ProcessBuilder(
         * "cmd", "/c",
         * javaPath = System.getProperty("java.home")
         * .concat(File.separator)
         * .concat("bin")
         * .concat(File.separator)
         * .concat("java.exe"),
         * "-jar", jarPath).start();
         * 
         * AgcForm.UI.showInfoDialog(jarPath);
         * AgcForm.UI.showInfoDialog(javaPath);
         * } catch (IOException e) {
         * e.printStackTrace();
         * }
         * // #endregion
         * 
         * // #region Debugging help!
         * String errorString = null;
         * if (restarter != null) {
         * try (
         * InputStream inputStream = restarter.getErrorStream();
         * BufferedReader errorStream = new BufferedReader(
         * new InputStreamReader(inputStream))) {
         * for (String line; (line = errorStream.readLine()) != null;) {
         * errorString += line.concat("\n");
         * }
         * } catch (Exception e) {
         * e.printStackTrace();
         * }
         * 
         * AgcForm.UI.showErrorDialog(errorString, "Errors!");
         * }
         * // #endregion
         * 
         * if (errorString == null)
         * Sketch.agcExit();
         * // Yeah. Just hang in there..
         * // Don't exit if you can't restart :joy:
         * }
         * })
         */

        // End of super-constructor call:
        );
    }

}
