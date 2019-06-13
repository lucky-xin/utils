package com.xin.utils.test;


import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: 面试学习(用一句话描述该类做什么)
 * @date 2018-07-26 15:43
 * @Copyright (C)2017 , Luchaoxin
 */
public class InterviewLearn {

    public void sayHello(Character c) {
        System.out.println("Hello,Character!");
    }

    public void sayHello(char c) {
        System.out.println("Hello,char!");
    }

    public void sayHello(int c) {
        System.out.println("Hello,int!");
    }

    public void sayHello(long c) {
        System.out.println("Hello,long!");
    }
    public void sayHello(float c) {
        System.out.println("Hello,float!");
    }

    public void sayHello(Serializable c) {
        System.out.println("Hello,Serializable!");
    }

    public void sayHello(Object c) {
        System.out.println("Hello,Object!");
    }

    public void sayHello(char... c) {
        System.out.println("Hello,char...!");
    }

    public static void main(String[] args) {
//        InterviewLearn interviewLearn = new InterviewLearn();
//        interviewLearn.sayHello('c');


    }


    public static void nioTest() {
        String MSG = "hello, I must be going \n";
        ServerSocketChannel serverSocketChannel = null;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            ServerSocket serverSocket = serverSocketChannel.socket();

            serverSocket.bind(new InetSocketAddress(9999));
            serverSocketChannel.configureBlocking(false);

            ByteBuffer buffer = ByteBuffer.wrap(MSG.getBytes());
            while (true) {
                SocketChannel serverChannel = serverSocketChannel.accept();
                if (serverChannel == null) {
                    // no connections, snooze a while ...
                    Thread.sleep(1000);
                } else {
                    System.out.println("Incoming connection from " + serverChannel.socket().getRemoteSocketAddress());
                    buffer.rewind();
                    //write msg to client
                    serverChannel.write(buffer);
                    serverChannel.close();
                }
            }


        } catch (Exception e) {

        }
    }
}
