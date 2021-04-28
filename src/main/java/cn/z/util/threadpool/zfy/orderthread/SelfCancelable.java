package cn.z.util.threadpool.zfy.orderthread;
import java.util.concurrent.Future;
/**
 * 用于某些定时任务，可以由自己决定是否继续执行
 * @author atlas
 * @date 2013-8-16
 */
public abstract class SelfCancelable {
	private volatile boolean canceled = false;
	private volatile Future<?> future;
	public final boolean cancel() {
		canceled = true;
		Future<?> future = this.future;
		if (future != null) {
			return future.cancel(true);
		}
		return false;
	}
	public final void setFuture(Future<?> future) {
		this.future = future;
		if (canceled)
			future.cancel(true);
	}
	public final boolean isCanceled() {
		return canceled;
	}
}
