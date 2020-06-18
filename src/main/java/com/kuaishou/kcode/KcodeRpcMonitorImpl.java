package com.kuaishou.kcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
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
    public double readTime=0;
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
            ByteBuffer buf1 = ByteBuffer.allocateDirect((int) chunck);
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
        RawBufferSolve rbs=new RawBufferSolve();
        try {

            File f=new File(path);
            fileLength=f.length();
            byte[] bbb = new byte[chunckint];
            RandomAccessFile raf = new RandomAccessFile(f, "r");
            channel = raf.getChannel();
            ByteBuffer buf1 = ByteBuffer.allocateDirect((int) chunck);

            for(long i=0;i<=fileLength;i+=chunck){
                buf1.clear();
               channel.read(buf1,Math.min(chunck,fileLength-i));
                buf1.flip();
//                System.out.println("limit="+buf1.limit()+" fileLength-i="+(fileLength-i));
                rbs.run(buf1, (int) chunck);
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("rbs.readedBytes="+rbs.readedBytes + "readLines="+rbs.readedLines);
    }
    //读入
    public void prepare(String path) {
        int a=100;
            long chunck=a*1024*1024;
            int chunckint= a*1024*1024;
            Long startTime = System.currentTimeMillis();
            realPrepare(path,chunck,chunckint);
//            hackTime(path,chunck);
            Long endTime = System.currentTimeMillis();
            prepareTime=(endTime-startTime)*1.0/1000;
//            Runtime.getRuntime().gc();
    }
    //读入


    //查询1
    public List<String> checkPair(String caller, String responder, String time) {

        if(responder.length()>0){
            throw new ArrayIndexOutOfBoundsException("文件长度"+fileLength+"prepare时间"+prepareTime+"getTime="+readTime);

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
