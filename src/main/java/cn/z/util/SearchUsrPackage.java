package cn.z.util;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
/**
 * @author zch
 */
@Slf4j
public class SearchUsrPackage {
  public static void sendPacket() {
    try {
      String sendBufStr = "30313233343536373839303132333435363738393031323334353637383930313233343536373839";
      String sendBufStr2 = "FF010102";
      byte[] sendBuf = StringUtil.strTobytes2(sendBufStr);
      byte[] sendBuf2 = StringUtil.strTobytes2(sendBufStr2);
      InetSocketAddress inetSocketAddress = new InetSocketAddress("255.255.255.255", 1500);
      TerminalManager.sendToPos(inetSocketAddress, sendBuf);
      Thread.sleep(1000);
      TerminalManager.sendToPos(inetSocketAddress, sendBuf2);
    }
    catch (InterruptedException e) {
      log.error(e.getMessage(), e);
      Thread.currentThread().interrupt();
    }
  }
}
