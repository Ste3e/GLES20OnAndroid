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
    private float[] camPos = {0, 0, 0};
    private float[] camMat = {1, 0, 0,  0, 1, 0,  0, 0, 1};
    private float[] gameDat = {1, 0, 0,  0, 1, 0,  0, 0, 1, 0, 0, 0};

    public Game(Exchanger<float[]> ex){
        this.ex = ex;
    }

    @Override
    public void onSurfaceCreated(GL10 nil, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClearDepthf(1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);
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
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUseProgram(shader.shad);
        try{
            gameDat = ex.exchange(gameDat);
            camMat[0] = gameDat[0]; camMat[1] = gameDat[1]; camMat[2] = gameDat[2];
            camMat[3] = gameDat[3]; camMat[4] = gameDat[4]; camMat[5] = gameDat[5];
            camMat[6] = gameDat[6]; camMat[7] = gameDat[7]; camMat[8] = gameDat[8];
            camPos[0] = gameDat[9];
            camPos[1] = gameDat[10];
            camPos[2] = gameDat[11];
        }catch(InterruptedException e){
            Log.d("Exchanger read error", e.toString());
        }

        GLES20.glUniform3fv(shader.cPosLoc, 1, camPos, 0);
        GLES20.glUniformMatrix3fv(shader.cMatLoc, 1, false, camMat, 0);

        m.draw();

        GLES20.glUseProgram(0);
    }


}


