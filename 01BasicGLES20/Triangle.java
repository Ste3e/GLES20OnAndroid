package com.sjonesart.GL1;

//Copyright 2013 Stephen K Jones
//All Rights Reserved

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Triangle {
    private MainShader shader;
    private int vbo;

    public Triangle(MainShader shader) {
        this.shader = shader;
        float[] verts = {
                -0.9f, -0.9f, 0.0f,
                0.9f, -0.9f, 0.0f,
                0.9f, 0.9f, 0.0f
        };

        int floatSz = 4;
        int tot = verts.length * floatSz;
        FloatBuffer v = ByteBuffer.allocateDirect(tot).order(ByteOrder.nativeOrder()).asFloatBuffer();
        v.put(verts).position(0);

        final int tmp[] = new int[1];
        GLES20.glGenBuffers(1, tmp, 0);
        vbo = tmp[0];
        if(vbo < 1) {
            String er = "" + vbo;
            Log.e("VBO ERROR: ", er);
        }
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, tot, v, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    public void draw() {
        GLES20.glUseProgram(shader.shad);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo);
        GLES20.glEnableVertexAttribArray(shader.posLoc);
        GLES20.glVertexAttribPointer(shader.posLoc, 3, GLES20.GL_FLOAT, false, 0, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);

        GLES20.glDisableVertexAttribArray(shader.posLoc);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }
}