package com.kuaishou.kcode;

import java.util.ArrayList;
import java.util.HashMap;

public class PrepareMultiThreadDataCore {
    public static HashMap[][] hashCheckPair =new HashMap[32][5001];
    public static ArrayList[][] hashCheckPairArray=new ArrayList[5001][32] ;
    public static ArrayList[] hashCheckPairArrayFlat=new ArrayList[160032+1] ;
    public static CheckResponderPayLoad[][] hashCheckResponder =new CheckResponderPayLoad[32][1200];
//    public static String[][][] CheckResponderPayLoadArray =new String[2000][][];
    public static String[][][] CheckResponderFastArray =new String[32][32][1200];
    public static String[] CheckResponderFastArrayFlat =new String[1048576+1];


}
