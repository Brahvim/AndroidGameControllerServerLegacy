package com.brahvim.androidgamecontroller.server;

import com.brahvim.androidgamecontroller.serial.configs.AgcConfigurationPacket;

public class AgcClient {
    /**
     * The configuration of buttons for this client.
     */
    @SuppressWarnings("unused")
    private AgcConfigurationPacket config;

    private String ip;

    /**
     * The manufacturer-assigned name of the device as reported by
     * {@code Build.MODEL}.
     */
    private String deviceName;

    private int port;

    public AgcClient(String p_ip, int p_port, String p_deviceName) {
        this.ip = p_ip;
        this.port = p_port;
        this.deviceName = p_deviceName;
    }

    /**
     * @return The IPv4 address of the device.
     */
    public String getIp() {
        return this.ip;
    }

    public int getPort() {
        return port;
    }

    /**
     * @return The manufacturer-assigned name of the device as reported by
     *         {@code Build.MODEL}.
     */
    public String getDeviceName() {
        return this.deviceName;
    }

};
