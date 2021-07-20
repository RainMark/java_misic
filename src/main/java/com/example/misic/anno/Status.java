package com.example.misic.anno;

import lombok.Builder;
import lombok.Data;

/**
 * @author rain
 */

@Data
@Builder
public class Status {
    private boolean ok;
    private String message;

    public static Status error(String fmt, Object... objs) {
        return Status.builder().ok(false).message(String.format(fmt, objs)).build();
    }

    public static Status success() {
        return Status.builder().ok(true).message("success").build();
    }
}
