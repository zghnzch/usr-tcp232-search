package cn.z.util;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 终端设备通信监控管理
 * @author hw zch wxb
 * public class TerminalManager implements ApplicationContextAware{
 */
@Service
public class TerminalManager {
	/**
	 * log
	 */
	private static Logger myLog = Logger.getLogger(TerminalManager.class);
	/**
	 * netty
	 */
	public static ChannelHandlerContext ctx = null;
	/**
	 * 发送命令到消费机， 改方法负责crc校验
	 */
	public static void sendToPos(InetSocketAddress inetSocketAddress, byte[] b) {
		System.out.println("send1:"+LogUtil.bytes2HexString(b));
		ByteBuf buf = Unpooled.copiedBuffer(b);
		DatagramPacket datagramPacket = new DatagramPacket(buf, inetSocketAddress);
		TerminalManager.ctx.writeAndFlush(datagramPacket);
		// System.out.println("send2:"+LogUtil.bytes2HexString(b));
	}
}
