/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distributedhello;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;

/**
 *
 * @author psmaatta
 */
public interface Heater extends Remote {
    
    int getID() throws RemoteException, ServerNotActiveException;
    int getPower() throws RemoteException, ServerNotActiveException;
    String getDumpPackage(long timeInMillis) throws RemoteException, ServerNotActiveException;
}
