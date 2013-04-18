package com.sjonesart.GL1;

//Copyright 2013 Stephen K Jones
//All Rights Reserved

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Floor{
    private MainShader shader;
    private int tid;
    private int vbo, voff, coff, tot;
    private int vcount;
    public float[] pos = {0, -5, 0};

    public Floor(MainShader shader) {
        this.shader = shader;
        final float[] rawDat = {
                -90.0f, 0.0f, 90.0f, 90.0f, 0.0f, 90.0f, 90.0f, 0.0f, -90.0f,
                -90.0f, 0.0f, 90.0f, 90.0f, 0.0f, -90.0f, -90.0f, 0.0f, -90.0f,

                0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f
        };
        vcount = 6;
        voff = 0;
        coff = vcount * 3 * 4;//size of verts bytes
        tot = coff + vcount * 2 * 4;//size of coords bytes

        FloatBuffer dat = ByteBuffer.allocateDirect(tot).order(ByteOrder.nativeOrder()).asFloatBuffer();
        dat.put(rawDat).position(0);

        final int tmp[] = new int[1];
        GLES20.glGenBuffers(1, tmp, 0);
        vbo = tmp[0];
        if(vbo < 1) {
            String er = "" + vbo;
            Log.e("VBO ERROR: ", er);
        }
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, tot, dat, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        //Textures
        tid = Textures.getInstance().getTexId("floor.png");
    }

    public void draw() {
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo);

        GLES20.glEnableVertexAttribArray(shader.posLoc);
        GLES20.glEnableVertexAttribArray(shader.coordLoc);

        GLES20.glVertexAttribPointer(shader.posLoc, 3, GLES20.GL_FLOAT, false, 0, voff);
        GLES20.glVertexAttribPointer(shader.coordLoc, 2, GLES20.GL_FLOAT, false, 0, coff);

        GLES20.glUniform3fv(shader.mPosLoc, 1, pos, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tid);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vcount);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glDisableVertexAttribArray(shader.posLoc);
        GLES20.glDisableVertexAttribArray(shader.coordLoc);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }
}
