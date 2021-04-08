package miniQQ_RMI.Interface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IServerTalk extends Remote {
    void sendToAll(String message) throws RemoteException;
    void connect(String clientName) throws RemoteException;
}
