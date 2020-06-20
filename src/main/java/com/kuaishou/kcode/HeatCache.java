package com.kuaishou.kcode;

import java.util.ArrayList;

public class HeatCache {
    public static void HeatCheckPair(){
        for(int i=0;i<4999;++i){
            for(int j=0;j<32;++j){
                ArrayList<String> s=PrepareMultiThreadDataCore.hashCheckPairArray[i][j];
            }
        }

    }
}
