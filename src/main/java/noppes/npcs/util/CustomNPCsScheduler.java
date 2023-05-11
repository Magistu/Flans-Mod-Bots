package noppes.npcs.util;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledExecutorService;

public class CustomNPCsScheduler
{
    private static ScheduledExecutorService executor;
    
    public static void runTack(Runnable task, int delay) {
        CustomNPCsScheduler.executor.schedule(task, delay, TimeUnit.MILLISECONDS);
    }
    
    public static void runTack(Runnable task) {
        CustomNPCsScheduler.executor.schedule(task, 0L, TimeUnit.MILLISECONDS);
    }
    
    static {
        executor = Executors.newScheduledThreadPool(1);
    }
}
