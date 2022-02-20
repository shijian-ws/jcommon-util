package com.github.jcommon.util;


import com.github.jcommon.holder.Holder;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Future工具
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2021-01-30
 */
public final class FutureUtil {

    /**
     * 取消任务运行
     */
    public static void cancel(Holder<? extends Future<?>> holder) {
        if (holder == null || holder.isAbsent()) {
            return;
        }
        synchronized (holder) {
            Future<?> future = holder.get();
            if (future == null) {
                return;
            }

            try {
                if (!future.isCancelled()) {
                    future.cancel(true);
                    getUnchecked(future);
                }
            } finally {
                holder.remove();
            }
        }
    }

    /**
     * 获取任务结果
     */
    public static <V> V getUnchecked(Future<V> future) {
        if (future == null) {
            return null;
        }
        Throwable ex;
        try {
            return future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            ex = e;
        } catch (ExecutionException e) {
            ex = e.getCause() == null ? e : e.getCause();
        } catch (Throwable e) {
            ex = e;
        }
        if (ex instanceof RuntimeException) {
            throw (RuntimeException) ex;
        }
        throw new RuntimeException(ex.getMessage(), ex);
    }

    private FutureUtil() throws IllegalAccessException {
        throw new IllegalAccessException("不允许实例化");
    }
}
