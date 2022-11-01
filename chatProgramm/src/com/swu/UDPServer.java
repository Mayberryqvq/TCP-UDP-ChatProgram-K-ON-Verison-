package com.swu;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.*;

public class UDPServer extends JFrame implements Runnable, ActionListener, KeyListener {
    public static void main(String[] args) {
        UDPServer server = new UDPServer();
    }

    //私有化属性
    private JTextArea jta;
    private JScrollPane jsp;
    private JPanel jp;
    private JTextField jtf;
    private JButton jb;
    private JButton pauseButton;
    private ImageIcon image;
    private JLabel jl;
    private JPanel topPanel;
    private boolean flag = true;
    private DatagramSocket ds;
    private SocketAddress clientIP = null;

    //无参构造方法
    //播放音乐
    /**
         * 因为Runnable()是函数式接口，所以可以使用Lambda表达式此处用了匿名内部类 + Lambda表达式的写法，完整写法应该如下：
         * Runnable runnable = () -> {
         *     while (true) {
         *                 playMusic();
         *             }
         * }
         * new Thread(runnable).start;
         * */
    public UDPServer() {
        new Thread(() -> {
            while (true) {
                playMusic();
            }
        }).start();
        //设置背景
        image = new ImageIcon("/Users/zengjuehua/Desktop/004.jpg");
        jl = new JLabel(image);
        jl.setBounds(0, 0, image.getIconWidth(), image.getIconHeight());
        this.getLayeredPane().add(jl, Integer.valueOf(Integer.MIN_VALUE));
        topPanel = (JPanel) this.getContentPane();
        topPanel.setOpaque(false);
        //设置文本域、滚动条
        jta = new JTextArea();
        jta.setEditable(false);
        jta.setOpaque(false);
        jta.setText("Waiting for connection..");
        jta.append(System.lineSeparator());
        jta.setEditable(false);
        jsp = new JScrollPane(jta);
        jsp.setOpaque(false);
        jsp.getViewport().setOpaque(false);
        //设置文本框、发送按钮、播放按钮
        pauseButton = new JButton("Play/Pause");
        jp = new JPanel();
        jtf = new JTextField(10);
        jb = new JButton("发送");
        jp.add(jb);
        jp.add(jtf);
        jp.add(pauseButton);
        //将滚动条与面板全部添加到窗体中,滚动条位于窗体中间，面板位于窗体下方
        this.add(jsp, BorderLayout.CENTER);
        this.add(jp, BorderLayout.SOUTH);
        //设置窗体相关属性
        this.setTitle("軽音部活動室");
        this.setSize(image.getIconWidth(), image.getIconHeight());
        this.setLocation(150, 800);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//当窗体关闭时程序退出
        this.setVisible(true);
        this.setResizable(false);

        // ************************* 实现UDP通信 *************************
        //给播放/暂停按钮绑定一个点击事件
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                flag = !flag;
            }
        });
        //给发送按钮绑定一个监听点击事件
        jb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendDataToSocket();
            }
        });
        //给文本框绑定一个键盘监听点击事件
        jtf.addKeyListener(this);
        try {
            //指定ip地址
            InetAddress addr = Inet4Address.getByAddress(new byte[]{127, 0, 0, 1});
            //创建套接字，绑定端口号
            ds = new DatagramSocket(2333, addr);
            new Thread(this).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendDataToSocket() {
        //获取文本框中发送内容
        String text = jtf.getText();
        //拼接内容
        text = "UDPServer: " + text;
        //在自己的文本域中显示
        jta.append(text + System.lineSeparator());
        jtf.setText(" ");
        try {
            //发送文本
            byte[] dd = text.getBytes();
            DatagramPacket Data = new DatagramPacket(dd, dd.length, clientIP);
            ds.send(Data);
            //清空文本框内容
            jtf.setText(" ");
        } catch (Exception E) {
            E.printStackTrace();
        }
    }

    private void playMusic() {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(new File("/Users/zengjuehua/Music/Music/Media.localized/Music/豊崎愛生、日笠陽子、佐藤聡美、寿美菜子/けいおん!はいれぞ!「Come with Me!!」セット/ふわふわ時間(唯&澪 Mix).wav"));
            AudioFormat aif = ais.getFormat();
            final SourceDataLine sdl;
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, aif);
            sdl = (SourceDataLine) AudioSystem.getLine(info);
            sdl.open(aif);
            sdl.start();
            FloatControl fc = (FloatControl) sdl.getControl(FloatControl.Type.MASTER_GAIN);
            int nByte = 0;
            int writeByte = 0;
            final int SIZE = 1024 * 64;
            byte[] buffer = new byte[SIZE];
            while (nByte != -1) {// 判断 播放/暂停 状态
                if (flag) {
                    nByte = ais.read(buffer, 0, SIZE);
                    sdl.write(buffer, 0, nByte);
                } else {
                    nByte = ais.read(buffer, 0, 0);
                }
            }
            sdl.stop();
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
        //如果点击按键是esc，则播放/暂停音乐
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            flag = !flag;
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                byte[] data = new byte[255];
                DatagramPacket dp = new DatagramPacket(data, data.length);
                ds.receive(dp);
                clientIP = dp.getSocketAddress();
                String str = new String(dp.getData(), 0, dp.getLength());
                jta.append(str + '\n');
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }
}
