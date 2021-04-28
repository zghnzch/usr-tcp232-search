package cn.z.util.threadpool.zfy.orderthread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
/**
 * 做个类的原因是：
 * <p/>
 * 1,使调度线程池和普通线程池可以共享线程，又不会有
 * {@link ScheduledThreadPoolExecutor}
 * 的固定大小的限制，这个线程池有一个分发线程，会把到期的任务提交给父线程池执行。 大部分代码来自JDK的
 * {@link ScheduledThreadPoolExecutor}.<br/>
 * 对于具有大量定时任务和异步任务，让它们可以共享线程池是个比较好的选择
 * <p/>
 * 2,支持动态间隔的周期性任务，
 * {@link #scheduleWithDynamicInterval(DynamicIntervalTask)}
 * @author atlas
 * @date 2013-8-8
 */
public class DefaultTimeScheduler extends ThreadPoolExecutor implements TimeScheduler, Runnable {
	/**
	 * Sequence number to break scheduling ties, and in turn to guarantee FIFO
	 * order among tied entries.
	 */
	private static final AtomicLong sequencer = new AtomicLong(0);
	/**
	 * Base of nanosecond timings, to avoid wrapping
	 */
	private static final long NANO_ORIGIN = System.nanoTime();
	protected static Logger log = LoggerFactory.getLogger(DefaultTimeScheduler.class);
	private final DelayQueue<RunnableScheduledFuture<?>> dq = new DelayQueue<RunnableScheduledFuture<?>>();
	protected volatile boolean running;
	private Thread runner = null;
	public DefaultTimeScheduler(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
		init();
	}
	public DefaultTimeScheduler(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
		init();
	}
	public DefaultTimeScheduler(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
		init();
	}
	public DefaultTimeScheduler(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
		init();
	}
	/**
	 * Returns nanosecond time offset by origin
	 */
	final long now() {
		return System.nanoTime() - NANO_ORIGIN;
	}
	public String getName() {
		return "Scheduler";
	}
	private void init() {
		setRejectedExecutionHandler(new CallerRunsPolicy());
		running = true;
		runner = new Thread(this, getName() + "-Dispatcher");
		runner.setDaemon(true);
		runner.start();
	}
	@Override
	public void run() {
		String name = Thread.currentThread().getName();
		try {
			Thread.currentThread().setName(name + "=>Dispatcher");
			RunnableScheduledFuture<?> job = null;
			while (running) {
				try {
					job = this.dq.take();
					if (job != null) {
						super.execute(job);
					}
				}
				catch (InterruptedException e) {
				}
				catch (OutOfMemoryError oom) {
					System.gc();
					log.error("OOM Error", oom);
					if (!job.isDone()) {
						this.dq.offer(job);
					}
					try {
						Thread.sleep(200);
					}
					catch (InterruptedException e) {
					}
				}
				catch (Throwable e) {
					log.error("Unknown Error", e);
				}
			}
		}
		finally {
			Thread.currentThread().setName(name);
		}
	}
	@Override
	public Future<?> scheduleWithDynamicInterval(final DynamicIntervalTask command) {
		if (command == null) {
			throw new NullPointerException();
		}
		RunnableScheduledFuture<?> t = new DynamicIntervalScheduledFutureTask(command);
		delayedExecute(t);
		return t;
	}
	/**
	 * Returns the trigger time of a delayed action.
	 */
	private long triggerTime(long delay, TimeUnit unit) {
		return triggerTime(unit.toNanos((delay < 0) ? 0 : delay));
	}
	/**
	 * Returns the trigger time of a delayed action.
	 */
	private long triggerTime(long delay) {
		return now() + ((delay < (Long.MAX_VALUE >> 1)) ? delay : overflowFree(delay));
	}
	/**
	 * Constrains the values of all delays in the queue to be within
	 * Long.MAX_VALUE of each other, to avoid overflow in compareTo. This may
	 * occur if a task is eligible to be dequeued, but has not yet been, while
	 * some other task is added with a delay of Long.MAX_VALUE.
	 */
	private long overflowFree(long delay) {
		Delayed head = dq.peek();
		if (head != null) {
			long headDelay = head.getDelay(TimeUnit.NANOSECONDS);
			if (headDelay < 0 && (delay - headDelay < 0)) {
				delay = Long.MAX_VALUE + headDelay;
			}
		}
		return delay;
	}
	@Override
	public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
		if (command == null || unit == null) {
			throw new NullPointerException();
		}
		RunnableScheduledFuture<?> t = new ScheduledFutureTask<Void>(command, null, triggerTime(delay, unit));
		delayedExecute(t);
		return t;
	}
	private void delayedExecute(RunnableScheduledFuture<?> command) {
		dq.add(command);
	}
	@Override
	public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
		if (callable == null || unit == null) {
			throw new NullPointerException();
		}
		RunnableScheduledFuture<V> t = new ScheduledFutureTask<V>(callable, triggerTime(delay, unit));
		delayedExecute(t);
		return t;
	}
	@Override
	public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
		if (command == null || unit == null) {
			throw new NullPointerException();
		}
		if (period <= 0) {
			throw new IllegalArgumentException();
		}
		RunnableScheduledFuture<?> t = new ScheduledFutureTask<Object>(command, null, triggerTime(initialDelay, unit), unit.toNanos(period));
		delayedExecute(t);
		return t;
	}
	@Override
	public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
		if (command == null || unit == null) {
			throw new NullPointerException();
		}
		if (delay <= 0) {
			throw new IllegalArgumentException();
		}
		RunnableScheduledFuture<?> t = new ScheduledFutureTask<Boolean>(command, null, triggerTime(initialDelay, unit), unit.toNanos(-delay));
		delayedExecute(t);
		return t;
	}
	@Override
	public void shutdown() {
		stop();
		super.shutdown();
		dq.clear();
	}
	private void stop() {
		if (!running) {
			return;
		}
		this.running = false;
		Thread runner = this.runner;
		this.runner = null;
		if (runner != null) {
			runner.interrupt();
		}
		for (RunnableScheduledFuture<?> future : dq.toArray(new RunnableScheduledFuture[]{})) {
			future.cancel(false);
		}
	}
	@Override
	public List<Runnable> shutdownNow() {
		stop();
		List<Runnable> taskList = super.shutdownNow();
		dq.drainTo(taskList);
		dq.clear();
		return taskList;
	}
	private class DynamicIntervalScheduledFutureTask<V> extends ScheduledFutureTask<V> {
		private DynamicIntervalTask command;
		DynamicIntervalScheduledFutureTask(DynamicIntervalTask r) {
			super(r, null, triggerTime(r.nextInterval(), TimeUnit.MILLISECONDS));
			this.command = r;
		}
		@Override
		public boolean isPeriodic() {
			return true;
		}
		@Override
		protected void runPeriodic() {
			boolean ok = DynamicIntervalScheduledFutureTask.super.runAndReset();
			boolean down = isShutdown();
			// Reschedule if not cancelled and not shutdown
			if (ok && !down) {
				long interval = command.nextInterval();
				if (interval <= 0) {
					cancel(false);
					return;
				}
				this.time = triggerTime(interval, TimeUnit.MILLISECONDS);
				delayedExecute(this);
			}
		}
	}
	private class ScheduledFutureTask<V> extends FutureTask<V> implements RunnableScheduledFuture<V> {
		/**
		 * Sequence number to break ties FIFO
		 */
		private final long sequenceNumber;
		/**
		 * Period in nanoseconds for repeating tasks. A positive value indicates
		 * fixed-rate execution. A negative value indicates fixed-delay
		 * execution. A value of 0 indicates a non-repeating task.
		 */
		private final long period;
		/**
		 * The time the task is enabled to execute in nanoTime units
		 */
		protected long time;
		/**
		 * Creates a one-shot action with given nanoTime-based trigger time.
		 */
		private ScheduledFutureTask(Runnable r, V result, long ns) {
			super(r, result);
			this.time = ns;
			this.period = 0;
			this.sequenceNumber = sequencer.getAndIncrement();
		}
		/**
		 * Creates a periodic action with given nano time and period.
		 */
		private ScheduledFutureTask(Runnable r, V result, long ns, long period) {
			super(r, result);
			this.time = ns;
			this.period = period;
			this.sequenceNumber = sequencer.getAndIncrement();
		}
		/**
		 * Creates a one-shot action with given nanoTime-based trigger.
		 */
		private ScheduledFutureTask(Callable<V> callable, long ns) {
			super(callable);
			this.time = ns;
			this.period = 0;
			this.sequenceNumber = sequencer.getAndIncrement();
		}
		@Override
		public long getDelay(TimeUnit unit) {
			return unit.convert(time - now(), TimeUnit.NANOSECONDS);
		}
		@Override
		public int compareTo(Delayed other) {
			if (other == this) // compare zero ONLY if same object
			{
				return 0;
			}
			if (other instanceof ScheduledFutureTask) {
				ScheduledFutureTask<?> x = (ScheduledFutureTask<?>) other;
				long diff = time - x.time;
				if (diff < 0) {
					return -1;
				}
				else if (diff > 0) {
					return 1;
				}
				else if (sequenceNumber < x.sequenceNumber) {
					return -1;
				}
				else {
					return 1;
				}
			}
			long d = (getDelay(TimeUnit.NANOSECONDS) - other.getDelay(TimeUnit.NANOSECONDS));
			return (d == 0) ? 0 : ((d < 0) ? -1 : 1);
		}
		/**
		 * Returns true if this is a periodic (not a one-shot) action.
		 * @return true if periodic
		 */
		@Override
		public boolean isPeriodic() {
			return period != 0;
		}
		/**
		 * Runs a periodic task.
		 */
		protected void runPeriodic() {
			boolean ok = ScheduledFutureTask.super.runAndReset();
			boolean down = isShutdown();
			// Reschedule if not cancelled and not shutdown
			if (ok && !down) {
				long p = period;
				if (p > 0) {
					time += p;
				}
				else {
					time = triggerTime(-p);
				}
				delayedExecute(this);
			}
		}
		/**
		 * Overrides FutureTask version so as to reset/requeue if periodic.
		 */
		@Override
		public void run() {
			if (isPeriodic()) {
				runPeriodic();
			}
			else {
				ScheduledFutureTask.super.run();
			}
		}
	}
	class RobustRunnable implements Runnable {
		final Runnable command;
		public RobustRunnable(Runnable command) {
			this.command = command;
		}
		@Override
		public void run() {
			if (command != null) {
				try {
					command.run();
				}
				catch (Throwable t) {
					log.error("exception executing task " + command + ": " + t, t);
				}
			}
		}
	}
}
