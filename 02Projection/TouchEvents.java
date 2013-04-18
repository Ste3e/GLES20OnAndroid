package com.sjonesart.GL1;

//Copyright 2013 Stephen K Jones
//All Rights Reserved

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

class TouchEvents extends GLSurfaceView {
    private Game game;

    public TouchEvents(Context context) {
        super(context);

        this.setEGLContextClientVersion(2);
        this.setEGLConfigChooser(8, 8, 8, 0, 16, 0);

        game = new Game();
        setRenderer(game);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        return true;
    }
}