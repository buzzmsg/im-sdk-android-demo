
package com.im.sdk.core.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class TransferThreadPool {

    private static ExecutorService executorMainTask;
    private static ExecutorService executorPartTask;

    static synchronized void init(final int transferThreadPoolSize) {

        final int poolSize = Math.max((int) (Math.ceil((double) transferThreadPoolSize / 2)), 1);

        if (executorMainTask == null) {
            executorMainTask = buildExecutor(poolSize);
        }
        if (executorPartTask == null) {
            executorPartTask = buildExecutor(poolSize);
        }
    }

    static int getDefaultThreadPoolSize() {
        return 2 * (Runtime.getRuntime().availableProcessors() + 1);
    }

    public static <T> Future<T> submitTask(Callable<T> c) {
        init(getDefaultThreadPoolSize());
        return executorMainTask.submit(c);
    }

    public static void closeThreadPool() {
        if (executorPartTask != null) {
            shutdown(executorPartTask);
            executorPartTask = null;
        }
        if (executorMainTask != null) {
            shutdown(executorMainTask);
            executorMainTask = null;
        }
    }

    private static final int WAIT_TIME = 250;

    private static void shutdown(ExecutorService executor) {
        if (executor == null) {
            return;
        }
        // Attempt to shutdown executor
        executor.shutdown();
        try {
            // Wait for existing tasks
            if (!executor.awaitTermination(WAIT_TIME, TimeUnit.MILLISECONDS)) {
                // Cancel tasks in execution
                executor.shutdownNow();
            }
        } catch (final InterruptedException ie) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private static ExecutorService buildExecutor(int maxThreadsAllowed) {
        /*
         * Create a bounded thread pool for executing transfers; it creates
         * threads as needed (up to maximum) and reclaims them when finished.
         */
        final ThreadPoolExecutor executor = new ThreadPoolExecutor(maxThreadsAllowed,
                maxThreadsAllowed, 10, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());
        /*
         * It's safe to discard tasks, as they are saved in database and will be
         * recovered on next database scan.
         */
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }
}
