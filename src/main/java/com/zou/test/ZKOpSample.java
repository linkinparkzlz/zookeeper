package com.zou.test;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ZKOpSample {

    private ZooKeeper zk = null;

    public ZKOpSample(String connectString) {
        try {
            zk = new ZooKeeper(connectString, 1000, null);
        } catch (IOException e) {
            e.printStackTrace();

            if (zk != null) {
                try {
                    zk.close();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }

    }

    public ZooKeeper getZK() {
        return zk;
    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    //创建节点
    public String testCreateNode(String path, byte[] data, List<ACL> acls) {
        String res = "";

        try {
            res = zk.create(path, data, acls, CreateMode.PERSISTENT);
            System.out.println("创建节点 " + res + "成功");
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return res;
    }

    //异步删除节点
    public boolean deleteNodeWithAsync(String path, int version) {
        String context = "上下文对象测试";
        System.out.println("删除");
        zk.delete(path, version, new DeleteCallBack(), context);
        return true;
    }

    public List<ACL> getIpAcl() {
        List<ACL> acls = new ArrayList<ACL>();
        Id ipId = new Id("ip", "127.0.0.1");
        acls.add(new ACL(ZooDefs.Perms.ALL, ipId));
        return acls;
    }


    public List<ACL> getDigestAcl() {
        List<ACL> acls = new ArrayList<ACL>();
        Id digestId = new Id("digest",
                "javaclient2:CGf2ylBdcKMdCYuzd08lQfOPvN0=");
        acls.add(new ACL(ZooDefs.Perms.ALL, digestId));
        return acls;
    }


}























