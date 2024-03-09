package com.longfish.ddns.util;

import lombok.extern.slf4j.Slf4j;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class IPv6Util {

    public static String getIpAddress() {
        List<String> list = new LinkedList<>();
        try {
            Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
            while (enumeration.hasMoreElements()) {
                NetworkInterface network = enumeration.nextElement();
                if (!network.isVirtual() || !network.isUp()) {
                    Enumeration<InetAddress> addresses = network.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress address = addresses.nextElement();
                        if (address instanceof Inet6Address) {
                            byte first = address.getAddress()[0];
                            if (first != 0 && first != -2) {
                                list.add(address.getHostAddress());
                            }
                        }
                    }
                }
            }
        } catch (SocketException e) {
            log.error("网络异常！, {}", e.getMessage());
        }
        return list.get(0);
    }
}
