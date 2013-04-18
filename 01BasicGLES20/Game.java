package com.sjonesart.GL1;

//Copyright 2013 Stephen K Jones
//All Rights Reserved

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

public class Game implements GLSurfaceView.Renderer{
    private MainShader shader;
    private Triangle tri;

    @Override
    public void onSurfaceCreated(GL10 nil, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        shader = new MainShader();
        tri = new Triangle(shader);
    }

    @Override
    public void onDrawFrame(GL10 nil) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        tri.draw();
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }
}


