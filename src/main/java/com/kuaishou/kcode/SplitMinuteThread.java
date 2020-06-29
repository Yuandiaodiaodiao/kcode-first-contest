package com.kuaishou.kcode;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;

import static com.kuaishou.kcode.PrepareMultiThreadManager.Time_CHUNCK_SIZE;

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
    public static long maxBisectionTimes=0;
    public static long maxFindTimes=0;
    public static long splitTimeUse=0;
    public static boolean useBisection=true;
    //    public static int MINBUFFERLEN=436773150;
    public static int MINBUFFERLEN = 431141347;
//    public static int MINBUFFERLEN = 1347;

    SplitMinuteThread(int size, int size2) {
        BUFF_SIZE = size;
        TIME_SIZE = size2;


    }

    public void LinkBlockingQueue(ArrayBlockingQueue<ByteBuffer> canuse, ArrayBlockingQueue<ByteBuffer> canread) {
        this.canuse = canuse;
        this.canread = canread;
    }

    public void freeMemory() {
        buff = null;
    }

    public static long SplitMinute_waitBuffer = 0;
    public static long SplitMinute_waitBa = 0;

    @Override
    public void run() {
        super.run();
        try {

            Field field = ByteBuffer.allocate(1).getClass().getSuperclass().getDeclaredField("hb");
            field.setAccessible(true);
            buff = new byte[BUFF_SIZE];
            ba = PrepareMultiThreadManager.solvedMinutes.take();
            ba.clear();
            byte[] baArray=(byte[])field.get(ba);
            while (true) {
                int remaining = 0;
                long timestart=0;
                if (lastBuffLength > PrepareMultiThreadManager.DIRECT_CHUNCK_SIZE / 4) {
//                    System.out.println("单走一个6");
                    timestart=System.currentTimeMillis();
                } else {
                    long t1 = System.currentTimeMillis();
                    ByteBuffer b = canread.take();
                    long t2 = System.currentTimeMillis();
                    SplitMinute_waitBuffer += (t2 - t1);
//                    System.out.println("SplitMinute waitBuffer="+(t2-t1) +"ms");
                    if (b.limit() == 0) {
                        //扔出最后一minute

//                        System.out.println( name+"结束" + "ba状态" +"rmaning"+ba.remaining()+" pos"+ba.position()+"limit"+ba.limit());
//                        MINBUFFERLEN=Math.min(MINBUFFERLEN,ba.position());
//                        MAXBUFFERLEN=Math.max(MAXBUFFERLEN,ba.position());

                        ba.flip();
                        PrepareMultiThreadManager.unsolvedMinutes.put(ba);
                        canread.put(b);
                        return;
                    }
                    timestart=System.currentTimeMillis();

                    //直接拉满
                    remaining = b.remaining();
                    //从directbuffer中抽出来
                    b.get(buff, lastBuffIndex + lastBuffLength, remaining); //也可以把下面的取数变成get 这样少一次拷贝 但是buff不能立刻归还
                    canuse.put(b);
                }

                long[] timearray=new long[16];
                timearray[0]=System.currentTimeMillis();
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
                        if (buff[bufferIndex] == 10) {
                            //推掉这个\n 向后找
                            bufferIndex++;
                            break;
                        }
                    }
//                    System.out.println("jump"+bufferIndex+" start="+startIndex);
                    //这样保证移动后到一个整行
                }
                timearray[1]=System.currentTimeMillis();

                //这个位置 开始二分 从bufferIndex 到endIndex 找出是否有time时间戳变化
                //bufferIndex可能在一个句子的任何位置需要先向前推进一个\n
                //倒着找
                boolean hasTwoMinute = false;
                for (int testEnd = endIndex - 1; testEnd >= bufferIndex; --testEnd) {
                    if (buff[testEnd] == 10) {
                        int secondTime = 0;
                        for (int i = nowSecondByteNum + 3; i > 3; --i) {
                            secondTime = buff[testEnd - i] - 48 + secondTime * 10;
                        }
                        if (!(secondTime >= nowSecond && secondTime < nowSecond + 60)) {
                            //下一分钟
                            hasTwoMinute = true;
                        }
                        break;
                    }
                }
                timearray[2]=System.currentTimeMillis();

                if(!hasTwoMinute){
                    //当前buff内都是同一分钟的 可以直接结算了
                    bufferIndex=endIndex;
                    timearray[3]=System.currentTimeMillis();

                }else{
                    //有分界线 说明需要找到分界
                    if(useBisection){

                        //二分区间 bufferIndex~endIndex
                        int left = bufferIndex;
                        int right = endIndex;
                        int searchTime=0;
                        //[left,right)
                        while (true) {
                            int mid=(left+right)/2;
                            boolean isNextMinute=false;
                            int enterIndex=-1;
                            searchTime++;

                            {
                                for (enterIndex = mid; buff[enterIndex] != 10; ++enterIndex) {
                                }
                                //enterIndex在\n的位置
                                int secondTime = 0;
                                for (int i = nowSecondByteNum + 3; i > 3; --i) {
                                    secondTime = buff[enterIndex - i] - 48 + secondTime * 10;
                                }
                                if (!(secondTime >= nowSecond && secondTime < nowSecond + 60)) {
                                    //下一分钟
                                    isNextMinute = true;
                                }
                                //查找mid后的\n
                                //查看\n是否属于下一分钟
                            }
//                        System.out.println("size="+(right-left)+"("+left+","+right+")");
                            if(isNextMinute){
                                //mid是下一分钟 区间向左
                                right=mid;
                            }else{
                                //mid是当前分钟 区间向右
                                left=mid;
                            }
                            if(right-left<70){
                                //说明左右都在一行里了
                                //这时候取上一个\n就是上一minute的结尾
//                                bufferIndex=left;
                                for (enterIndex =left; buff[enterIndex] != 10; --enterIndex) {
                                }
                                //enterIndex=上一个\n位置
                                bufferIndex=enterIndex;
                                break;
                            }
                        }
                        maxBisectionTimes=Math.max(maxBisectionTimes,(long)searchTime);
//                        System.out.println("二分次数"+searchTime);
                    }
                    timearray[3]=System.currentTimeMillis();

                    int findTimes=0;
                    for (; bufferIndex < endIndex; ++bufferIndex) {
                        if (buff[bufferIndex] == 10) { //find \n
                            int secondTime = 0;
                             findTimes++;

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
//                                System.out.println("byteNum=" + nowSecondByteNum);

                                getEnter = true;
                                //分钟刷新了
                                bufferIndex++;
                                break;
                            }
                            //希望换行没事
                            lastEnterIndex = bufferIndex;

                            bufferIndex += 70;
                        }
                    }
                    maxFindTimes=Math.max(maxFindTimes,(long)findTimes);

//                    System.out.println("查找次数"+findTimes);


                }
                timearray[4]=System.currentTimeMillis();

                if (bufferIndex>=endIndex) {
                    //在最后刚好\n了 全部拷贝

                    System.arraycopy(buff, startIndex, baArray, ba.position(), endIndex - startIndex);
                    ba.position(ba.position()+(endIndex - startIndex));

//                    ba.put(buff, startIndex, endIndex - startIndex);
                    lastBuffIndex = 0;
                    lastBuffLength = 0;
                } else {
                    //断了或者退出了 反正是会有字符串剩余 0 1 2 3\n  lastEnterIndex=3 startIndex=0 复制长度0 1 2 3 =4
//                    if (lastEnterIndex - startIndex + 1 < 0 || lastEnterIndex - startIndex + 1 > buff.length || lastEnterIndex - startIndex + 1 > ba.limit()) {
//                        System.out.println("aaaa");
//                    }
                    long t = System.currentTimeMillis();

                    

                    System.arraycopy(buff, startIndex, baArray, ba.position(), lastEnterIndex - startIndex + 1);
                    long t2=System.currentTimeMillis();
                    ba.position(ba.position()+(lastEnterIndex - startIndex + 1));


//                    ba.put(buff, startIndex, lastEnterIndex - startIndex + 1);
                    System.out.println("ba.put耗时 ms" + (t2 - t) +" len="+(lastEnterIndex-startIndex+1) +" speed="+(1.0*(lastEnterIndex-startIndex+1)/1024/1024/(t2 - t)*1000)+"MB/s");
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
                timearray[5]=System.currentTimeMillis();

//                System.out.println("t1="+timearray[1]+" t2="+timearray[2]+" t3="+timearray[3]+" t4="+timearray[4]+" t5="+timearray[5]);

                if (startMinute != nowTime) {
                    ba.flip();
                    PrepareMultiThreadManager.unsolvedMinutes.add(ba);
                    splitTimeUse+=System.currentTimeMillis()-timestart;

                    long ta = System.currentTimeMillis();
                    ba = PrepareMultiThreadManager.solvedMinutes.take();
                    long tb = System.currentTimeMillis();
                    SplitMinute_waitBa += (tb - ta);
                    ba.clear();
                    baArray=(byte[])field.get(ba);
                }else{
                    splitTimeUse+=System.currentTimeMillis()-timestart;

                }
                timearray[6]=System.currentTimeMillis();

                for(int i=1;i<=6;++i){
                    System.out.print((timearray[i]-timearray[i-1])+" ");
                }
                System.out.println();

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
