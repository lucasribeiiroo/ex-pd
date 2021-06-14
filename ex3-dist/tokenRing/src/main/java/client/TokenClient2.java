package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TokenClient2 {
    static boolean setSendData;
    static boolean hasToken;

    public static void main(String arg[]) throws Exception {

        InetAddress lclhost;
        BufferedReader br;
        String str1;
        TokenClient21 tkcl;
        TokenClient21 ser;

        while (true) {
            lclhost = InetAddress.getLocalHost();
            tkcl = new TokenClient21(lclhost);
            tkcl.setRecPort(8004);
            tkcl.setSendPort(9003);
            lclhost = InetAddress.getLocalHost();
            ser = new TokenClient21(lclhost);
            ser.setSendPort(9000);
            System.out.println("entering if");

            if (hasToken == true) {
                System.out.println("Do you want write data on the file --> YES/NO");
                br = new BufferedReader(new InputStreamReader(System.in));
                str1 = br.readLine();
                if (str1.equalsIgnoreCase("yes")) {
                    for (int i = 0; i < 50; i++) {
                        ser.setSendData = true;
                        ser.sendData();
                        ser.setSendData = false;
                    }
                } else if (str1.equalsIgnoreCase("no")) {
                    tkcl.sendToken();
                    hasToken = false;
                    System.out.println("Sending data to client 3");
                }
            } else {
                System.out.println("entering recieving mode");
                tkcl.recData();
                hasToken = true;
            }
        }
    }
}

class TokenClient21 {
    InetAddress lclhost;
    int sendport, recport;
    boolean setSendData = false;
    boolean hasToken = false;
    TokenClient21 tkcl;
    TokenClient21 ser;
    HttpClient httpClient = HttpClient.newHttpClient();

    TokenClient21(InetAddress lclhost) {
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

    void recData() throws Exception {
        String msgstr;
        byte buffer[] = new byte[256];
        DatagramSocket ds;
        DatagramPacket dp;
        ds = new DatagramSocket(recport);
        //ds = new DatagramSocket(4000);
        dp = new DatagramPacket(buffer, buffer.length);
        ds.receive(dp);

        ds.close();
        msgstr = new String(dp.getData(), 0, dp.getLength());
        System.out.println("The data is " + msgstr);

        if (msgstr.equals("Token")) {
            hasToken = true;
        }
    }

}
