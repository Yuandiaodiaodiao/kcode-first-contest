package com.kuaishou.kcode;

import java.util.concurrent.atomic.AtomicIntegerArray;

public class P99Solve {
    public static int solve(AtomicIntegerArray bucket, int allNum){
        double i=0.99*allNum;
        int p99= (int) Math.ceil(i);
        int bucketIndex=bucket.length();
        while(--bucketIndex>=0){
            allNum-=bucket.get(bucketIndex);
            if(allNum<p99){
                return bucketIndex;
            }
        }
        return 0;
    }
    public static void testP99(){
        int[] bucket=new int[300];
        for(int a=1;a<=101;++a){
            bucket[a]+=1;
        }
//        int p99=solve(bucket,100);
//        System.out.println("p99="+p99);
    }
}
