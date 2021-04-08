package miniQQ_RMI.Server;

import miniQQ_RMI.Interface.IClientTalk;
import miniQQ_RMI.Interface.IServerTalk;
import miniQQ_RMI.Util.Providers;

import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

public class QQServer {

    private Registry registry;  //注册中心实例

    private StringBuffer talkRecord;  //聊天记录存储区

    public QQServer(String host, int hostPort) {

        talkRecord=new StringBuffer();
        try{
            registry= LocateRegistry.getRegistry(host, hostPort);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void start(){
        try{
            IServerTalk server=new ServerTalk();
            registry.bind(Providers.talk_server,server);
            System.out.println("服务端启动成功");
        }catch (RemoteException | AlreadyBoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 给特定客户端发送信息
     * @param message
     * @param name
     */
    public void send(String message,String name){
        try{
            IClientTalk iClientTalk= (IClientTalk) registry.lookup(name);
            iClientTalk.addMessage(message);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 给所有客户端发送信息
     * @param message
     * @throws RemoteException
     */
    public void sendToAll(String message) throws RemoteException {

        talkRecord.append(message).append("\n");

        Arrays.stream(registry.list()).forEach(name->{
            if (name.contains("client")){
                IClientTalk iClientTalk= null;
                try {
                    iClientTalk = (IClientTalk) registry.lookup(name);
                } catch (RemoteException | NotBoundException e) {
                    e.printStackTrace();
                }
                try {
                    iClientTalk.addMessage(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

        });

    }

    /**
     * 服务端接口实现类
     */
    private class ServerTalk extends UnicastRemoteObject implements IServerTalk{

        protected ServerTalk() throws RemoteException {
        }

        @Override
        public void sendToAll(String message) throws RemoteException {
            QQServer.this.sendToAll(message);
        }

        /**
         * 第一次链接，返回历史消息记录
         * @param clientName
         * @throws RemoteException
         */
        @Override
        public void connect(String clientName) throws RemoteException {
            if (talkRecord.length()!=0){
                send(talkRecord.toString(),clientName);
            }
        }
    }
}
