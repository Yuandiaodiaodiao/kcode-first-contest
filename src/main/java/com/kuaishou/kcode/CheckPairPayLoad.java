package com.kuaishou.kcode;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class CheckPairPayLoad {
    public AtomicInteger successTimes=0;
    public int failedTimes=0;
    public long ip;
    public AtomicIntegerArray bucket=new AtomicIntegerArray(300);
}
