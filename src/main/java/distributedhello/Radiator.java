/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distributedhello;

import java.io.ByteArrayOutputStream;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.util.Random;
import java.util.logging.Logger;
import java.util.*;
import java.sql.Timestamp;
/**
 *
 * @author psmaatta
 */
public class Radiator implements Heater {
    
    final static int KB = 1024;
    final static int MB = 1024*KB;
    final static int TENMBS = 10*MB;
    int power = 1000;
    final int id;
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    String data_small;
    String data_mid;
    String data_large;

    public Radiator() {
        // let it be a natural number just for clarity
        int tmpid = new Random().nextInt();
        if (tmpid < 0) id = tmpid*-1;
        else id = tmpid;
        LOGGER.info("local Radiator ID: " + id);
        byte a = (byte)'a';
        byte[] kByteArray = new byte[KB];
        Arrays.fill(kByteArray, a);
        ByteArrayOutputStream small = new ByteArrayOutputStream(100*KB);
        ByteArrayOutputStream mid = new ByteArrayOutputStream(1000*KB);
        ByteArrayOutputStream large = new ByteArrayOutputStream(10000*KB);
        int size = 10000;
        for(int i=0; i<size; i++) {
            if(i < size/100) { small.write(kByteArray, 0, KB); }
            if(i < size/10) { mid.write(kByteArray, 0, KB); }
            large.write(kByteArray, 0, KB);
        }
        this.data_small = small.toString();
        this.data_mid = mid.toString();
        this.data_large = large.toString();
    }

    @Override
    public int getID() throws RemoteException, ServerNotActiveException {
        String remoteip = java.rmi.server.RemoteServer.getClientHost();
        LOGGER.info(remoteip + " called server method getID");
        return id; 
    }

    @Override
    public int getPower() throws RemoteException, ServerNotActiveException {
        String remoteip = java.rmi.server.RemoteServer.getClientHost();
        LOGGER.info(remoteip + " called method getPower. Heater power is " + power);
        return power++; 
    }

    @Override
    public String getDumpPackage(long timeInMillis, String packageType) throws RemoteException, ServerNotActiveException {
        String remoteip = java.rmi.server.RemoteServer.getClientHost();
        System.out.print("---\n" + remoteip + " called method getDumpPackage at " + (new Timestamp((new Date()).getTime())) + "---\n");
        String data = "";
        switch (packageType) {
            case "small":
                data = this.data_small + ";" + timeInMillis;
                break;
            case "mid":
                data = this.data_mid + ";" + timeInMillis;
                break;
            case "large":
                data = this.data_large + ";" + timeInMillis;
                break;
            default:
                System.out.println("Package Size not defined");
        }
        return data;
    }
}
