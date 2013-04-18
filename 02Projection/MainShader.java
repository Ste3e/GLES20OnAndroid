package com.sjonesart.GL1;

//Copyright 2013 Stephen K Jones
//All Rights Reserved

import android.opengl.GLES20;
import android.util.Log;
import java.nio.IntBuffer;

public class MainShader {
    private final float degrad = (float)Math.PI / 180;
    private  float near = 1f, far = 1024f, fov = 15f;
    public int width, height;
    public boolean ok;
    public int shad;
    public int posLoc;
    public int projLoc;

    public MainShader(){
         final String vCode =
             "attribute vec3 pos;" +
             "uniform vec4 proj;" +
             "void main() {" +
             "vec4 clip;" +
             "clip.x = pos.x * proj.x;" +
             "clip.y = pos.y * proj.y;" +
             "clip.z = pos.z * proj.z + proj.w;" +
             "clip.w = -pos.z;" +
             "gl_Position = clip;" +
             "}";
         final String fCode =
                "void main() {" +
                "gl_FragColor = vec4(0.6, 0.5, 0.8, 1.0);" +
                "}";
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
        if(posLoc == -1) Log.e("MainShader", "vPos not initialized");

        projLoc = GLES20.glGetUniformLocation(shad, "proj");
        if(projLoc == -1) Log.e("MainShader", "projLoc not initialized");
    }


    public void setupProjection(int width, int height){
        this.width = width;
        this.height = height;

        float aspect = (float)height / (float)width;
        float tan = (float)Math.tan(fov * degrad);
        float w = near * tan;
        float h = w * aspect;
        float l = -w, r = w, t = h, b = -h;
        float x1 = (2 * near) / (r - l);
        float y1 = (2 * near) / (t - b);
        float z1 = (far + near) / (near - far);
        float z2 = (2 * far * near) / (near - far);
        float[] proj = {x1, y1, z1, z2};

        GLES20.glUseProgram(shad);
        GLES20.glUniform4fv(projLoc, 1, proj, 0);
        GLES20.glUseProgram(0);
    }
}
