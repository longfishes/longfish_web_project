package com.longfish.jclogindemo.pojo;

import lombok.Data;

@Data
public class R {

    private Boolean success;

    public static R success() {
        R r = new R();
        r.success = true;
        return r;
    }

    public static R fail() {
        R r = new R();
        r.success = false;
        return r;
    }
}
