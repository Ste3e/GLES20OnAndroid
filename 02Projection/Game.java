package com.sjonesart.GL1;

//Copyright 2013 Stephen K Jones
//All Rights Reserved

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import java.util.concurrent.Exchanger;

public class Game implements GLSurfaceView.Renderer{
    private Exchanger<float[]> ex;
    private MainShader shader;
    private Model m;

    @Override
    public void onSurfaceCreated(GL10 nil, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);
        GLES20.glFrontFace(GLES20.GL_CCW);

        shader = new MainShader();
        m = new Model(shader);
    }
    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        shader.setupProjection(width, height);
    }

    @Override
    public void onDrawFrame(GL10 nil) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glUseProgram(shader.shad);

        m.draw();

        GLES20.glUseProgram(0);
    }
}


