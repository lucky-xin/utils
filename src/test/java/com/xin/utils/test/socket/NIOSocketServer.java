package com.xin.utils.test.socket;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: nio Socket(用一句话描述该类做什么)
 * @date 2018-07-27 8:19
 * @Copyright (C)2017 , Luchaoxin
 */
public class NIOSocketServer {

    static String message = "Hello,NIO";

    public static void main(String[] args) {
        int port = 1234;
       server2(port);
    }

    public static void server1(int port) {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress(1234));

            ByteBuffer writeBuffer = ByteBuffer.wrap(message.getBytes());
            ByteBuffer readerBuffer = null;
            while (true) {
                SocketChannel socketChannel = serverSocketChannel.accept();
                if (socketChannel == null) {
                    Thread.sleep(1000);
                } else {
                    readerBuffer = ByteBuffer.allocate(1024);
                    readerBuffer.clear();
                    socketChannel.read(readerBuffer);
                    readerBuffer.flip();
                    //output get
                    String result = new String(readerBuffer.array());
                    readerBuffer.clear();
                    System.out.println(result);
                    readerBuffer.rewind();
                    socketChannel.write(writeBuffer);
                }
            }

        } catch (Exception e) {

        } finally {

        }
    }

    public static void server2(int port) {
        //创建一个 TCP 套接字通道
        SocketChannel socketChannel = null;
        try {
            Selector selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);

            //向选择器注册一个通道
            SelectionKey selectionKey = socketChannel.register(selector, SelectionKey.OP_READ);

            boolean isConnect = socketChannel.connect(new InetSocketAddress("127.0.0.1", port));
            while (!socketChannel.finishConnect()) {
            }
            int num = selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();

            ByteBuffer writeBuffer = ByteBuffer.wrap(message.getBytes());

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                SocketChannel channel = (SocketChannel) key.channel();
                //判断通道就绪事件类型
                if (key.isReadable()) {
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    channel.read(buffer);
                    buffer.flip();
                    System.out.println(buffer.toString());
//                    channel.close();
                } else if (key.isWritable()) {
                    channel.write(writeBuffer);
                } else if (key.isConnectable()) {

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Test
    public void test2() {

    }
}
