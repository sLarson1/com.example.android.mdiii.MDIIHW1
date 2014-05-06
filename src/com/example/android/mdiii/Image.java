/**
 * 
 */
package com.example.android.mdiii;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glVertexAttribPointer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;




import java.util.Arrays;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

/**
 * @author slarson
 *
 */
public class Image implements Drawable {

	private static final String TAG = "Image";
	private final FloatBuffer mVertexBuffer;
    private final FloatBuffer mColorBuffer;
    private final FloatBuffer mTextureBuffer;

	private String vertexShaderCode =
		// This matrix member variable provides a hook to manipulate
		// the coordinates of the objects that use this vertex shader
		"uniform mat4 uMVPMatrix;" +
		"attribute vec4 vPosition;" +
		"attribute vec4 vColor;" +
		"attribute vec2 vTexCoord;"+
		"varying vec2 vertex_TexCoord;"+
		"varying vec4 vertex_color;      \n" +
		"void main() {" +
		// the matrix must be included as a modifier of gl_Position
		"  gl_Position = uMVPMatrix * vPosition;" +
		"  vertex_color = vColor;" +
		"  vTexCoord = vertex_TexCoord"+
		"}";

	private String fragmentShaderCode =
		"precision mediump float;" +
		"varying vec4 vertex_color;      \n" +
		"void main() {" +
		"  gl_FragColor = vertex_color;" + 
		"}";


	private final ShortBuffer drawListBuffer;
	private final int mProgram;
	private int mPositionHandle;
	private int mColorHandle;
	private int mTextureUniformHandle;

	/* these are not there */
	private int mTextureCoordinateHandle;
	private int mTextureCoordSize;
	private int mTextureDataHandle;
	private int mMVPMatrixHandle;

	// number of coordinates per vertex in this array
	static final int COORDS_PER_VERTEX = 3;
	static final int COORDS_PER_TEXTURE = 2;

	private final int vertexStride = (COORDS_PER_VERTEX +COORDS_PER_TEXTURE) * 4; // 4 bytes per vertex
	float color[] = { 0.2f, 0.709803922f, 0.898039216f, 1.0f };
	
	//	Use Triangle Strip - so only need 4 vertices
	static float vertices[] =
	{
		-1.0f, -1.0f,  0.0f,		// bottom left
		-1.0f,  1.0f,  0.0f,		// top left
		1.0f, -1.0f,  0.0f,		// bottom right
		1.0f,  1.0f,  0.0f		// top right	    	    	      
	};	
	
	private float texture[] = 
	{    		
		0.0f, 1.0f,		// top left
		0.0f, 0.0f,		// bottom left	
		1.0f, 1.0f,		// top right
		1.0f, 0.0f		// bottom right
	};
	
	private int textureId;	    
	
	static float vColor[] =
	{
		0.4f, 0.4f, 0.8f,
		0.4f, 0.4f, 0.8f,
		0.4f, 0.4f, 0.8f,
		0.4f, 0.4f, 0.8f,
	};

	
	static short groundIndices[] =
	{
		0, 1, 2,	//	v1, v2, v3
		//2,3,0?	             
		2, 1, 3		//	v3, v2, v4
	};      
	      
	      
	public Image( ) {
	
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
		
		ByteBuffer tbb = ByteBuffer.allocateDirect(texture.length * 4);
		nbb.order(ByteOrder.nativeOrder());
		mTextureBuffer = tbb.asFloatBuffer(); 
		mTextureBuffer.put(texture);
		mTextureBuffer.position(0);		
			  
		ByteBuffer dlb = ByteBuffer.allocateDirect(groundIndices.length * 2);
		dlb.order(ByteOrder.nativeOrder());
		drawListBuffer = dlb.asShortBuffer();
		drawListBuffer.put(groundIndices);
		drawListBuffer.position(0);
		  
		vertexShaderCode = GraphicsUtils.getShaderCode("image.vs");
		fragmentShaderCode = GraphicsUtils.getShaderCode("image.ps");         
		Log.v(TAG, "shaderCode:"+vertexShaderCode);
		Log.v(TAG, "fragmentCode:"+fragmentShaderCode);
		
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

is gltext somehow overriding my shader compilation?		
		
	      // %%% more error checking
	      int[] tbuffer = new int[1];
	      GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, tbuffer, 0);
	      if(tbuffer[0] == GLES20.GL_FALSE) {
	          Log.e("ShaderHelper", GLES20.glGetProgramInfoLog(mProgram));
	          GLES20.glDeleteProgram(mProgram);
	         // return -1;
	          throw new RuntimeException("error with shader!");
	      }		
	}


    public void draw(float[] mvpMatrix) { 
     
		// Bind the Texture	
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		MyGLRenderer.checkGlError("glActiveTexture");
		GLES20.glBindTexture( GLES20.GL_TEXTURE_2D, textureId );
		MyGLRenderer.checkGlError("glBindTexture");
		// Add program to OpenGL environment
		GLES20.glUseProgram(mProgram);
		MyGLRenderer.checkGlError("useProgram2");
		//  Is this used anywhere or do we do glBindAttribLocation or lGetAttribLocation



		// hook up  vertex array with vertex shader input
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");
//		Log.e(TAG, "position:"+Arrays.toString());
		Log.e(TAG, "position:"+mVertexBuffer.toString());		
		MyGLRenderer.checkGlError("glGetAttribLocation");
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
								  GLES20.GL_FLOAT, false,
								  vertexStride, mVertexBuffer);

		// Associate color array with vertex shader input
		mColorHandle = GLES20.glGetAttribLocation(mProgram, "a_Color");
		GraphicsUtils.checkGlError("glGetAttribLocation");
		GLES20.glEnableVertexAttribArray(mColorHandle);
		GLES20.glVertexAttribPointer(mColorHandle, COORDS_PER_VERTEX,
								  GLES20.GL_FLOAT, false,
								  vertexStride, mColorBuffer);		
		

		mTextureCoordinateHandle = glGetAttribLocation(mProgram, "a_TexCoord");
		GraphicsUtils.checkGlError("glGetAttribLocation");
		glEnableVertexAttribArray(mTextureCoordinateHandle);
		glVertexAttribPointer(mTextureCoordinateHandle,COORDS_PER_TEXTURE ,
								  GL_FLOAT, false,
								  vertexStride, mTextureBuffer); 
    
     
		// send matrix to vertex shader
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
		MyGLRenderer.checkGlError("glGetUniformLocation");
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
		MyGLRenderer.checkGlError("glUniformMatrix4fv"); 


		mTextureUniformHandle = GLES20.glGetUniformLocation(mProgram, "u_Sampler_Texture");

		// Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
		GLES20.glUniform1i(mTextureUniformHandle, 0);     


		// Draw the ground
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, groundIndices.length,
							GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
		//Use triangle strip instead of regular triangles
//		GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, groundIndices.length,
//			 GLES20.GL_UNSIGNED_SHORT, drawListBuffer); 

		MyGLRenderer.checkGlError("glDrawElements");

		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mPositionHandle);
    
    }

	@SuppressLint("NewApi")
   public void loadTexture(Context context){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.background_small, options);
		
		Canvas canvas = new Canvas(bitmap);
		
		int[] texturePointers =  new int[1]; 

		GLES20.glGenTextures(1, texturePointers, 0);
		textureId = texturePointers[0];

		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

		// create nearest filtered texture
		GLES20.glTexParameterf( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST );  // Set Minification Filter
		GLES20.glTexParameterf( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR );  // Set Magnification Filter

		//do we need these?	
		//    GLES20.glTexParameterf( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE );  // Set U Wrapping
		//    GLES20.glTexParameterf( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE );  // Set V Wrapping	


		// Use Android GLUtils to specify a two-dimensional texture image from our bitmap 
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
		GLES20.glBindTexture( GLES20.GL_TEXTURE_2D, 0 );      	 // Unbind Texture	

		MyGLRenderer.checkGlError("loadTexture");
		// Clean up
		bitmap.recycle();			
	}

	

}
