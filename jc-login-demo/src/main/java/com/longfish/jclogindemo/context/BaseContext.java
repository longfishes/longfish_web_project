package com.longfish.jclogindemo.context;


import com.longfish.jclogindemo.enums.StatusCodeEnum;
import com.longfish.jclogindemo.exception.BizException;

public class BaseContext {

    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        if (threadLocal.get() == null){
            throw new BizException(StatusCodeEnum.NO_LOGIN);
        }
        return threadLocal.get();
    }

    public static void removeCurrentId() {
        threadLocal.remove();
    }

}
