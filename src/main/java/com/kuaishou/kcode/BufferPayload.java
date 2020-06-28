package com.kuaishou.kcode;

public class BufferPayload {
    int startPos;
    int length;
    MultiByteBuffer buf;
    BufferPayload(int startPos,int length,MultiByteBuffer b){
        this.startPos=startPos;
        this.length=length;

    }
    BufferPayload(){
        length=-1;
    }
}
