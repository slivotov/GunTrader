package ru.forceofshit.heartrbeat;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class RefreshStatistics {
    static Logger log = Logger.getLogger(RefreshStatistics.class.getName());
    private static final long INITIAL_DELAY = 60;
    private static final long REFRESH_PERIOD = 15 * 60;
    final ThreadFactory threadFactory =
            new ThreadFactoryBuilder().setNameFormat("Heartbeat" + "-%s").setDaemon(true).build();
    private ScheduledExecutorService heartbeatTimer;
    private volatile long totalTimeSpentOnRefresh;
    private volatile int refreshCount;

    public RefreshStatistics() {
        this.heartbeatTimer = Executors.newSingleThreadScheduledExecutor(threadFactory);
        heartbeatTimer.scheduleAtFixedRate(new HeartbeatTimerTask(), INITIAL_DELAY, REFRESH_PERIOD, TimeUnit.SECONDS);
    }

    public synchronized void updateRefreshTime(long refreshTime) {
        totalTimeSpentOnRefresh = totalTimeSpentOnRefresh + refreshTime;
        refreshCount++;
    }

    private final class HeartbeatTimerTask implements Runnable {
        @Override
        public void run() {
            log.info("Average refresh time : " + ((float) totalTimeSpentOnRefresh / refreshCount) / 1000);
        }
    }

}
