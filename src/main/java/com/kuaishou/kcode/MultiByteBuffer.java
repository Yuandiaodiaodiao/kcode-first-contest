package com.kuaishou.kcode;

import java.nio.ByteBuffer;

public class MultiByteBuffer {
    byte[][] bytebuffer;
    int MAXSIZE=Integer.MAX_VALUE;
    MultiByteBuffer(MultiByteBuffer b){
        this.bytebuffer=b.bytebuffer;
    }
    MultiByteBuffer(){
    }
    public final void allocate(){
        bytebuffer=new byte[4][];
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

    public final void copyOldToNew(int start ,int length){
        int lastoffset=offset;
        nextOffset();
        System.arraycopy(bytebuffer[lastoffset],start,bytebuffer[offset],0,length);
        position=length;
    }
    public final ByteBuffer warpByteBuffer(int start,int length){
        ByteBuffer b=ByteBuffer.wrap(bytebuffer[offset],start,length);
        b.position(length);
        b.flip();
        return b;
    }
    public final int canRead(){
        return MAXSIZE-position;
    }
    public final void eatByteBuffer(ByteBuffer buf) {
        buf.get(bytebuffer[offset],position,buf.remaining());
        position+=buf.remaining();
    }
    public final void eatByteBuffer(ByteBuffer buf,int length) {
        buf.get(bytebuffer[offset],position,length);
        position+=length;
    }
    public final void nextOffset(){

        offset=(offset+1)%BUFFNUM;
        position=0;
    }

}
