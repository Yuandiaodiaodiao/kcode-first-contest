package com.kuaishou.kcode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.nanoTime;

public class SolveMinuteArrayListAnswerThread extends Thread {
    public static ArrayList<String> NOANSWERARRAY = new ArrayList<String>();

    @Override
    public void run() {
        super.run();

    }
    public static void solve(int m4index,CheckPairPayLoad[][] hArray) {
        DecimalFormat DFORMAT = new DecimalFormat("#.00%");
        if (m4index == -1) return;
//        CheckPairPayLoad[][] hArray = PrepareMultiThreadDataCore.hashCheckPair[m4index];
        for (int i = 0; i <= 4999; ++i) {

//            HashMap<Long, CheckPairPayLoad> serviceMap = hArray[i];
//            if (serviceMap == null) {
//                PrepareMultiThreadDataCore.hashCheckPairArrayFlat[(i<<5)+m4index] = NOANSWERARRAY;
//                continue;
//            }

            ArrayList<String> as = new ArrayList<>(32);
            PrepareMultiThreadDataCore.hashCheckPairArrayFlat[(i<<5)+m4index] = as;
            CheckPairPayLoad[] hArray2=hArray[i];
            for (int j=0;j<2553;++j) {


                CheckPairPayLoad payLoad =hArray2[j];
                if(payLoad==null)continue;
                long ipTwo = payLoad.ip;
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
                as.add(str.toString());
            }
        }
    }
}
