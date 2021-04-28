package cn.z.util.threadpool.zfy.orderthread;
/**
 * @author atlas
 */
public interface DynamicIntervalTask extends Runnable {
	/**
	 * @return the next scheduled interval in ms. If <= 0 the task will not
	 * be re-scheduled
	 */
	long nextInterval();
}
