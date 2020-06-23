package com.kuaishou.kcode;

import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;

public class SplitMinuteThread extends Thread {
    ArrayBlockingQueue<ByteBuffer> canuse;
    ArrayBlockingQueue<ByteBuffer> canread;

    int BUFF_SIZE = 1024 * 1024 * 1024;
    int TIME_SIZE = 1024 * 1024 * 1024;
    byte[] buff;

    public static int firstTime = -1;
    int nowTime = 0;
    int nowSecondByteNum = 0;
    int nowSecond;
    int lastBuffIndex = 0;
    int lastBuffLength = 0;
    ByteBuffer ba;

    public static int MINBUFFERLEN=436773150;
//    public static int MINBUFFERLEN=430773150;
    SplitMinuteThread(int size, int size2) {
        BUFF_SIZE = size;
        TIME_SIZE = size2;
        buff = new byte[BUFF_SIZE];
        try {
            ba = PrepareMultiThreadManager.solvedMinutes.take();
            ba.clear();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void LinkBlockingQueue(ArrayBlockingQueue<ByteBuffer> canuse, ArrayBlockingQueue<ByteBuffer> canread) {
        this.canuse = canuse;
        this.canread = canread;
    }

    public void freeMemory() {
        buff = null;
    }
    public static long SplitMinute_waitBuffer=0;
    public static long SplitMinute_waitBa=0;
    @Override
    public void run() {
        super.run();
        try {
            while (true) {
                int remaining = 0;
                if (lastBuffLength > PrepareMultiThreadManager.DIRECT_CHUNCK_SIZE / 8) {
//                    System.out.println("单走一个6");
                } else {
                    long t1=System.currentTimeMillis();
                    ByteBuffer b = canread.take();
                    long t2=System.currentTimeMillis();
                    SplitMinute_waitBuffer+=(t2-t1);
//                    System.out.println("SplitMinute waitBuffer="+(t2-t1) +"ms");
                    if (b.limit() == 0) {
                        //扔出最后一minute

//                        System.out.println( name+"结束" + "ba状态" +"rmaning"+ba.remaining()+" pos"+ba.position()+"limit"+ba.limit());
//                        MINBUFFERLEN=Math.min(MINBUFFERLEN,ba.position());
                        ba.flip();
                        PrepareMultiThreadManager.unsolvedMinutes.put(ba);
                        canread.put(b);
                        return;
                    }
                    //直接拉满
                    remaining = b.remaining();
                    //从directbuffer中抽出来
                    b.get(buff, lastBuffIndex + lastBuffLength, remaining); //也可以把下面的取数变成get 这样少一次拷贝 但是buff不能立刻归还
                    canuse.put(b);

                }


                int startIndex = lastBuffIndex;
                int bufferIndex = startIndex;
                int endIndex = remaining + startIndex + lastBuffLength;
                int lastEnterIndex = bufferIndex - 1;
                boolean getEnter = false;
                int startMinute = nowTime;
                if (firstTime == -1) {
                    for (; bufferIndex < endIndex; ++bufferIndex) {
                        if (buff[bufferIndex] == 10) { //find \n
                            int minuteTime = 0;
                            for (int timepos = -13; timepos < -3; ++timepos) {
                                //从时间戳的头取到倒数第四位
                                minuteTime = buff[bufferIndex + timepos] - 48 + minuteTime * 10;
                            }
                            firstTime = minuteTime / 60;
                            nowTime = firstTime;
                            startMinute = nowTime;
                            minuteTime = nowTime * 60;
                            for (int i = 3; i <= 10; ++i) {
                                int mulI = (int) Math.pow(10, i);
                                if (minuteTime % mulI < mulI - 121) {
                                    nowSecond = minuteTime % mulI;
                                    nowSecondByteNum = i;
                                    break;
                                }
                            }
                            //nowSecond是一个0~999的值  对于一个时间n 如果n+60+60<=999那么不会出现意外 那么基准n<879 就有效 否则要计算完整time
                            // 那么 当另一个(time>=nowSecond&&time<nowSecond+60)时 认为是在一个分钟内
                            break;
                        }
                    }
                }
                if(ba.position()<MINBUFFERLEN){
                    //新的分钟
                    //那直接读就完事了
                    int safeArea=MINBUFFERLEN-ba.position();
                    bufferIndex+=Math.min(safeArea,endIndex-bufferIndex);
                    bufferIndex-=300;
                    bufferIndex=Math.max(startIndex,bufferIndex);
                }
                for (; bufferIndex < endIndex; ++bufferIndex) {
                    if (buff[bufferIndex] == 10) { //find \n
                        int secondTime = 0;
                        for (int i = nowSecondByteNum + 3; i > 3; --i) {
                            secondTime = buff[bufferIndex - i] - 48 + secondTime * 10;
                        }
//                        int lastThreeNumber= (buff[bufferIndex - 6]-48)*100+(buff[bufferIndex-5]-48)*10+(buff[bufferIndex-4]-48);
//                        secondTime = buff[bufferIndex - 6] * 100 + buff[bufferIndex - 5] * 10 + buff[bufferIndex - 4] - 5328;
//                        secondTime=lastThreeNumber;

                        if (!(secondTime >= nowSecond && secondTime < nowSecond + 60)) {
                            //下一分钟
                            int minuteTime = 0;
                            for (int timepos = -13; timepos < -3; ++timepos) {
                                //从时间戳的头取到倒数第四位
                                minuteTime = buff[bufferIndex + timepos] - 48 + minuteTime * 10;
                            }
                            nowTime = minuteTime / 60;
                            minuteTime = nowTime * 60;

                            for (int i = 3; i <= 10; ++i) {
                                int mulI = (int) Math.pow(10, i);
                                if (minuteTime % mulI < mulI - 121) {
                                    nowSecond = minuteTime % mulI;
                                    nowSecondByteNum = i;
                                    break;
                                }
                            }
                            getEnter = true;
                            //分钟刷新了
                            break;
                        }
                        //希望换行没事
                        lastEnterIndex = bufferIndex;
                        bufferIndex+=70;
                    }
                }
//                if(nowTime==26538355){
//                    System.out.println("我摊牌了");
//                }
                if (lastEnterIndex == endIndex - 1) {
                    //在最后刚好\n了 全部拷贝
                    ba.put(buff, startIndex, endIndex - startIndex);
                    lastBuffIndex = 0;
                    lastBuffLength = 0;
                } else {
                    //断了或者退出了 反正是会有字符串剩余 0 1 2 3\n  lastEnterIndex=3 startIndex=0 复制长度0 1 2 3 =4
//                    if (lastEnterIndex - startIndex + 1 < 0 || lastEnterIndex - startIndex + 1 > buff.length || lastEnterIndex - startIndex + 1 > ba.limit()) {
//                        System.out.println("aaaa");
//                    }
//                    long t = System.currentTimeMillis();
                    ba.put(buff, startIndex, lastEnterIndex - startIndex + 1);
//                    System.out.println("ba.put耗时 ms" + (System.currentTimeMillis() - t) +" len="+(lastEnterIndex-startIndex+1) +" speed="+(1.0*(lastEnterIndex-startIndex+1)/1024/1024/(System.currentTimeMillis() - t)*1000)+"MB/s");
                    //并且要把buff续上
                    lastBuffLength = endIndex - (lastEnterIndex + 1);
                    if (1L + endIndex + PrepareMultiThreadManager.DIRECT_CHUNCK_SIZE < buff.length - 100) {
                        //还能继续读
                        lastBuffIndex = lastEnterIndex + 1;
//                        System.out.println("我还可以");
                    } else {
                        //空间不够了 要把剩余的拷贝到数组首部(其实可以循环数组实现的 但是太乱了 这得自己封装一个数组了)
                        System.arraycopy(buff, lastEnterIndex + 1, buff, 0, lastBuffLength);
                        lastBuffIndex = 0;

                    }
                }
//                if(lastBuffLength==1539469608){
//                    System.out.println("难顶");
//                }
                if (startMinute != nowTime) {
//                    MINBUFFERLEN=Math.min(MINBUFFERLEN,ba.position());

                    ba.flip();
                    PrepareMultiThreadManager.unsolvedMinutes.put(ba);
                    long ta=System.currentTimeMillis();
                    ba = PrepareMultiThreadManager.solvedMinutes.take();
                    long tb=System.currentTimeMillis();
                    SplitMinute_waitBa+=(tb-ta);
                    ba.clear();
                }


            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
