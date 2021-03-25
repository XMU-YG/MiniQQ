package miniQQ.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * miniQQ 客户端
 * 客户端界面，消息发送以及接收
 * 每一个客户端新建一个消息监听线程接收消息
 * 界面使用JFrame设计
 * @version 1.0 群发字符串
 * @author yg
 * @email 1725993571@qq.com
 */
public class QQClient extends JFrame {
    private JTextArea outArea;   //输出区域
    private JTextArea inputArea;  //输入区域
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private Socket socket;
    private boolean isConnect;
    private Thread receive;//消息监听线程
    private int name;  //客户端名字，目前以端口做名字


    public QQClient(String hostAddress,int port){
        try{
            socket=new Socket(hostAddress,port);
            isConnect=true;
            name=socket.getLocalPort();
            outArea =new JTextArea();
            inputArea =new JTextArea();
            dataInputStream=new DataInputStream(socket.getInputStream());
            dataOutputStream=new DataOutputStream(socket.getOutputStream());
            receive=new Thread(new ReceiverThread());
            receive.start();
            System.out.println("连接成功！ Name: "+name+ "  ,Address: "+socket.getLocalSocketAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    /**
     * 窗口设置
     */
    public void createFrame(){
        /*窗口面板设置*/
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
                System.exit(0);
                disConnect();
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
     * 客户端断开连接
     */
    private void disConnect() {
        try {
            isConnect=false;
            //消息线程释放cpu
            receive.join();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (socket != null) {
                    socket.close();
                    socket = null;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void sendMessage(String text) {

        try{
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dataOutputStream.writeUTF(formatter.format(new Date())+"   " +name+": \n"+"     "+text);
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 消息接收线程类
     */
    private class ReceiverThread implements Runnable{

        @Override
        public void run() {
            try {
                System.out.println(name+" 的消息线程启动，开始监听...");
                while (isConnect){
                    String message=dataInputStream.readUTF();
                    outArea.setText(outArea.getText() + "\n" + message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
