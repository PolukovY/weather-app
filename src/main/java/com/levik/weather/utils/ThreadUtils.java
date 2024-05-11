package com.levik.weather.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.util.concurrent.TimeUnit;

@UtilityClass
public class ThreadUtils {

    @SneakyThrows
    public static void delayWithSeconds(int delay) {
        TimeUnit.SECONDS.sleep(delay);
    }
}
