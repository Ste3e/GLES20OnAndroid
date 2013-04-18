package com.sjonesart.GL1;

//Copyright 2013 Stephen K Jones
//All Rights Reserved

import android.renderscript.Float2;
import android.renderscript.Float4;
import android.util.Log;
import java.util.concurrent.Exchanger;

public class Player {
    private float offset = 75;
    private Exchanger<float[]> ex;
    public Float2[] dat;
    private float[] gameDat = {1, 0, 0,  0, 1, 0,  0, 0, 1, 0, 0, 0};
    private float[] dummy = {1, 0, 0,  0, 1, 0,  0, 0, 1, 0, 0, 0};
    private float cosh = 1, sinh = 0, cosp = 1, sinp = 0;
    private Float4 lPos, rPos;
    private boolean ford = false, back = false, lstrf = false, rstrf = false;
    private boolean left = false, right = false, up = false, down = false;
    private final float walk = 0.25f, strafe = 0.2f, hspeed = 0.01f, pspeed = 0.01f;
    private float head = 0.0f, pitch = 0.0f, dist = 0.0f, strf = 0.0f;
    private final float maxPitch = 0.4f;
    private final float pi2 = (float)Math.PI * 2;

    public Player(Exchanger<float[]> ex, Float2 lThumb, Float2 rThumb){
        this.ex = ex;
        lPos = new Float4(lThumb.x - offset, lThumb.x + offset, lThumb.y - offset, lThumb.y + offset); //left thumb bounds
        rPos = new Float4(rThumb.x - offset, rThumb.x + offset, rThumb.y - offset, rThumb.y + offset); //right thumb bounds
        dat = new Float2[5];
        dat[0] = new Float2(0, 0); dat[1] = new Float2(0, 0); dat[2] = new Float2(0, 0); dat[3] = new Float2(0, 0); dat[4] = new Float2(0, 0);
    }

    public void update(int touches){
        boolean stopped = true;
        boolean notRot = true;

        for(int i = 0; i < touches; i++){
            if(dat[i].x > lPos.x && dat[i].x < lPos.y && dat[i].y > lPos.z && dat[i].y < lPos.w){
                float x = dat[i].x - 150;
                float y = 550 - dat[i].y;
                lstrf = x < -25; rstrf = x > 25; ford = y > 25; back = y < -25;
                stopped = false;
            }
            if(dat[i].x > rPos.x && dat[i].x < rPos.y && dat[i].y > rPos.z && dat[i].y < rPos.w){
                float x = dat[i].x - 1130;
                float y = 550 - dat[i].y;
                left = x < -25; right = x > 25; up = y > 25; down = y < -25;
                notRot = false;
            }
        }

        if(stopped){
            lstrf = rstrf = ford = back = false;
        }
        if(notRot){
            left = right = up = down = false;
        }
    }

    public void tick(){
        if(ford) dist = -walk;
        if(back) dist = walk;
        if(lstrf) strf = -strafe;
        if(rstrf) strf = strafe;
        if(left){
            if(head < -pi2) head += pi2;
            head -= hspeed;
        }
        if(right){
            if(head > pi2) head -= pi2;
            head += hspeed;
        }
        if(up){
            if(pitch > maxPitch) return;
            pitch += pspeed;
        }
        if(down){
            if(pitch < -maxPitch) return;
            pitch -= pspeed;
        }

        if(left || right){
            cosh = (float)Math.cos(head);
            sinh = (float)Math.sin(head);
        }

        gameDat[9] += strf * cosh;
        gameDat[9] += dist * -sinh;
        gameDat[11] += strf * sinh;
        gameDat[11] += dist * cosh;
        gameDat[10] = 3.0f;
        dist = 0.0f; strf = 0.0f;

        if(up || down){
            cosp = (float)Math.cos(pitch);
            sinp = (float)Math.sin(pitch);
        }

        gameDat[0] = cosh;
        gameDat[1] = 0;
        gameDat[2] = sinh;
        gameDat[3] = -sinh * sinp;
        gameDat[4] = cosp;
        gameDat[5] = cosh * sinp;
        gameDat[6] = -sinh * cosp;
        gameDat[7] = -sinp;
        gameDat[8] = cosh * cosp;

        try{
            dummy = ex.exchange(gameDat);
        }catch(InterruptedException e){
            Log.d("Exchanger send error", e.toString());
        }
    }
}
