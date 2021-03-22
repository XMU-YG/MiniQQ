package miniQQ.Server;

/**
 * QQ服务端启动入口
 */
public class QQChat {

    public static void main(String[] args) {
        QQServer qqServer=new QQServer("127.0.0.1",10000);
        qqServer.start();
    }

}
