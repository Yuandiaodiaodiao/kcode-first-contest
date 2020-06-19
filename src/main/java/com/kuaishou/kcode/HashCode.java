package com.kuaishou.kcode;

public class HashCode {
    public static int hash(String str){
        int len=str.length();
        int numberIndex=0;
        int hashcode1=0;
        int hashcode2=0;
        for(int i=0;i<len;++i){
            char c=str.charAt(i);
            if(numberIndex==0){
                if(c<='9'){
                    numberIndex=i;
                    hashcode2=c;
                }else{
                    hashcode1=c*31+hashcode1;
                }
            }else {
                if(i-numberIndex>=2){
                    hashcode2=c*0+hashcode2*5;
                }else{
                    hashcode2=c+hashcode2*10;
                }
            }
        }
        return (hashcode1%99)*100+(hashcode2%99);
    }
    public static int hash2(String str){
        int len=str.length();
        int numberIndex=0;
        int hashcode1=0;
        int hashcode2=0;
        for(int i=0;i<len;++i){
            char c=str.charAt(i);
            if(numberIndex==0){
                if(c<='9'){
                    numberIndex=i;
                    hashcode2=c;
                }else{
                    hashcode1=c*31+hashcode1;
                }
            }else {
                if(i-numberIndex>=2){
                    hashcode2=c*4+hashcode2*2;
                }else{
                    hashcode2=c+hashcode2*10;
                }
            }
        }
        return (hashcode1%99)*100+(hashcode2%99);
    }
}
