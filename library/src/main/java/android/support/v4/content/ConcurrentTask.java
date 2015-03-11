package android.support.v4.content;

import android.support.v4.content.ModernAsyncTask;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: panmingwei
 * Date: 13-6-19
 * Time: 上午10:33
 */
public abstract class ConcurrentTask<Params, Progress, Result> extends ModernAsyncTask<Params, Progress, Result> {
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 128;
    private static final int KEEP_ALIVE = 1;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "ModernAsyncTask #" + mCount.getAndIncrement());
        }
    };

    private static final BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<Runnable>(10);

    public static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory, new ConcurrentPolicy());
    public static final Executor SERIAL_EXECUTOR = Executors.newSingleThreadExecutor();
    private Executor executor;

    protected ConcurrentTask() {
        this(THREAD_POOL_EXECUTOR);
    }

    protected ConcurrentTask(Executor executor) {
        this.executor = executor;
    }

    public void exe(Params... params) {
        executeOnExecutor(executor, params);
    }

    /**
     * the custom {@link java.util.concurrent.RejectedExecutionHandler}.
     */
    private static class ConcurrentPolicy implements RejectedExecutionHandler {
        private ConcurrentPolicy() {

        }

        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            handleRejection(r);
        }
    }

    /**
     * if you want to handle the {@link java.util.concurrent.RejectedExecutionException} and do what you want, just override this method.
     * @param runnable the rejected {@link java.lang.Runnable}.
     */
    protected static void handleRejection(Runnable runnable) {

    }
}
