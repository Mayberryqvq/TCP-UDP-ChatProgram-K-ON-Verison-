package com.swu.Server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.Socket;

import static com.swu.Server.TCPServer.*;

/**
 * 服务器线程类
 */

public class ServerThread extends Thread implements ActionListener, KeyListener {
    //私有属性
    private Socket client;
    private BufferedWriter bw = null;
    private ReadingThread rt;
    //公有属性
    public String name;
    public int flag = 0;

    //无参构造方法
    public ServerThread() {
    }

    public void setRt(ReadingThread rt) {
        this.rt = rt;
    }

    //有参构造方法
    public ServerThread(Socket s) throws IOException {
        client = s;
        //此处TCPServer中未给bw和br赋初值，所以放在ServerThread中处理
        bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
        List.add(bw);
        //给文本框绑定一个键盘监听点击事件
        start();
    }

    @Override
    public void run() {
        try {
            bw.write("Successfully connected, please input your username：");
            bw.newLine();
            bw.flush();
            jta.append(getName() + System.lineSeparator());
            while (!"byeClient".equals(rt.line)) {
                bw.flush();
                // 第一次进入，保存名字
                if (flag == 0) {
                    if (rt.line != null) {
                        name = rt.line;
                        flag++;
                        bw.write("Welcome, " + name + ", you can start to chat now...");
                        bw.newLine();
                        bw.flush();
                        jta.append(name + "connected to server..." + System.lineSeparator());
                    }
                } else {
                    flag++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {// 用户退出聊天室
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
