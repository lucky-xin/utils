package com.xin.utils.test.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: nio客户端
 * @date 2018-08-06 14:08
 * @Copyright (C)2018 , Luchaoxin
 */
public class NIOSocketClient {

    public static void main(String[] args) throws IOException {

        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 1234));
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        while (true) {
//            Scanner sc = new Scanner(System.in);
//            String next = sc.next();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String next = reader.readLine();
            sendMessage(socketChannel, next);
            if (socketChannel.read(readBuffer) != -1) {
                System.out.println("服务器返回数据：" + new String(readBuffer.array()));
            }
        }
    }

    public static void sendMessage(SocketChannel socketChannel, String msg) throws IOException {
        if (msg == null || msg.isEmpty()) {
            return;
        }
        byte[] bytes = msg.getBytes("UTF-8");
        int size = bytes.length;
        ByteBuffer buffer = ByteBuffer.allocate(size);
        buffer.put(bytes);
        buffer.flip();
        while (buffer.hasRemaining()) {
            socketChannel.write(buffer);
        }
    }
}
