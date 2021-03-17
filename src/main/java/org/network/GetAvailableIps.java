package org.network;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

public class GetAvailableIps {
    ArrayList ipsFound = new ArrayList();
    ArrayList availableIps = new ArrayList();

    public ArrayList main() throws SocketException {

        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            if (!networkInterface.isUp()) {
                continue;
            }

            for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                int npf = interfaceAddress.getNetworkPrefixLength();
                InetAddress address = interfaceAddress.getAddress();
                InetAddress broadcast = interfaceAddress.getBroadcast();

                if (broadcast != null) {
                    String ip = address.toString().substring(1);

                    ipsFound.add(getIpWithoutHost(ip));

                }
            }
        }

        availableIps = removeDuplicates(ipsFound);

        return availableIps;
    }


    //from an ip like 10.0.0.10 returns 10.0.0.
    public static String getIpWithoutHost(String fullIp){
        StringBuilder ipReverse;
        ipReverse = new StringBuilder(fullIp).reverse();
        String ipCutted  = ipReverse.toString();
        ipCutted = ipCutted.substring(ipCutted.indexOf("."), ipCutted.length());
        return new StringBuilder(ipCutted).reverse().toString();
    }

    public static ArrayList removeDuplicates(ArrayList list)
    {
        Set set = new LinkedHashSet();

        set.addAll(list);

        list.clear();
        list.addAll(set);
        return list;
    }
}
