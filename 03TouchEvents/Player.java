package com.sjonesart.GL1;

//Copyright 2013 Stephen K Jones
//All Rights Reserved

import android.renderscript.Float2;
import android.renderscript.Float4;
import android.util.Log;
import java.util.concurrent.Exchanger;

public class Player {
    private Exchanger<float[]> ex;
    private float[] gameDat = {1, 0, 0,  0, 1, 0,  0, 0, 1, 0, 0, 0};
    private float[] dummy = {1, 0, 0,  0, 1, 0,  0, 0, 1, 0, 0, 0};
    private Float4 lPos;
    private boolean ford = false, back = false, left = false, right = false;
    private boolean zoomIn = false, zoomOut = false;
    public Float2[] dat;

    public Player(Exchanger<float[]> ex){
        this.ex = ex;
        lPos = new Float4(75, 225, 475, 625); //left thumb bounds
        dat = new Float2[5];
        dat[0] = new Float2(-1, -1); dat[1] = new Float2(-1, -1); dat[2] = new Float2(-1, -1); dat[3] = new Float2(-1, -1); dat[4] = new Float2(-1, -1);
    }

    public void update(int touches){
        boolean stopped = true;

        for(int i = 0; i < touches; i++){
            if(dat[i].x > lPos.x && dat[i].x < lPos.y && dat[i].y > lPos.z && dat[i].y < lPos.w){
                float x = dat[i].x - 150;
                float y = 550 - dat[i].y;
                left = x < 0; right = x > 0; ford = y > 0; back = y < 0;
                stopped = false;
            }else{
                Log.e("OTHER", "Another finger down");
            }
        }

        if(stopped){
            left = right = ford = back = false;
        }
    }

    public void tick(){
        if(left) gameDat[9] += 0.05f;
        if(right) gameDat[9] -= 0.05f;
        if(ford) gameDat[10] -= 0.05f;
        if(back) gameDat[10] += 0.05f;
        try{
            dummy = ex.exchange(gameDat);
        }catch(InterruptedException e){
            Log.d("Exchanger send error", e.toString());
        }
    }
}
