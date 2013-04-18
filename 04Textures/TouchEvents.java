package com.sjonesart.GL1;

//Copyright 2013 Stephen K Jones
//All Rights Reserved

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Exchanger;

class TouchEvents extends GLSurfaceView {
    private Exchanger<float[]> ex;
    private Player player;
    private Game game;
    private Timer time;

    public TouchEvents(Context context) {
        super(context);

        this.setEGLContextClientVersion(2);
        this.setEGLConfigChooser(8, 8, 8, 0, 16, 0);

        ex = new Exchanger<float[]>();
        game = new Game(ex, context);
        player = new Player(ex);
        setRenderer(game);

        time = new Timer();
        time.schedule(new TimerTask(){
            @Override
            public void run(){
                player.tick();
            }
        }, 0, 33);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int ptrCount = e.getPointerCount();
        int i = 0;
        for(; i < ptrCount; i++){
            if(i > 4) break;
            player.dat[i].x = e.getX(i);
            player.dat[i].y = e.getY(i);
        }

        switch(e.getAction()){
            case MotionEvent.ACTION_UP:
                player.update(0);
                return true;
        }
        player.update(i);
        return true;
    }
}