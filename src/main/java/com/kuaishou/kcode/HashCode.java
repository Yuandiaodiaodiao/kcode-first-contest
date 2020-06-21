package com.kuaishou.kcode;

public class HashCode {
    public static int hashByte(byte[] b1, int len) {
        int numberIndex = 0;
        int hashcode1 = 0;
        int hashcode2 = 0;
        int i = 0;
        byte c;
        hashcode1 = b1[0];
        for (; i < len; ++i) {
            if (b1[i] <= '9') {
                numberIndex = i;
                break;
            }
        }

        for (; i < len; ++i) {
            c = b1[i];
            if (i - numberIndex >= 2) {
                hashcode2 = (((int) c) << 1) + (hashcode2 << 3);
            } else {
                hashcode2 = c + (hashcode2 << 6);
            }
        }
        return ((hashcode1 % 30) << 5) + (hashcode2 % 75);
    }

    public static int hash(String str) {
        int len = str.length();
        int numberIndex = 0;
        int hashcode2 = 0;
        int i = 0;
        char c = str.charAt(0);
        int hashcode1 = c;

        for (; i < len; ++i) {
            if (str.charAt(i) <= '9') {
                numberIndex = i;
                break;
            }
        }

        for (; i < len; ++i) {
            c = str.charAt(i);
            if (i - numberIndex >= 2) {
                hashcode2 = (((int) c) << 1) + (hashcode2 << 3);
            } else {
                hashcode2 = c + (hashcode2 << 6);
            }
        }
        return ((hashcode1 % 30) << 5) + (hashcode2 % 75);
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
                hashcode2 = hashcode2 * 5;
            } else {
                hashcode2 = str.charAt(i) + hashcode2 * 10;
            }
        }
        return (hashcode1 % 99) * 100 + (hashcode2 % 99);
    }

    public static int hashTwoString2(String str1, String str2) {
        return (hash2(str1) * 10000 + hash2(str2)) % 5109;
    }

    public static int hashTwoString3(String str1, String str2) {
        return ((hash4(str1) << 11) + (hash4(str2))) % 5000;
    }

    public static int hashTwoString(String str1, String str2) {
        return ((hash5(str1) << 4) + (hash5(str2))) % 4999;
    }


    public static int hashTwoByte3(byte[] b1, int len1, byte[] b2, int len2) {
        return ((hash4byte(b1, len1) << 11) + (hash4byte(b2, len2))) % 5000;
    }
    public static int hashTwoByte(byte[] b1, int len1, byte[] b2, int len2) {
        return ((hash5byte(b1, len1) << 4) + (hash5byte(b2, len2))) % 4999;
    }
    public static int hash5byte(byte[] b1, int len) {
        int hashcode2 = 0;
        int hashcode1 = b1[0];
        int c;
        while (true) {
            c=b1[--len];
            if(c=='e'){
                break;
            }
            hashcode2=(c << 3) + (hashcode2 << 2);

        }
        return ((hashcode2 % 97) << 6) + (hashcode1 % 26);
    }

    public static int hash4byte(byte[] b1, int len) {

        int numberIndex = 0;
        int hashcode1 = 0;
        int hashcode2 = 0;
        int i = 0;
        byte c;
        for (; i < len; ++i) {
            c = b1[i];
            if (i <= 7) {
                hashcode1 = c + hashcode1;
            } else if (c <= '9') {
                numberIndex = i;
                break;
            }
        }

        for (; i < len; ++i) {
            c = b1[i];
            if (i - numberIndex >= 2) {
                hashcode2 = c + hashcode2;
            } else {
                hashcode2 = c + hashcode2;
            }
        }
        return ((hashcode1 % 78) << 1) + (hashcode2 % 93);
    }

    public static int hash5(String str) {
        int len = str.length();
        int hashcode2 = 0;
        int hashcode1 = str.charAt(0);
        char c;
        while (true) {
            c=str.charAt(--len);
            if(c=='e'){
                break;
            }
            hashcode2=(((int) c) << 3) + (hashcode2 << 2);

        }
        return ((hashcode2 % 97) << 6) + (hashcode1 % 26);
    }

    public static int hash4(String str) {
        int len = str.length();
        int numberIndex = 0;
        int hashcode1 = 0;
        int hashcode2 = 0;
        int i = 0;
        char c;
        for (; i < len; ++i) {
            c = str.charAt(i);
            if (i <= 7) {
                hashcode1 = c + hashcode1;
            } else if (c <= '9') {
                numberIndex = i;
                break;
            }
        }

        for (; i < len; ++i) {
            c = str.charAt(i);
            if (i - numberIndex >= 2) {
                hashcode2 = c + hashcode2;
            } else {
                hashcode2 = c + hashcode2;
            }
        }
        return ((hashcode1 % 78) << 1) + (hashcode2 % 93);
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
            if (i <= 7) {
                hashcode1 = c * 17 + hashcode1;
            } else if (c <= '9') {
                numberIndex = i;
                break;
            }
        }

        for (; i < len; ++i) {
            c = str.charAt(i);
            if (i - numberIndex >= 2) {
                hashcode2 = c * 3 + hashcode2 * 8;
            } else {
                hashcode2 = str.charAt(i) + hashcode2 * 10;
            }
        }
        return (hashcode1 % 99) * 100 + (hashcode2 % 99);
    }
}
