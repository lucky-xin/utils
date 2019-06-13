package com.xin.utils.zookeeper;

import com.xin.utils.StringUtil;
import org.apache.log4j.Logger;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: zookeeper实现分布式锁
 * @date 2018-08-05 23:37
 * @Copyright (C)2018 , Luchaoxin
 */
public class ZkDistributedLock implements Watcher {

    Logger logger = Logger.getLogger(ZkDistributedLock.class);

    private ZooKeeper zk = null;

    /**
     * 根节点
     */
    private static final String ROOT_LOCK = "/locks";

    /**
     * 竞争的资源
     */
    private String lockName;

    /**
     * 等待的前一个锁
     */
    private String waitLock;

    /**
     * 当前锁
     */
    private String currentLock;

    /**
     * 计数器
     */
    private CountDownLatch countDownLatch;

    private int sessionTimeout = 30000;

    /**
     * 配置分布式锁
     *
     * @param servers  连接的url
     * @param lockName 竞争资源
     */
    public ZkDistributedLock(String servers, String lockName) {
        this.lockName = lockName;
        init(servers);
    }

    private void init(String servers) {
        try {
            /**
             *   连接zookeeper
             */
            zk = new ZooKeeper(servers, sessionTimeout, this);
            Stat stat = zk.exists(ROOT_LOCK, false);
            if (stat == null) {
                /**
                 * 如果根节点不存在，则创建根节点
                 */
                String result = zk.create(ROOT_LOCK, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                if (!ROOT_LOCK.equals(result)) {
                    throw new RuntimeException("zookeeper分布式锁: 创建根节点异常");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("zookeeper分布式锁初始化异常" + e.getMessage());
        }
    }

    /**
     * 节点监视器
     */
    @Override
    public void process(WatchedEvent event) {

        Event.EventType eventType = event.getType();
        Event.KeeperState state = event.getState();

        switch (eventType) {
            case NodeCreated:
                break;
            case NodeDataChanged:
                break;
            case NodeChildrenChanged:
                try {
                    //再监听该子节点
                    String watchPath = event.getPath();
                    if (null == zk.exists(getWaitLock(), false)) {
                        logger.info(Thread.currentThread().getName() + " 释放了锁。。。");
                        this.countDownLatch.countDown();
                    }
                } catch (Exception e) {

                }
                break;
            default:
                break;
        }

    }

    public boolean tryLock() throws KeeperException, InterruptedException {

        String splitStr = "_lock_";
        if (lockName.contains(splitStr)) {
            throw new RuntimeException("锁名有误");
        }
        /**
         * 创建临时有序节点
         */
        String newLockName = ROOT_LOCK + "/" + lockName + splitStr;
        currentLock = zk.create(newLockName, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

        /**
         * 取所有子节点
         */
        List<String> subNodes = zk.getChildren(ROOT_LOCK, false);
        /**
         * 取出所有lockName的锁
         */
        List<String> lockObjects = new ArrayList<String>();
        String subNodPath = null;
        for (String subNod : subNodes) {
            subNodPath = subNod.split(splitStr)[0];
            if (subNodPath.equals(lockName)) {
                lockObjects.add(subNod);
            }
        }
        Collections.sort(lockObjects);
        /**
         * 若当前节点为最小节点，则获取锁成功
         */
        if (currentLock.equals(ROOT_LOCK + "/" + lockObjects.get(0))) {
            return true;
        }
        /**
         * 若不是最小节点，则找到自己的前一个节点
         */
        String prevNode = currentLock.substring(currentLock.lastIndexOf("/") + 1);
        waitLock = lockObjects.get(Collections.binarySearch(lockObjects, prevNode) - 1);
        return false;
    }

    public boolean tryLock(long timeout, TimeUnit timeUnit) throws KeeperException, InterruptedException {
        if (this.tryLock()) {
            return true;
        }
        return waitForLock(getWaitLock(), timeout, timeUnit);
    }

    private boolean waitForLock(String prev, long waitTime, TimeUnit timeUnit) throws KeeperException, InterruptedException {
        Stat stat = zk.exists(prev, true);
        if (stat != null) {
            this.countDownLatch = new CountDownLatch(1);
            /**
             *  计数等待，若等到前一个节点消失，则precess中进行countDown，停止等待，获取锁
             */
            this.countDownLatch.await(timeUnit.toMillis(waitTime), TimeUnit.MILLISECONDS);
            logger.info(Thread.currentThread().getName() + " 获得了锁。。。");
            this.countDownLatch = null;
        }
        return true;
    }

    public void unlock() throws KeeperException, InterruptedException {
        zk.delete(currentLock, -1);
        currentLock = null;
        zk.close();
    }

    private String getWaitLock() {
        return StringUtil.isEmpty(waitLock) ? null : ROOT_LOCK + "/" + waitLock;
    }
}
