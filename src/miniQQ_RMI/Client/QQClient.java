package miniQQ_RMI.Client;


import miniQQ_RMI.Interface.IClientTalk;
import miniQQ_RMI.Interface.IServerTalk;
import miniQQ_RMI.Util.Providers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 基于RMI
 * miniQQ 客户端
 * 客户端界面，消息发送以及接收
 * 界面使用JFrame设计
 * @version 1.0 群发字符串
 * @author yg
 * @email 1725993571@qq.com
 */
public class QQClient extends JFrame{
    private final JTextArea outArea;   //输出区域
    private final JTextArea inputArea;  //输入区域

    private String name;  //客户端名字
    private final String host; //注册中心地址
    private final int hostPort; //注册中心端口
    private Registry registry;  //注册中心实例
    private String bindName;   //注册名

    public QQClient(String host, int hostPort){
        this.host = host;
        this.hostPort = hostPort;
        outArea =new JTextArea();
        inputArea =new JTextArea();
    }

    /**
     * 启动
     * 注册服务
     */
    public void start(){
        try{
            registry= LocateRegistry.getRegistry(host,hostPort);
            IClientTalk iClientTalk=new ClientTalk();
            bindName=Providers.talk_client+"_"+name;
            registry.bind(bindName,iClientTalk);
            System.out.println("客户端启动成功");
            IServerTalk iServerTalk= (IServerTalk) registry.lookup(Providers.talk_server);
            iServerTalk.connect(bindName);
            createFrame();
        } catch (RemoteException | AlreadyBoundException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 接收消息
     * @param message 消息
     */
    public void addMessage(String message){
        String text=outArea.getText()+"\n"+message;
        outArea.setText(text);
    }

    /**
     * 发送消息
     * @param message 消息
     */
    public void sendMessage(String message){
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String text=formatter.format(new Date())+"   " +name+": \n"+"     "+message;
            IServerTalk iServerTalk= (IServerTalk) registry.lookup(Providers.talk_server);
            iServerTalk.sendToAll(text);
        }catch (RemoteException | NotBoundException e){
            e.printStackTrace();
        }

    }
    /**
     * 聊天窗口设置
     */
    public void createFrame(){
        /*窗口面板设置*/
        this.frameInit();
        this.setLocation(250,100);
        this.setTitle("中间件虚拟群");
        this.setPreferredSize(new Dimension(550,600));
        /*输出区域*/

        outArea.setBackground(Color.LIGHT_GRAY);
        outArea.setEditable(false);
        outArea.setPreferredSize(new Dimension(550,400));

        /*输入区域*/

        inputArea.setBackground(Color.WHITE);
        inputArea.setPreferredSize(new Dimension(550,50));
        inputArea.setLineWrap(true);

        /*名字*/
        JTextArea nameArea=new JTextArea();
        nameArea.setText("name:  "+name);
        nameArea.setEditable(false);
        nameArea.setBackground(Color.gray);

        /*发送按钮*/
        JButton send=new JButton();
        send.setText("发送");

        add(outArea,BorderLayout.NORTH);
        add(inputArea,BorderLayout.CENTER);
        add(nameArea,BorderLayout.SOUTH);
        add(send,BorderLayout.EAST);

        //窗口关闭监听
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    registry.unbind(bindName);
                } catch (RemoteException | NotBoundException remoteException) {
                    remoteException.printStackTrace();
                }
                System.out.println(name+" 用户退出");
                System.exit(0);
            }
        });
        //发送按钮响应
        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = inputArea.getText().trim();
                // 清空输入区域信息
                inputArea.setText("");
                // 回车后发送数据到服务器
                sendMessage(text);
            }
        });

        pack();
        setVisible(true);

    }

    /**
     * 登陆界面
     */
    public void login(){
        /*窗口面板设置*/
        this.setLocation(250,250);

        this.setTitle("login");
        this.setPreferredSize(new Dimension(400,100));

        JTextField nameArea=new JTextField();
        nameArea.setPreferredSize(new Dimension(100,20));
        nameArea.setBackground(Color.LIGHT_GRAY);

        JTextField nameText=new JTextField("用户名： ");
        nameText.setPreferredSize(new Dimension(70,20));
        /*发送按钮*/
        JButton login=new JButton();
        login.setPreferredSize(new Dimension(70,20));
        login.setText("login");

        add(nameText,BorderLayout.WEST);
        add(nameArea,BorderLayout.CENTER);
        add(login,BorderLayout.SOUTH);

        //发送按钮响应
        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                name=nameArea.getText().trim();
                start();
            }
        });
        pack();
        setVisible(true);
    }

    /**
     * 客户端接口实现类
     */
    public class ClientTalk extends UnicastRemoteObject implements IClientTalk{

        protected ClientTalk() throws RemoteException {
        }

        @Override
        public void addMessage(String message) throws RemoteException {
            QQClient.this.addMessage(message);
        }
    }



}
