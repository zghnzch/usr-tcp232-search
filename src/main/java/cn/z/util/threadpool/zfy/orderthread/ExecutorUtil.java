package cn.z.util.threadpool.zfy.orderthread;
public class ExecutorUtil {
	public static RichTimeScheduler getInstance() {
		return LazyHolder.INSTANCE;
	}
	private static class LazyHolder {
		private static final RichTimeScheduler INSTANCE = MixTimeScheduler.newCachedThreadPool("XB_POOL_SERIAL");
	}
}
