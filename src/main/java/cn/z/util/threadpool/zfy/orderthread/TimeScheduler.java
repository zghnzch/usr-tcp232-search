package cn.z.util.threadpool.zfy.orderthread;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
/**
 * @author atlas
 * @date 2013-8-8
 */
public interface TimeScheduler extends ScheduledExecutorService {
	Future<?> scheduleWithDynamicInterval(DynamicIntervalTask task);
}
