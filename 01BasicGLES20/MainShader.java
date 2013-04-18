package com.sjonesart.GL1;

//Copyright 2013 Stephen K Jones
//All Rights Reserved

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class MainShader {
    public boolean ok;
    public int shad;
    public int posLoc;

    private final String vCode =
            "uniform mat4 uMVPMatrix;" +
                "attribute vec3 pos;" +
                "void main() {" +
                    "gl_Position = vec4(pos, 1.0);\n" +
                "}";

    private final String fCode =
            "precision mediump float;" +
            "void main() {" +
                "gl_FragColor = vec4(0.6, 0.5, 0.4, 1.0);" +
            "}";


    public MainShader(){
        ok = true;
        //vert shader
        int vshad = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        if (vshad > 0){
            GLES20.glShaderSource(vshad, vCode);
            GLES20.glCompileShader(vshad);
            final int[] status = new int[1];
            GLES20.glGetShaderiv(vshad, GLES20.GL_COMPILE_STATUS, status, 0);
            if (status[0] == GLES20.GL_FALSE){
                ok = false;
                IntBuffer ival = IntBuffer.allocate(1);
                GLES20.glGetShaderiv(vshad, GLES20.GL_INFO_LOG_LENGTH, ival);
                int size = ival.get();
                if(size > 1){
                    String log = GLES20.glGetShaderInfoLog(vshad);
                    Log.e("main vertex shader", log);
                }
            }
        }
        //frag shader
        int fshad = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        if (fshad > 0 && ok){
            GLES20.glShaderSource(fshad, fCode);
            GLES20.glCompileShader(fshad);
            final int[] status = new int[1];
            GLES20.glGetShaderiv(fshad, GLES20.GL_COMPILE_STATUS, status, 0);
            if (status[0] == GLES20.GL_FALSE){
                ok = false;
                IntBuffer ival = IntBuffer.allocate(1);
                GLES20.glGetShaderiv(vshad, GLES20.GL_INFO_LOG_LENGTH, ival);
                int size = ival.get();
                if(size > 1){
                    String log = GLES20.glGetShaderInfoLog(fshad);
                    Log.e("main fragment shader", log);
                }
            }
        }
        //main program shader
        shad = GLES20.glCreateProgram();
        if (shad > 0 && ok){
            GLES20.glAttachShader(shad, vshad);
            GLES20.glAttachShader(shad, fshad);
            GLES20.glLinkProgram(shad);
            final int[] status = new int[1];
            GLES20.glGetProgramiv(shad, GLES20.GL_LINK_STATUS, status, 0);
            if (status[0] == GLES20.GL_FALSE){
                ok = false;
                IntBuffer ival = IntBuffer.allocate(1);
                GLES20.glGetShaderiv(shad, GLES20.GL_INFO_LOG_LENGTH, ival);
                int size = ival.get();
                if(size > 1){
                    String log = GLES20.glGetShaderInfoLog(shad) + "\n\n";
                    Log.e("main program shader", log);
                }
            }
        }

        posLoc = GLES20.glGetAttribLocation(shad, "pos");
        if(posLoc == -1) Log.e("MainShader", "posLoc not intialized");
    }
}
