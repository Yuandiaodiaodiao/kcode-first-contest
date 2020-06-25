package com.kuaishou.kcode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Date;

public class SytemBash {

    public static void main(String[] args) {
    }
    public static String getAllInfo(){
        String s="";
//        s+=getInfo("cat /etc/issue");
        s+=getInfo("cat /proc/cpuinfo | grep name | cut -f2 -d: | uniq -c");
//        s+=getInfo("gcc -v");
//        s+=getInfo("g++ -v");
//        s+=getInfo("java -version");
//        s+=getInfo("curl www.baidu.com");
//        s.replace("\n","");
        return s;
    }
    public static String getInfo(String cmd) {
        try {

            Process p = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", cmd});
//            Process p = Runtime.getRuntime().exec("javac");
            InputStream is = p.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            String ans="";
            while ((line = reader.readLine()) != null) {
                ans+=line;
            }
            p.waitFor();
            is.close();
            reader.close();
            p.destroy();
            return ans;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "";
    }
}
