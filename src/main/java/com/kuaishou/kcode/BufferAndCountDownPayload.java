package com.kuaishou.kcode;

import java.util.concurrent.CountDownLatch;

public class BufferAndCountDownPayload {
    byte[] buffer;
    int startIndex;
    int endIndex;
    CountDownLatch countDown;
    CheckPairPayLoad[][] cacheCheckPair;
    CheckResponderPayLoad[] cacheCheckResponder;
}
