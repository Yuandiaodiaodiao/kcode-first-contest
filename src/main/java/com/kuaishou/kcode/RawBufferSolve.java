package com.kuaishou.kcode;

import java.io.File;
import java.io.FileWriter;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

public class RawBufferSolve {
    private byte[] byteBuff;
    public int startMinute = 0;
    public int nowTime = 0;
    public int byteLinePos = 0;
    public String lastStr;
    public byte[] byteLine = new byte[100];
    public byte[] byteLineLast = new byte[100];
    public byte[] stringConcatBuff = new byte[200];
    public byte[] service1 = new byte[40];
    public byte[] service2 = new byte[40];
    public long readedBytes = 0l;
    public int readedLines = 0;
    public ArrayBlockingQueue abq=new ArrayBlockingQueue(128,false);
    public String CONSTSTRING = "serviceaservicebservicec";
    public HashMap<Integer, HashMap<String, HashMap<Long, CheckPairPayLoad>>> hashM = new HashMap<>(256);
    public HashMap<String, TreeMap<Integer, CheckResponderPayLoad>> hashM2 = new HashMap<>(256);
    public HashMap<String, CheckResponderTimePayLoad[]> hashM3 = new HashMap<>(128);
    public ArrayList<HashMap<String, ArrayList<String>>> hashM4 = new ArrayList<>(64);
    public SolveMinuteThread thread1;
    public String[][][] hashM3Array =new String[10000][][];
    public CheckResponderTimePayLoad[][][] hashM3Array2 =new CheckResponderTimePayLoad[10000][][];
    public ArrayPayLoad[][] hashM4Array=new ArrayPayLoad[10000][32] ;
//    public HashSet<String>serviceNameSet=new HashSet<>(8192);
    public RawBufferSolve() {
        thread1=new SolveMinuteThread(this);
        thread1.start();

    }
    private static final DecimalFormat DFORMAT = new DecimalFormat("#.00%");
    private static final String NOANSWER = "-1.00%";

    public void hashMap2Array(){
        for (Map.Entry entry : hashM3.entrySet()) {
            String[][] c=new String[32][32];
            CheckResponderTimePayLoad[] db=(CheckResponderTimePayLoad[]) entry.getValue();
            for(int a=0;a<=31;++a){
                for(int b=0;b<=31;++b){
                    CheckResponderTimePayLoad i = db[a];
                    CheckResponderTimePayLoad j = db[b];
                    int calleeTimes = j.calledTimes - i.calledTimes;
                    if (calleeTimes > 0) {
                        c[a][b]= DFORMAT.format((j.rate - i.rate) / calleeTimes);
                    } else {
                        c[a][b]= NOANSWER;
                    }
                }
            }
            hashM3Array[HashCode.hash((String) entry.getKey())]= c;
        }
//        for (Map.Entry entry : hashM3.entrySet()) {
//            hashM3Array2[HashCode.hash2((String) entry.getKey())]= (CheckResponderTimePayLoad[]) entry.getValue();
//        }
    }
    public void solveResponder() {
        for (String key : hashM2.keySet()) {
            TreeMap<Integer, CheckResponderPayLoad> tm = hashM2.get(key);
            ArrayList<CheckResponderTimePayLoad> al = new ArrayList<>(32);

            CheckResponderTimePayLoad lastct = new CheckResponderTimePayLoad();
            CheckResponderTimePayLoad ct;
            for (Map.Entry entry : tm.entrySet()) {
                CheckResponderPayLoad payload = (CheckResponderPayLoad) entry.getValue();
                ct = new CheckResponderTimePayLoad();
                ct.time = (int) entry.getKey();
                ct.rate = ((double) payload.success) / (payload.success + payload.failed);
                if (!al.isEmpty()) {
                    ct.rate += lastct.rate;
                    al.add(ct);
                } else {
                    al.add(ct);
                }
                lastct = ct;
            }
            for (int i = 0; i < al.size(); ++i) {
                ct = al.get(i);
                ct.calledTimes = i + 1;
            }

            CheckResponderTimePayLoad[] dbarray = new CheckResponderTimePayLoad[35];
            for (CheckResponderTimePayLoad i : al) {
                dbarray[i.time - startMinute] = i;
            }
            dbarray[0] = new CheckResponderTimePayLoad();
            for (int a = 1; a <= 32; ++a) {
                if (dbarray[a] == null || dbarray[a].calledTimes == 0) {
                    dbarray[a] = dbarray[a - 1];
                }
            }
            hashM3.put(key, dbarray);
        }
        hashMap2Array();

    }

    private void printArray(byte[] input, int limit) {
        for (int a = 0; a < limit && input[a] != '\n'; ++a) {
            printByte(input[a]);
        }
        System.out.println();
    }

    private void printByte(byte input) {
        System.out.print((char) input);
    }

    public void run(ByteBuffer inputB, int limit) {


        byte b;
        while (inputB.hasRemaining()) {
//            readedBytes += 1;
            b = inputB.get();
//            System.out.print((char)b);
            byteLine[byteLinePos++] = b;
            if (b == '\n') {
                //solve
//                readedLines += 1;
                solveSentense(byteLine, limit);
//                System.arraycopy(byteLine, 0, byteLineLast, 0, byteLinePos + 1);
//                byteLineLast[byteLinePos] = '\n';
                //solveEnd
                byteLinePos = 0;
            }
        }
//        if (byteLinePos == 57) {
//            printArray(byteLineLast, 100);
//            printArray(byteLine, byteLinePos);
//        }
//        System.out.println("读完一轮 pos=" + byteLinePos);

    }

    public long stringIp2Long(String s) {
        s += ",";
        byte[] input = s.getBytes();
        int readIndex = -1;
        int numBuff = 0;
        long ip = 0L;
        for (byte b = input[++readIndex]; b != ','; b = input[++readIndex]) {
            if (b != '.') {
                numBuff *= 10;
                numBuff += (b - '0');
            } else {
                ip = (ip << 8) + numBuff;
                numBuff = 0;
            }
        }
        ip = (ip << 8) + numBuff;
        return ip;
    }


    public void solveSentense(byte[] input, int limit) {
        int readIndex = 0;
        byte b;

        long ip1 = 0;
        long ip2 = 0;
        b = input[readIndex];
        int strIndexA = 0;
        for (; b != ','; b = input[++readIndex]) {
            service1[strIndexA++] = b;
        }

        int numBuff = 0;
        for (b = input[++readIndex]; b != ','; b = input[++readIndex]) {
            if (b != '.') {
                numBuff *= 10;
                numBuff += (b - '0');
            } else {
                ip1 <<= 8;
                ip1 += numBuff;
                numBuff = 0;
            }
        }
        ip1 <<= 8;
        ip1 += numBuff;
        numBuff = 0;

        b = input[++readIndex];
        int strIndexB = 0;
        for (; b != ','; b = input[++readIndex]) {
            service2[strIndexB++] = b;
        }

        for (b = input[++readIndex]; b != ','; b = input[++readIndex]) {
            if (b != '.') {
                numBuff *= 10;
                numBuff += (b - '0');
            } else {
                ip2 <<= 8;
                ip2 += numBuff;
                numBuff = 0;
            }
        }
        ip2 <<= 8;
        ip2 += numBuff;

        b = input[++readIndex];
        int success = 0;
        int failed = 0;
        if (b == 't') {
            success = 1;
            readIndex += 4;
        } else {
            failed = 1;
            readIndex += 5;
        }
        int useTime = 0;
        for (b = input[++readIndex]; b != ','; b = input[++readIndex]) {
            useTime *= 10;
            useTime += (b - '0');
        }
        int minTime = 0;
        b = input[++readIndex];

        for (int timepos = 1; timepos <= 10; ++timepos, b = input[++readIndex]) {
            minTime *= 10;
            minTime += (b - '0');
        }
        int timec = minTime;
        minTime /= 60;
        if (startMinute == 0) {
            startMinute = minTime - 1;
        }
        if (minTime > nowTime) {
            if(nowTime!=0){
                abq.add(nowTime);
            }
            nowTime = minTime;
            //刷新time
//            System.out.println(timec);
        } else if (minTime == nowTime) {

        } else {
            String outstr = " ip1= " + ip1 + " ip2= " + ip2 + " " + (new String(service1)) + " " + (new String(service2)) + " " + useTime + " " + minTime;

            System.out.println("逆序!!!!" + " 上一个是 " + lastStr);
            System.out.println(outstr);

            throw new Error("逆序");
        }
//        lastStr=outstr;
        int stringConcatPos = 0;
//        serviceNameSet.add(new String(service1,0,strIndexA)+" "+new String(service2,0,strIndexB));
        System.arraycopy(service1, 0, stringConcatBuff, 0, strIndexA);
        stringConcatBuff[strIndexA]=32;
        System.arraycopy(service2, 0, stringConcatBuff, strIndexA+1, strIndexB);
        String twoServices = new String(stringConcatBuff, 0, strIndexA+1 + strIndexB);
        String secondServices = new String(stringConcatBuff, strIndexA+1, strIndexB);
//        ip1=stringIp2Long("255.254.253.252");
//        ip2=stringIp2Long("255.254.253.252");
        long twoIPs = ( ip1 << 32) +  ip2;
//        String s = Long2Ip(twoIPs);

        HashMap<String, HashMap<Long, CheckPairPayLoad>> timeLevel = hashM.get(minTime);
        if (timeLevel == null) {
            //service *service
            timeLevel = new HashMap<>(256);
            hashM.put(minTime, timeLevel);
        }
        HashMap<Long, CheckPairPayLoad> serviceLevel = timeLevel.get(twoServices);
        if (serviceLevel == null) {
            serviceLevel = new HashMap<>(256);
            timeLevel.put(twoServices, serviceLevel);
        }
        CheckPairPayLoad payload = serviceLevel.get(twoIPs);
        if (payload == null) {
            payload = new CheckPairPayLoad();
            serviceLevel.put(twoIPs, payload);
        }
        //change payload
        payload.successTimes += success;
        //1^1 =0 0^1 =1
        payload.failedTimes += success ^ 1;
        payload.bucket[useTime] += 1;

        TreeMap<Integer, CheckResponderPayLoad> timeSet = hashM2.get(secondServices);
        if (timeSet == null) {
            timeSet = new TreeMap<>();
            hashM2.put(secondServices, timeSet);
        }
        CheckResponderPayLoad payload2 = timeSet.get(minTime);
        if (payload2 == null) {
            payload2 = new CheckResponderPayLoad();
            timeSet.put(minTime, payload2);
        }
        payload2.success += success;
        payload2.failed += success ^ 1;


    }

    public String Long2Ip(long input) {
        int ip2 = (int) (long) (input);
        long ip1 = (input >>> 32);
        int ip14 = (int) (ip1 & 0x000000FF);
        int ip13 = (int) ((ip1 >>= 8) & 0x000000FF);
        int ip12 = (int) ((ip1 >>= 8) & 0x000000FF);
        int ip11 = (int) ((ip1 >>= 8) & 0x000000FF);
        int ip24 = ip2 & 0x000000FF;
        int ip23 = (ip2 >>= 8) & 0x000000FF;
        int ip22 = (ip2 >>= 8) & 0x000000FF;
        int ip21 = (ip2 >>= 8) & 0x000000FF;
        String s = "" + ip11 + "." + ip12 + "." + ip13 + "." + ip14 + " " + ip21 + "." + ip22 + "." + ip23 + "." + ip24 + " ";
        return s;
    }

    public void analyseHashMap() {
        int timeLength = hashM.keySet().size();
        ArrayList<Integer> servicePairLength = new ArrayList<>();
        ArrayList<Integer> ipPairLength = new ArrayList<>();
        ArrayList<String> ServiceNames = new ArrayList<>();
        for (Integer timeT : hashM.keySet()) {
            HashMap<String, HashMap<Long, CheckPairPayLoad>> nameSet = hashM.get(timeT);
            servicePairLength.add(nameSet.keySet().size());
            for (String strT : nameSet.keySet()) {
                ServiceNames.add(strT);
                HashMap<Long, CheckPairPayLoad> ipSet = nameSet.get(strT);
                ipPairLength.add(nameSet.keySet().size());
                for (Long ipT : ipSet.keySet()) {

                    CheckPairPayLoad payload = ipSet.get(ipT);
                }
            }
        }
        ArrayList<String> ServiceNames2 = new ArrayList<>();

        ServiceNames2.addAll(hashM3.keySet());

        try {
            File fservice = new File("service.txt");
            FileWriter out = new FileWriter(fservice);
            for (Integer a : servicePairLength) {
                out.write(a.toString());
                out.write("\n");
            }
            out.close();
            File fip = new File("ip.txt");
            out = new FileWriter(fip);
            for (Integer a : ipPairLength) {
                out.write(a.toString());
                out.write("\n");
            }
            out.close();

            fip = new File("serviceNames.txt");
            out = new FileWriter(fip);
            for (String a : ServiceNames) {
                out.write(a);
                out.write("\n");
            }
            out.close();
            fip = new File("serviceNames2.txt");
            out = new FileWriter(fip);
            for (String a : ServiceNames2) {
                out.write(a);
                out.write("\n");
            }
            out.close();

        } catch (Exception e) {
            System.out.println(e.toString());
        }


        System.out.println("时间戳种类(分钟)" + timeLength);
    }
}
