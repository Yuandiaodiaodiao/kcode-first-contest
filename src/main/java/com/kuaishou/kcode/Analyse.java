package com.kuaishou.kcode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Analyse {
    public static void findIpPair(){

        File fservice = new File("ippair.text");
        FileWriter out = null;
        try {
            out = new FileWriter(fservice);
        } catch (IOException e) {
            e.printStackTrace();
        }


        for(int time=0;time<32;++time){
            HashMap<Long, CheckPairPayLoad>[] hArray = PrepareMultiThreadDataCore.hashCheckPair[time];
            for (int i = 0; i <= 4999; ++i) {
                HashMap<Long, CheckPairPayLoad> serviceMap = hArray[i];
                if (serviceMap == null) {
                    continue;
                }

                for (Map.Entry entry2 : serviceMap.entrySet()) {
                    long ipTwo = (long) entry2.getKey();
                    CheckPairPayLoad payLoad = (CheckPairPayLoad) entry2.getValue();
                    int allTimes = payLoad.successTimes + payLoad.failedTimes;
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
                    str.append(' ');
                    str.append((int) ((ip2 >> 24) & 0x000000FF));
                    str.append('.');
                    str.append((int) ((ip2 >> 16) & 0x000000FF));
                    str.append('.');
                    str.append((int) ((ip2 >> 8) & 0x000000FF));
                    str.append('.');
                    str.append((int) (ip2 & 0x000000FF));
                    str.append("\n");
                    try {
                        out.write(str.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    out.write("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
