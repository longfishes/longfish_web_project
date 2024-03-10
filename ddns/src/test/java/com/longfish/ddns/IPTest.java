package com.longfish.ddns;

import com.longfish.ddns.util.IPv6Util;
import org.junit.jupiter.api.Test;

import java.net.*;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

public class IPTest {

    private static List<String> getIpAddress() {
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
            e.printStackTrace();
        }
        return list;
    }

    @Test
    public void test1() {
        System.out.println(getIpAddress());
    }

    @Test
    public void test2() {
        System.out.println(IPv6Util.getIpAddress());
    }
}
