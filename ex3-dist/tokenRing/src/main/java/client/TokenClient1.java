package client;

import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

public class TokenClient1 {

    public static void main(String arg[]) throws Exception {
        InetAddress lclhost;
        BufferedReader br;
        String str = "";
        TokenClient12 tkcl, tkser;
        //boolean hasToken;
        //boolean setSendData;

        while (true) {
            lclhost = InetAddress.getLocalHost();
            tkcl = new TokenClient12(lclhost);
            tkser = new TokenClient12(lclhost);
            //tkcl.setSendPort(9001);
            tkcl.setSendPort(9004);
            tkcl.setRecPort(8002);
            lclhost = InetAddress.getLocalHost();
            tkser.setSendPort(9000);

            if (tkcl.hasToken) {
                System.out.println("Do you want write data on the file --> YES/NO");
                br = new BufferedReader(new InputStreamReader(System.in));
                str = br.readLine();
                if (str.equalsIgnoreCase("yes")) {
                    for (int i = 0; i < 50; i++) {
                        tkser.setSendData = true;
                        tkser.sendData();
                        tkser.setSendData = false;
                    }
                } else if (str.equalsIgnoreCase("no")) {
                    //tkcl.hasToken=false;
                    System.out.println("Sending token to client 2");
                    tkcl.sendToken();
                    tkcl.recData();
                }
            } else {
                System.out.println("ENTERING RECEIVING MODE...");
                tkcl.recData();
            }
        }
    }
}

class TokenClient12 {
    InetAddress lclhost;
    int sendport, recport;
    boolean hasToken = true;
    boolean setSendData = false;
    TokenClient12 tkcl, tkser;
    HttpClient httpClient = HttpClient.newHttpClient();

    TokenClient12(InetAddress lclhost) {
        this.lclhost = lclhost;
    }

    void setSendPort(int sendport) {
        this.sendport = sendport;
    }

    void setRecPort(int recport) {
        this.recport = recport;
    }

    void sendData() throws Exception {
        if (setSendData) {
            HttpRequest get = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create("http://localhost:8080/api/read"))
                    .build();

            Integer lastValue = Integer.valueOf(httpClient.send(get, HttpResponse.BodyHandlers.ofString()).body());
            System.out.println(lastValue);

            int random = (int) (Math.random() * 100);
            int result = lastValue + random;

            HttpRequest post = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(new WriteRequest(String.valueOf(result)).toString()))
                    .uri(URI.create("http://localhost:8080/api/write"))
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json; utf-8")
                    .build();

            httpClient.send(post, HttpResponse.BodyHandlers.ofString()).body();

            setSendData = false;
            hasToken = false;
        }
    }

    void sendToken() throws Exception {
        String str = "Token";
        DatagramSocket ds;
        DatagramPacket dp;

        ds = new DatagramSocket(sendport);
        dp = new DatagramPacket(str.getBytes(), str.length(), lclhost, sendport - 1000);
        ds.send(dp);

        setSendData = false;
        hasToken = false;
    }

    String recData() throws Exception {
        String msgstr;
        byte buffer[] = new byte[256];
        DatagramSocket ds;
        DatagramPacket dp;
        ds = new DatagramSocket(8002);
        dp = new DatagramPacket(buffer, buffer.length);
        ds.receive(dp);
        ds.close();
        msgstr = new String(dp.getData(), 0, dp.getLength());
        System.out.println("The data is " + msgstr);

        if (msgstr.equals("Token")) {
            hasToken = true;
        }
        return msgstr;
    }
}