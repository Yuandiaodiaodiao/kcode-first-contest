package com.kuaishou.kcode;

import java.util.concurrent.CountDownLatch;

public final class BufferAndCountDownPayload {
    BufferAndCountDownPayload(byte[] buffer,int startIndex,CountDownLatch countDown,CheckPairPayLoad[][] cacheCheckPair,CheckResponderPayLoad[] cacheCheckResponder){

    }
    byte[] buffer;
    int startIndex;
    int endIndex;
    CountDownLatch countDown;
    CheckPairPayLoad[][] cacheCheckPair;
    CheckResponderPayLoad[] cacheCheckResponder;
}
