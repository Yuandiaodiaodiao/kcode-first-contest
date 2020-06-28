package com.kuaishou.kcode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import static java.lang.System.nanoTime;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import com.kuaishou.kcode.ArrayPayLoad;
public class SolveMinuteThread extends Thread {
    private RawBufferSolve rbs;
    private ArrayBlockingQueue abq;
    private static final DecimalFormat DFORMAT = new DecimalFormat("#.00%");
    public static ArrayList<String> NOANSWERARRAY = new ArrayList<String>();

    SolveMinuteThread(RawBufferSolve rbsin) {
        rbs = rbsin;
        abq = rbs.abq;
    }

    public void solveArray(int time){
        int m4index = time - rbs.startMinute;
        HashMap<String, ArrayList<String>> answerHashMap=rbs.hashM4.get(m4index);
        for(Map.Entry entry : answerHashMap.entrySet()){
            String[] s= ((String) entry.getKey()).split(" ");
            rbs.hashM4Array[HashCode.hashTwoString(s[0],s[1])][m4index]= (new ArrayPayLoad((ArrayList<String>) entry.getValue()));
        }
        for(int i=0;i<=9999;++i){
            for(int j=0;j<32;++j){
                if(rbs.hashM4Array[i][j]==null){
                    rbs.hashM4Array[i][j]=new ArrayPayLoad(NOANSWERARRAY);
                }
            }
        }
    }
//    public void AddNull
    @Override
    public void run() {
        super.run();
        while (true) {
            try {
                int time = (int) abq.take();
                if (time == -1) return;
                HashMap<String, HashMap<Long, CheckPairPayLoad>> hmap = rbs.hashM.get(time);
                int m4index = time - rbs.startMinute;
//                System.out.println("hmap size="+hmap.size());
                HashMap<String, ArrayList<String>> answerHashMap = new HashMap<>(256);
                if(m4index<rbs.hashM4.size()){
                    rbs.hashM4.set(m4index, answerHashMap);
                }else if(m4index==rbs.hashM4.size()){
                    rbs.hashM4.add(answerHashMap);
                }else{
                    while(m4index>rbs.hashM4.size()){
                        rbs.hashM4.add(new HashMap<String, ArrayList<String>>());
                    }
                    rbs.hashM4.add(answerHashMap);
                }
                long startNs = nanoTime();

                for (Map.Entry entry : hmap.entrySet()) {

                    HashMap<Long, CheckPairPayLoad> serviceMap = (HashMap<Long, CheckPairPayLoad>) entry.getValue();
                    ArrayList<String> as = new ArrayList<>(32);
                    answerHashMap.put((String) entry.getKey(), as);
                    for (Map.Entry entry2 : serviceMap.entrySet()) {
                        long ipTwo = (long) entry2.getKey();
                        CheckPairPayLoad payLoad = (CheckPairPayLoad) entry2.getValue();
                        int allTimes = payLoad.successTimes + payLoad.failedTimes;
                        double rate = ((double) payLoad.successTimes) / allTimes;
                        int p99 = P99Solve.solve(payLoad.bucket, allTimes);
                        StringBuilder str = new StringBuilder(40);
                        int ip2 = (int) (long) (ipTwo);
                        long ip1 = (ipTwo >>> 32);
                        str.append((int) ((ip1 >> 24) & 0x000000FF));
                        str.append('.');
                        str.append((int) ((ip1 >> 16) & 0x000000FF));
                        str.append('.');
                        str.append((int) ((ip1 >> 8) & 0x000000FF));
                        str.append('.');
                        str.append((int) (ip1 & 0x000000FF));
                        str.append(',');
                        str.append((int) ((ip2 >> 24) & 0x000000FF));
                        str.append('.');
                        str.append((int) ((ip2 >> 16) & 0x000000FF));
                        str.append('.');
                        str.append((int) ((ip2 >> 8) & 0x000000FF));
                        str.append('.');
                        str.append((int) (ip2 & 0x000000FF));
                        str.append(',');
                        str.append(DFORMAT.format(rate));
                        str.append(',');
                        str.append(p99);
//                        payLoad.ans = str.toString();
                        as.add(str.toString());
                    }
                }
//                System.out.println("Thread 耗时(ms):" + NANOSECONDS.toMillis(nanoTime() - startNs));
            solveArray(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
