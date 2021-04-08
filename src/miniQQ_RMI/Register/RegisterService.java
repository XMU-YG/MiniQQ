package miniQQ_RMI.Register;



import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import java.util.concurrent.CountDownLatch;

public class RegisterService {
    private final int port;

    public RegisterService(int port) {
        this.port = port;
    }
    public void register(){

        try{
            LocateRegistry.createRegistry(port);
            System.out.println("注册表启动成功！hostName: 127.0.0.1"+"   port:  "+port);
            CountDownLatch latch=new CountDownLatch(1);
            latch.await();
        } catch (RemoteException | InterruptedException e) {
            e.printStackTrace();
        }



    }

}
