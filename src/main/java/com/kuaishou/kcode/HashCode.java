package com.kuaishou.kcode;

public class HashCode {
    public static int hash(String str){
        int len = str.length();
        int numberIndex = 0;
        int hashcode1 = 0;
        int hashcode2 = 0;
        int i = 0;
        char c;
        for (; i < len; ++i) {
            c = str.charAt(i);
            if (i <= 6) {
                hashcode1 =c + hashcode1;
            } else if (c <= '9') {
                numberIndex = i;
                break;
            }
        }

        for (; i < len; ++i) {
            c=str.charAt(i);
            if (i - numberIndex >= 2) {
                hashcode2 =  (((int)c)<<3)+ hashcode2;
            } else {
                hashcode2 = c + (hashcode2<<5);
            }
        }
        return ((hashcode1 % 70)<<5) + (hashcode2 % 71);
    }
    public static int hash3(String str) {
        int len = str.length();
        int numberIndex = 0;
        int hashcode1 = 0;
        int hashcode2 = 0;
        int i = 0;
        char c;
        for (; i < len; ++i) {
            c = str.charAt(i);
            if (i <= 6) {
                hashcode1 = c * 31 + hashcode1;
            } else if (c <= '9') {
                numberIndex = i;
                break;
            }
        }

        for (; i < len; ++i) {
            if (i - numberIndex >= 2) {
                hashcode2 =  hashcode2 * 5;
            } else {
                hashcode2 = str.charAt(i) + hashcode2 * 10;
            }
        }
        return (hashcode1 % 99) * 100 + (hashcode2 % 99);
    }
    public static int hashTwoString(String str1,String str2){
        return (hash2(str1)*10000+hash2(str2))%9933;
    }
    public static int hash2(String str) {
        int len = str.length();
        int numberIndex = 0;
        int hashcode1 = 0;
        int hashcode2 = 0;
        int i = 0;
        char c;
        for (; i < len; ++i) {
            c = str.charAt(i);
            if (i <= 5) {
                hashcode1 = c * 20 + hashcode1;
            } else if (c <= '9') {
                numberIndex = i;
                break;
            }
        }

        for (; i < len; ++i) {
            if (i - numberIndex >= 2) {
                hashcode2 =  hashcode2 * 5;
            } else {
                hashcode2 = str.charAt(i) + hashcode2 * 10;
            }
        }
        return (hashcode1 % 99) * 100 + (hashcode2 % 99);
    }
}
