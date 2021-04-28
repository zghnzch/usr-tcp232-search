package cn.z.udp;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.ResourceLeakDetector;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
/**
 * @author zchcpywin10
 */
@Slf4j
public class UdpServer implements Runnable {
  public static void main(String[] args) {
    new UdpServer().startServer();
  }
  private void startServer() {
    EventLoopGroup group = new NioEventLoopGroup();
    Bootstrap b = new Bootstrap();
    b.group(group).channel(NioDatagramChannel.class).option(ChannelOption.SO_BROADCAST, true).handler(new UdpSeverHandler());
    ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
    try {
      InetAddress localHost = InetAddress.getLocalHost();
      log.info("localHost = " + localHost);
      InetAddress loopbackAddress = InetAddress.getLoopbackAddress();
      log.info("loopbackAddress = " + loopbackAddress);
      getAllIps();
      String bindIp = getNeededIps("192.168");
      log.info("bindIp = " + bindIp);
      b.bind(bindIp, 55555).sync().channel().closeFuture().await();
    }
    catch (InterruptedException e) {
      log.error("udp线程异常1:" + e.getMessage());
      group.shutdownGracefully();
      Thread.currentThread().interrupt();
    }
    catch (UnknownHostException e) {
      e.printStackTrace();
    }
  }
  public static void getAllIps() {
    try {
      log.info("===================================================");
      log.info("InetAddress.getLocalHost():{}", InetAddress.getLocalHost().toString());
      Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
      Enumeration<InetAddress> addresses;
      while (en.hasMoreElements()) {
        NetworkInterface networkinterface = en.nextElement();
        log.info(networkinterface.getName());
        addresses = networkinterface.getInetAddresses();
        while (addresses.hasMoreElements()) {
          // log.info("/t" + addresses.nextElement().getHostAddress() + "")
          log.info("getHostAddress:{}", addresses.nextElement().getHostAddress());
        }
      }
      log.info("===================================================");
    }
    catch (Exception e) {
      log.error(e.getLocalizedMessage(), e);
    }
  }

  public static String getNeededIps(String keyWords) {
    try {
      log.info("===================================================");
      log.info("InetAddress.getLocalHost():{}", InetAddress.getLocalHost().toString());
      Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
      Enumeration<InetAddress> addresses;
      while (en.hasMoreElements()) {
        NetworkInterface networkinterface = en.nextElement();
        log.info(networkinterface.getName());
        addresses = networkinterface.getInetAddresses();
        while (addresses.hasMoreElements()) {
          String hostAddress = addresses.nextElement().getHostAddress();
          if(hostAddress.contains(keyWords)){
            return hostAddress;
          }
        }
      }
      log.info("===================================================");
    }
    catch (Exception e) {
      log.error(e.getLocalizedMessage(), e);
    }
    log.warn("127.0.0.1");
    return "127.0.0.1";
  }
  @Override
  public void run() {
    try {
      new UdpServer().startServer();
    }
    catch (Exception e) {
      log.error("udp线程异常2:" + e.getMessage());
      e.printStackTrace();
    }
  }
}