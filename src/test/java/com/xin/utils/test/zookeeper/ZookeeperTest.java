package com.xin.utils.test.zookeeper;

import com.xin.utils.zookeeper.ZkDistributedLock;
import com.xin.utils.zookeeper.ZookeeperUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorEventType;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: zookeeper测试
 * @date 2018-08-05 23:24
 * @Copyright (C)2018 , Luchaoxin
 */
public class ZookeeperTest {
    static String servers = "192.168.80.129:2181";

    @Test
    public void test1() {
        CuratorFramework client = ZookeeperUtil.getClient(servers);
        client.start();
        client.getCuratorListenable();
        try {
            String path = client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .inBackground(new BackgroundCallback() {

                        @Override
                        public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
                            CuratorEventType eventType = event.getType();
                            int rc = event.getResultCode();
                            String path = event.getPath();
                            byte[] data = event.getData();
                            System.out.println("create Node:" + eventType + "---" + path + "---" + new String(data));
                        }
                    })
                    .forPath("/test", "/test".getBytes());
            System.out.println(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        client.checkExists();
    }

    static int n = 500;

    public static void secskill() {
        System.out.println(--n);
    }

    @Test
    public void test2() {
        try {
            getLock();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void getLock() throws KeeperException, InterruptedException {
        ZkDistributedLock lock = null;

        try {
            lock = new ZkDistributedLock(servers, "distributed");
            lock.tryLock(60 * 5, TimeUnit.SECONDS);
            secskill();
            System.out.println(Thread.currentThread().getName() + "正在运行");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (lock != null) {
//                lock.unlock();
            }
        }
    }

    public static void main(String[] args) {
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    getLock();
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        for (int i = 0; i < 5; i++) {
            Thread t = new Thread(runnable);
            t.start();
        }
    }
}
