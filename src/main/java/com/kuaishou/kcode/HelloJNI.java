package com.kuaishou.kcode;

/**
 * Created by HuaChao on 2016/12/29.
 */
public class HelloJNI {
    static {
        // hello.dll (Windows) or libhello.so (Unixes)
        System.loadLibrary("hello");
    }

    private native void sayHello();

    public static void main(String[] args) {

        new HelloJNI().sayHello();  // invoke the native method
    }

}
