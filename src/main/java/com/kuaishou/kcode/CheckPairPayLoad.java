package com.kuaishou.kcode;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class CheckPairPayLoad {
    public AtomicInteger successTimes=new AtomicInteger();
    public AtomicInteger failedTimes=new AtomicInteger();
    public long ip;
    public AtomicIntegerArray bucket=new AtomicIntegerArray(300);
}
