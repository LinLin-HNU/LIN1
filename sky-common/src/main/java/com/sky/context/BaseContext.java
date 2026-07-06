package com.sky.context;

public class BaseContext {

    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        Long currentId = threadLocal.get();

        if (currentId == null) {
            currentId = 4L;
            threadLocal.set(currentId);
        }

        return currentId;
//      return threadLocal.get();
    }

    public static void removeCurrentId() {
        threadLocal.remove();
    }

}
