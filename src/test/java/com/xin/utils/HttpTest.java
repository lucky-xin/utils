package com.xin.utils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

public class HttpTest {

    private static ReentrantLock lock = new ReentrantLock();

    static final CountDownLatch latch = new CountDownLatch(2);

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        Socket socket = null;
        InputStream is = null;
        OutputStream os = null;
        InputStreamReader isr = null;
        BufferedReader reader = null;

        try {
            serverSocket = new ServerSocket(9999);
            socket = serverSocket.accept();
            is = socket.getInputStream();
            os = socket.getOutputStream();
            isr = new InputStreamReader(is);
            reader = new BufferedReader(isr);
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }


        } catch (Exception e) {

        } finally {
            close(is);
            close(os);
            close(isr);
            close(serverSocket);
        }

    }

    private static void close(Closeable io){
        try {
            io.close();
        } catch (Exception e) {
        }
    }



}
