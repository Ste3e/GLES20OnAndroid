package com.sjonesart.GL1;

//Copyright 2013 Stephen K Jones
//All Rights Reserved

import android.graphics.*;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.renderscript.Float2;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Hud {
    private HudShader shader;
    private boolean hidden = true;
    private int width, height;
    private int vbo, voff, coff, tot;
    private int vcount;
    private int leftTid;
    private int[] textField;

    public Hud(HudShader shader){
        this.shader = shader;
        textField = new int[1];
    }

    public void makeHud(int width, int height, Float2 lThumb, Float2 rThumb){
        this.width = width;
        this.height = height;
        build(lThumb, rThumb);

        String str = GLES20.glGetString(GLES20.GL_EXTENSIONS);
        if(str.contains("GL_NV_fbo_color_attachments")){
            addText("GL_NV_fbo_color_attachments are supported on your device");
        }else{
            addText("GL_NV_fbo_color_attachments are not supported on your device");
        }
        hidden = false;
    }

    private void build(Float2 lThumb, Float2 rThumb){
        float[] vdat;
        vdat = new float[54 + 36];//9 * 6, 12 * 6

        Float2 L = new Float2(lThumb.x - width / 2, lThumb.y - height / 2);
        L.y = -L.y;//to lower left
        L.x = L.x / width;//ratio
        L.y = L.y / height;
        L.x = L.x * 2; //NDC
        L.y = L.y * 2;
        Float2 R = new Float2(rThumb.x - width / 2, rThumb.y - height / 2);
        R.y = -R.y;//to lower left
        R.x = R.x / width;//ratio
        R.y = R.y / height;
        R.x = R.x * 2; //NDC
        R.y = R.y * 2;

        float bh = 0.1f;//button height
        float aspect = (float)height / (float)width;
        float bw = bh * aspect;
        int i = 0;

        vdat[i++] = L.x - bw; vdat[i++] = L.y - bh; vdat[i++] = -1f;
        vdat[i++] = L.x + bw; vdat[i++] = L.y - bh; vdat[i++] = -1f;
        vdat[i++] = L.x + bw; vdat[i++] = L.y + bh; vdat[i++] = -1f;
        vdat[i++] = L.x - bw; vdat[i++] = L.y - bh; vdat[i++] = -1f;
        vdat[i++] = L.x + bw; vdat[i++] = L.y + bh; vdat[i++] = -1f;
        vdat[i++] = L.x - bw; vdat[i++] = L.y + bh; vdat[i++] = -1f;

        vdat[i++] = R.x - bw; vdat[i++] = R.y - bh; vdat[i++] = -1f;
        vdat[i++] = R.x + bw; vdat[i++] = R.y - bh; vdat[i++] = -1f;
        vdat[i++] = R.x + bw; vdat[i++] = R.y + bh; vdat[i++] = -1f;
        vdat[i++] = R.x - bw; vdat[i++] = R.y - bh; vdat[i++] = -1f;
        vdat[i++] = R.x + bw; vdat[i++] = R.y + bh; vdat[i++] = -1f;
        vdat[i++] = R.x - bw; vdat[i++] = R.y + bh; vdat[i++] = -1f;

        vdat[i++] = L.x; vdat[i++] = -0.95f; vdat[i++] = -1f;
        vdat[i++] = -L.x; vdat[i++] = -0.95f; vdat[i++] = -1f;
        vdat[i++] = -L.x; vdat[i++] = L.y - bh; vdat[i++] = -1f;
        vdat[i++] = L.x; vdat[i++] = -0.95f; vdat[i++] = -1f;
        vdat[i++] = -L.x; vdat[i++] = L.y - bh; vdat[i++] = -1f;
        vdat[i++] = L.x; vdat[i++] = L.y - bh; vdat[i++] = -1f;

        vdat[i++] = 0f; vdat[i++] = 1f;  vdat[i++] = 1f; vdat[i++] = 1f;  vdat[i++] = 1f; vdat[i++] = 0f;
        vdat[i++] = 0f; vdat[i++] = 1f;  vdat[i++] = 1f; vdat[i++] = 0f;  vdat[i++] = 0f; vdat[i++] = 0f;

        vdat[i++] = 1f; vdat[i++] = 1f;  vdat[i++] = 0f; vdat[i++] = 1f;  vdat[i++] = 0f; vdat[i++] = 0f;
        vdat[i++] = 1f; vdat[i++] = 1f;  vdat[i++] = 0f; vdat[i++] = 0f;  vdat[i++] = 1f; vdat[i++] = 0f;

        vdat[i++] = 0f; vdat[i++] = 1f;  vdat[i++] = 1f; vdat[i++] = 1f;  vdat[i++] = 1f; vdat[i++] = 0f;
        vdat[i++] = 0f; vdat[i++] = 1f;  vdat[i++] = 1f; vdat[i++] = 0f;  vdat[i++] = 0f; vdat[i] = 0f;

        vcount = 18;
        voff = 0;
        coff = vcount * 3 * 4;//size of verts bytes
        tot = coff + (vcount * 2 * 4);//size of coords bytes

        FloatBuffer dat = ByteBuffer.allocateDirect(tot).order(ByteOrder.nativeOrder()).asFloatBuffer();
        dat.put(vdat).position(0);

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

        leftTid = Textures.getInstance().getTexId("button.png");
    }

    public void addText(String text){
        GLES20.glDeleteTextures(1, textField, 0);
        GLES20.glGenTextures(1, textField, 0);
        if(textField[0] < 1){ throw new RuntimeException("GL failed to assign texture id for text field"); }

        Bitmap bmp = Bitmap.createBitmap(512, 60, Bitmap.Config.ARGB_8888);//aspect handled

        Canvas c = new Canvas(bmp);
        c.drawARGB(128, 128, 128, 128);

        Paint p = new Paint();
        p.setColor(Color.argb(255, 230, 230, 202));
        p.setTextSize(14);
        p.setAntiAlias(true);
        p.setTypeface(Typeface.SERIF);

        c.drawText(text, 5, 30, p);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textField[0]);
        GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR );
        GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR );
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        bmp.recycle();
    }

    public void draw() {
        if(hidden) return;
        GLES20.glEnable (GLES20.GL_BLEND);
        GLES20.glBlendFunc (GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glUseProgram(shader.shad);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo);

        GLES20.glEnableVertexAttribArray(shader.posLoc);
        GLES20.glEnableVertexAttribArray(shader.coordLoc);

        GLES20.glVertexAttribPointer(shader.posLoc, 3, GLES20.GL_FLOAT, false, 0, voff);
        GLES20.glVertexAttribPointer(shader.coordLoc, 2, GLES20.GL_FLOAT, false, 0, coff);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, leftTid);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 6, 6);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textField[0]);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 12, 6);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glDisableVertexAttribArray(shader.posLoc);
        GLES20.glDisableVertexAttribArray(shader.coordLoc);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glUseProgram(0);
        GLES20.glDisable (GLES20.GL_BLEND);
    }
}
