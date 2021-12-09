package com.godbot;

public class Test {
    public static void main(String[] args) {
        String test  = "%s %s";

        String.format(test, "hello world");
        String.format(test, "my man");

        System.out.println(test);
    }
}
