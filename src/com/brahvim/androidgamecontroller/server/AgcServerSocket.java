package com.brahvim.androidgamecontroller.server;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.jetbrains.annotations.NotNull;

import com.brahvim.androidgamecontroller.RequestCode;
import com.brahvim.androidgamecontroller.UdpSocket;
import com.brahvim.androidgamecontroller.serial.ByteSerial;
import com.brahvim.androidgamecontroller.serial.configs.AgcConfigurationPacket;
import com.brahvim.androidgamecontroller.server.forms.NewConnectionForm;

/**
 * Singleton!~
 */
public class AgcServerSocket extends UdpSocket {
    // #region Fields.
    public ArrayList<AgcClient> requestQueue;

    private static AgcServerSocket INSTANCE;

    private ArrayList<AgcClient> clients;
    // public static ArrayList<AgcClient> peers;
    // The ones that are currently being used! ...:D!
    private ArrayList<AgcClient> bannedClients;
    // #endregion

    private AgcServerSocket() {
        super(RequestCode.SERVER_PORT);
        this.clients = new ArrayList<>();
        this.requestQueue = new ArrayList<>();
        this.bannedClients = new ArrayList<>();
    }

    // #region Singleton stuff:
    public static AgcServerSocket getInstance() {
        return AgcServerSocket.INSTANCE;
    }

    public static AgcServerSocket init() {
        return AgcServerSocket.INSTANCE = new AgcServerSocket();
    }
    // #endregion

    // #region Client management methods.
    public void addClientIfAbsent(@NotNull AgcClient p_client) {
        boolean absent = true;
        for (AgcClient c : this.clients)
            if (c.equals(p_client))
                absent = false;

        if (absent) {
            this.clients.add(p_client);
        }
    }

    public boolean hasClient(@NotNull AgcClient p_client) {
        return this.clients.contains(p_client);
    }

    public void removeClient(String p_ip) {
        // Section `3.1` on: [https://www.baeldung.com/java-list-iterate-backwards].

        for (int i = this.clients.size(); i-- > 0;) {
            AgcClient c = this.clients.get(i);
            if (c.getIp().equals(p_ip))
                this.clients.remove(c);
        }
    }

    public void removeClient(@NotNull AgcClient p_client) {
        this.clients.remove(p_client);
    }

    public AgcClient getClientFromIp(String p_ip) {
        for (AgcClient c : this.clients)
            if (c.getIp().equals(p_ip))
                return c;
        return null;
    }

    public void unbanClient(String p_ip) {
        for (AgcClient c : this.bannedClients)
            if (c.getIp().equals(p_ip)) {
                this.bannedClients.remove(c);
                break;
            }
    }

    public void banClient(String p_ip, int p_port) {
        AgcClient client = null;

        String clientName = "";
        int clientPort = -1;

        for (AgcClient c : this.clients)
            if (c.getIp().equals(p_ip)) {
                clientName = c.getDeviceName();
                clientPort = c.getPort();
                client = c;
            }

        this.clients.remove(client);

        if (clientName == "")
            clientName = p_ip;

        if (clientPort == -1)
            throw new RuntimeException("WHAT?!");

        this.banClient(client);
    }

    public void banClient(@NotNull AgcClient p_client) {
        this.sendCode(RequestCode.CLIENT_WAS_BANNED, p_client);

        for (Sketch s : Sketch.SKETCHES) {
            if (s.client.equals(p_client)) {

            }
        }

        this.clients.remove(p_client);
        this.bannedClients.add(p_client);
    }

    /**
     * @return An {@linkplain ArrayList} of {@linkplain AgcClient}s.
     */
    public ArrayList<AgcClient> getClients() {
        return this.clients;
    }

    /**
     * @return An {@linkplain ArrayList} of banned
     *         {@linkplain AgcClient}s.
     */
    public ArrayList<AgcClient> getBannedClients() {
        return this.bannedClients;
    }

    public boolean isClientBanned(@NotNull AgcClient p_client) {
        return this.bannedClients.size() == 0 ? false
                : this.bannedClients.contains(p_client);
    }

    public boolean isIpBanned(String p_ip) {
        for (AgcClient c : this.bannedClients)
            if (c.getIp().equals(p_ip))
                return true;
        return false;
    }
    // #endregion

    public void restartReceiver() {
        super.receiver.stop();
        super.receiver.start();
        System.out.println("Restarted some receiver...");
    }

    // #region Custom methods.
    // From back when the `bannedIpStrings` and `bannedClientNames`
    // `ArrayList<String>`s were-a-thing!:

    /*
     * public void unbanIp(String p_ip) {
     * this.bannedIpStrings.remove(p_ip);
     * }
     * 
     * public void banClient(@NotNull AgcClient p_client) {
     * this.bannedIpStrings.add(p_client.getIp());
     * this.bannedClientNames.add(p_client.deviceName);
     * }
     * 
     * public void banClient(String p_ip, String p_name) {
     * this.bannedIpStrings.add(p_ip);
     * this.bannedClientNames.add(p_name);
     * }
     * 
     * public void banClient(String p_ip) {
     * String name = null;
     * 
     * for (AgcClient c : this.clients) {
     * if (c.getIp().equals(p_ip)) {
     * name = c.deviceName;
     * }
     * }
     * 
     * if (name == null) {
     * name = "`".concat(p_ip).concat("`");
     * }
     * 
     * this.bannedIpStrings.add(p_ip);
     * this.bannedClientNames.add(name);
     * }
     * 
     * public boolean isClientBanned(@NotNull @NotNull AgcClient p_client) {
     * if (this.bannedIpStrings.size() == 0)
     * return false;
     * 
     * String clientIp = p_client.getIp();
     * 
     * for (String s : this.bannedIpStrings)
     * if (s.equals(clientIp))
     * return true;
     * return false;
     * }
     * 
     */

    // Using `AgcServerSocket.AgcClient`s:
    public void sendCode(RequestCode p_code, @NotNull AgcClient p_client) {
        this.sendCode(p_code, p_client.getIp(), p_client.getPort());
    }

    public void sendCode(RequestCode p_code, String p_extraData, @NotNull AgcClient p_client) {
        this.sendCode(p_code, p_extraData, p_client.getIp(), p_client.getPort());
    }

    public void tellAllClients(RequestCode p_code) {
        for (AgcClient c : this.clients)
            this.sendCode(p_code, c);
    }

    public void tellAllClients(RequestCode p_code, String p_extraData) {
        for (AgcClient c : this.clients)
            this.sendCode(p_code, p_extraData, c);
    }

    /*
     * Older versions:
     * public void sendCode(RequestCode p_code, String p_ip, int p_port) {
     * byte[] toSend = new byte[RequestCode.CODE_SUFFIX.length + Integer.BYTES];
     * 
     * // Copy over the suffix,
     * for (int i = 0; i < RequestCode.CODE_SUFFIX.length; i++)
     * toSend[i] = RequestCode.CODE_SUFFIX[i];
     * 
     * // Put the code in!:
     * for (int i = RequestCode.CODE_SUFFIX.length; i < toSend.length; i++)
     * toSend[i] = RequestCode.CODE_SUFFIX[i];
     * 
     * super.send(toSend, p_ip, p_port);
     * }
     * 
     * public void sendCode(RequestCode p_code, String p_extraData, String p_ip, int
     * p_port) {
     * byte[] extraBytes = p_extraData.getBytes(StandardCharsets.UTF_8);
     * 
     * byte[] toSend = new byte[RequestCode.CODE_SUFFIX.length + Integer.BYTES +
     * extraBytes.length];
     * 
     * // Copy over the suffix,
     * for (int i = 0; i < RequestCode.CODE_SUFFIX.length; i++)
     * toSend[i] = RequestCode.CODE_SUFFIX[i];
     * 
     * // Put the code in!:
     * byte[] codeBytes = p_code.toBytes();
     * for (int i = RequestCode.CODE_SUFFIX.length; i < toSend.length; i++)
     * toSend[i] = codeBytes[i];
     * 
     * super.send(toSend, p_ip, p_port);
     * }
     */

    public void sendCode(RequestCode p_code, String p_ip, int p_port) {
        byte[] codeBytes = p_code.toBytes();
        byte[] toSend = new byte[codeBytes.length + RequestCode.CODE_SUFFIX.length];

        // System.out.printf("Copying the suffix, which takes `%d` out of `%d`
        // bytes.\n",
        /// RequestCode.CODE_SUFFIX.length, toSend.length);

        int i = 0;

        // Copy over the suffix,
        for (; i < RequestCode.CODE_SUFFIX.length; i++) {
            // System.out.printf("Value of iterator: `%d`.\n", i);
            toSend[i] = RequestCode.CODE_SUFFIX[i];
        }

        // System.out.printf("Copying the CODE, which takes `%d` out of `%d` bytes.\n",
        // codeBytes.length, toSend.length);

        // Put the code in!:
        for (i = 0; i < Integer.BYTES; i++) {
            // System.out.printf("Value of iterator: `%d`.\n",
            // RequestCode.CODE_SUFFIX.length + i);
            toSend[RequestCode.CODE_SUFFIX.length + i] = codeBytes[i];
        }

        System.out.printf("Sent `%s` to IP: `%s`, port: `%d`.\n", new String(toSend), p_ip, p_port);
        super.send(toSend, p_ip, p_port);
    }

    public void sendCode(RequestCode p_code, String p_extraData, String p_ip, int p_port) {
        byte[] extraBytes = p_extraData.getBytes(StandardCharsets.UTF_8);

        byte[] toSend = new byte[RequestCode.CODE_SUFFIX.length + Integer.BYTES + extraBytes.length];
        byte[] codeBytes = p_code.toBytes();

        // System.out.printf("Copying the suffix, which takes `%d` out of `%d`
        // bytes.\n",
        /// RequestCode.CODE_SUFFIX.length, toSend.length);

        int i = 0;

        // Copy over the suffix,
        for (; i < RequestCode.CODE_SUFFIX.length; i++) {
            // System.out.printf("Value of iterator: `%d`.\n", i);
            toSend[i] = RequestCode.CODE_SUFFIX[i];
        }

        // System.out.printf("Copying the CODE, which takes `%d` out of `%d` bytes.\n",
        // codeBytes.length, toSend.length);

        // Put the code in!:
        for (i = 0; i < Integer.BYTES; i++) {
            // System.out.printf("Value of iterator: `%d`.\n",
            // RequestCode.CODE_SUFFIX.length + i);
            toSend[RequestCode.CODE_SUFFIX.length + i] = codeBytes[i];
        }

        // System.out.printf("Copying EXTRA DATA, which takes `%d` out of `%d`
        // bytes.\n",
        // extraBytes.length, toSend.length);

        // Copy over extra bytes!:
        int startIdExtDataCopy = RequestCode.CODE_SUFFIX.length + codeBytes.length;
        for (i = 0; i < extraBytes.length; i++) {
            // System.out.printf("Value of iterator: `%d`.\n",
            // RequestCode.CODE_SUFFIX.length + i);
            toSend[startIdExtDataCopy + i] = extraBytes[i];
        }

        System.out.printf("Sent `%s` to IP: `%s`, port: `%d`.\n",
                new String(toSend).replace('\n', '\0'), p_ip, p_port);
        super.send(toSend, p_ip, p_port);
    }
    // #endregion

    // #region Overrides.
    @Override
    public void onReceive(@NotNull byte[] p_data, String p_ip, int p_port) {
        AgcClient sender = null;
        boolean isSenderKnown = true;

        // #region Give `sender` a value.
        for (AgcClient c : AgcServerSocket.getInstance().getClients()) {
            if (c.getIp().equals(p_ip)) {
                sender = c;
            }
        }

        if (sender == null) {
            isSenderKnown = false;
            sender = new AgcClient(p_ip, p_port, new String(
                    RequestCode.getPacketExtras(p_data)));
        }
        // #endregion

        // #region Send event to all sketches' scenes
        synchronized (Sketch.SKETCHES) {
            for (Sketch s : Sketch.SKETCHES) {
                if (s.currentScene != null)
                    if (s.currentScene.onReceive(p_data, sender))
                        break;
            }
        }
        // #endregion

        // All scenes reserve the right to data, not just a few select ones!:
        /*
         * if (this.currentScene == this.awaitingConnectionsScene)
         * ; // this.currentScene.onReceive(p_data, sender); return;
         * else if (this.currentScene == this.workScene) {
         * this.currentScene.onReceive(p_data, sender);
         * return;
         * } else if (this.currentScene == this.exitScene)
         * ;
         */

        if (RequestCode.packetHasCode(p_data)) {
            switch (RequestCode.fromReceivedPacket(p_data)) {
                case ADD_ME:
                    if (!NewConnectionForm.noMorePings)
                        System.out.printf("%s wants to connect!\n", sender.getDeviceName());

                    AgcClient toAdd = sender;
                    if (AgcServerSocket.getInstance().isClientBanned(toAdd))
                        return;

                    // TODO: Solve the queue list bug and this:

                    // *Me later:*
                    // Wait, there's a bug - O_o?
                    // There isn't one, meh :|
                    if (AgcServerSocket.getInstance().hasClient(sender)) {
                        AgcServerSocket.getInstance().sendCode(RequestCode.CLIENT_WAS_REGISTERED, sender);
                    }

                    // If the client isn't already in our list,
                    if (!(AgcServerSocket.getInstance().hasClient(toAdd)
                            // #region Big comment, LOL.
                            // Storing only arrays of hashcodes to compare
                            // would've been a good optimization, heh.
                            // ...which I OF COURSE, don't need to do.

                            // Pointers are just integers! Neat!
                            // (...but `AgcClient::equals()` ain't neat! :joy:)
                            // (Imagine if `equals()` *actually* used hashcodes.
                            // ...that would fail *pr'tty* quickly.)
                            // #endregion
                            || AgcServerSocket.getInstance().requestQueue.contains(sender))) {
                        // Yes, yes, `NewConnectionsForm.build()` will also check this,
                        // but the app breaks if you remove this check ¯\_(ツ)_/¯
                        if (!NewConnectionForm.noMorePings) {
                            NewConnectionForm.build(toAdd).show();
                        } else
                            return;
                    }
                    AgcServerSocket.getInstance().requestQueue.add(sender);

                case CLIENT_SENDS_CONFIG:
                    // TODO: Make sure no other sketch uses `Sketch.createNewInstance` this!

                    AgcServerSocket.getInstance().requestQueue.remove(sender);

                    if (AgcServerSocket.getInstance().getClients().size() == 1) {
                        Sketch firstSketch = Sketch.SKETCHES.get(0);

                        try {
                            sender.setConfig((AgcConfigurationPacket) ByteSerial
                                    .decode(RequestCode.getPacketExtras(p_data)));
                        } catch (IOException e) {
                            firstSketch.setScene(firstSketch.awaitingConnectionsScene);
                        }

                        firstSketch.client = sender;
                        firstSketch.setScene(firstSketch.workScene);
                    } else {
                        Sketch.createNewInstance(sender);
                    }
                    break;

                // Should never happen thatnks to the symlinks..:

                default:
                    try {
                        System.out.printf(
                                "Unknown request code packet from sender of IP `%s`: `%s`.",
                                sender.getIp(), new String(p_data));
                    } catch (IllegalArgumentException e) {
                    }
            }
        } else {
            if (!isSenderKnown)
                System.out.printf("IP `%s` sent a message with state data.\n", sender.getIp());
        }

    }

    @Override
    protected void onStart() {
        // super.setPort(RequestCodes.get("SERVER_PORT"));
        System.out.println("The socket has begun!");
        System.out.printf("Socket-Stats!:\n\t- IP: `%s`\n\t- Port: `%d`\n", super.getIp(), super.getPort());
    }

    @Override
    protected void onClose() {
        System.out.println("The socket's been disposed off, thanks for taking the service :)");
    }
    // #endregion

}
