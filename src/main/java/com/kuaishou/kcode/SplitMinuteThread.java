package com.kuaishou.kcode;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;



public class SplitMinuteThread extends Thread {
    public  ArrayBlockingQueue<BufferPayload> remain;


    int BUFF_SIZE = 1024 * 1024 * 1024;
    int TIME_SIZE = 1024 * 1024 * 1024;
    byte[] buff;

    public static int firstTime = -1;
    int nowTime = 0;
    int nowSecondByteNum = 0;
    int nowSecond;
    long lastBuffIndex = 0;
    long lastBuffLength = 0;
    ByteBuffer ba;
    public static long maxBisectionTimes = 0;
    public static long maxFindTimes = 0;
    public static long splitTimeUse = 0;
    public static boolean useBisection = true;
    //    public static int MINBUFFERLEN=436773150;
    public static int MINBUFFERLEN = 431141347;
//    public static int MINBUFFERLEN = 1347;

    SplitMinuteThread(int size, int size2) {
        BUFF_SIZE = size;
        TIME_SIZE = size2;


    }

    public void LinkBlockingQueue(ArrayBlockingQueue<BufferPayload> remain) {
       this.remain=remain;
    }

    public void freeMemory() {
        buff = null;
    }

    public static long SplitMinute_waitBuffer = 0;
    public static long SplitMinute_waitBa = 0;
    public static long copyCost = 0;
    public  MultiByteBuffer buffer;
    @Override
    public void run() {
        super.run();
        try {

            int lastMinuteStartPosition=0;


            ba = PrepareMultiThreadManager.solvedMinutes.take();
            ba.clear();
            ArrayList<ByteBuffer>bufferArrayList=new ArrayList<>();
            while (true) {
                int remaining = 0;
                long timestart = 0;
                if (lastBuffLength > 0) {
                    timestart = System.currentTimeMillis();
                } else {
                    BufferPayload payload= remain.take();
                    buffer=payload.buf;
//                    System.out.println("SplitMinute waitBuffer="+(t2-t1) +"ms");
                    if (payload.length == -1) {
                        //塞进空数组
                        PrepareMultiThreadManager.unsolvedMinutes.put(new ArrayList<>());
                        return;
                    }
                    timestart = System.currentTimeMillis();
                    remaining= (int) payload.length;
                    lastBuffIndex=payload.startPos;
                    lastBuffLength=0;
                }

                long[] timearray = new long[16];
                timearray[0] = System.currentTimeMillis();
                long startIndex = lastBuffIndex;
                long bufferIndex = startIndex;
                int endIndex = (int) (remaining + startIndex + lastBuffLength);
                long lastEnterIndex = bufferIndex - 1;
                boolean getEnter = false;
                int startMinute = nowTime;
                if (firstTime == -1) {
                    for (; bufferIndex < endIndex; ++bufferIndex) {
                        if (buffer.get(bufferIndex) == 10) { //find \n
                            int minuteTime = 0;
                            for (int timepos = -13; timepos < -3; ++timepos) {
                                //从时间戳的头取到倒数第四位
                                minuteTime = buffer.get(bufferIndex + timepos) - 48 + minuteTime * 10;
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
//                            System.out.println("byteNum=" + nowSecondByteNum);
                            //nowSecond是一个0~999的值  对于一个时间n 如果n+60+60<=999那么不会出现意外 那么基准n<879 就有效 否则要计算完整time
                            // 那么 当另一个(time>=nowSecond&&time<nowSecond+60)时 认为是在一个分钟内
                            break;
                        }
                    }
                }


                if (ba.position() < MINBUFFERLEN) {
                    //新的分钟
                    //那直接读就完事了
                    int safeArea = MINBUFFERLEN - ba.position();
                    bufferIndex += Math.min(safeArea, endIndex - bufferIndex);
                    bufferIndex -= 300;
                    bufferIndex = Math.max(startIndex, bufferIndex);
                    for (; bufferIndex < endIndex; ++bufferIndex) {
                        if (buffer.get(bufferIndex) == 10) {
                            //推掉这个\n 向后找
                            bufferIndex++;
                            break;
                        }
                    }
//                    System.out.println("jump"+bufferIndex+" start="+startIndex);
                    //这样保证移动后到一个整行
                }
                timearray[1] = System.currentTimeMillis();

                //这个位置 开始二分 从bufferIndex 到endIndex 找出是否有time时间戳变化
                //bufferIndex可能在一个句子的任何位置需要先向前推进一个\n
                //倒着找
                boolean hasTwoMinute = false;
                for (long testEnd = endIndex - 1; testEnd >= bufferIndex; --testEnd) {
                    if (buffer.get(testEnd) == 10) {
                        int secondTime = 0;
                        for (int i = nowSecondByteNum + 3; i > 3; --i) {
                            secondTime = buffer.get(testEnd - i) - 48 + secondTime * 10;
                        }
                        if (!(secondTime >= nowSecond && secondTime < nowSecond + 60)) {
                            //下一分钟
                            hasTwoMinute = true;
                        }
                        break;
                    }
                }
                timearray[2] = System.currentTimeMillis();
                boolean getNextMinute=false;

                if (!hasTwoMinute) {
                    //当前buff内都是同一分钟的 可以直接结算了
                    bufferIndex = endIndex;
                    timearray[3] = System.currentTimeMillis();

                } else {
                    //有分界线 说明需要找到分界
                    if (useBisection) {

                        //二分区间 bufferIndex~endIndex
                        long left = bufferIndex;
                        long right = endIndex;
                        int searchTime = 0;
                        //[left,right)
                        while (true) {
                            long mid = (left + right) / 2;
                            boolean isNextMinute = false;
                            long enterIndex = -1;
                            searchTime++;

                            {
                                for (enterIndex = mid; buffer.get(enterIndex) != 10; ++enterIndex) {
                                }
                                //enterIndex在\n的位置
                                int secondTime = 0;
                                for (int i = nowSecondByteNum + 3; i > 3; --i) {
                                    secondTime = buffer.get(enterIndex - i) - 48 + secondTime * 10;
                                }
                                if (!(secondTime >= nowSecond && secondTime < nowSecond + 60)) {
                                    //下一分钟
                                    isNextMinute = true;
                                }
                                //查找mid后的\n
                                //查看\n是否属于下一分钟
                            }
//                        System.out.println("size="+(right-left)+"("+left+","+right+")");
                            if (isNextMinute) {
                                //mid是下一分钟 区间向左
                                right = mid;
                            } else {
                                //mid是当前分钟 区间向右
                                left = mid;
                            }
                            if (right - left < 70) {
                                //说明左右都在一行里了
                                //这时候取上一个\n就是上一minute的结尾
//                                bufferIndex=left;
                                for (enterIndex = left; buffer.get(enterIndex) != 10; --enterIndex) {
                                }
                                //enterIndex=上一个\n位置
                                bufferIndex = enterIndex;
                                break;
                            }
                        }
                        maxBisectionTimes = Math.max(maxBisectionTimes, (long) searchTime);
//                        System.out.println("二分次数"+searchTime);
                    }
                    timearray[3] = System.currentTimeMillis();
                    int findTimes = 0;
                    for (; bufferIndex < endIndex; ++bufferIndex) {
                        if (buffer.get(bufferIndex) == 10) { //find \n
                            int secondTime = 0;
                            findTimes++;

                            for (int i = nowSecondByteNum + 3; i > 3; --i) {
                                secondTime = buffer.get(bufferIndex - i) - 48 + secondTime * 10;
                            }


                            if (!(secondTime >= nowSecond && secondTime < nowSecond + 60)) {
                                //下一分钟
                                int minuteTime = 0;
                                for (int timepos = -13; timepos < -3; ++timepos) {
                                    //从时间戳的头取到倒数第四位
                                    minuteTime = buffer.get(bufferIndex + timepos) - 48 + minuteTime * 10;
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
                                getNextMinute=true;
                                //分钟刷新了
                                bufferIndex++;
                                break;
                            }
                            //希望换行没事
                            lastEnterIndex = bufferIndex;

                            bufferIndex += 70;
                        }
                    }
                    maxFindTimes = Math.max(maxFindTimes, (long) findTimes);

//                    System.out.println("查找次数"+findTimes);


                }
                timearray[4] = System.currentTimeMillis();

                //读完了 在同一分钟
                //没读完 在不同分钟
                if(bufferIndex>=endIndex){
                    //读冒了 或者刚好读完 说明都在同一分钟内

                    bufferArrayList.add(buffer.warpByteBuffer(startMinute, (int) (endIndex-startIndex)));
                    lastBuffLength=0;
                    lastBuffIndex=0;
                }else{
                    //没读完 把读完的放到bufferArrayList里  把没读完的放到下一波处理
                    //剩下的放到下一分钟了
                    bufferArrayList.add(buffer.warpByteBuffer(startMinute, (int) (lastEnterIndex-startIndex+1)));
                    //
                    lastBuffIndex= lastEnterIndex+1;
                    //取最后一个有效换行 可能1 后几个字符没有换行 可能2 换到下一个minute了
                    lastBuffLength = endIndex-lastBuffIndex;
                }


                if(getNextMinute){
                    //遇到下一分钟了
                    //把分钟数据扔出去 然后新换一个bufferArrayList
                    PrepareMultiThreadManager.unsolvedMinutes.add(bufferArrayList);
                    bufferArrayList= new ArrayList<>();
                }
                //

                timearray[5] = System.currentTimeMillis();


                timearray[6] = System.currentTimeMillis();

                for (int i = 1; i <= 6; ++i) {
                    System.out.print(" "+(timearray[i]-timearray[i-1]));
                }
                System.out.println();

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
