package com.example.onlinemusicapp;

import static org.junit.Assert.*;

import org.junit.Test;

public class MainActivityTest {

    @Test
    public void helloTessting()
    {
        String helllo="hello";
        int num=MainActivity.helloTESTING(helllo);
        assertEquals(1,num);
    }
}