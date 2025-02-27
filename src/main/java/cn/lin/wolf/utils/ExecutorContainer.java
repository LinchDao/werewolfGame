package cn.lin.wolf.utils;


import cn.lin.wolf.data.GamingCheckService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Description:线程池容器
 * @Author: linch
 * @Date: 2025-02-22
 */

public class ExecutorContainer {

    //声画同步
    public static final ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
    //定时检测玩家状态
    public static final ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);

    private ExecutorContainer() {
    }

    static {
        executeCheckPlayerOnline();
    }

    public static void pauseGameProcess(long time) {
        singleThreadExecutor.submit(() -> {
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static void executeCheckPlayerOnline() {
        scheduledThreadPool.scheduleWithFixedDelay(new RunnableTask(), 2, 10, TimeUnit.SECONDS);
    }

    static class RunnableTask implements Runnable {
        @Override
        public void run() {
            GamingCheckService.checkGamingAndDelete();
        }
    }
}
