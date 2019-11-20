/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package distributedhello;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.util.LinkedList;
import java.util.logging.Logger;

/**
 *
 * @author psmaatta
 */
public class Main {
    
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    static String hostname;
    static int port;
    static NodeInfo node_info = new NodeInfo();
    static LinkedList<Node> peers;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        if (args.length < 1) {
            System.out.println("Enter Node id or IP address as an argument\n");
            return;
        }
        // Get peers
        peers = processOrVM(args[0]);
        String peerString = "";
        for (Node n : peers) {
            peerString += " " +n.hostname;
        }
        LOGGER.info("peers are" + peerString);           
        System.setProperty("java.rmi.server.hostname", hostname);
        LOGGER.info("java.rmi.server.hostname="+System.getProperty("java.rmi.server.hostname"));
        // this is the main data object resembling this node's local heater
        Heater localHeater = new Radiator();
        LOGGER.info("starting up server");
        try {
            // start RMI server by exporting Object radiator in port 1100, starting the registry and placing the object into the registry
            Heater radiator = (Heater) UnicastRemoteObject.exportObject(localHeater,port+1);
            Registry registry = LocateRegistry.createRegistry(port);
            registry.bind("Heater", radiator);
            LOGGER.info("started up server");
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        
        (new ClientThread(peers,localHeater)).run();
    }
    
    private static LinkedList<Node> processOrVM(String arg0) {
        final String IPregex = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";  
        if (arg0.matches(IPregex)) { // this is the case when nodes are Operating systems of their own
            hostname = arg0;
            port = 1099;
            //last digit of ip address resembles the node ID of this particular node
            int id = Integer.parseInt(""+arg0.charAt(arg0.length()-1));
            switch(id) { // Get peers from environment variables
                case 1:
                    node_info.registerNode(2, System.getenv("PEER1"), 1099);
                    node_info.registerNode(3, System.getenv("PEER2"), 1099);
                    break;
                case 2:
                    node_info.registerNode(1, System.getenv("PEER1"), 1099);
                    node_info.registerNode(3, System.getenv("PEER2"), 1099);
                    break;
                case 3:
                    node_info.registerNode(1, System.getenv("PEER1"), 1099);
                    node_info.registerNode(2, System.getenv("PEER2"), 1099);
            }
            return node_info.getRemainingNodes(id);
        }  
        // this is the case when nodes are just different processes on a same computer
        node_info.registerNode(1, "127.0.0.1", 1099);
        node_info.registerNode(2, "127.0.0.1", 1199);
        node_info.registerNode(3, "127.0.0.1", 1299);
        int id = Integer.parseInt(arg0);
        Node node = node_info.getNodeInfo(id);
        hostname = node.hostname;
        port = node.port;
        return node_info.getRemainingNodes(id);
    }
    
}
