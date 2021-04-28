package cn.z.util;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
/**
 * 终端设备通信监控管理
 *
 * @author hw zch wxb
 * public class TerminalManager implements ApplicationContextAware{
 */
@Service
public class TerminalManager {
  /**
   * netty
   */
  public static ChannelHandlerContext ctx = null;
  /**
   * 发送命令到消费机， 改方法负责crc校验
   */
  public static void sendToPos(InetSocketAddress inetSocketAddress, byte[] b) {
    System.out.println("send1:" + LogUtil.bytes2HexString(b));
    ByteBuf buf = Unpooled.copiedBuffer(b);
    DatagramPacket datagramPacket = new DatagramPacket(buf, inetSocketAddress);
    TerminalManager.ctx.writeAndFlush(datagramPacket);
    // System.out.println("send2:"+LogUtil.bytes2HexString(b))
  }
}
