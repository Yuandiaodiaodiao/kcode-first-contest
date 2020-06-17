package com.kuaishou.kcode;

import static com.kuaishou.kcode.KcodeUtils.createCheckPairMap;
import static com.kuaishou.kcode.KcodeUtils.createCheckResponderMap;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author kcode
 * Created on 2020-06-01
 * 该评测程序主要便于选手在本地优化和调试自己的程序
 */
public class KcodeRpcMonitorTest {
    public static void main(String[] args) throws Exception {
        KcodeRpcMonitor kcodeRpcMonitor = new KcodeRpcMonitorImpl();
        Long startTime = System.currentTimeMillis();
        kcodeRpcMonitor.prepare("D:\\Github\\KcodeRpcMonitor-master\\2kcodeRpcMonitor.data");
        Long endTime = System.currentTimeMillis();
        System.out.println("prepare耗时"+(endTime-startTime)/1000);
        // 读取checkPair.result文件
        Map<CheckPairKey, Set<String>> checkPairMap = createCheckPairMap("D:\\Github\\KcodeRpcMonitor-master\\checkPair.result");

        // 读取checkResponder.result文件
        Map<CheckResponderKey, String> checkResponderMap = createCheckResponderMap("D:\\Github\\KcodeRpcMonitor-master\\checkResponder.result");

        // 评测checkPair
        checkPair(kcodeRpcMonitor, checkPairMap);

        // 评测checkResponder
        checkResponder(kcodeRpcMonitor, checkResponderMap);

    }

    public static void checkPair(KcodeRpcMonitor kcodeRpcMonitor, Map<CheckPairKey, Set<String>> checkPairMap) {
        int checkPairTime = 100000;
        long cast = 0L;
        while (true) {
            long now;
            for (Map.Entry<CheckPairKey, Set<String>> entry : checkPairMap.entrySet()) {
                CheckPairKey key = entry.getKey();
                now = System.currentTimeMillis();
                List<String> result = kcodeRpcMonitor.checkPair(key.getCaller(), key.getResponder(), key.getTime());
                cast += (System.currentTimeMillis() - now);
                Set<String> checkResult = entry.getValue();
                if (Objects.isNull(result) || checkResult.size() != result.size()) {
                    System.out.println("key:" + key + ", result:" + result + ", checkResult:" + checkResult);
                    throw new RuntimeException("评测结果错误");
                }
                if (result.size() != 0) {
                    if (!checkResult.containsAll(result)) {
                        System.out.println("key:" + key + ", result:" + result + ", checkResult:" + checkResult);
                        throw new RuntimeException("评测结果错误");
                    }
                }
                if (checkPairTime-- <= 0) {
                    System.out.println("checkPair 结束, cast:" + cast);
                    return;
                }
            }
        }
    }

    public static void checkResponder(KcodeRpcMonitor kcodeRpcMonitor, Map<CheckResponderKey, String> checkResponderMap) {
        int checkResponderTime = 100000;
        long cast = 0L;
        while (true) {
            long now;
            for (Map.Entry<CheckResponderKey, String> entry : checkResponderMap.entrySet()) {
                CheckResponderKey key = entry.getKey();
                now = System.currentTimeMillis();
                String result = kcodeRpcMonitor.checkResponder(key.getName(), key.getStartTime(), key.getEndTime());
                cast += (System.currentTimeMillis() - now);
                String checkResponderResult = entry.getValue();
                if (Objects.isNull(result) || !checkResponderResult.equals(result)) {
                    System.out.println("key:" + key + ", result:" + result + ", checkResult:" + checkResponderResult);
                    throw new RuntimeException("评测结果错误");
                }
                if (checkResponderTime-- <= 0) {
                    System.out.println("checkResponder 结束, cast:" + cast);
                    return;
                }
            }
        }
    }
}
