/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package distributedhello;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author psmaatta
 */
public class Main {
    
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // print vm arguments of interest (two first are commented out because they were related to the SecurityManager)
        //LOGGER.info("java.security.policy=" + System.getProperty("java.security.policy"));
        //LOGGER.info("java.rmi.server.codebase=" + System.getProperty("java.rmi.server.codebase"));
        LOGGER.info("java.util.logging.SimpleFormatter.format="+System.getProperty("java.util.logging.SimpleFormatter.format"));
        String hostname = (args.length < 1) ? "localhost" : args[0];
        System.setProperty("java.rmi.server.hostname", hostname);
        LOGGER.info("java.rmi.server.hostname="+System.getProperty("java.rmi.server.hostname"));
        
        // I thought I needed this, but it is apparently not needed after all. If this is set, then we need also set the security.policy and why to bother?
        //System.setSecurityManager(new SecurityManager());
        Heater local = new Radiator();
        LOGGER.info("starting up server");
        try {
            // start RMI server by exporting Object radiator in port 1100, starting the registry and placing the object into the registry
            Heater radiator = (Heater) UnicastRemoteObject.exportObject(local,1100);
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.bind("Heater", radiator);
            LOGGER.info("started up server");
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        
        // Get peers from environment variables
        String peer1 = System.getenv("PEER1");
        String peer2 = System.getenv("PEER2");
        LOGGER.info(String.format("peers %s %s", peer1, peer2));
        (new ClientThread(peer1,peer2,local)).run();
    }
}
