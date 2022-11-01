package com.swu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.*;

public class UDPClient extends JFrame implements Runnable, ActionListener, KeyListener {
    public static void main(String[] args) {
        UDPClient client = new UDPClient();
    }

    //私有化属性
    private JTextArea jta;
    private JScrollPane jsp;
    private JPanel jp;
    private JTextField jtf;
    private JButton jb;
    private DatagramSocket ds;
    private ImageIcon image;
    private JLabel jl;
    private JPanel topPanel;

    //无参构造方法
    public UDPClient() {
        //设置背景
        image = new ImageIcon("/Users/zengjuehua/Desktop/003.jpg");
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
            //指定IP地址
            InetAddress addr = Inet4Address.getByAddress(new byte[]{127, 0, 0, 1});
            //创建套接字对象
            ds = new DatagramSocket();
            ///连接到指定IP地址和端口
            ds.connect(addr, 2333);
            String str = "Client is connected";
            byte[] data = str.getBytes();
            DatagramPacket dp = new DatagramPacket(data, data.length);
            ds.send(dp);
            new Thread(this).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        sendDataToSocket();
    }

    private void sendDataToSocket() {
        //获取文本框中发送内容
        String text = jtf.getText();
        //拼接内容
        text = "UDPClient: " + text;
        //在自己的文本域中显示
        jta.append(text + System.lineSeparator());
        jtf.setText(" ");
        try {
            //发送文本
            byte[] dd = text.getBytes();
            DatagramPacket Data = new DatagramPacket(dd,dd.length);
            ds.send(Data);
            //清空文本框内容
            jtf.setText(" ");
        } catch (Exception E) {
            E.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                byte[] data = new byte[255];
                DatagramPacket DP = new DatagramPacket(data, data.length);
                ds.receive(DP);
                String str = new String(DP.getData());
                jta.append(str + '\n');
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
