package cn.z.util;
import java.net.InetSocketAddress;
/**
 * @author zch
 */
public class SearchUsrPackage {
	public static void sendPacket(){
		String sendBufStr = "30313233343536373839303132333435363738393031323334353637383930313233343536373839";
		String sendBufStr2 = "FF010102";
		byte[] sendBuf = StringUtil.strTobytes2(sendBufStr2);
		InetSocketAddress inetSocketAddress = new InetSocketAddress("255.255.255.255", 1500);
		TerminalManager.sendToPos(inetSocketAddress, sendBuf);
	}
}
