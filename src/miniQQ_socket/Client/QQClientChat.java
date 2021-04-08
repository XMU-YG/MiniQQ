package miniQQ_socket.Client;



public class QQClientChat {
    public static void main(String[] args) {
        QQClient qqClient=new QQClient("127.0.0.1",8849);
        qqClient.createFrame();
    }
}
