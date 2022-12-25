package com.brahvim.androidgamecontroller.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

public class AgcSettings {
    private volatile static HashMap<String, String> table;
    public static File settingsFile;

    public static void init() {
        AgcSettings.refresh();

    }

    private static void refreshIfNeeded() {
        if (AgcSettings.table == null)
            AgcSettings.refresh();
    }

    private static synchronized void refresh() {
        // Search all files for the settings file:
        for (File f : Sketch.DATA_DIR_FILES) {
            String fileName = f.getName();
            if (fileName.contains("AgcSettings_")) {
                AgcSettings.settingsFile = f;
                AgcSettings.table = AgcSettings.parseTable(fileName);
            }
        }
        // System.out.println("settablelen");
        // System.out.println(table.keySet().size());
    }

    // Singleton! No constructor...
    // ...and yet I used `static` everywhere.
    private AgcSettings() {
    }

    private static HashMap<String, String> parseTable(String p_fileName) {
        File file = new File(Sketch.DATA_DIR, p_fileName);
        HashMap<String, String> ret = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            int eqPos, numEnd;
            for (String line; (line = reader.readLine()) != null;) {
                if (line.isBlank()) // Need to handle this separately...
                    continue;
                if (line.charAt(0) == '#')
                    continue;

                eqPos = line.indexOf('=');
                numEnd = line.indexOf('#');

                if (numEnd == -1)
                    numEnd = line.length();

                ret.put(line.substring(0, eqPos),
                        line.substring(eqPos + 1, numEnd));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;

        // To revert values:
        /*
         * // Revert the `values` `HashMap` and store the reverted values into `keys`.
         * // Too lazy to use `BiDiMap`/`BiMap`! :joy:
         * // ...apparently it's from a library, "Guava". No. Please no.
         * // // No more dependencies!
         * // reversed = new HashMap<Integer, String>();
         * 
         * // for (Map.Entry<String, Integer> entry : values.entrySet())
         * // reversed.put(entry.getValue(), entry.getKey());
         */
    }

    public static String getSetting(String p_key) {
        AgcSettings.refreshIfNeeded();

        String ret = AgcSettings.table.get(p_key);

        if (ret == null) {
            System.err.printf("Setting `%s` not found!\n", p_key);
            return "";
        }

        return ret;
    }

    public static synchronized void setElseAdd(String p_key, String p_value) {
        AgcSettings.settingsFile.delete();

        boolean retry = false;
        do {
            try {
                AgcSettings.settingsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                retry = true;
            }
        } while (retry);

        // Append the entire thing into the file:

        StringBuilder fileData = new StringBuilder();
        for (Map.Entry<String, String> entry : AgcSettings.table.entrySet()) {
            String setting = entry.getKey().concat("=").concat(entry.getValue());
            fileData.append(setting);

            // [https://stackoverflow.com/a/1625263/13951505]:
            try {
                Files.write(
                        AgcSettings.settingsFile.toPath(),
                        setting.getBytes(),
                        StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        AgcSettings.refresh();
    }

    public static synchronized void setElseAdd(String... p_settings) {
    }

    private static synchronized void add(String p_key, String p_value) {
        AgcSettings.refreshIfNeeded();

        // [https://stackoverflow.com/a/1625263/13951505]
        String setting = p_key.concat("=").concat(p_value);
        try {
            Files.write(
                    AgcSettings.settingsFile.toPath(),
                    setting.getBytes(),
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }

        AgcSettings.refresh();
    }

    private static synchronized void add(String... p_settings) {
        AgcSettings.refreshIfNeeded();

        // [https://stackoverflow.com/a/1625263/13951505]
        if ((p_settings.length & 1) != 0) {
            throw new IllegalArgumentException(
                    "The last key did not have a value! No settings written.");
        }

        try (FileWriter fw = new FileWriter("myfile.txt", true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw)) {

            int maxLen = p_settings.length - 1;
            for (int i = 0; i < maxLen; i++) {
                out.printf("%s=%s", p_settings[i], p_settings[i + 1]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
