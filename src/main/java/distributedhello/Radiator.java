/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distributedhello;

import java.rmi.RemoteException;
import java.util.Random;
import java.util.logging.Logger;

/**
 *
 * @author psmaatta
 */
public class Radiator implements Heater {
    
    int power = 1000;
    final int id; 
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    
    public Radiator() {
        // let it be a natural number just for clarity 
        int tmpid = new Random().nextInt();
        if (tmpid < 0) id = tmpid*-1;
        else id = tmpid;
        LOGGER.info("local Radiator ID: " + id);
    }
    
    @Override
    public int getID() throws RemoteException {
        LOGGER.info("method executed - id: " + id);
        return id; 
    }
    
    @Override
    public int getPower() throws RemoteException {
        LOGGER.info("method executed - power: " + power);
        return power++; //To change body of generated methods, choose Tools | Templates.
    }
    
}
