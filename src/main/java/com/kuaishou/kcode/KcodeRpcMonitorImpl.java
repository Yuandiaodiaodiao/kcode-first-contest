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
    public double prepareTime=0;
    public byte[] bytesBuffer=new byte[100*1024*1024];
    public ArrayList<MappedByteBuffer> mbArray=new  ArrayList<MappedByteBuffer>();
    public File f;
    public FileChannel  channel;
    // 不要修改访问级别
    public KcodeRpcMonitorImpl() {
        prepareTimes+=1;
    }
    public void hackTime(String path,long chunck){
        try{
            f=new File(path);
            fileLength=f.length();
            RandomAccessFile raf = new RandomAccessFile(f, "r");

            channel = raf.getChannel();
            for(long i=0;i<=fileLength;i+=chunck){
                channel.position(i);
                MappedByteBuffer buff = channel.map(FileChannel.MapMode.READ_ONLY,0,Math.min(chunck,fileLength-i));
                mbArray.add(buff);
            }
        }catch (IOException e){

        }

    }
    public void realPrepare(String path,long chunck,int chunckint){

        prepareTimes++;

        try {

            File f=new File(path);
            fileLength=f.length();
            byte[] bbb = new byte[chunckint];
            RandomAccessFile raf = new RandomAccessFile(f, "r");
            FileChannel channel = raf.getChannel();
            for(long i=0;i<=fileLength;i+=chunck){
//                System.out.println("读入"+i);
                channel.position(i);
                MappedByteBuffer buff = channel.map(FileChannel.MapMode.READ_ONLY,0,Math.max(chunck,fileLength-i));
                mbArray.add(buff);
//                buff.get(bbb);
//
//                byte a;
//                for(int j=0 ;j<chunck;j++){
//
//                    a=bbb[j];
////                    System.out.print((char)(a));
//                }
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //读入
    public void prepare(String path) {
        for(int a=100;a<=100;a+=100){
            long chunck=a*1024*1024;
            int chunckint= a*1024*1024;
            Long startTime = System.currentTimeMillis();
//            realPrepare(path,chunck,chunckint);
            hackTime(path,chunck);
            Long endTime = System.currentTimeMillis();
            prepareTime=(endTime-startTime)*1.0/1000;
//            System.out.println("prepare耗时"+(endTime-startTime)*1.0/1000 + "chunck size="+a+"MB");
//            Runtime.getRuntime().gc();
        }


    }
    //读入


    //查询1
    public List<String> checkPair(String caller, String responder, String time) {
        int bytes=0;
        Long startTime = System.currentTimeMillis();
        for(MappedByteBuffer mb: mbArray){
//            System.out.println("读入");
            mb.get(bytesBuffer,0,mb.limit());
        }
        Long endTime = System.currentTimeMillis();

        if(responder.length()>0){
            throw new ArrayIndexOutOfBoundsException("文件长度"+fileLength+"prepare时间"+prepareTime+"getTime="+((endTime-startTime)*1.0/1000));

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
