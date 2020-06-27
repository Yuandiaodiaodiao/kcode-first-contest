package com.kuaishou.kcode;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.nio.*;
import java.util.concurrent.ConcurrentHashMap;


public class SuperByteBuffer {
    public static Unsafe unsafe;

    static {
        Field f = null;
        try {
            f = ConcurrentHashMap.class.getDeclaredField("U");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        f.setAccessible(true);
        try {
            unsafe = (Unsafe) f.get(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
//        long address=unsafe.allocateMemory((long)2*4096*1024*1024);
//            for(long a=0;a<2L*4096*1024*1024;++a){
//                unsafe.putByte(address + a, (byte) a);
////                unsafe.
//            }
    }

    long address;
    public long position=0;
    public long capacity;
    SuperByteBuffer(long cap) {
        this.capacity=cap;
        int ps = unsafe.pageSize();
        long size = Math.max(1L, cap + ps);


        long base = 0;
        try {
            base = unsafe.allocateMemory(size);
        } catch (OutOfMemoryError x) {
            throw x;
        }
//        unsafe.setMemory(base, size, (byte) 0);
        if ((base % ps != 0)) {
            // Round up to page boundary
            address = base + ps - (base & (ps - 1));
        } else {
            address = base;
        }
    }

    public final void prepareMemory(){
        unsafe.setMemory(address,capacity-1, (byte) 0);
    }
    public final byte get(long i) {
        return unsafe.getByte(address + i);
    }

    public final void put(long i, byte b) {
        unsafe.putByte(address + i, b);
    }
    public final void eatByteBuffer(ByteBuffer buf,long length) {
        long end = position + length;
        for (long i = position; i < end; i++)
            unsafe.putByte(address + i, buf.get());
        position=end;
    }
    public final void eatByteBuffer(ByteBuffer buf,long offset,long length) {
        long end = offset + length;
        for (long i = offset; i < end; i++)
            unsafe.putByte(address + i, buf.get());
    }
    public final void putByteBuffer(ByteBuffer buf, long offset, long length) {
        long end = offset + length;
        for (long i = offset; i < end; i++)
            buf.put(unsafe.getByte(address + i));
    }


}
