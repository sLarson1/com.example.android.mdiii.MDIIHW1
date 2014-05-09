package com.example.android.mdiii;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

public class CopyOfGraphicsUtils {
	
	static Context context;
	private static final String TAG = "GraphicsUtils";

    // Buffers to be passed to gl*Pointer() functions must be
    // direct, i.e., they must be placed on the native heap
    // where the garbage collector cannot move them.
    //
    // Buffers with multi-byte data types (e.g., short, int,
    // float) must have their byte order set to native order
	public static FloatBuffer allocDBFloat (float[] inputFLoats)
	{      
		FloatBuffer mOutputBuffer;
        ByteBuffer dlb = ByteBuffer.allocateDirect(inputFLoats.length * 4);
        dlb.order(ByteOrder.nativeOrder());
        mOutputBuffer = dlb.asFloatBuffer();
        mOutputBuffer.put(inputFLoats);
        mOutputBuffer.position(0);
        return mOutputBuffer;
	}

	
	public static ShortBuffer allocDBShort (short[] inputShorts)
	{      
		ShortBuffer mOutputBuffer;
        ByteBuffer dlb = ByteBuffer.allocateDirect(inputShorts.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        mOutputBuffer = dlb.asShortBuffer();
        mOutputBuffer.put(inputShorts);
        mOutputBuffer.position(0);
        return mOutputBuffer;
	}



	public static String getShaderCode(String name)
	{
	  InputStream is;
	  StringBuilder stringBuilder = new StringBuilder();
	 
	  try {
	    is = context.getAssets().open(name);
	    BufferedReader bufferedBuilder = new BufferedReader(new InputStreamReader(is));
	 
	    String line;
	    while ((line = bufferedBuilder.readLine()) != null) {
	      stringBuilder.append(line + '\n');
	    }
	  } catch (IOException e) {
	    e.printStackTrace();
	  }
	  return stringBuilder.toString();
	}
	

    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);
		checkGlError("glCreateShader");

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
		checkGlError("glCreateShader");
		GLES20.glCompileShader(shader);
		checkGlError("glCreateShader");
       
        int[] buffer = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, buffer, 0);
        if(buffer[0] == GLES20.GL_FALSE) {
            Log.e("ShaderHelper", GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            return -1;
        }
        return shader;
        
    }

	
   
	public static int setShaders(String VSName,String PSName )
	{
		int mProgram;
		
		String vertexShaderCode = getShaderCode(VSName);
		String pixelShaderCode = getShaderCode(PSName);
		    
		  
		// prepare shaders and OpenGL program
		int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
		if (vertexShader == -1)  return -1;
		
		int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, pixelShaderCode);
		if (fragmentShader == -1)  return -1;
		
		// create empty OpenGL Program
		mProgram = GLES20.glCreateProgram();             
		checkGlError("glCreateProgram");
		// add the vertex shader to program
		GLES20.glAttachShader(mProgram, vertexShader);   
		checkGlError("glAttachShader");
		 // add the fragment shader to program
		GLES20.glAttachShader(mProgram, fragmentShader);
		checkGlError("glAttachShader");
		// create OpenGL program executables
		GLES20.glLinkProgram(mProgram);                  
		checkGlError("glLinkProgram");
		
	    int[] tbuffer = new int[1];
	    GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, tbuffer, 0);
	    if(tbuffer[0] == GLES20.GL_FALSE) {
	        Log.e("ShaderHelper", GLES20.glGetProgramInfoLog(mProgram));
	        GLES20.glDeleteProgram(mProgram);
	        return -1;
	    }
	      
		return mProgram;
	}
	
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }
    

}
