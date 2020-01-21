import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZookeeperProSync implements Watcher {
    private static CountDownLatch countDownLatch=new CountDownLatch(1);
    private static ZooKeeper zooKeeper=null;
    private static Stat stat=new Stat();

    public static void main(String[] args) throws Exception {
        String path="/username";
        zooKeeper=new ZooKeeper("192.168.0.2:2181",5000,new ZookeeperProSync());
        countDownLatch.await();
        System.out.println(new String(zooKeeper.getData(path,true,stat)));
        Thread.sleep(Integer.MAX_VALUE);
    }

    public void process(WatchedEvent watchedEvent) {
        if(Event.KeeperState.SyncConnected==watchedEvent.getState()){
            if(watchedEvent.getType()== Event.EventType.None&&watchedEvent.getPath()==null){
                System.out.println("try to connect");
                countDownLatch.countDown();
            }else if(watchedEvent.getType()== Event.EventType.NodeDataChanged){
                try {
                    System.out.println("have been updated "+new String(zooKeeper.getData(watchedEvent.getPath(),true,stat)));
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
