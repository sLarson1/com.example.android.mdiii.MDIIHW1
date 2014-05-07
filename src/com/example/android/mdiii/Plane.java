package com.example.android.mdiii;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class Plane implements Drawable{

	
	   private final FloatBuffer mVertexBuffer;
	   private final FloatBuffer mColorBuffer;

	    private final String vertexShaderCode =
	        // This matrix member variable provides a hook to manipulate
	        // the coordinates of the objects that use this vertex shader
	        "uniform mat4 uMVPMatrix;" +
	        "attribute vec4 vPosition;" +
	        "attribute vec4 vColor;" +
			  "varying vec4 vertex_color;      \n" +
	        "void main() {" +
	        // the matrix must be included as a modifier of gl_Position
	        "  gl_Position = uMVPMatrix * vPosition;" +
	        "  vertex_color = vColor;" +
	        "}";
	    

	    private final String fragmentShaderCode =
	        "precision mediump float;" +
			  "varying vec4 vertex_color;      \n" +
	        "void main() {" +
	        "  gl_FragColor = vertex_color;" +
	        "}";


	    private final ShortBuffer drawListBuffer;
	    private final int mProgram;
	    private int mPositionHandle;
	    private int mColorHandle;
	    private int mMVPMatrixHandle;

	    // number of coordinates per vertex in this array
	    static final int COORDS_PER_VERTEX = 3;

	    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
	    float color[] = { 0.2f, 0.709803922f, 0.898039216f, 1.0f };
		private boolean isFinished;
	    
	    static float vertices[] =
	        {
/*	    	
	    	0, -0.5f, -0.5f,	//0
	    	0, -0.5f, 0,		//1
	    	0,	0,	0,			//2
	    	0,	0,	-0.5f,		//3
	    	0,	-0.5f,	0,		//4
	    	0,	-0.5f,	0.5f,	//5
	    	0,	0,	0.5f,		//6
	    	0,	0,	0,			//7
	    	0,	0,	-0.5f,		//8
	    	0,	-0.5f,	-0.5f,	//9
	    	0,	0.5f,	0,		//10
	    	0,	0.5f,	-0.5f,	//11
	    	0,	0.5f,	0.5f,	//12
	    	0,	0,	0.5f,		//13
	    	0,	0.5f,	0.5f,	//14
	    	0,	0.5f,	0,		//15
*/	    	
	    	0,			0,			0,
	    	-0.5f,	0,			0,
	    	0,			-0.5f,	0,
	    	0.5f,		0,			0,
	    	0,			0,			-0.5f	    		    	
	         };
	    
	    
	    static float vColor[] =
	        {
            0.4f, 0.4f, 0.8f,
            0.3f, 0.2f, 0.1f,
            0.4f, 0.4f, 0.8f,
            0.3f, 0.2f, 0.1f,
            0.4f, 0.4f, 0.8f,
	    	/*
	    	0,			0,			0,
	    	-0.5f,	0,			0,
	    	0,			-0.5f,	0,
	    	0.5f,		0,			0,
	    	0,			0,			-0.5f
	    	*/		    	
	    	/*
            0.4f, 0.4f, 0.8f,
            0.4f, 0.4f, 0.8f,
            0.4f, 0.4f, 0.8f,
            0.4f, 0.4f, 0.8f,
            0.3f, 0.2f, 0.1f,
            0.3f, 0.2f, 0.1f,
            0.3f, 0.2f, 0.1f,
            0.3f, 0.2f, 0.1f,
            0.3f, 0.2f, 0.1f,
            0.3f, 0.2f, 0.1f,
            0.3f, 0.2f, 0.1f,
            0.3f, 0.2f, 0.1f,
            0.4f, 0.4f, 0.8f,
            0.4f, 0.4f, 0.8f,
            0.4f, 0.4f, 0.8f,
            0.4f, 0.4f, 0.8f
*/

	         };

	    
	      static short groundIndices[] =
	          {

		    	1,	4,	0,
		    	0,	4,	3,
		    	2,	0,	4
	          };      
	      
	      
   public Plane() {
	   isFinished = false;
      // Buffers to be passed to gl*Pointer() functions must be
      // direct, i.e., they must be placed on the native heap
      // where the garbage collector cannot move them.
      //
      // Buffers with multi-byte data types (e.g., short, int,
      // float) must have their byte order set to native order
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		mVertexBuffer = vbb.asFloatBuffer();
		mVertexBuffer.put(vertices);
		mVertexBuffer.position(0);
		  
		
		ByteBuffer nbb = ByteBuffer.allocateDirect(vColor.length * 4);
		nbb.order(ByteOrder.nativeOrder());
		mColorBuffer = nbb.asFloatBuffer();
		mColorBuffer.put(vColor);
		mColorBuffer.position(0);
	      
        ByteBuffer dlb = ByteBuffer.allocateDirect(groundIndices.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(groundIndices);
        drawListBuffer.position(0);
        
      
      // prepare shaders and OpenGL program
      int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                                                 vertexShaderCode);
      int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                                                   fragmentShaderCode);

      mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
      GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
      MyGLRenderer.checkGlError("glAttachShader");
      GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
      MyGLRenderer.checkGlError("glAttachShader");
      GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables
      MyGLRenderer.checkGlError("glLinkProgram");
   }
   

   public void draw(float[] mvpMatrix) { 
	   
       // Add program to OpenGL environment
       GLES20.glUseProgram(mProgram);
       
       // Associate color array with vertex shader input
       mColorHandle = GLES20.glGetAttribLocation(mProgram, "vColor");
       GLES20.glEnableVertexAttribArray(mColorHandle);
       GLES20.glVertexAttribPointer(mColorHandle, COORDS_PER_VERTEX,
                                    GLES20.GL_FLOAT, false,
                                    vertexStride, mColorBuffer);
       

       // hook up  vertex array with vertex shader input
       mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
       MyGLRenderer.checkGlError("glGetAttribLocation");
       GLES20.glEnableVertexAttribArray(mPositionHandle);
       GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                                    GLES20.GL_FLOAT, false,
                                    vertexStride, mVertexBuffer);
       
       // send matrix to vertex shader
       mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
       MyGLRenderer.checkGlError("glGetUniformLocation");
       GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
       MyGLRenderer.checkGlError("glUniformMatrix4fv"); 


       // Draw the ground
       GLES20.glDrawElements(GLES20.GL_TRIANGLES, groundIndices.length,
                             GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
       MyGLRenderer.checkGlError("glDrawElements");

       // Disable vertex array
       GLES20.glDisableVertexAttribArray(mPositionHandle);

  }


/* (non-Javadoc)
 * @see com.example.android.mdiii.Drawable#isFinished()
 */
@Override
public boolean isFinished() {
	// TODO Auto-generated method stub
	return isFinished;
}


/* (non-Javadoc)
 * @see com.example.android.mdiii.Drawable#draw(com.example.android.mdiii.Coord, float[])
 */
@Override
public void draw(Coord coord, float[] mvpMatrix) {
	// TODO Auto-generated method stub
	
}	

   
}
