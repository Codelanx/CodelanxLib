/*
 * Copyright (C) 2015 Codelanx, All Rights Reserved
 *
 * This work is licensed under a Creative Commons
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *
 * This program is protected software: You are free to distrubute your
 * own use of this software under the terms of the Creative Commons BY-NC-ND
 * license as published by Creative Commons in the year 2015 or as published
 * by a later date. You may not provide the source files or provide a means
 * of running the software outside of those licensed to use it.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the Creative Commons BY-NC-ND license
 * long with this program. If not, see <https://creativecommons.org/licenses/>.
 */
package com.codelanx.codelanxlib.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Façade utility class for simplifying scheduling tasks
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
 */
public class Scheduler {

    private static final List<ScheduledFuture<?>> executives = new ArrayList<>();
    private static ScheduledExecutorService es;
    
    /**
     * Runs a repeating asynchronous task
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param r The runnable to execute
     * @param startAfter Time (in seconds) to wait before execution
     * @param delay Time (in seconds) between execution to wait
     * @return The scheduled Task
     */
    public static ScheduledFuture<?> runAsyncTaskRepeat(Runnable r, long startAfter, long delay) {
        ScheduledFuture<?> sch = Scheduler.getService().scheduleWithFixedDelay(r, startAfter, delay, TimeUnit.SECONDS);
        Scheduler.executives.add(sch);
        return sch;
    }

    /**
     * Runs a single asynchronous task
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param r The runnable to execute
     * @param delay Time (in seconds) to wait before execution
     * @return The scheduled Task
     */
    public static ScheduledFuture<?> runAsyncTask(Runnable r, long delay) {
        ScheduledFuture<?> sch = Scheduler.getService().schedule(r, delay, TimeUnit.SECONDS);
        Scheduler.executives.add(sch);
        return sch;
    }
    
    /**
     * Runs a Callable
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param c The callable to execute
     * @param delay Time (in seconds) to wait before execution
     * @return The scheduled Task
     */
    public static ScheduledFuture<?> runCallable(Callable<?> c, long delay) {
        ScheduledFuture<?> sch = Scheduler.getService().schedule(c, delay, TimeUnit.SECONDS);
        Scheduler.executives.add(sch);
        return sch;
    }
    
    /**
     * Cancels all running tasks/threads and clears the cached queue.
     * 
     * @since 0.1.0
     * @version 0.1.0
     */
    public static void cancelAllTasks() {
        Scheduler.executives.forEach(s -> s.cancel(false));
        Scheduler.executives.clear();
        try {
            Scheduler.getService().awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            DebugUtil.error("Error halting scheduler service!", ex);
        }
    }

    /**
     * Returns the underlying {@link ScheduledExecutorService} used for this
     * utility class
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @return The underlying {@link ScheduledExecutorService}
     */
    public static ScheduledExecutorService getService() {
        if (Scheduler.es == null || Scheduler.es.isShutdown()) {
            Scheduler.es = Executors.newScheduledThreadPool(10); //Going to find an expanding solution to this soon
        }
        return Scheduler.es;
    }

}
