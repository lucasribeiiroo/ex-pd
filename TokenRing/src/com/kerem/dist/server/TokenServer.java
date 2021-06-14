package com.kerem.dist.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class TokenServer {


    public static void main(String agrs[]) throws Exception {

        while (true) {
            Server sr = new Server();
            sr.recPort(8000);
            sr.recData();
        }
    }
}

class Server {
    boolean hasToken = false;
    boolean sendData = false;
    public static String global = "1000";
    int recport;

    void recPort(int recport) {
        this.recport = recport;
    }

    void recData() throws Exception {
        byte buff[] = new byte[256];
        DatagramSocket ds;
        DatagramPacket dp;
        String str;
        ds = new DatagramSocket(recport);
        dp = new DatagramPacket(buff, buff.length);
        ds.receive(dp);
        ds.close();
        str = new String(dp.getData(), 0, dp.getLength());
        System.out.println("The message is " + str);

        String[] stringArray = str.split(" ");
        if (stringArray[0].equals("READ")) {
            read(Integer.valueOf(stringArray[1]));
        }
        if (stringArray[0].equals("WRITE")) {
            write(Integer.valueOf(stringArray[1]));
        }
    }

    void read(Integer clientPort) throws IOException {
        String res = "RESPONSE " + global;
        DatagramPacket datagramPacket = new DatagramPacket(res.getBytes(StandardCharsets.UTF_8), res.length(), InetAddress.getLocalHost(), clientPort);
        DatagramSocket datagramSocket = new DatagramSocket(Integer.valueOf(clientPort));
        datagramSocket.send(datagramPacket);
        datagramSocket.close();
    }

    void write(Integer integer) {
        System.out.println("Resposta: " + integer);
    }
}