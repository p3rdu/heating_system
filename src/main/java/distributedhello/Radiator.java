/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distributedhello;

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

    int power = 1000;
    final int id;
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    String data_small = "";
    String data_mid = "";
    String data_large = "";

    public Radiator() {
        // let it be a natural number just for clarity
        int tmpid = new Random().nextInt();
        if (tmpid < 0) id = tmpid*-1;
        else id = tmpid;
        LOGGER.info("local Radiator ID: " + id);

        String kbyte = "";

        for(int i=0; i<1024; i++) {
            kbyte += "a";
        }

        int size = 10000;
        for(int i=0; i<size; i++) {
            if(i < size/100) { this.data_small += kbyte; }
            if(i < size/10) { this.data_mid += kbyte; }
            this.data_large += kbyte;
        }
    }

    @Override
    public int getID() throws RemoteException, ServerNotActiveException {
        String remoteip = java.rmi.server.RemoteServer.getClientHost();
        LOGGER.info("method getID executed from " + remoteip );
        return id; 
    }

    @Override
    public int getPower() throws RemoteException {
        LOGGER.info("method executed - power: " + power);
        return power++; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDumpPackage() throws RemoteException {
        System.out.println("method executed - getDumpPackage: " + (new Timestamp((new Date()).getTime())));
        String data = this.data_large + ";" + (new Date()).getTime();
        return data;
    }
}
