package com.sjonesart.GL1;

//Copyright 2013 Stephen K Jones
//All Rights Reserved

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.IntBuffer;

public class MainShader {
    private final float degrad = (float)Math.PI / 180;
    public float near = 1f, far = 1024f, fov = 15f;
    public int width = 0, height;
    public boolean ok;
    public int shad;
    public int posLoc, coordLoc;
    public int mPosLoc, cPosLoc;
    public int cMatLoc;
    public int projLoc;
    public int cMapLoc;


    public MainShader(Context context){
        ok = true;
        //vert shader
        String vertCode = "";
        try{
            InputStream in = context.getAssets().open("Shaders/main.vert");
            InputStreamReader ir = new InputStreamReader(in);
            BufferedReader reader = new BufferedReader(ir);


            String line;
            while((line = reader.readLine()) != null){
                vertCode += line + "\n";
            }
        }catch(IOException e){
            e.printStackTrace();
        }

        int vshad = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        if (vshad > 0){
            GLES20.glShaderSource(vshad, vertCode);
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
        String fragCode = "";
        try{
            InputStream in = context.getAssets().open("Shaders/main.frag");
            InputStreamReader ir = new InputStreamReader(in);
            BufferedReader reader = new BufferedReader(ir);

            String line;
            while((line = reader.readLine()) != null){
                fragCode += line + "\n";
            }
        }catch(IOException e){
            e.printStackTrace();
        }

        int fshad = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        if (fshad > 0 && ok){
            GLES20.glShaderSource(fshad, fragCode);
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
        if(!ok){ throw new RuntimeException("GLSL: No MainShader created"); }

        //attributes
        posLoc = GLES20.glGetAttribLocation(shad, "pos");
        if(posLoc == -1) Log.e("MainShader", "pos attrib not intialized");
        coordLoc = GLES20.glGetAttribLocation(shad, "coords");
        if(coordLoc == -1) Log.e("MainShader", "coord attrib not intialized");

        //uniforms
        cMatLoc = GLES20.glGetUniformLocation(shad, "cMat");
        if(cMatLoc == -1) Log.e("MainShader", "cMat not intialized");

        projLoc = GLES20.glGetUniformLocation(shad, "proj");
        if(projLoc == -1) Log.e("MainShader", "projLoc not intialized");
        mPosLoc = GLES20.glGetUniformLocation(shad, "mPos");
        if(mPosLoc == -1) Log.e("MainShader", "mPos not intialized");
        cPosLoc = GLES20.glGetUniformLocation(shad, "cPos");
        if(cPosLoc == -1) Log.e("MainShader", "cPos not intialized");

        //maps
        cMapLoc = GLES20.glGetUniformLocation(shad, "cMap");
        if(cMapLoc == -1) Log.e("Main Shader", "cMapLoc not initialized");

        GLES20.glUseProgram(shad);
        GLES20.glUniform1i(cMapLoc, 0);
        GLES20.glUseProgram(0);
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
