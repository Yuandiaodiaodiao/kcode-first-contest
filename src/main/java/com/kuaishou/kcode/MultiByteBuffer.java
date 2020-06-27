package com.kuaishou.kcode;

import java.nio.ByteBuffer;

public class MultiByteBuffer {
    byte[][] bytebuffer=new byte[4][];
    int MAXSIZE=Integer.MAX_VALUE;
    MultiByteBuffer(){

    }
    public final void allocate(){
        for(int i=0;i<BUFFNUM;++i){
            bytebuffer[i]=new byte[MAXSIZE];
        }
    }
    int BUFFNUM=2;
    int offset=0;
    int position=0;
    public final byte get(long i) {
        if(i>MAXSIZE){
            System.out.println("MultiByteBuffer溢出");
        }
        return bytebuffer[offset][(int)i];
    }

    public final void eatByteBuffer(ByteBuffer buf) {
        buf.get(bytebuffer[offset],position,buf.remaining());
        position+=buf.remaining();
    }
    public final void nextOffset(){
        offset=(offset+1)%BUFFNUM;
    }

}
