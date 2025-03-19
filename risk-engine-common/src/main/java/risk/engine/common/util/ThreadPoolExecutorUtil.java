package risk.engine.common.util;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.DisposableBean;
import java.util.concurrent.*;
import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/19 13:10
 * @Version: 1.0
 */
@Getter
@Component
public class ThreadPoolExecutorUtil implements DisposableBean {
    // 获取线程池实例（供高级自定义使用）
    private final ThreadPoolExecutor executor;

    // 默认构造函数，使用固定参数初始化线程池
    public ThreadPoolExecutorUtil() {
        this.executor = new ThreadPoolExecutor(
                10, // 核心线程数
                20, // 最大线程数
                60L, // 非核心线程存活时间
                TimeUnit.SECONDS, // 时间单位
                new ArrayBlockingQueue<>(100), // 有界队列，容量100
                Executors.defaultThreadFactory(), // 默认线程工厂
                new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略：调用者执行
        );
        // 预启动核心线程
        executor.prestartAllCoreThreads();
    }

    // 扩展构造函数，支持自定义参数（未使用，但保留扩展性）
    public ThreadPoolExecutorUtil(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                  TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                  ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        this.executor = new ThreadPoolExecutor(
                corePoolSize, maximumPoolSize, keepAliveTime, unit,
                workQueue, threadFactory, handler
        );
        executor.prestartAllCoreThreads();
    }

    // 提交Runnable任务
    public void execute(Runnable task) {
        executor.execute(task);
    }

    // 提交Callable任务，返回Future
    public <T> Future<T> submit(Callable<T> task) {
        return executor.submit(task);
    }

    // 提交Runnable任务，返回Future
    public Future<?> submit(Runnable task) {
        return executor.submit(task);
    }

    // 批量提交任务，返回Future列表
    public <T> List<Future<T>> invokeAll(List<Callable<T>> tasks) throws InterruptedException {
        return executor.invokeAll(tasks);
    }

    // 获取线程池状态
    public String getPoolStatus() {
        return String.format(
                "ThreadPool Status: [Active: %d, Core: %d, Max: %d, Queue: %d]",
                executor.getActiveCount(), executor.getCorePoolSize(),
                executor.getMaximumPoolSize(), executor.getQueue().size()
        );
    }

    // Spring容器关闭时销毁线程池
    @Override
    public void destroy() throws Exception {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                    System.out.println("线程池未在60秒内关闭，已强制终止");
                } else {
                    System.out.println("线程池已优雅关闭");
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    // 测试方法（可删除）
    public static void main(String[] args) throws Exception {
        ThreadPoolExecutorUtil util = new ThreadPoolExecutorUtil();
        for (int i = 0; i < 15; i++) {
            int taskId = i;
            util.execute(() -> {
                try {
                    System.out.println("Task " + taskId + " running on " + Thread.currentThread().getName());
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        System.out.println(util.getPoolStatus());
        util.destroy();
    }
}
