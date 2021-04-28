package cn.z.util.threadpool.zfy.orderthread;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
/**
 * 1,支持往执行线程添加线程变量
 * <p>
 * 2,支持相同key的任务串行执行
 * @author atlas
 * @date 2013-8-9
 */
public class MixTimeScheduler extends DefaultTimeScheduler implements RichTimeScheduler {
	/**
	 * 通过线程变量的方式给异步任务传参数
	 */
	public static final ThreadLocal<Object> threadInfo = new ThreadLocal<Object>();
	private String name;
	private ConcurrentMap<Object, SequentialJob> sequentialJobs = new ConcurrentHashMap<Object, SequentialJob>();
	/**
	 * 有序任务的存活时间，由于占用一个线程，不能太长，也不能太短，否则会频繁创建和销毁SequentialJob
	 */
	private long keepAliveTime = 8 * 1000L;
	public MixTimeScheduler() {
		this(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new NamedThreadFactory());
	}
	public MixTimeScheduler(String name) {
		this(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new NamedThreadFactory(name));
		this.name = name;
	}
	public MixTimeScheduler(int corePoolSize, int maximumPoolSize, String name) {
		this(corePoolSize, maximumPoolSize, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new NamedThreadFactory(name));
		this.name = name;
	}
	public MixTimeScheduler(int corePoolSize, int maximumPoolSize) {
		this(corePoolSize, maximumPoolSize, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new NamedThreadFactory());
	}
	public MixTimeScheduler(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
	}
	public MixTimeScheduler(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
	}
	public MixTimeScheduler(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
	}
	public static RichTimeScheduler newCachedThreadPool() {
		return new MixTimeScheduler();
	}
	public static RichTimeScheduler newCachedThreadPool(String name) {
		return new MixTimeScheduler(name);
	}
	/**
	 * 推荐使用这个方法创建：线程池具有[corePoolSize,maximumPoolSize]个线程，采用同步队列SynchronousQueue，
	 * 由调用者提供RejectedExecutionHandler
	 * @param corePoolSize
	 * @param maximumPoolSize
	 * @param name
	 * @param handler
	 * @return
	 */
	public static MixTimeScheduler newCachedThreadPool(int corePoolSize, int maximumPoolSize, String name, RejectedExecutionHandler handler) {
		MixTimeScheduler mixTimeScheduler = new MixTimeScheduler(corePoolSize, maximumPoolSize, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new NamedThreadFactory(name));
		mixTimeScheduler.setRejectedExecutionHandler(handler);
		return mixTimeScheduler;
	}
	public static RichTimeScheduler newFixedThreadPool(int nThreads) {
		return new MixTimeScheduler(nThreads, nThreads, 0L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new NamedThreadFactory());
	}
	public static RichTimeScheduler newFixedThreadPool(int nThreads, String name) {
		return new MixTimeScheduler(nThreads, nThreads, 0L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new NamedThreadFactory(name));
	}
	public static <V> FutureTask<V> createFutureTask(Runnable runnable, V result) {
		return new FutureTask<V>(runnable, result);
	}
	public static Runnable wrap(Runnable r) {
		if (r instanceof RobustRunnable) {
			return r;
		}
		else {
			return new RobustRunnable(r);
		}
	}
	public static Runnable wrap(final String name, final Runnable r) {
		return wrap(name, r, null);
	}
	public static <V> Callable<V> wrap(final Callable<V> r) {
		if (r instanceof RobustCallable) {
			return r;
		}
		return wrap(null, r);
	}
	public static <V> Callable<V> wrap(final String name, final Callable<V> r) {
		return wrap(name, r, null);
	}
	public static <V> Callable<V> wrap(final String name, final Callable<V> r, final Object threadLocalInfo) {
		return new RobustCallable<V>(name, r, threadLocalInfo);
	}
	public static Runnable wrap(final String name, final Runnable r, final Object threadLocalInfo) {
		return new RobustRunnable(name, r, threadLocalInfo);
	}
	public static Object getThreadLocalObject() {
		return threadInfo.get();
	}
	/**
	 * 将多个任务打包成一个串行执行的任务
	 * @param jobs
	 * @return
	 */
	public static Runnable packSequentialJob(final List<Runnable> jobs) {
		if (jobs.size() == 1) {
			return wrap(jobs.get(0));
		}
		return new Runnable() {
			@Override
			public void run() {
				for (Runnable r : jobs) {
					wrap(r).run();
				}
			}
		};
	}
	@Override
	public String getName() {
		if (this.name != null) {
			return name;
		}
		return "MixScheduler";
	}
	/**
	 * schedule a one-shot job
	 * @param name
	 * @param command
	 * @param delay milliseconds
	 * @return
	 */
	@Override
	public ScheduledFuture<?> schedule(String name, Runnable command, long delay) {
		return super.schedule(wrap(name, command), delay, TimeUnit.MILLISECONDS);
	}
	@Override
	public <V> ScheduledFuture<V> schedule(String name, Callable<V> callable, long delay) {
		return super.schedule(wrap(name, callable, null), delay, TimeUnit.MILLISECONDS);
	}
	@Override
	public ScheduledFuture<?> scheduleWithFixedDelay(String info, Runnable command, long initialDelay, long delay) {
		return super.scheduleWithFixedDelay(wrap(info, command), initialDelay, delay, TimeUnit.MILLISECONDS);
	}
	@Override
	public ScheduledFuture<?> scheduleWithFixedRate(String info, Runnable command, long initialDelay, long delay) {
		return super.scheduleAtFixedRate(wrap(info, command), initialDelay, delay, TimeUnit.MILLISECONDS);
	}
	@Override
	public <V> Future<V> submit(String name, Callable<V> r) {
		return super.submit(wrap(name, r, null));
	}
	@Override
	public <V> Future<V> submit(String name, Callable<V> r, Object threadLocalInfo) {
		return super.submit(wrap(name, r, threadLocalInfo));
	}
	@Override
	public Future<?> submit(Runnable task) {
		return super.submit(wrap(task));
	}
	@Override
	public void execute(Runnable command) {
		super.execute(wrap(command));
	}
	@Override
	public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
		return super.schedule(wrap(command), delay, unit);
	}
	@Override
	public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
		return super.scheduleAtFixedRate(wrap(command), initialDelay, period, unit);
	}
	@Override
	public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
		return super.scheduleWithFixedDelay(wrap(command), initialDelay, delay, unit);
	}
	@Override
	public Future<?> submit(String name, Runnable r) {
		return super.submit(wrap(name, r));
	}
	@Override
	public void execute(final String threadName, final Runnable r) {
		super.execute(wrap(threadName, r));
	}
	@Override
	public void execute(final String threadName, final Runnable r, Object threadLocalInfo) {
		super.execute(wrap(threadName, r, threadLocalInfo));
	}
	@Override
	public void executeSimultaneously(final List<Runnable> jobs) {
		executeSimultaneously(null, jobs);
	}
	@Override
	public void executeSimultaneously(String name, final List<Runnable> jobs) {
		final CountDownLatch cdl = new CountDownLatch(jobs.size());
		for (int i = 0; i < jobs.size(); i++) {
			final int index = i;
			Runnable wrapper = new Runnable() {
				@Override
				public void run() {
					try {
						jobs.get(index).run();
					}
					finally {
						cdl.countDown();
					}
				}
			};
			String newName = null;
			newName = "CDL" + index + (name != null ? "-" + name : "");
			execute(newName, wrapper);
		}
		try {
			cdl.await();
		}
		catch (InterruptedException e) {
		}
	}
	@Override
	public <V> Map<String, V> executeSimultaneously(String name, final Map<String, Callable<V>> jobs, long timeout) {
		Tick tick = new Tick();
		final CountDownLatch cdl = new CountDownLatch(jobs.size());
		final Map<String, V> results = new HashMap<String, V>(jobs.size());
		for (Map.Entry<String, Callable<V>> entry : jobs.entrySet()) {
			final String key = entry.getKey();
			final Callable<V> job = entry.getValue();
			name = (name != null) ? name + "#" + key : key;
			submit(name, new Runnable() {
				@Override
				public void run() {
					try {
						V r = job.call();
						results.put(key, r);
					}
					catch (Exception e) {
						log.error("exception executing task " + job + ": " + e, e);
					}
					finally {
						cdl.countDown();
					}
				}
			});
		}
		try {
			timeout = timeout - tick.elapsedTime();
			if (timeout > 0){
				cdl.await(timeout, TimeUnit.MILLISECONDS);
          }
		}
		catch (InterruptedException e) {
		}
		return results;
	}
	@Override
	public Object getThreadLocalInfo() {
		return threadInfo.get();
	}
	@Override
	public <T> Future<T> executeSequentially(Object key, Runnable r) {
		return executeSequentially(key, Arrays.asList(r));
	}
	@Override
	public <T> Future<T> executeSequentially(Object key, List<Runnable> jobs) {
		Runnable r = packSequentialJob(jobs);
		synchronized (sequentialJobs) {
			SequentialJob job = sequentialJobs.get(key);
			boolean schedule = false;
			if (job == null) {
				job = new SequentialJob(key);
				sequentialJobs.put(key, job);
				schedule = true;
			}
			FutureTask<T> ft = createFutureTask(r, null);
			job.addJob(ft);
			if (schedule) {
				Future<?> future = schedule(job, 0, TimeUnit.MILLISECONDS);
				job.setFuture(future);
			}
			return ft;
		}
	}
	private SequentialJob removeSequentialJob(Object key) {
		synchronized (sequentialJobs) {
			return sequentialJobs.remove(key);
		}
	}
	@Override
	public void executeSequentially(Object key, Runnable r, int delay) {
		executeSequentially(key, null, r, delay);
	}
	@Override
	public Future<?> executeSequentially(Object key, String name, Runnable r) {
		return executeSequentially(key, wrap(name, r));
	}
	@Override
	public void executeSequentially(final Object key, final String name, final Runnable r, int delay) {
		if (delay <= 0) {
			executeSequentially(key, name, r);
		}
		else {
			Runnable lateR = new Runnable() {
				@Override
				public void run() {
					executeSequentially(key, name, r);
				}
			};
			schedule(name, lateR, delay);
		}
	}
	public static class RobustRunnable implements Runnable {
		private final Runnable command;
		private final String name;
		private final Object threadLocalInfo;
		public RobustRunnable(Runnable command) {
			this(null, command, null);
		}
		public RobustRunnable(String name, Runnable command) {
			this(name, command, null);
		}
		public RobustRunnable(String name, Runnable command, Object threadLocalInfo) {
			this.command = command;
			this.name = name;
			this.threadLocalInfo = threadLocalInfo;
		}
		@Override
		public void run() {
			if (command != null) {
				String oldName = Thread.currentThread().getName();
				try {
					if (name != null) {
						Thread.currentThread().setName(oldName + "=>" + name);
					}
					if (threadLocalInfo != null) {
						threadInfo.set(threadLocalInfo);
					}
					command.run();
				}
				catch (RuntimeException e) {
					log.error("exception running task " + command + ": " + e, e);
				}
				finally {
					if (name != null) {
						Thread.currentThread().setName(oldName);
					}
					// must release it.
					if (threadLocalInfo != null) {
						threadInfo.set(null);
					}
				}
			}
		}
	}
	public static class RobustCallable<V> implements Callable<V> {
		private final Callable<V> command;
		private final String name;
		private final Object threadLocalInfo;
		public RobustCallable(String name, Callable<V> command, Object threadLocalInfo) {
			this.command = command;
			this.name = name;
			this.threadLocalInfo = threadLocalInfo;
		}
		@Override
		public V call() throws Exception {
			String oldName = Thread.currentThread().getName();
			try {
				if (name != null) {
					Thread.currentThread().setName(oldName + "=>" + name);
				}
				if (threadLocalInfo != null) {
					threadInfo.set(threadLocalInfo);
				}
				V v = command.call();
				return v;
			}
			catch (Exception e) {
				log.error("exception calling task " + command + ": " + e, e);
				throw e;
			}
			finally {
				if (name != null) {
					Thread.currentThread().setName(oldName);
				}
				if (threadLocalInfo != null) {
					threadInfo.set(null);
				}
			}
		}
	}
	private class SequentialJob implements Runnable {
		private Future<?> future;
		private BlockingQueue<Runnable> jobs = new LinkedBlockingQueue<Runnable>();
		private Object key;
		public SequentialJob(Object key) {
			this.key = key;
		}
		@Override
		public void run() {
			String name = Thread.currentThread().getName();
			try {
				Thread.currentThread().setName(name + "=>[Seq-" + key + "]");
				doRun();
			}
			finally {
				Thread.currentThread().setName(name);
			}
		}
		public void doRun() {
			Runnable r = null;
			while (true) {
				try {
					r = jobs.poll(keepAliveTime, TimeUnit.MILLISECONDS);
					if (r != null) {
						try {
							r.run();
						}
						catch (Exception e) {
							// already logged.ignore here
						}
					}
					else {
						synchronized (sequentialJobs) {
							if (jobs.isEmpty()) {
								removeSequentialJob(key);
								return;
							}
							else {
								continue;
							}
						}
					}
				}
				catch (InterruptedException e) {
					// ignore
					if (isShutdown()) {
						log.warn("SequentialJob with key {} interrupted.", key);
						return;
					}
				}
			}
		}
		public void addJob(Runnable job) {
			jobs.add(job);
		}
		@Override
		public String toString() {
			return "Seq-" + key;
		}
		public Future<?> getFuture() {
			return future;
		}
		public void setFuture(Future<?> future) {
			this.future = future;
		}
	}
}
