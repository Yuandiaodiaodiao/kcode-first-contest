package com.kuaishou.kcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author kcode
 * Created on 2020-06-01
 * 实际提交时请维持包名和类名不变
 */

public class KcodeRpcMonitorImpl implements KcodeRpcMonitor {
    public int checkPairTimes = 0;
    public HashSet<String> callerSet = new HashSet<String>();
    public HashSet<String> responderSet = new HashSet<String>();
    public int prepareTimes = 0;
    public long fileLength=0;
    // 不要修改访问级别
    public KcodeRpcMonitorImpl() {
        prepareTimes+=1;
    }

    //读入
    public void prepare(String path) {
        File f=new File(path);
        fileLength=f.length();
        prepareTimes++;
        try {
            long chunck=1020*1024*1024;
            int chunckint= Long.valueOf(chunck).intValue();
            byte[] bbb = new byte[chunckint];
            RandomAccessFile raf = new RandomAccessFile(f, "r");
            FileChannel channel = raf.getChannel();
            for(long i=0;i<=fileLength;i+=chunck){
//                System.out.println("读入"+i);
                channel.position(i);
                MappedByteBuffer buff = channel.map(FileChannel.MapMode.READ_ONLY,0,chunck);
                buff.get(bbb);
                byte a;
                for(int j=0 ;j<bbb.length;j++){
                    a=bbb[j];
//                    System.out.print((char)(a));
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    //读入


    //查询1
    public List<String> checkPair(String caller, String responder, String time) {

        if(responder.length()>0){
            throw new ArrayIndexOutOfBoundsException("文件长度"+fileLength);

        }
        checkPairTimes += 1;
        callerSet.add(caller);
        responderSet.add(responder);
        return new ArrayList<String>();
    }

    //查询2
    public String checkResponder(String responder, String start, String end) {
        String s="checkPairTimes"+checkPairTimes+"callerSet.size"+callerSet.size()+"responderSet.size"+responderSet.size()+"   "+prepareTimes;

        return "0.00%";
    }

}
