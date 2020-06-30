package com.kuaishou.kcode;

import java.util.concurrent.CountDownLatch;

public final class BufferAndCountDownPayload {
    BufferAndCountDownPayload(byte[] buffer, int startIndex, int endIndex, CountDownLatch countDown, CheckPairPayLoad[][] cacheCheckPair, CheckResponderPayLoad[] cacheCheckResponder) {
        this.buffer = buffer;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.countDown = countDown;
        this.cacheCheckPair = cacheCheckPair;
        this.cacheCheckResponder = cacheCheckResponder;
    }

    byte[] buffer;
    int startIndex;
    int endIndex;
    CountDownLatch countDown;
    CheckPairPayLoad[][] cacheCheckPair;
    CheckResponderPayLoad[] cacheCheckResponder;
}
