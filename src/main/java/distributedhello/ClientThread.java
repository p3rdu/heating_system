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
 * @author FAMP
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
                System.out.println(i++ +". try to connect peer: " + s.hostname + "; " + s.port);
                if (isSocketAlive(s.hostname,s.port)) break;
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                    System.err.println(e.toString());
                }
            }
            try {
                Registry registry = LocateRegistry.getRegistry(s.hostname, s.port);
                Heater stub = (Heater) registry.lookup("Heater");
                String packageSizes[] = {"small", "mid", "large"};
                long timeDifference = 0;
                long interArrivelrate = 0;
                long interArrivelrateAvg = 0;
                int iter = 50;

                for(String packageSize : packageSizes) {
                    for(int j=0; j<iter; j++) {
                        Date now = new Date();
                        long nowInMillis = now.getTime();
                        //System.out.printf("Calling remote method getDumpPackage on host %s at %s%n", s.hostname, now.toString());
                        // the remote call !!!
                        if(j != 0) {
                            interArrivelrateAvg += System.nanoTime() - interArrivelrate;
                        }
                        String response = stub.getDumpPackage(nowInMillis, packageSize);
                        long timeFromPacket = Long.parseLong(response.split(";")[1]);
                        timeDifference += new Date().getTime() - timeFromPacket;
                        interArrivelrate = System.nanoTime();
                        //System.out.printf("Remote call execution took %d ms%n",  timeDifference);
                        //Thread.sleep(2000);
                    }
                    timeDifference /= iter;
                    System.out.println("Package size: " + packageSize + "; average Time: " + timeDifference + "ms");
                    interArrivelrateAvg /= iter;
                    System.out.println("Inter arrival rate Average time: " + interArrivelrateAvg + "ns");
                }
                
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
