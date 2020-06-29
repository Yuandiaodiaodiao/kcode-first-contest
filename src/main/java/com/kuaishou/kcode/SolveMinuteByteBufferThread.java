package com.kuaishou.kcode;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;


public class SolveMinuteByteBufferThread extends Thread {
    ArrayBlockingQueue<ByteBuffer> unsolvedMinutes;
    ArrayBlockingQueue<ByteBuffer> solvedMinutes;


    SolveMinuteByteBufferThread(ArrayBlockingQueue<ByteBuffer> unsolvedMinutes, ArrayBlockingQueue<ByteBuffer> solvedMinutes) {
        this.solvedMinutes = solvedMinutes;
        this.unsolvedMinutes = unsolvedMinutes;
    }

    public void clearCheckPair(CheckPairPayLoad[][] c) {
        long t1 = System.currentTimeMillis();
        for (int i = 0; i <= 4999; ++i) {
            for (int j = 0; j <= 2552; ++j) {
                c[i][j] = null;
            }
        }
        long t2 = System.currentTimeMillis();
//        System.out.println("清空耗时="+(t2-t1));
    }

    @Override
    public void run() {
        super.run();
        try {
            Field field = ByteBuffer.allocate(1).getClass().getSuperclass().getDeclaredField("hb");
            field.setAccessible(true);

            CheckPairPayLoad[][] cacheCheckPair = null;
            long allTime = 0;
            long solvedTimes = 0;
            long allIncrease = 0;
            while (true) {
                ByteBuffer f = unsolvedMinutes.take();
                if (f.limit() == 0) {
//                Thread t = Thread.currentThread();
//                String name = t.getName();
//                System.out.println( name+"结束");
                    unsolvedMinutes.put(f);
                    return;
                }
                if (cacheCheckPair == null) {
                    cacheCheckPair = PrepareMultiThreadDataCore.newhashCheckPair();
                }

                //直接拉满
                //从directbuffer中抽出来
                int startMinute = -1;

                CheckResponderPayLoad[] cacheCheckResponder = new CheckResponderPayLoad[0];
                long t0 = System.currentTimeMillis();

                if (startMinute == -1) {
                    byte b = f.get();
//                    if (b == 10) continue;
                    long ip1 = 0;
                    long ip2 = 0;

                    int hashService1hash1 = b;
                    f.position(f.position() + 10);
                    for (; b != 44; b = f.get()) {
                    }
                    int len1 = f.position() - 1;
                    int hashService1 = ((((f.get(len1 - 5) + (f.get(len1 - 4) << 2) +
                            (f.get(len1 - 3) << 6) + (f.get(len1 - 2) << 13) + (f.get(len1 - 1) << 17)) % 69) << 12) +
                            ((hashService1hash1 - 97) << 8));


                    int numBuff = 0;
                    for (b = f.get(); b != 44; b = f.get()) {
                        if (b != 46) {
                            numBuff = (b - 48) + numBuff * 10;
                        } else {
                            ip1 <<= 8;
                            ip1 += numBuff;
                            numBuff = 0;
                        }
                    }
                    ip1 <<= 8;
                    ip1 += numBuff;
                    numBuff = 0;

                    b = f.get();
                    int hashService2hash1 = b;
                    f.position(f.position() + 10);
                    for (; b != 44; b = f.get()) {

                    }
                    int len2 = f.position() - 1;

                    int hashService2 = ((hashService2hash1 - 97) +
                            ((((f.get(len2 - 6)) + (f.get(len2 - 5) << 5) +
                                    (f.get(len2 - 4) << 10) + (f.get(len2 - 3) << 14) +
                                    (f.get(len2 - 2) << 15) + (f.get(len2 - 1) << 24)) % 89) << 3));
                    for (b = f.get(); b != 44; b = f.get()) {
                        if (b != 46) {
                            numBuff = (b - 48) + numBuff * 10;
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
                    if (b == 116) {
                        success = 1;
                        f.position(f.position() + 4);
                    } else {
                        //failed
                        f.position(f.position() + 5);
                    }


                    int useTime = 0;
                    for (b = f.get(); b != ','; b = f.get()) {
                        useTime = (b - 48) + useTime * 10;
                    }
                    b = f.get();

                    if (startMinute == -1) {
                        int minTime = 0;

                        for (int timepos = 1; timepos <= 10; ++timepos, b = f.get()) {
                            minTime = (b - 48) + minTime * 10;
                        }
                        f.position(f.position() + 3);
                        minTime /= 60;
                        startMinute = minTime - SplitMinuteThread.firstTime;

                        for (int i = 0; i < 999; ++i) {
                            PrepareMultiThreadDataCore.hashCheckResponder[startMinute][i] = new CheckResponderPayLoad();
                        }
                        cacheCheckResponder = PrepareMultiThreadDataCore.hashCheckResponder[startMinute];
//                    for(int i=0;i<=4999;++i){
//                        PrepareMultiThreadDataCore.hashCheckPair[startMinute][i]=new HashMap<>(64);
//                    }
//                        cacheCheckPair = PrepareMultiThreadDataCore.hashCheckPair[startMinute];
//                    Thread t = Thread.currentThread();
//                    String name = t.getName();
//                    System.out.println( "time= "+startMinute+" "+name+"接单 size="+f.remaining()+" pos="+f.position()+ " limit="+f.limit());

                    } else {
                        f.position(f.position() + 13);
                    }
                    int stringHash = (hashService1 + hashService2) % 4999;
                    int secondServicesHash = hashService2;


                    long twoIPs = (ip1 << 32) + ip2;
                    int ipHash = HashCode.hashIp(ip1, ip2);

                    CheckPairPayLoad payload = cacheCheckPair[stringHash][ipHash];
                    if (payload == null) {
                        payload = new CheckPairPayLoad();
                        cacheCheckPair[stringHash][ipHash] = payload;
                        payload.ip = twoIPs;
                    }

                    //change payload

                    payload.successTimes += success;
                    //1^1 =0 0^1 =1
                    payload.failedTimes += success ^ 1;
                    payload.bucket[useTime] += 1;


                    CheckResponderPayLoad payload2 = cacheCheckResponder[secondServicesHash];

                    payload2.success += success;
                    payload2.failed += success ^ 1;

                }
//                Field[] fd = f.getClass().getSuperclass().getDeclaredFields();
//                for(Field field : fd){
//                    field.setAccessible(true);
//                    System.out.println(field.getName()+":"+field.get(f));
//                }

                byte[] byteArray = (byte[]) field.get(f);
                long t1 = System.currentTimeMillis();
                int position = f.position();
                byte b;
                int limit = f.limit();
                long newCost = 0;
                long newCost2 = 0;
//                System.out.println("找time"+(t1-t0));
                while (position < limit) {
//                    if (b == 10) continue;
                    long ip1 = 0;
                    long ip2 = 0;

                    int hashService1hash1 = byteArray[position];
//                    f.position(f.position() + 10);
                    position += 10;
                    while (byteArray[++position] != 44) {
                    }
                    int hashService1 = ((((byteArray[position - 5] + (byteArray[position - 4] << 2) +
                            (byteArray[position - 3] << 6) + (byteArray[position - 2] << 13) + (byteArray[position - 1] << 17)) % 69) << 12) +
                            ((hashService1hash1 - 97) << 8));


                    int numBuff = 0;

                    while ((b = byteArray[++position]) != 44) {
                        ip1 = (b != 46) ? ip1 : (ip1 << 8) + numBuff;
                        numBuff = (b != 46) ? (b - 48) + numBuff * 10 : 0;
                    }

                    ip1 = (ip1 << 8) + numBuff;

                    int hashService2hash1 = byteArray[++position];
                    position += 10;
                    while (byteArray[++position] != 44) {
                    }

                    int hashService2 = ((hashService2hash1 - 97) +
                            ((((byteArray[position - 6]) + (byteArray[position - 5] << 5) +
                                    (byteArray[position - 4] << 10) + (byteArray[position - 3] << 14) +
                                    (byteArray[position - 2] << 15) + (byteArray[position - 1] << 24)) % 89) << 3));

                    numBuff = 0;

                    while ((b = byteArray[++position]) != 44) {
                        ip2 = (b != 46) ? ip2 : (ip2 << 8) + numBuff;
                        numBuff = (b != 46) ? (b - 48) + numBuff * 10 : 0;
                    }

                    ip2 = (ip2 << 8) + numBuff;


                    int success = (byteArray[++position] == 116 ? 0 : 1);
                    position += 4 + success;


                    int useTime = 0;
                    while ((b = byteArray[++position]) != 44) {
                        useTime = (b - 48) + useTime * 10;
                    }


                    position += 15;
                    int stringHash = (hashService1 + hashService2) % 4999;


                    int ipHash = (int) ((((ip1 - 167772160) % 3457) << 9) + ((ip2 - 167772160) % 2833)) % 2551;

                    CheckPairPayLoad payload = cacheCheckPair[stringHash][ipHash];
                    if (payload == null) {
                        payload = new CheckPairPayLoad();
                        cacheCheckPair[stringHash][ipHash] = payload;
                        payload.ip = (ip1 << 32) + ip2;
                    }
                    //change payload

                    payload.successTimes += success ^ 1;
                    //1^1 =0 0^1 =1
                    payload.failedTimes += success;
                    payload.bucket[useTime] += 1;


                    CheckResponderPayLoad payload2 = cacheCheckResponder[hashService2];

                    payload2.success += success ^ 1;
                    payload2.failed += success;


                }
                long t2 = System.currentTimeMillis();
                allTime += (t2 - t1);
                solvedTimes++;
                allIncrease += newCost2 - newCost;
//                System.out.println("平均处理时间=" + (1.0 * allTime / solvedTimes) );

                solvedMinutes.put(f);

                SolveMinuteArrayListAnswerThread.solve(startMinute, cacheCheckPair);
                clearCheckPair(cacheCheckPair);
                PrepareMultiThreadManager.endCountDown.countDown();
//                System.out.println("处理分钟" + (timestart4 - timestart) + "桶排序" + (timestart3 - timestart2));

            }

        } catch (InterruptedException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }

    }
}
