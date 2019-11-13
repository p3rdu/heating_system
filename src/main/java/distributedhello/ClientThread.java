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
import java.util.HashMap;

/**
 *
 * @author psmaatta
 */
public class ClientThread implements Runnable {
    
    String[] peers; 
    HashMap<String,Heater> peerRegistries;
    Heater local; 
    

    public ClientThread(String peer1, String peer2, Heater local) {
        this.peers = new String[]{peer1, peer2};
        this.local = local;
    }
    
    @Override
    public void run() {
        System.out.printf("thread %d running %n",java.lang.Thread.currentThread().getId()); 
        for (String s : peers) {
            int i = 1;
            while(true){
                System.out.println(i++ +". try to connect peer1: " + s);
                if (isSocketAlive(s,1099)) break;
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                    System.err.println(e.toString());
                }
            }
            try {
                Registry registry = LocateRegistry.getRegistry(s);
                Heater stub = (Heater) registry.lookup("Heater");
                String response = "power " + stub.getPower();
                System.out.printf("radiator ID %d%n", stub.getID());
                System.out.println("response: " + response);
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
                    System.out.println("SocketTimeoutException " + host + ":" + port + ". " + exception.getMessage());
            } catch (IOException exception) {
                    System.out.println("IOException - Unable to connect to " + host + ":" + port + ". " + exception.getMessage());
        }
        return isAlive;
    }
    
}
