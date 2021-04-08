package miniQQ_socket.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * miniQQ服务端
 * 处理客户端连接，以及消息群发
 * @author yg
 * @email 1725993571@qq.com
 */
public class QQServer extends Thread {

    /**
     * 服务端使用ServerSocket，port作为监听的端口
     * 客户端使用Socket
     */
    private ServerSocket serverSocket;

    private boolean isStart;

    /**
     * 客户端集群
     */
    private List<Client> clients;

    public QQServer(String host,int port){
        try {
            serverSocket=new ServerSocket();
            SocketAddress socketAddress=new InetSocketAddress(host,port);
            serverSocket.bind(socketAddress,port);
            isStart=true;
            clients=new ArrayList<>();
            System.out.println("服务器启动成功！"+serverSocket.getLocalSocketAddress());
        }catch (Exception e){
            System.out.println("服务器启动异常： "+e.getMessage());
            System.exit(0);
        }
    }

    @Override
    public synchronized void start() {
        try {
            System.out.println("等待连接中...");
            while (isStart){
                /*循环等待*/
                Socket socket=serverSocket.accept();
                System.out.println("连接成功! Address:  "+socket.getRemoteSocketAddress());
                Client client=new Client(socket);
                clients.add(client);
                System.out.println("当前在线人数： "+(clients.isEmpty()?0:clients.size()));
                /*这里只能用start，用run会阻塞*/
                new Thread(client).start();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 客户端
     */
    class Client implements Runnable{

        private DataInputStream dataInputStream;
        private DataOutputStream dataOutputStream;
        private boolean isConnect;
        private Socket client;

        public Client(Socket socket) {
            try{
                this.client=socket;
                dataInputStream=new DataInputStream(socket.getInputStream());
                dataOutputStream=new DataOutputStream(socket.getOutputStream());
                this.isConnect=true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 群发消息
         * @param message 消息
         */
        private void sendMessage(String message){
            try {
                dataOutputStream.writeUTF(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {

            try {
                while(isConnect){
                    String message=dataInputStream.readUTF();
                    //为每个客户端发送消息
                    for (Client client : clients) {
                        client.sendMessage(message);
                    }
                }
            }catch (Exception e) {
                e.printStackTrace();
            }finally {
                try {
                    if (dataOutputStream != null) {
                        dataOutputStream.close();
                    }
                    if (serverSocket != null) {
                        serverSocket.close();
                        serverSocket = null;
                    }
                    isConnect=false;
                    clients.remove(client);
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
