package com.kuaishou.kcode;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;

public class SolveMinuteByteBufferThread extends Thread{
    ArrayBlockingQueue<ByteBuffer> unsolvedMinutes ;
    ArrayBlockingQueue<ByteBuffer> solvedMinutes ;



    public byte[] service1 = new byte[40];
    public byte[] service2 = new byte[40];

    SolveMinuteByteBufferThread(ArrayBlockingQueue<ByteBuffer> unsolvedMinutes,ArrayBlockingQueue<ByteBuffer> solvedMinutes){
        this.solvedMinutes=solvedMinutes;
        this.unsolvedMinutes=unsolvedMinutes;
    }

    @Override
    public void run() {
        super.run();
        try {
        while (true){
            ByteBuffer f = unsolvedMinutes.take();
            if (f.limit() == 0) {
                Thread t = Thread.currentThread();
                String name = t.getName();
                System.out.println( name+"结束");
                unsolvedMinutes.put(f);
                return;
            }

            //直接拉满
            int remaining = f.remaining();
            //从directbuffer中抽出来
            int startMinute = -1;

            while(f.hasRemaining()){
                byte b=f.get();
                if(b=='\n')continue;
                long ip1 = 0;
                long ip2 = 0;
                int strIndexA = 0;
                for (; b != ','; b =f.get()) {
                    service1[strIndexA++] = b;
                }

                int numBuff = 0;
                for (b = f.get(); b != ','; b = f.get()) {
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

                b =f.get();
                int strIndexB = 0;
                for (; b != ','; b = f.get()) {
                    service2[strIndexB++] = b;
                }

                for (b = f.get(); b != ','; b = f.get()) {
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

                b = f.get();
                int success = 0;
                int failed = 0;
                if (b == 't') {
                    success = 1;
                    f.position(f.position()+ 4);
                } else {
                    failed = 1;
                    f.position(f.position()+ 5);
                }



                int useTime = 0;
                for (b = f.get(); b != ','; b = f.get()) {
                    useTime *= 10;
                    useTime += (b - '0');
                }
                b = f.get();

                if (startMinute == -1) {
                    int minTime = 0;

                    for (int timepos = 1; timepos <= 10; ++timepos, b = f.get()) {
                        minTime *= 10;
                        minTime += (b - '0');
                    }
                    f.position(f.position()+ 3);
                    minTime /= 60;
                    startMinute=minTime-SplitMinuteThread.firstTime;
//                    Thread t = Thread.currentThread();
//                    String name = t.getName();
//                    System.out.println( "time= "+startMinute+" "+name+"接单 size="+f.remaining()+" pos="+f.position()+ " limit="+f.limit());

                }else{
                    f.position(f.position()+ 13);
                }
                int stringHash=HashCode.hashTwoByte(service1,strIndexA,service2,strIndexB);
                int secondServicesHash= HashCode.hashByte(service2,strIndexB);


                long twoIPs = ( ip1 << 32) +  ip2;
                HashMap<Long, CheckPairPayLoad>ipset= PrepareMultiThreadDataCore.hashCheckPair[startMinute][stringHash];
                if(ipset==null){
                    ipset=new HashMap<>(256);
                    PrepareMultiThreadDataCore.hashCheckPair[startMinute][stringHash]=ipset;
                }
                CheckPairPayLoad payload = ipset.get(twoIPs);
                if (payload == null) {
                    payload = new CheckPairPayLoad();
                    ipset.put(twoIPs, payload);
                }
                //change payload
                payload.successTimes += success;
                //1^1 =0 0^1 =1
                payload.failedTimes += success ^ 1;
                payload.bucket[useTime] += 1;


                CheckResponderPayLoad payload2=PrepareMultiThreadDataCore.hashCheckResponder[startMinute][secondServicesHash];
                if(payload2==null){
                    payload2= new CheckResponderPayLoad();
                    PrepareMultiThreadDataCore.hashCheckResponder[startMinute][secondServicesHash]=payload2;
                }
                payload2.success += success;
                payload2.failed += success ^ 1;



            }
            solvedMinutes.put(f);
            SolveMinuteArrayListAnswerThread.solve(startMinute);
        }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
