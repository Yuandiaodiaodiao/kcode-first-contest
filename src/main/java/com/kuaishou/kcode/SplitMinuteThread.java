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
    int nowSecond;
    int lastBuffIndex = 0;
    int lastBuffLength = 0;
    ByteBuffer ba;

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
    public void freeMemory(){
        buff=null;
    }
    @Override
    public void run() {
        super.run();
        try {
            while (true) {
                int remaining=0;
                if(lastBuffLength>PrepareMultiThreadManager.DIRECT_CHUNCK_SIZE/8){
//                    System.out.println("单走一个6");
                }else{

                    ByteBuffer b = canread.take();
                    if (b.limit() == 0) {
                        //扔出最后一minute
                        Thread t = Thread.currentThread();
                        String name = t.getName();
//                        System.out.println( name+"结束" + "ba状态" +"rmaning"+ba.remaining()+" pos"+ba.position()+"limit"+ba.limit());

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
                int endIndex = remaining + startIndex+lastBuffLength;
                int lastEnterIndex = bufferIndex-1;
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
                            nowSecond = minuteTime % 1000; //nowSecond是一个0~999的值  对于一个时间n 如果n+60+60<=999那么不会出现意外 那么基准n<879 就有效 否则要计算完整time
                            // 那么 当另一个(time>=nowSecond&&time<nowSecond+60)时 认为是在一个分钟内
                            break;
                        }
                    }
                }
                for (; bufferIndex < endIndex; ++bufferIndex) {
                    if (buff[bufferIndex] == 10) { //find \n
                        int lastThreeNumber= (buff[bufferIndex - 6]-48)*100+(buff[bufferIndex-5]-48)*10+(buff[bufferIndex-4]-48);
                        int secondTime = buff[bufferIndex - 6] * 100 + buff[bufferIndex - 5] * 10 + buff[bufferIndex - 4] - 5328;
                        secondTime=lastThreeNumber;
                        if (nowSecond < 800) {
                            if (!(secondTime >= nowSecond && secondTime < nowSecond + 60)) {
                                //下一分钟
                                int minuteTime = 0;
                                for (int timepos = -13; timepos < -3; ++timepos) {
                                    //从时间戳的头取到倒数第四位
                                    minuteTime = buff[bufferIndex + timepos] - 48 + minuteTime * 10;
                                }
                                nowTime = minuteTime / 60;
                                minuteTime =nowTime * 60;
                                nowSecond = minuteTime % 1000;
                                getEnter = true;
                                //分钟刷新了
                                break;
                            }
                        } else {
                            int minuteTime = 0;
                            for (int timepos = -13; timepos < -3; ++timepos) {
                                //从时间戳的头取到倒数第四位
                                minuteTime = buff[bufferIndex + timepos] - 48 + minuteTime * 10;
                            }
                            if (minuteTime / 60 > nowTime) {
                                nowTime = minuteTime /60;
                                minuteTime = nowTime * 60;
                                nowSecond = minuteTime % 1000;
                                getEnter = true;
                                break; //分钟刷新
                            }
                        }
                        //希望换行没事
                        lastEnterIndex = bufferIndex;
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
                    if( lastEnterIndex - startIndex + 1 <0|| lastEnterIndex - startIndex + 1 > buff.length ||lastEnterIndex - startIndex + 1>ba.limit()){
                        System.out.println("aaaa");
                    }
                    ba.put(buff, startIndex, lastEnterIndex - startIndex + 1);
                    //并且要把buff续上
                    lastBuffLength = endIndex - (lastEnterIndex + 1);
                    if (1L+endIndex+ PrepareMultiThreadManager.DIRECT_CHUNCK_SIZE < buff.length-100) {
                        //还能继续读
                        lastBuffIndex = lastEnterIndex + 1;
//                        System.out.println("我还可以");
                    } else {
                        //空间不够了 要把剩余的拷贝到数组首部(其实可以循环数组实现的 但是太乱了 这得自己封装一个数组了)
                        System.arraycopy(buff, lastEnterIndex + 1, buff, 0, endIndex - (lastEnterIndex + 1));
                        lastBuffIndex = 0;

                    }
                }
//                if(lastBuffLength==1539469608){
//                    System.out.println("难顶");
//                }
                if (startMinute != nowTime) {
                    ba.flip();
                    PrepareMultiThreadManager.unsolvedMinutes.put(ba);
                    ba=PrepareMultiThreadManager.solvedMinutes.take();
                    ba.clear();
                }


            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
