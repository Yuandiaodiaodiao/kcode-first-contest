package com.kuaishou.kcode;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import static java.lang.System.nanoTime;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

public class SolveMinuteThread extends Thread{
    private RawBufferSolve rbs;
    private ArrayBlockingQueue abq;
    SolveMinuteThread(RawBufferSolve rbsin){
        rbs=rbsin;
        abq=rbs.abq;
    }
    @Override
    public void run() {
        super.run();
        while(true){
            try {
                HashMap<String, HashMap<Long, CheckPairPayLoad>> hmap= (HashMap<String, HashMap<Long, CheckPairPayLoad>>) abq.take();
                if(hmap==null){
                    System.out.println("hmap null");
                    break;

                }
//                System.out.println("hmap size="+hmap.size());
                if(hmap.size()==0){
                    break;
                }
                long startNs = nanoTime();

                for (Map.Entry entry : hmap.entrySet()) {
                    HashMap<Long, CheckPairPayLoad>serviceMap= (HashMap<Long, CheckPairPayLoad>) entry.getValue();
                    for(Map.Entry entry2: serviceMap.entrySet()){
                        CheckPairPayLoad payLoad= (CheckPairPayLoad) entry2.getValue();
                        int allTimes=payLoad.successTimes+payLoad.failedTimes;
                        payLoad.rate=((double)payLoad.successTimes)/allTimes;
                        payLoad.p99=P99Solve.solve(payLoad.bucket,allTimes);
                    }
                }
                System.out.println("Thread 耗时(ms):" + NANOSECONDS.toMillis(nanoTime() - startNs));

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
