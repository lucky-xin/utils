package com.xin.utils.test.socket;

import java.io.*;
import java.net.Socket;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: socket客户端(用一句话描述该类做什么)
 * @date 2018-07-26 17:40
 * @Copyright (C)2017 , Luchaoxin
 */
public class TcpClient {

    public static void main(String[] args) {
        Socket socket = null;
        try {
            socket = new Socket("127.0.0.1", 9999);
            OutputStream os = socket.getOutputStream();
//            InputStreamReader inputStreamReader = new InputStreamReader(System.in);
            InputStream is = socket.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(is);
            BufferedReader bufferedRead = new BufferedReader(inputStreamReader);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(os);

//            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

            PrintWriter printWriter = new PrintWriter(os);//将输出流包装成打印流
            printWriter.print("服务端你好，我是Balla_兔子");
            printWriter.flush();

            String line = null;
//            System.out.println(bufferedRead.readLine());
//            while ((line = bufferedRead.readLine()) != null) {
//                System.out.println("收到服务端信息：" + line);
//            }

            //写完以后进行读操作
            BufferedReader br = new BufferedReader(inputStreamReader);
            StringBuffer sb = new StringBuffer();
            String temp;
            int index;
            while ((temp=br.readLine()) != null) {
                if ((index = temp.indexOf("eof")) != -1) {
                    sb.append(temp.substring(0, index));
                    break;
                }
                sb.append(temp);
            }
            System.out.println("from server: " + sb);
            br.close();
        } catch (Exception e) {

        } finally {
            close(socket);
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
