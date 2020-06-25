package com.kuaishou.kcode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class SolveRespondThread extends Thread {
    @Override
    public void run() {
        super.run();
    }

    private static final String NOANSWER = "-1.00%";
    private static final DecimalFormat DFORMAT = new DecimalFormat("#.00%");

    public static void solve() {
        for (int i = 0; i < 1024; ++i) {

            ArrayList<CheckResponderTimePayLoad> al = new ArrayList<>(35);
            CheckResponderTimePayLoad lastct = new CheckResponderTimePayLoad();
            CheckResponderTimePayLoad ct;

            for (int t = 0; t <= 31; ++t) {
                CheckResponderPayLoad payload = PrepareMultiThreadDataCore.hashCheckResponder[t][i];
                if (payload == null) {
                    continue;
                }
                ct = new CheckResponderTimePayLoad();
                ct.time = t;
                ct.rate = ((double) payload.success) / (payload.success + payload.failed);
                if (!al.isEmpty()) {
                    ct.rate += lastct.rate;
                    al.add(ct);
                } else {
                    al.add(ct);
                }
                lastct = ct;

            }
            for (int j = 0; j < al.size(); ++j) {
                ct = al.get(j);
                ct.calledTimes = j + 1;
            }

            CheckResponderTimePayLoad[] dbarray = new CheckResponderTimePayLoad[35];
            for (CheckResponderTimePayLoad k : al) {
                dbarray[k.time + 1] = k;
            }
            dbarray[0] = new CheckResponderTimePayLoad();
            for (int a = 1; a <= 32; ++a) {
                if (dbarray[a] == null || dbarray[a].calledTimes == 0) {
                    dbarray[a] = dbarray[a - 1];
                }
            }
//            String[][] c = new String[32][32];
            CheckResponderTimePayLoad[] db = dbarray;
            for (int a = 0; a <= 31; ++a) {
                for (int b = 0; b <= 31; ++b) {
                    CheckResponderTimePayLoad ii = db[a];
                    CheckResponderTimePayLoad jj = db[b];
                    int calleeTimes = jj.calledTimes - ii.calledTimes;
                    if (calleeTimes > 0) {
                        PrepareMultiThreadDataCore.CheckResponderFastArrayFlat[(a<<15)+(b<<10)+i]=DFORMAT.format((jj.rate - ii.rate) / calleeTimes);
//                        c[a][b] = DFORMAT.format((jj.rate - ii.rate) / calleeTimes);
                    } else {
                        PrepareMultiThreadDataCore.CheckResponderFastArrayFlat[(a<<15)+(b<<10)+i]=NOANSWER;

//                        c[a][b] = NOANSWER;
                    }
                }
            }
//            PrepareMultiThreadDataCore.CheckResponderPayLoadArray[i] = c;

        }

//        for (int i = 0; i <= 4999; ++i) {
//
//            for (int a = 0; a <= 31; ++a) {
//                for (int b = 0; b <= 31; ++b) {
//                    PrepareMultiThreadDataCore.CheckResponderFastArray[a][b][i] =
//                            PrepareMultiThreadDataCore.CheckResponderPayLoadArray[i][a][b];
//
//                }
//            }
//
//        }

    }
}
