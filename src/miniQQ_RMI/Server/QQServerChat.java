package miniQQ_RMI.Server;

/**
 * QQ服务端启动入口
 */
public class QQServerChat {

    public static void main(String[] args) {
        QQServer QQServer = new QQServer("127.0.0.1", 8888);
        QQServer.start();
    }
}
