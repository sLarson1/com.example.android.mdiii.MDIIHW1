package com.example.android.mdiii.text;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.example.android.mdiii.GraphicsUtils;


public class Vertices {
	    
    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static final int TEX_COORDS_PER_VERTEX = 2;   
  
    float color[] = { 0.2f, 0.709803922f, 0.898039216f, 1.0f };
    
    static float verts[] =
        {
      -50f, -50f, 0.0f,          1.0f, 1.0f, 
      -050f, 050f,  0.0f,   1.0f, 0.0f,  
      050f,  050f,  0.0f, 0.0f, 0.0f,  
      050f, -050f,  0.0f,  0.0f, 1.0f
         };
    
    static float texCoords[] =
        {
      1.0f, 1.0f, 
      1.0f, 0.0f,  
      0.0f, 0.0f,  
      0.0f, 1.0f
         };
    
    static float vColor[] =
        {
      0.4f, 0.4f, 0.8f,
      0.4f, 0.4f, 0.8f,
      0.4f, 0.4f, 0.8f,
      0.4f, 0.4f, 0.8f
         };
   
    static short cubeIndices[] =
       {
          0, 1, 2,
          2, 3, 0
       };      
      

   //--Constants--//
   final static int POSITION_CNT_2D = 2;              // Number of Components in Vertex Position for 2D
   final static int POSITION_CNT_3D = 3;              // Number of Components in Vertex Position for 3D
   final static int COLOR_CNT = 4;                    // Number of Components in Vertex Color
   final static int TEXCOORD_CNT = 2;                 // Number of Components in Vertex Texture Coords
   final static int NORMAL_CNT = 3;                   // Number of Components in Vertex Normal

   final static int INDEX_SIZE = Short.SIZE / 8;      // Index Byte Size (Short.SIZE = bits)

   //--Members--//
   // NOTE: all members are constant, and initialized in constructor!
   final boolean hasColor;                            // Use Color in Vertices
   final boolean hasTexCoords;                        // Use Texture Coords in Vertices
   final boolean hasNormals;                          // Use Normals in Vertices
   public final int positionCnt;                      // Number of Position Components (2=2D, 3=3D)
   public final int vertexStride;                     // Vertex Stride (Element Size of a Single Vertex)
   public final int vertexSize;                       // Bytesize of a Single Vertex
   final FloatBuffer vertices;                          // Vertex Buffer
   final ShortBuffer indices;                         // Index Buffer
   public int numVertices;                            // Number of Vertices in Buffer
   public int numIndices;                             // Number of Indices in Buffer
   final int[] tmpBuffer;                             // Temp Buffer for Vertex Conversion
   
   private  String vertexShaderCode;
   private  String pixelShaderCode;
   private final int mProgram;
   
   private int mPositionHandle;
   private int mTexCoordHandle;
   private int  mTextureUniformHandle;


   /**
    * create the vertices/indices as specified (for 2d/3d)
    * @param maxVertices - maximum vertices allowed in buffer
    * @param maxIndices - maximum indices allowed in buffer
    * @param hasColor - use color values in vertices
    * @param hasTexCoords - use texture coordinates in vertices
    * @param hasNormals - use normals in vertices
    */
   public Vertices( int maxVertices, int maxIndices, boolean hasColor, boolean hasTexCoords, boolean hasNormals)  {
      this(maxVertices, maxIndices, hasColor, hasTexCoords, hasNormals, true );  // Call Overloaded Constructor
   }

   public Vertices(int maxVertices, int maxIndices, boolean hasColor, boolean hasTexCoords, boolean hasNormals, boolean use3D)  {
	   
	  this.hasColor = hasColor;                       // Save Color Flag
      this.hasTexCoords = hasTexCoords;               // Save Texture Coords Flag
      this.hasNormals = hasNormals;                   // Save Normals Flag
      // Set Position Component Count
      this.positionCnt = use3D ? POSITION_CNT_3D : POSITION_CNT_2D;  
      // Calculate Vertex Stride
      this.vertexStride = this.positionCnt + ( hasColor ? COLOR_CNT : 0 ) + ( hasTexCoords ? TEXCOORD_CNT : 0 ) + ( hasNormals ? NORMAL_CNT : 0 );
      this.vertexSize = this.vertexStride * 4;        // Calculate Vertex Byte Size
      // Allocate Buffer for Vertices (Max)
      ByteBuffer buffer = ByteBuffer.allocateDirect( maxVertices * vertexSize );
      buffer.order( ByteOrder.nativeOrder() );        // Set Native Byte Order
      this.vertices = buffer.asFloatBuffer();         // Save Vertex Buffer

      // IF Indices Required
      if ( maxIndices > 0 )  {                        
    	 // Allocate Buffer for Indices (MAX)
         buffer = ByteBuffer.allocateDirect( maxIndices * 2 );
         buffer.order( ByteOrder.nativeOrder() );     // Set Native Byte Order
         this.indices = buffer.asShortBuffer();       // Save Index Buffer
      }
      else                                            // ELSE Indices Not Required
         indices = null;                              // No Index Buffer

      numVertices = 0;                                // Zero Vertices in Buffer
      numIndices = 0;                                 // Zero Indices in Buffer

      this.tmpBuffer = new int[maxVertices * vertexSize / 4];  // Create Temp Buffer
      
     
      vertexShaderCode = GraphicsUtils.getShaderCode("text.vs");
      pixelShaderCode = GraphicsUtils.getShaderCode("text.ps"); 

   	  // prepare shaders and OpenGL program 
      int vertexShader = GraphicsUtils.loadShader(GL_VERTEX_SHADER,
                                                 vertexShaderCode);
      int fragmentShader = GraphicsUtils.loadShader(GL_FRAGMENT_SHADER,
    		  pixelShaderCode); 

      mProgram = glCreateProgram();             	// create empty OpenGL Program
      glAttachShader(mProgram, vertexShader);   	// add the vertex shader to program
      GraphicsUtils.checkGlError("glAttachShader");
      glAttachShader(mProgram, fragmentShader); 	// add the fragment shader to program
      GraphicsUtils.checkGlError("glAttachShader");
      glLinkProgram(mProgram);                  	// create OpenGL program executables
      GraphicsUtils.checkGlError("glLinkProgram");
      // %%% more error checking
      int[] tbuffer = new int[1];
      GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, tbuffer, 0);
      if(tbuffer[0] == GLES20.GL_FALSE) {
          Log.e("ShaderHelper", GLES20.glGetProgramInfoLog(mProgram));
          GLES20.glDeleteProgram(mProgram);
         // return -1;
      }
       
   }


   /**
    * set the specified vertices in the vertex buffer
    * NOTE: optimized to use integer buffer!
    * @param ivertices - array of vertices (floats) to set
    * @param offset - offset to first vertex in array
    * @param length - number of floats in the vertex array (total)
   					  for easy setting use: vtx_cnt * (this.vertexSize / 4)
    */
   public void setVertices(float[] ivertices, int offset, int length)  {
	   
	  this.vertices.clear();    
	  this.vertices.put(ivertices, 0, length );      	// Set New Vertices
	  this.vertices.flip();
      this.numVertices = length / this.vertexStride;  	// Save Number of Vertices
   }


   
   //--Set Indices--//
   // D: 	set the specified indices in the index buffer
   // A: 	indices - array of indices (shorts) to set
   //    	offset - offset to first index in array
   //    	length - number of indices in array (from offset)
   public void setIndices(short[] indices, int offset, int length)  {
	   
      this.indices.clear();                           // Clear Existing Indices
      this.indices.put( indices, offset, length );    // Set New Indices
      this.indices.flip();                            // Flip Index Buffer
      this.numIndices = length;                       // Save Number of Indices
   }

   /**
    * perform all required binding/state changes before rendering batches.
    * USAGE: call once before calling draw() multiple times for this buffer.
    */
   public void bind()  {
	   
	      glUseProgram(mProgram);
 
          // hook up  vertex array with vertex shader input
          mPositionHandle = glGetAttribLocation(mProgram, "vPosition");
          
          GraphicsUtils.checkGlError("glGetAttribLocation");
          
          glEnableVertexAttribArray(mPositionHandle);
          
          glVertexAttribPointer(mPositionHandle, this.positionCnt ,
                                       GL_FLOAT, false,
                                       vertexSize, this.vertices);


          // hook up  vertex array with vertex shader input
          this.vertices.position(this.positionCnt);
          
          mTexCoordHandle = glGetAttribLocation(mProgram, "aTexPos");
          
          GraphicsUtils.checkGlError("glGetAttribLocation");
          
          glEnableVertexAttribArray(mTexCoordHandle);
          
          // &vertexData[0].normal
          glVertexAttribPointer(mTexCoordHandle,TEXCOORD_CNT ,
                                       GL_FLOAT, false,
                                       vertexSize, this.vertices); 


          this.vertices.position(0);
         
          mTextureUniformHandle = GLES20.glGetUniformLocation(mProgram, "uSampler");
       
          // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
          GLES20.glUniform1i(mTextureUniformHandle, 0);
	             
   }

   /**
    * draw the currently bound vertices in the vertex/index buffers
    * USAGE: can only be called after calling bind() for this buffer.
    * @param primitiveType - the type of primitive to draw
    * @param offset - the offset in the vertex/index buffer to start at
    * @param numVertices - the number of vertices (indices) to draw
    */
   public void draw(int primitiveType, int offset, int numVertices)  {
	   
	   final float[] mPlaneMatrix = new float[16];
	   Matrix.setIdentityM(mPlaneMatrix, 0); 	   
 
      if ( indices != null )  {                       		// IF Indices Exist
         indices.position( offset );                  		// Set Index Buffer to Specified Offset
         // Draw Indexed
         glDrawElements( GL_TRIANGLES, numVertices, GLES20.GL_UNSIGNED_SHORT, indices );  
         GraphicsUtils.checkGlError("glDrawElements");
      }
      // ELSE No Indices Exist
      else  {                                         
    	  glDrawArrays( GL_TRIANGLES, offset, numVertices );  // Draw Direct (Array)
      }
   }

   //--Unbind--//
   // D: clear binding states when done rendering batches.
   //    USAGE: call once before calling draw() multiple times for this buffer.
   // A: [none]
   // R: [none]
   public void unbind()  {
	   
       GLES20.glDisableVertexAttribArray(mPositionHandle);

   }

}
