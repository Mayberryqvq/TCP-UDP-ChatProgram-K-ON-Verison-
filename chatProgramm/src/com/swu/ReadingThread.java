package com.swu.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;


import static com.swu.Server.TCPServer.*;

public class ReadingThread extends Thread{
    //私有属性
    private BufferedReader br;
    private ServerThread st;
    //公有属性
    public String line;

    //无参构造方法
    public ReadingThread() {
    }

    //有参构造方法
    public ReadingThread(Socket socket, ServerThread st) throws IOException {
        br = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
        this.st = st;
        start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (!((line = br.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (st.flag == 0) {
                jta.append(line + System.lineSeparator());
            } else {
                jta.append(currentTime() + "  " + st.name + ":" + line + System.lineSeparator());
            }
        }
    }

    //获取当前时间
    private String currentTime() {
        return new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss").format(new Date());
    }
}
