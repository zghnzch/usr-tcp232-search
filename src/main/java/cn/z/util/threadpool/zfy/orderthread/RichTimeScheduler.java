package cn.z.util.threadpool.zfy.orderthread;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
/**
 * 注意，定时任务如果抛出非检查异常，则定时任务会终止
 * <p>
 * 接口中所有name（threadName）参数都是用于封装Runnable
 * @author atlas
 * @date 2013-8-9
 */
public interface RichTimeScheduler extends TimeScheduler {
	/**
	 * 使用'CDL${index}-name'作为每个任务线程名字同时执行所有任务,并等待它们执行完毕
	 * @param name
	 * @param jobs
	 */
	void executeSimultaneously(String name, final List<Runnable> jobs);
	/**
	 * 使用'CDL${index}'作为每个任务线程名字同时执行所有任务,并等待它们执行完毕
	 * @param jobs
	 */
	void executeSimultaneously(final List<Runnable> jobs);
	/**
	 * 执行任务r，并设定线程变量threadLocalInfo
	 * @param name
	 * @param r
	 * @param threadLocalInfo
	 */
	void execute(final String name, final Runnable r, Object threadLocalInfo);
	void execute(final String name, final Runnable r);
	ScheduledFuture<?> scheduleWithFixedDelay(String name, Runnable command, long initialDelay, long delay);
	ScheduledFuture<?> scheduleWithFixedRate(String name, Runnable command, long initialDelay, long delay);
	<V> ScheduledFuture<V> schedule(String name, Callable<V> callable, long delay);
	ScheduledFuture<?> schedule(String name, Runnable command, long delay);
	Future<?> submit(String name, Runnable r);
	<V> Future<V> submit(String name, Callable<V> r);
	<V> Future<V> submit(String name, Callable<V> r, Object threadLocalInfo);
	<V> Map<String, V> executeSimultaneously(String name, final Map<String, Callable<V>> jobs, long timeout);
	Object getThreadLocalInfo();
	/**
	 * 具有相同key的Runnable有序串行执行
	 * @param key
	 * @param name
	 * @param r
	 * @return a future when is job is finished.
	 */
	Future<?> executeSequentially(Object key, String name, Runnable r);
	/**
	 * 具有相同key的Runnable有序串行执行,延迟delay毫秒后放入key的执行队列
	 * @param key
	 * @param name
	 * @param r
	 * @param delay 如果为0时立刻放入key的执行队列
	 */
	void executeSequentially(Object key, String name, Runnable r, int delay);
	/**
	 * 具有相同key的Runnable有序串行执行
	 * @param key
	 * @param r
	 * @return 当前任务执行的Future
	 */
	<T> Future<T> executeSequentially(Object key, Runnable r);
	/**
	 * 具有相同key的多个Runnable有序串行执行，
	 * @param key
	 * @param jobs jobs里面的任务会按顺序执行
	 * @return 当前任务（jobs包装成一个集合任务）执行的Future
	 */
	<T> Future<T> executeSequentially(Object key, List<Runnable> jobs);
	/**
	 * 具有相同key的Runnable有序串行执行,延迟delay毫秒后放入线程池
	 * @param key
	 * @param r
	 * @param delay
	 */
	void executeSequentially(Object key, Runnable r, int delay);
}
