package miniQQ_RMI.Interface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IClientTalk extends Remote {
    public void addMessage(String message) throws RemoteException;
}
