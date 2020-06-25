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
            solvedMinutes.add(ByteBuffer.allocate(PrepareMultiThreadManager.Time_CHUNCK_SIZE));

            while (true){
                long timestart=System.currentTimeMillis();
            ByteBuffer f = unsolvedMinutes.take();
            if (f.limit() == 0) {
//                Thread t = Thread.currentThread();
//                String name = t.getName();
//                System.out.println( name+"结束");
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

                int hashService1hash1=b;
                f.position(f.position()+5);
                for (; b != ','; b =f.get()) {
                }
                int len1=f.position()-1;
                int hashService1=((((f.get(len1-5)+(f.get(len1-4)<<5)+
                        (f.get(len1-3)<<10)+(f.get(len1-2)<<15 )+(f.get(len1-1)<<20))% 90) << 4) +
                        (hashService1hash1 % 29));


                int numBuff = 0;
                for (b = f.get(); b != ','; b = f.get()) {
                    if (b != '.') {
                        numBuff = (b - '0')+numBuff*10;
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
                int hashService2hash1=b;
                f.position(f.position()+9);
                for (; b != ','; b = f.get()) {

                }
                int len2=f.position()-1;
                int hashService2=((((f.get(len2-5)+(f.get(len2-4)<<5)+(f.get(len2-3)<<10)+
                        (f.get(len2-2)<<15 )+(f.get(len2-1)<<20))% 90) << 12) +
                        ((hashService2hash1 % 29)<<8));
                int hashService2Only=(((hashService2hash1-97)%36)+
                        ((((f.get(len2-6))+(f.get(len2-5)<<1)+
                                (f.get(len2-4)<<6)+(f.get(len2-3)<<9)+
                                (f.get(len2-2)<<16)+(f.get(len2-1)<<21))%73)<<3));
                for (b = f.get(); b != ','; b = f.get()) {
                    if (b != '.') {
                        numBuff = (b - '0') + numBuff*10;
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
                if (b == 't') {
                    success = 1;
                    f.position(f.position()+ 4);
                } else {
                    //failed
                    f.position(f.position()+ 5);
                }



                int useTime = 0;
                for (b = f.get(); b != ','; b = f.get()) {
                    useTime = (b - '0')+useTime*10;
                }
                b = f.get();

                if (startMinute == -1) {
                    int minTime = 0;

                    for (int timepos = 1; timepos <= 10; ++timepos, b = f.get()) {
                        minTime = (b - '0')+minTime*10;
                    }
                    f.position(f.position()+ 3);
                    minTime /= 60;
                    startMinute=minTime-SplitMinuteThread.firstTime;

                    for(int i=0;i<1024;++i){
                        PrepareMultiThreadDataCore.hashCheckResponder[startMinute][i]=new CheckResponderPayLoad();
                    }
//                    Thread t = Thread.currentThread();
//                    String name = t.getName();
//                    System.out.println( "time= "+startMinute+" "+name+"接单 size="+f.remaining()+" pos="+f.position()+ " limit="+f.limit());

                }else{
                    f.position(f.position()+ 13);
                }
                int stringHash=(hashService1+hashService2)%4997;
                int secondServicesHash= hashService2Only;


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
                long timestart2=System.currentTimeMillis();

                SolveMinuteArrayListAnswerThread.solve(startMinute);
                long timestart3=System.currentTimeMillis();
//                System.out.println("处理分钟"+(timestart2-timestart)+"桶排序"+(timestart3-timestart2));

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
