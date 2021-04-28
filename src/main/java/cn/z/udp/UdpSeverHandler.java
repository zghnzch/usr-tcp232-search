package cn.z.udp;
import cn.z.util.LogUtil;
import cn.z.util.TerminalManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
/**
 * nio udp包处理程序 如果有问题直接删除此class 将 udpServer 类中注释掉的部分放开
 *
 * @author zchcpy
 */
@Slf4j
public class UdpSeverHandler extends SimpleChannelInboundHandler<DatagramPacket> {
  @Override
  public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
    TerminalManager.ctx = ctx;
    log.info("ctx:" + ctx);
    super.channelRegistered(ctx);
  }
  @Override
  protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
    //		System.out.println("ctx:" + ctx)
    //		System.out.println("name:" + ctx.name())
    //		System.out.println("alloc:" + ctx.alloc())
    //		System.out.println("channel:" + ctx.channel())
    //		System.out.println("handler:" + ctx.handler())
    //		System.out.println("pipeline:" + ctx.pipeline())
    //		System.out.println("executor:" + ctx.executor())
    //		System.out.println("packet:" + packet)
    //		System.out.println("sender:" + packet.sender())
    ByteBuf buf = packet.copy().content();
    try {
      byte[] b = new byte[buf.readableBytes()];
      buf.readBytes(b);
      // String receive1 = LogUtil.bytes2HexString(b)
      // 处理数据
      byte[] tmpBuf;
      tmpBuf = b;
      String receive2 = LogUtil.bytes2HexString(tmpBuf);
      //			System.out.println(packet+"-"+ receive2)
      //			System.out.println(packet.sender())
      //			System.out.println(packet.sender().getAddress())
      //			System.out.println(packet.sender().getPort())
      //			System.out.println(packet.sender().getHostName())
      //////////////////////////////////////////////////////////////////////
      //			D8B04CD9DC89B18B01A8C01227C201A8C017270101A8C00000C2010300018400FFFFFF
      //
      //			192.168.1.139
      //			192.168.001.139:10002
      //			C0.A8.01.8B:2712
      //			192.168.001.012:10007
      //			C0.A8.01.0C:2717
      //			192.168.1.194
      //			C0.A8.01.C2
      //			D8B04CD9DC89 B1 8B01A8C0 1227 C201A8C0 1727 0101 A8C00000C2010300018400FFFFFF
      //			D8B04CD9DC89 B1 8B01A8C0 1227 C201A8C0 1727 0101 A8C00000C2010300018400FFFFFF
      //			192.168.001.009:20108
      //			C0.A8.01.09:4E8C
      //			192.168.0.7
      //			C0.A8.00.07
      //			039.108.061.011:10002
      //			27.6C.3D.0B:2712
      //			9CA525862100 B1 0B3D6C27 1227 0700A8C0 8C4E 0100 A8C0 0000 C201 0300018400FFFFFF
      //      IP:PORT=192.168.001.109:10052 NAME:18352 MAC:9CA5258E3728 VER:4017   REMOTE:192.168.001.103:10001 tcpclient
      //      IP:PORT=C0A8016D:2744 NAME:47B0 MAC:9CA5258E3728 VER:0FB1 REMOTE:C0A80167:2711
      //      IP:PORT=6D01A8C0:4427
      //      MAC+?+REMOTE+STATIC-IP+STATIC-MASK
      //      9CA5258E3728 B1 6701A8C0 1127 0700A8C0 4427 0100A8C0 0100C2010300018000FFFFFF
      //      9CA5258E3728 B1 6701A8C0 1127 0700A8C0 4427 0100A8C0 0100C2010300018000FFFFFF
      //////////////////////////////////////////////////////////////////////
      log.info("receive:ip:" + packet.sender().getHostString() + ":port:" + packet.sender().getPort() + ":package:" + receive2);
      //      String macStr = receive2.substring(0,12);
      //      System.out.println("macStr = " + macStr);
      //      String remoteIp = receive2.substring(14,22);
      //      System.out.println("remoteIp = " + remoteIp);
      //      String remotePort = receive2.substring(22,26);
      //      System.out.println("remotePort = " + remotePort);
      //      log.info("MAC:{},remoteIp{},remotePort{}", macStr,remoteIp,remotePort);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      buf.release();
    }
  }
}
