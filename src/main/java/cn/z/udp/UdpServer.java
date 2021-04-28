package cn.z.udp;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.ResourceLeakDetector;
import lombok.extern.slf4j.Slf4j;
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
      b.bind(10002).sync().channel().closeFuture().await();
    }
    catch (InterruptedException e) {
      log.error("udp线程异常1:" + e.getMessage());
      group.shutdownGracefully();
      Thread.currentThread().interrupt();
    }
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