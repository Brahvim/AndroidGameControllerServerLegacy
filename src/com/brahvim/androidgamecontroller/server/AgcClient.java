package com.brahvim.androidgamecontroller.server;

import com.brahvim.androidgamecontroller.serial.configs.AgcConfigurationPacket;

public class AgcClient {
    /**
     * The configuration of buttons for this client.
     */
    private AgcConfigurationPacket config;

    private String ip;
    private int port;

    /**
     * The manufacturer-assigned name of the device as reported by
     * {@code Build.MODEL}.
     */
    private String deviceName;

    public AgcClient(String p_ip, int p_port, String p_deviceName) {
        this.ip = p_ip;
        this.port = p_port;
        this.deviceName = p_deviceName;
    }

    // #region Getters.
    /**
     * @return The IPv4 address of the device.
     */
    public String getIp() {
        return this.ip;
    }

    public int getPort() {
        return this.port;
    }

    /**
     * @return The manufacturer-assigned name of the device as reported by
     *         {@code Build.MODEL}.
     */
    public String getDeviceName() {
        return this.deviceName;
    }

    public AgcConfigurationPacket getConfig() {
        return this.config;
    }
    // #endregion

};
