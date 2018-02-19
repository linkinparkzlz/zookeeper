package com.zou.test;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.List;
import java.util.concurrent.CountDownLatch;

//获取子节点
public class ZK_GetChildren_Sync implements Watcher {

    private static CountDownLatch countDownLatch = new CountDownLatch(1);
    private static ZooKeeper zooKeeper = null;

    @Override
    public void process(WatchedEvent event) {

        if (event.getType() == Event.EventType.NodeChildrenChanged) {
            List<String> childrenList = null;
            try {
                childrenList = zooKeeper.getChildren(event.getPath(), true);
                System.out.println("添加节点后：" + childrenList.toString());
                countDownLatch.countDown();
            } catch (KeeperException e) {
                e.printStackTrace();

            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }

    }

    public static void main(String[] args) throws Exception {
        String path = "/acl";
        zooKeeper = new ZooKeeper("localhost:2181", 5000, new ZK_GetChildren_Sync());
        List<String> childrenList = zooKeeper.getChildren(path, true);
        System.out.println(childrenList.toString());
        countDownLatch.await();
    }
}




































