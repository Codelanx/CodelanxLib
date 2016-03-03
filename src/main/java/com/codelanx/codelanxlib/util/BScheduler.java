package com.codelanx.codelanxlib.util;

import com.codelanx.codelanxlib.CodelanxLib;
import com.codelanx.commons.util.Scheduler;
import org.bukkit.Bukkit;

import java.util.concurrent.ScheduledFuture;

public class BScheduler {

    /**
     * Runs a task after a specified delay on Bukkit's main thread
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param r The {@link Runnable} to execute
     * @param delay Time (in seconds) to wait before execution
     * @return The scheduled task that will execute the provided runnable
     */
    public static ScheduledFuture<?> runSyncTask(Runnable r, long delay) {
        //TODO: hook bukkit's scheduler directly for this operation
        return Scheduler.runAsyncTask(() -> {
            Bukkit.getServer().getScheduler().callSyncMethod(CodelanxLib.get(), () -> {
                r.run();
                return null;
            });
        }, delay);
    }

    /**
     * Runs a task after a specified time on Bukkit's main thread, and repeats
     * it in intervals as specified by the {@code delay} parameter
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param r The {@link Runnable} to execute
     * @param startAfter Time (in seconds) to wait before executing at all
     * @param delay Time (in seconds) to wait in between executions
     * @return The scheduled task that will execute the provided runnable
     */
    public static ScheduledFuture<?> runSyncTaskRepeat(Runnable r, long startAfter, long delay) {
        return Scheduler.runAsyncTaskRepeat(() -> {
            Bukkit.getServer().getScheduler().callSyncMethod(CodelanxLib.get(), () -> {
                r.run();
                return null;
            });
        }, startAfter, delay);
    }
}
