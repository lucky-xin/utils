package com.xin.utils.test.delayqueue;


import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: 队列元素
 * @date 2018-08-05 18:07
 * @Copyright (C)2018 , Luchaoxin
 */
public class Element implements Delayed {

    private Long delayTime;

    private Long time;

    public Element(Long delayTime, TimeUnit unit) {
        this.delayTime = unit.convert(delayTime, MILLISECONDS) + now();
        this.time = delayTime;
    }

    private long now() {
        return System.currentTimeMillis();
    }

    public Long getDelayTime() {
        return delayTime;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(delayTime - now(), MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        if (o == this) {
            return 0;
        }
        if (o instanceof Element) {
            Element e = (Element) o;
            long diff = delayTime - e.delayTime;
            if (diff < 0)
                return -1;
            else if (diff > 0)
                return 1;
            else
                return 1;
        }
        long diff = getDelay(NANOSECONDS) - o.getDelay(NANOSECONDS);
        return (diff < 0) ? -1 : (diff > 0) ? 1 : 0;
    }

    @Override
    public String toString() {
        return "Element{" +
                "delayTime=" + time +
                '}';
    }
}
