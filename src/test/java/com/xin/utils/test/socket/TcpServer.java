package com.xin.utils.test.socket;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: socket服务端(用一句话描述该类做什么)
 * @date 2018-07-26 17:39
 * @Copyright (C)2017 , Luchaoxin
 */
public class TcpServer {

    public static void main(String[] args) {

        ServerSocket serverSocket = null;
        Socket socket = null;
        InputStream is = null;
        OutputStream os = null;
        InputStreamReader isr = null;
        BufferedReader bufferedReader = null;
        try {
            serverSocket = new ServerSocket(9999);
            while (true) {
                socket = serverSocket.accept();
                is = socket.getInputStream();
                os = socket.getOutputStream();
                isr = new InputStreamReader(is);
                bufferedReader = new BufferedReader(isr);
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    System.out.println("已接收到客户端连接");
                    System.out.println("服务端接收到客户端信息：" + line + ",当前客户端ip为：" + socket.getInetAddress().getHostAddress());
                }
//                System.out.println("开始回写信息。。。");
//                PrintWriter printWriter = new PrintWriter(os);
//                printWriter.write("已经处理完信息。。。");
//                printWriter.flush();
//                printWriter.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(is);
            close(os);
            close(isr);
            close(bufferedReader);
            close(socket);
            close(serverSocket);
        }
    }

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
            }
        }
    }
}
