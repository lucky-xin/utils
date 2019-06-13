package com.xin.utils.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryUntilElapsed;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: Zookeeper工具类
 * @date 2018-08-05 23:19
 * @Copyright (C)2018 , Luchaoxin
 */
public class ZookeeperUtil {

    private static CuratorFramework client;

    public static CuratorFramework getClient(String zkServers) {
        RetryPolicy retryPolicy = new RetryUntilElapsed(5000, 1000);
        client = CuratorFrameworkFactory
                .builder()
                .connectString(zkServers)
                .sessionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .build();
        return client;
    }


    public boolean exists(String path) throws Exception {
        return client.checkExists().forPath(path) != null;
    }

}
