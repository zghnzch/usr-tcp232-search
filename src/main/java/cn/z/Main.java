package cn.z;
import cn.hutool.core.date.DateUtil;
import cn.z.udp.UdpServer;
import cn.z.util.SearchUsrPackage;
import cn.z.util.TerminalManager;
import cn.z.util.threadpool.zfy.orderthread.ExecutorUtil;
import lombok.extern.slf4j.Slf4j;
/**
 * @author zch
 */
@Slf4j
public class Main {
  public static void main(String[] args) {
    // Thread t = new Thread(new UdpServer()) t.setName("SystemUDPListener") t.start()
    ExecutorUtil.getInstance().executeSequentially("10002", new UdpServer());
    long currentSeconds = DateUtil.currentSeconds();
    while (currentSeconds > 0) {
      try {
        Thread.sleep(10000);
        if (TerminalManager.ctx != null) {
          SearchUsrPackage.sendPacket();
        }
      }
      catch (InterruptedException e) {
        log.error(e.getLocalizedMessage(), e);
        Thread.currentThread().interrupt();
      }
      finally {
        currentSeconds = DateUtil.currentSeconds();
      }
    }
  }
}
