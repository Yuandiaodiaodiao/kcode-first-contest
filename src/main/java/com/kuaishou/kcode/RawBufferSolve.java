package com.kuaishou.kcode;

import java.nio.ByteBuffer;
import java.util.HashMap;

public class RawBufferSolve {
    private byte[] byteBuff;
    private int nowTime = 0;
    public int byteLinePos = 0;
    public String lastStr;
    public byte[] byteLine = new byte[100];
    public byte[] byteLineLast = new byte[100];
    public byte[] service1 = new byte[40];
    public byte[] service2 = new byte[40];
    public long readedBytes = 0l;
    public int readedLines = 0;
    private HashMap<String, HashMap<String, HashMap<Long, HashMap<Long, HashMap<Long, Long>>>>> hashM = new HashMap<String, HashMap<String, HashMap<Long, HashMap<Long, HashMap<Long, Long>>>>>();

    public RawBufferSolve() {

    }

    private void printArray(byte[] input, int limit) {
        for (int a = 0; a < limit && input[a] != '\n'; ++a) {
            printByte(input[a]);
        }
        System.out.println();
    }
    private void printByte(byte input){
        System.out.print((char)input);
    }
    public void run(ByteBuffer inputB, int limit) {


        byte b;
        boolean out=false;
        if(byteLinePos==57)out=true;
        while(inputB.hasRemaining()) {
            readedBytes += 1;
            b = inputB.get();
            if(out){
                printByte(b);
            }
//            System.out.print((char)b);
            byteLine[byteLinePos++] = b;
            if (b == '\n') {
                //solve
                readedLines += 1;
                solveSentense(byteLine, limit);
                System.arraycopy(byteLine, 0, byteLineLast, 0, byteLinePos + 1);
                byteLineLast[byteLinePos] = '\n';
                //solveEnd
                byteLinePos = 0;
            }
        }
        if (byteLinePos == 57) {
            printArray(byteLineLast, 100);
            printArray(byteLine, byteLinePos);
        }
        System.out.println("读完一轮 pos="+byteLinePos);

    }

    public void solveSentense(byte[] input, int limit) {
        int readIndex = 0;
        byte b;

        int ip1 = 0;
        int ip2 = 0;
        b = input[readIndex];
        for (int strIndex = 0; b != ','; b = input[++readIndex]) {
            service1[strIndex++] = b;
        }

        int numBuff = 0;
        for (b = input[++readIndex]; b != ','; b = input[++readIndex]) {
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

        b = input[++readIndex];
        for (int strIndex = 0; b != ','; b = input[++readIndex]) {
            service2[strIndex++] = b;


        }

        for (b = input[++readIndex]; b != ','; b = input[++readIndex]) {
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

        b = input[++readIndex];
        boolean success = false;
        if (b == 't') {
            success = true;
            readIndex += 4;

        } else {
            readIndex += 5;
        }
        int useTime = 0;
        for (b = input[++readIndex]; b != ','; b = input[++readIndex]) {
            useTime *= 10;
            useTime += (b - '0');
        }
        int minTime = 0;
        b = input[++readIndex];

        for (int timepos = 1; timepos <= 10; ++timepos, b = input[++readIndex]) {
            minTime *= 10;
            minTime += (b - '0');
        }
        int timec = minTime;
        minTime /= 60;
        if (minTime > nowTime) {
            nowTime = minTime;
//            System.out.println(timec);
        } else if (minTime == nowTime) {

        } else {
            String outstr = " ip1= " + ip1 + " ip2= " + ip2 + " " + (new String(service1)) + " " + (new String(service2)) + " " + useTime + " " + minTime;

            System.out.println("逆序!!!!" + " 上一个是 " + lastStr);
            System.out.println(outstr);

            throw new Error("逆序");
        }
//        lastStr=outstr;


    }
}
