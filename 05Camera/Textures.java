package com.sjonesart.GL1;

//Copyright 2013 Stephen K Jones
//All Rights Reserved

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Textures {
    private static Map<String, Integer> tex;
    private static Textures instance;
    private Context context;

    private Textures(){}
    public static Textures getInstance(){
        if(instance == null){
            instance = new Textures();
        }
        return instance;
    }
    public void setContext(Context context){
        this.context = context;
        tex = new HashMap<String, Integer>();
    }

    public int getTexId(String key){
        if(context == null){
            throw new RuntimeException("CONTEXT ERROR: Texture.java context is not set");
        }
        if(tex.containsKey(key)){
            return tex.get(key);
        }
        Bitmap bmp;
        int[] tmp = new int[1];
        GLES20.glGenTextures(1, tmp, 0);
        if(tmp[0] < 1){ throw new RuntimeException("GL failed to assign texture id for: " + key); }

        try{
            InputStream in = context.getAssets().open(key);
            bmp = BitmapFactory.decodeStream(in);
        } catch(Exception e){
            throw new RuntimeException("BITMAP READ FAILED" + e.toString() + " for: " + key);
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tmp[0]);
        GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR );
        GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR );
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        bmp.recycle();

        tex.put(key, tmp[0]);
        return tmp[0];
    }
}
