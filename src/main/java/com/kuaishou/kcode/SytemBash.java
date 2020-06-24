package com.kuaishou.kcode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Date;

public class SytemBash {

    public static void main(String[] args) {
        getInfo();
    }

    public static String getInfo() {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{ "cmd", "/c", "dir"});
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
