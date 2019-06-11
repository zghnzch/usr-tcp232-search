package cn.z;
import cn.z.udp.UDPServer;
import cn.z.util.SearchUsrPackage;
import cn.z.util.TerminalManager;
/**
 * @author zch
 */
public class Main {
	public static void main(String[] args) {
		Thread t = new Thread(new UDPServer());
		// 2017.0410.zch
		t.setName("SystemUDPListener");
		t.start();
		while (true) {
			try {
				Thread.sleep(10000);
				if (TerminalManager.ctx != null) {
					SearchUsrPackage.sendPacket();
				}
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
