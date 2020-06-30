package com.kuaishou.kcode;

import java.util.concurrent.ArrayBlockingQueue;

public final class SolveMinuteByteBufferCoreThread extends Thread {

    private final ArrayBlockingQueue<BufferAndCountDownPayload> payloadQueue=new ArrayBlockingQueue<>(4);
    public void execute(BufferAndCountDownPayload payload){
        payloadQueue.offer(payload);
    }

    @Override
    public void run() {
        super.run();
        try {
            while (true) {
                BufferAndCountDownPayload queuepayload = payloadQueue.take();
                int position = queuepayload.startIndex;
                int limit = queuepayload.endIndex;
                byte[] byteArray = queuepayload.buffer;
                Byte b;
                CheckPairPayLoad[][] cacheCheckPair = queuepayload.cacheCheckPair;
                CheckResponderPayLoad[] cacheCheckResponder = queuepayload.cacheCheckResponder;
                while (position < limit) {
//                    if (b == 10) continue;
                    long ip1 = 0;
                    long ip2 = 0;

                    int hashService1hash1 = byteArray[position];
//                    f.position(f.position() + 10);
                    position += 10;
                    while (byteArray[++position] != 44) {
                    }
                    int hashService1 = ((((byteArray[position - 5] + (byteArray[position - 4] << 2) +
                            (byteArray[position - 3] << 6) + (byteArray[position - 2] << 13) + (byteArray[position - 1] << 17)) % 69) << 12) +
                            ((hashService1hash1 - 97) << 8));


                    int numBuff = 0;

                    while ((b = byteArray[++position]) != 44) {
                        ip1 = (b != 46) ? ip1 : (ip1 << 8) + numBuff;
                        numBuff = (b != 46) ? (b - 48) + numBuff * 10 : 0;
                    }

                    ip1 = (ip1 << 8) + numBuff;

                    int hashService2hash1 = byteArray[++position];
                    position += 10;
                    while (byteArray[++position] != 44) {
                    }

                    int hashService2 = ((hashService2hash1 - 97) +
                            ((((byteArray[position - 6]) + (byteArray[position - 5] << 5) +
                                    (byteArray[position - 4] << 10) + (byteArray[position - 3] << 14) +
                                    (byteArray[position - 2] << 15) + (byteArray[position - 1] << 24)) % 89) << 3));

                    numBuff = 0;

                    while ((b = byteArray[++position]) != 44) {
                        ip2 = (b != 46) ? ip2 : (ip2 << 8) + numBuff;
                        numBuff = (b != 46) ? (b - 48) + numBuff * 10 : 0;
                    }

                    ip2 = (ip2 << 8) + numBuff;


                    int success = (byteArray[++position] == 116 ? 0 : 1);
                    position += 4 + success;


                    int useTime = 0;
                    while ((b = byteArray[++position]) != 44) {
                        useTime = (b - 48) + useTime * 10;
                    }


                    position += 15;
                    int stringHash = (hashService1 + hashService2) % 4999;


                    int ipHash = (int) ((((ip1 - 167772160) % 3457) << 9) + ((ip2 - 167772160) % 2833)) % 2551;

                    CheckPairPayLoad payload = cacheCheckPair[stringHash][ipHash];
                    if (payload == null) {
                        payload = new CheckPairPayLoad();
                        cacheCheckPair[stringHash][ipHash] = payload;
                        payload.ip = (ip1 << 32) + ip2;
                    }
                    //change payload

                    payload.successTimes += success ^ 1;
                    //1^1 =0 0^1 =1
                    payload.failedTimes += success;
                    payload.bucket[useTime] += 1;


                    CheckResponderPayLoad payload2 = cacheCheckResponder[hashService2];

                    payload2.success += success ^ 1;
                    payload2.failed += success;


                }
                queuepayload.countDown.countDown();
            }
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }
}
