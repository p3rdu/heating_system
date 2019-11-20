/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distributedhello;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;


/**
 *
 * @author psmaatta
 */
public class ClientThread implements Runnable {

    LinkedList<Node> peers;
    HashMap<String,Heater> peerRegistries;
    Heater local;


    public ClientThread(LinkedList<Node> peers, Heater local) {
        this.peers = peers;
        this.local = local;
    }

    @Override
    public void run() {
        System.out.printf("thread %d running %n",java.lang.Thread.currentThread().getId());
        for (Node s : peers) {
            int i = 1;
            while(true){
                System.out.println(i++ +". try to connect peer: " + s.hostname + "; " + (s.port-1));
                if (isSocketAlive(s.hostname,s.port-1)) break;
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    System.err.println(e.toString());
                }
            }
            try {
                for(int j=0; j<100; j++) {
                    Registry registry = LocateRegistry.getRegistry(s.hostname, s.port-1);
                    Heater stub = (Heater) registry.lookup("Heater");
                    // String response = "power " + stub.getPower();
                    String[] response = (stub.getDumpPackage()).split(";");
                    System.out.println("Sending Time: " +
                        ((new Date()).getTime() - Long.parseLong(response[1])));
                    Thread.sleep(1000);
                }
                // System.out.printf("radiator ID %d%n", stub.getID());
                // System.out.println("response: " + response);
            } catch (Exception e) {
                System.err.println("Client exception: " + e.toString());
                e.printStackTrace();
            }
        }
    }

    public static boolean isSocketAlive(String host, int port) {
        boolean isAlive = false;

        SocketAddress socketAddress = new InetSocketAddress(host, port);
        Socket socket = new Socket();
        // Timeout required - it's in milliseconds
        int timeout = 2000;

        try {
            socket.connect(socketAddress, timeout);
            socket.close();
            isAlive = true;

            } catch (SocketTimeoutException exception) {
                    // System.out.println("SocketTimeoutException " + host + ":" + port + ". " + exception.getMessage());
            } catch (IOException exception) {
                    // System.out.println("IOException - Unable to connect to " + host + ":" + port + ". " + exception.getMessage());
        }
        return isAlive;
    }

}
