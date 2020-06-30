package com.kuaishou.kcode;

import java.util.concurrent.atomic.AtomicIntegerArray;

public class CheckPairPayLoad {
    public int successTimes=0;
    public int failedTimes=0;
    public long ip;
    public int[] bucket=new int[300];
    public AtomicIntegerArray[] bucket2=AtomicIntegerArray
}
