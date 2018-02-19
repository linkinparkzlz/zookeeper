package com.zou.test;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

public class ZKCreateSample implements Watcher {

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {

        ZooKeeper zooKeeper = new ZooKeeper("localhost:2181", 6000, new ZKCreateSample());

        System.out.println("begin  state :" + zooKeeper.getState());
        try {

            countDownLatch.await();

        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("Zookeeper session  establised ");
        }
        System.out.println("end  state :" + zooKeeper.getState());


    }


    @Override
    public void process(WatchedEvent event) {

        System.out.println("receive  watched  event :" + event);
        if (Event.KeeperState.SyncConnected == event.getState()) {
            countDownLatch.countDown();
        }
    }
}
