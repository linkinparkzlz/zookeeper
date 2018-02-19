package com.zou.test;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

public class ZK_GetData_Sync implements Watcher {

    private static CountDownLatch countDownLatch = new CountDownLatch(1);
    private static ZooKeeper zooKeeper = null;
    private static Stat stat = new Stat();

    @Override
    public void process(WatchedEvent event) {

        if (event.getType() == Event.EventType.NodeDataChanged) {
            try {
                byte data[] = zooKeeper.getData(event.getPath(), true, stat);
                System.out.println("添加节点后=" + new String(data));
                System.out.println("添加节点后 dataversion " + stat.getVersion());
                countDownLatch.countDown();
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }

    }

    public static void main(String[] args) throws Exception {
        String path = "/acl/node10";
        zooKeeper = new ZooKeeper("localhost:2181", 5000, new ZK_GetData_Sync());
        byte data[] = zooKeeper.getData(path, true, stat);
        System.out.println("before=" + new String(data));
        System.out.println("before dataversion=" + stat.getVersion());
        countDownLatch.await();

    }
}
