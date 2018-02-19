package com.zou.test;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

import java.util.List;

public class CuratorClientTest {

    private CuratorFramework client = null;

    public CuratorClientTest() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString("localhost:2181")
                .sessionTimeoutMs(1000).retryPolicy(retryPolicy)
                .namespace("base").build();

        client.start();
    }


    public void closeClient() {
        if (client != null) {
            this.client.close();
        }
    }

    public void createNode(String path, byte[] data) throws Exception {

        client.create().creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT).withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                .forPath(path, data);
    }

    public void deleteNode(String path, int version) throws Exception {
        client.delete().guaranteed().deletingChildrenIfNeeded().withVersion(version)
                .inBackground(new DeleteCallBack()).forPath(path);
    }

    public void readNode(String path) throws Exception {
        Stat stat = new Stat();
        byte[] data = client.getData().storingStatIn(stat).forPath(path);
        System.out.println("读取节点 " + path + "的数据" + new String(data));
        System.out.println(stat.toString());
    }

    public void updateNode(String path, byte[] data, int version) throws Exception {
        client.setData().withVersion(version).forPath(path, data);
    }

    public void getChildren(String path) throws Exception {
        List<String> children = client.getChildren().usingWatcher(new WatcherTest()).forPath("/curator");

        for (String pth : children) {
            System.out.println("child:" + pth);
        }
    }

    public void addNodeDataWatcher(String path) throws Exception {
        final NodeCache cache = new NodeCache(client, path);
        cache.start();
        cache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                String data = new String(cache.getCurrentData().getData());
                System.out.println("path:" + cache.getCurrentData().getPath() + "data:" + data);
            }
        });
    }

    public void addChildWatcher(String path) throws Exception {
        final PathChildrenCache childrenCache = new PathChildrenCache(this.client, path, true);
        childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        System.out.println(childrenCache.getCurrentData().size());

        childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                if (event.getType().equals(PathChildrenCacheEvent.Type.INITIALIZED)) {
                    System.out.println("客户端子节点cache初始化数据完成");
                    System.out.println("size=" + childrenCache.getCurrentData().size());
                } else if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)) {
                    System.out.println("添加子节点：" + event.getData().getPath());
                    System.out.println("修改子节点数据：" + new String(event.getData().getData()));
                } else if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)) {
                    System.out.println("删除子节点:" + event.getData().getPath());
                } else if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_UPDATED)) {
                    System.out.println("修改子节点数据：" + event.getData().getPath());
                    System.out.println("修改子节点数据:" + new String(event.getData().getData()));
                }

            }
        });
    }

    public static void main(String[] args) {


    }

}




































