package com.swu.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;


public class TCPClient1 extends JFrame implements ActionListener, KeyListener {
    public static void main(String[] args) {
        TCPClient1 client = new TCPClient1();
    }

    //私有化属性
    private JScrollPane jsp;
    private JPanel jp;
    private JTextField jtf;
    private JButton jb;
    private ImageIcon image;
    private JLabel jl;
    private JPanel topPanel;
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 2333;
    private int flag = 0;
    private String name;
    //公有静态属性
    public static  BufferedReader br = null;
    public static String line;
    public static BufferedWriter bw = null;
    public static JTextArea jta;
    public static Socket client;



    //无参构造方法
    public TCPClient1() {
        //设置背景
        image = new ImageIcon("/Users/zengjuehua/Desktop/002.jpg");
        jl = new JLabel(image);
        jl.setBounds(0, 0, image.getIconWidth(), image.getIconHeight());
        this.getLayeredPane().add(jl, Integer.valueOf(Integer.MIN_VALUE));
        topPanel = (JPanel) this.getContentPane();
        topPanel.setOpaque(false);
        //设置文本域、滚动条
        jta = new JTextArea();
        jta.setOpaque(false);
        jta.setText("Waiting for connection...");
        jta.append(System.lineSeparator());
        jta.setEditable(false);
        jsp = new JScrollPane(jta);
        jsp.setOpaque(false);
        jsp.getViewport().setOpaque(false);
        //设置文本框、发送按钮
        jp = new JPanel();
        jtf = new JTextField(10);
        jb = new JButton("发送");
        jp.add(jb);
        jp.add(jtf);
        //将滚动条与面板全部添加到窗体中,滚动条位于窗体中间，面板位于窗体下方
        this.add(jsp, BorderLayout.CENTER);
        this.add(jp, BorderLayout.SOUTH);
        //设置窗体相关属性
        this.setTitle("軽音部活動室");
        this.setSize(image.getIconWidth(), image.getIconHeight());
        this.setLocation(1000, 400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//当窗体关闭时程序退出
        this.setVisible(true);
        this.setResizable(false);

        // ************************* 实现TCP通信 *************************
        //给发送按钮绑定一个监听点击事件
        jb.addActionListener(this);
        //给文本框绑定一个键盘监听点击事件
        jtf.addKeyListener(this);
        try {
            //创建Socket对象（并尝试连接到服务器端）
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            client = socket;
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            jta.append("You have successfully connected to server..." + System.lineSeparator());
            while ((line = br.readLine()) != null) {
                if (flag <= 1) {
                    jta.append(line + System.lineSeparator());
                } else {
                    jta.append(currentTime() + "  TCPServer：" + line + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        sendDataToSocket();
    }

    //发送信息
    private void sendDataToSocket() {
        //获取文本框中发送内容
        String text = jtf.getText();
        //在自己的文本域中显示
        if (flag == 0) {
            name = text;
            jta.append(text + System.lineSeparator());
        } else {
            jta.append(currentTime() + "  " + name + ": " + text + System.lineSeparator());
        }
        jtf.setText(" ");
        try {
            //发送文本
            bw.write(text);
            bw.newLine();
            bw.flush();
            //清空文本框内容
            jtf.setText(" ");
            flag++;
        } catch (Exception E) {
            E.printStackTrace();
        }
    }

    //获取当前时间
    private String currentTime() {
        return new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss").format(new Date());
    }

    @Override
    public void keyPressed(KeyEvent e) {
        //如果点击的按键是回车键
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            //发送数据
            sendDataToSocket();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
