package cn.z.udp;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.ResourceLeakDetector;
/**
 UDP监听和分发服务
 @author 郝威 */
public class UDPServer implements Runnable{
	public static void main(String[] args){
		new UDPServer().startServer();
	}
	private void startServer(){
		EventLoopGroup group = new NioEventLoopGroup();
		Bootstrap b = new Bootstrap();
		b.group(group).channel(NioDatagramChannel.class).option(ChannelOption.SO_BROADCAST,true).handler(new UDPSeverHandler());
		ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
		try{
			b.bind(10002).sync().channel().closeFuture().await();
		} catch(InterruptedException e){
			System.out.println("udp线程异常1  " + e.getMessage());
			e.printStackTrace();
			group.shutdownGracefully();
		}
	}
	@Override
	public void run(){
		try{
			new UDPServer().startServer();
		} catch(Exception e){
			System.out.println("udp线程异常2  " + e.getMessage());
			e.printStackTrace();
		}
	}
}