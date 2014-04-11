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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.example.android.mdiii.GraphicsUtils;


public class Vertices {
	
	
	//0808080
	
//	   private final FloatBuffer mVertexBuffer;
//	   private final FloatBuffer mColorBuffer;
//	   private final FloatBuffer mTexCoordBuffer;
/*
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
*/
	   
//	   private  String vertexShaderCode;
//	   private  String pixelShaderCode;

//	    private final ShortBuffer drawListBuffer;
//	    private final int mProgram;
//	    private int mPositionHandle;
//	    private int mColorHandle;
//	    private int mTexCoordHandle;
//	    private int mMVPMatrixHandle;
//	    private int  mTextureDataHandle;
//	    private int  mTextureUniformHandle;
	    
	    // number of coordinates per vertex in this array
	    static final int COORDS_PER_VERTEX = 3;
	    static final int TEX_COORDS_PER_VERTEX = 2;

//	    private final int vertexStride =TEX_COORDS_PER_VERTEX * 4 +  COORDS_PER_VERTEX * 4; // 4 bytes per vertex
	    private final int texVertexStride = TEX_COORDS_PER_VERTEX * 4; // 4 bytes per vertex
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
	      
	      private int aVerPos, aTexPos, aVerCol;
	      private int uSamp;
	      private int texture;
	      private Bitmap textureBitmap;
	      private Context context;
	      
	      //08080

   //--Constants--//
   final static int POSITION_CNT_2D = 2;              // Number of Components in Vertex Position for 2D
   final static int POSITION_CNT_3D = 3;              // Number of Components in Vertex Position for 3D
   final static int COLOR_CNT = 4;                    // Number of Components in Vertex Color
   final static int TEXCOORD_CNT = 2;                 // Number of Components in Vertex Texture Coords
   final static int NORMAL_CNT = 3;                   // Number of Components in Vertex Normal

   final static int INDEX_SIZE = Short.SIZE / 8;      // Index Byte Size (Short.SIZE = bits)

   //--Members--//
   // NOTE: all members are constant, and initialized in constructor!
//   final GL10 gl;                                     // GL Instance
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
//   private int mColorHandle;
   private int mTexCoordHandle;
   private int mMVPMatrixHandle;
   private int  mTextureDataHandle;
   private int  mTextureUniformHandle;

   //--Constructor--//
   // D: create the vertices/indices as specified (for 2d/3d)
   // A: gl - the gl instance to use
   //    maxVertices - maximum vertices allowed in buffer
   //    maxIndices - maximum indices allowed in buffer
   //    hasColor - use color values in vertices
   //    hasTexCoords - use texture coordinates in vertices
   //    hasNormals - use normals in vertices
   //    use3D - (false, default) use 2d positions (ie. x/y only)
   //            (true) use 3d positions (ie. x/y/z)
   public Vertices( int maxVertices, int maxIndices, boolean hasColor, boolean hasTexCoords, boolean hasNormals)  {
      this(maxVertices, maxIndices, hasColor, hasTexCoords, hasNormals, true );  // Call Overloaded Constructor
   }
   public Vertices(int maxVertices, int maxIndices, boolean hasColor, boolean hasTexCoords, boolean hasNormals, boolean use3D)  {
 //     this.gl = gl;                                   // Save GL Instance

	  // context =  GraphicsUtils.context;
 	   //0808080 
	   /*
 
      // Buffers to be passed to gl*Pointer() functions must be
      // direct, i.e., they must be placed on the native heap
      // where the garbage collector cannot move them.
      //
      // Buffers with multi-byte data types (e.g., short, int,
      // float) must have their byte order set to native order
		ByteBuffer vbb = ByteBuffer.allocateDirect(verts.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		mVertexBuffer = vbb.asFloatBuffer();
		mVertexBuffer.put(verts);
		mVertexBuffer.position(0);
		  

		
	//	ByteBuffer nbb = ByteBuffer.allocateDirect(texCoords.length * 4);
	//	nbb.order(ByteOrder.nativeOrder());
	//	mTexCoordBuffer = nbb.asFloatBuffer();
	//	mTexCoordBuffer.put(texCoords);
	//	mTexCoordBuffer.position(0);

	      
        ByteBuffer dlb = ByteBuffer.allocateDirect(cubeIndices.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(cubeIndices);
        drawListBuffer.position(0);
        
        */ // 80808080
    /*    
        vertexShaderCode = GraphicsUtils.getShaderCode("text.vs");
        pixelShaderCode = GraphicsUtils.getShaderCode("text.ps");
        
      
      // prepare shaders and OpenGL program
      int vertexShader = GraphicsUtils.loadShader(GL_VERTEX_SHADER,
                                                 vertexShaderCode);
      int fragmentShader = GraphicsUtils.loadShader(GL_FRAGMENT_SHADER,
    		  pixelShaderCode); 

      mProgram = glCreateProgram();             // create empty OpenGL Program
      glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
      GraphicsUtils.checkGlError("glAttachShader");
      glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
      GraphicsUtils.checkGlError("glAttachShader");
      glLinkProgram(mProgram);                  // create OpenGL program executables
      GraphicsUtils.checkGlError("glLinkProgram");
      // %%% more error checking
      */
   //   initTextures();
      
   //   mTextureDataHandle = loadTexture(context, R.drawable.sullivan);
      //0808080
	   
	   this.hasColor = hasColor;                       // Save Color Flag
      this.hasTexCoords = hasTexCoords;               // Save Texture Coords Flag
      this.hasNormals = hasNormals;                   // Save Normals Flag
      this.positionCnt = use3D ? POSITION_CNT_3D : POSITION_CNT_2D;  // Set Position Component Count
      this.vertexStride = this.positionCnt + ( hasColor ? COLOR_CNT : 0 ) + ( hasTexCoords ? TEXCOORD_CNT : 0 ) + ( hasNormals ? NORMAL_CNT : 0 );  // Calculate Vertex Stride
      this.vertexSize = this.vertexStride * 4;        // Calculate Vertex Byte Size

      ByteBuffer buffer = ByteBuffer.allocateDirect( maxVertices * vertexSize );  // Allocate Buffer for Vertices (Max)
      buffer.order( ByteOrder.nativeOrder() );        // Set Native Byte Order
      this.vertices = buffer.asFloatBuffer();           // Save Vertex Buffer

      if ( maxIndices > 0 )  {                        // IF Indices Required
         buffer = ByteBuffer.allocateDirect( maxIndices * 2 );  // Allocate Buffer for Indices (MAX)
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

      mProgram = glCreateProgram();             // create empty OpenGL Program
      glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
      GraphicsUtils.checkGlError("glAttachShader");
      glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
      GraphicsUtils.checkGlError("glAttachShader");
      glLinkProgram(mProgram);                  // create OpenGL program executables
      GraphicsUtils.checkGlError("glLinkProgram");
      // %%% more error checking
      int[] tbuffer = new int[1];
      GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, tbuffer, 0);
      if(tbuffer[0] == GLES20.GL_FALSE) {
          Log.e("ShaderHelper", GLES20.glGetProgramInfoLog(mProgram));
          GLES20.glDeleteProgram(mProgram);
         // return -1;
      }
       
  //    mTextureDataHandle = loadTexture(context, R.drawable.sullivan); //%%%

   }

   
   public static int loadTexture(final Context context, final int resourceId)
   {
       final int[] textureHandle = new int[1];
    
       GLES20.glGenTextures(1, textureHandle, 0);
       GraphicsUtils.checkGlError("glGenTextures");
       
       if (textureHandle[0] != 0)
       {
           final BitmapFactory.Options options = new BitmapFactory.Options();
           options.inScaled = false;   // No pre-scaling
    
           // Read in the resource
           final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
     
           // Bind to the texture in OpenGL
           GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
           GraphicsUtils.checkGlError("glBindTexture");
           // Set filtering
           GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
           GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
           GraphicsUtils.checkGlError("glTexParameteri");
           // Load the bitmap into the bound texture.
           GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
           GraphicsUtils.checkGlError("texImage2D");
           // Recycle the bitmap, since its data has been loaded into OpenGL.
           bitmap.recycle();
       }
       if (textureHandle[0] == 0)
       {
           throw new RuntimeException("Error loading texture.");
       }
    
       return textureHandle[0];
   } 

   //--Set Vertices--//
   // D: set the specified vertices in the vertex buffer
   //    NOTE: optimized to use integer buffer!
   // A: vertices - array of vertices (floats) to set
   //    offset - offset to first vertex in array
   //    length - number of floats in the vertex array (total)
   //             for easy setting use: vtx_cnt * (this.vertexSize / 4)
   // R: [none]
   public void setVertices(float[] ivertices, int offset, int length)  {
	   /*  wha???
      this.vertices.clear();                          // Remove Existing Vertices
      int last = offset + length;                     // Calculate Last Element
      for ( int i = offset, j = 0; i < last; i++, j++ )  // FOR Each Specified Vertex
         tmpBuffer[j] = Float.floatToRawIntBits( vertices[i] );  // Set Vertex as Raw Integer Bits in Buffer
      this.vertices.put( tmpBuffer, 0, length );      // Set New Vertices
      this.vertices.flip();                           // Flip Vertex Buffer
      */
	  this.vertices.clear();    
	  this.vertices.put(ivertices, 0, length );      // Set New Vertices
	  this.vertices.flip();
      this.numVertices = length / this.vertexStride;  // Save Number of Vertices
      //this.numVertices = length / ( this.vertexSize / 4 );  // Save Number of Vertices
   }


   
   //--Set Indices--//
   // D: set the specified indices in the index buffer
   // A: indices - array of indices (shorts) to set
   //    offset - offset to first index in array
   //    length - number of indices in array (from offset)
   // R: [none]
   public void setIndices(short[] indices, int offset, int length)  {
      this.indices.clear();                           // Clear Existing Indices
      this.indices.put( indices, offset, length );    // Set New Indices
   //   this.indices.position(0);
      this.indices.flip();                            // Flip Index Buffer
      this.numIndices = length;                       // Save Number of Indices
   }

   //--Bind--//
   // D: perform all required binding/state changes before rendering batches.
   //    USAGE: call once before calling draw() multiple times for this buffer.
   // A: [none]
   // R: [none]
   public void bind()  {
	   
	//      if (true) return; // 707070
	      glUseProgram(mProgram);
 
             
	   //   this.vertices.position(0);
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
	             glVertexAttribPointer(mTexCoordHandle,TEXCOORD_CNT ,
	                                          GL_FLOAT, false,
	                                          vertexSize, this.vertices); // &vertexData[0].normal

	             this.vertices.position(0);
	            
	             mTextureUniformHandle = GLES20.glGetUniformLocation(mProgram, "uSampler");
	        //     mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_TexCoordinate");
	          
	             // Set the active texture unit to texture unit 0.
	          //   GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
	          
	             // Bind the texture to this unit.
	          //   GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle); // %%%
	          
	             // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
	             GLES20.glUniform1i(mTextureUniformHandle, 0);
	             
	             /*
	      
      gl.glEnableClientState( GL10.GL_VERTEX_ARRAY ); // Enable Position in Vertices
      vertices.position( 0 );                         // Set Vertex Buffer to Position
      gl.glVertexPointer( positionCnt, GL10.GL_FLOAT, vertexSize, vertices );  // Set Vertex Pointer

      if ( hasColor )  {                              // IF Vertices Have Color
         gl.glEnableClientState( GL10.GL_COLOR_ARRAY );  // Enable Color in Vertices
         vertices.position( positionCnt );            // Set Vertex Buffer to Color
         gl.glColorPointer( COLOR_CNT, GL10.GL_FLOAT, vertexSize, vertices );  // Set Color Pointer
      }

      if ( hasTexCoords )  {                          // IF Vertices Have Texture Coords
         gl.glEnableClientState( GL10.GL_TEXTURE_COORD_ARRAY );  // Enable Texture Coords in Vertices
         vertices.position( positionCnt + ( hasColor ? COLOR_CNT : 0 ) );  // Set Vertex Buffer to Texture Coords (NOTE: position based on whether color is also specified)
         gl.glTexCoordPointer( TEXCOORD_CNT, GL10.GL_FLOAT, vertexSize, vertices );  // Set Texture Coords Pointer
      }

      if ( hasNormals )  {
         gl.glEnableClientState( GL10.GL_NORMAL_ARRAY );  // Enable Normals in Vertices
         vertices.position( positionCnt + ( hasColor ? COLOR_CNT : 0 ) + ( hasTexCoords ? TEXCOORD_CNT : 0 ) );  // Set Vertex Buffer to Normals (NOTE: position based on whether color/texcoords is also specified)
         gl.glNormalPointer( GL10.GL_FLOAT, vertexSize, vertices );  // Set Normals Pointer
      }
      */
   }

   //--Draw--//
   // D: draw the currently bound vertices in the vertex/index buffers
   //    USAGE: can only be called after calling bind() for this buffer.
   // A: primitiveType - the type of primitive to draw
   //    offset - the offset in the vertex/index buffer to start at
   //    numVertices - the number of vertices (indices) to draw
   // R: [none]
   public void draw(int primitiveType, int offset, int numVertices)  {
	   
	    final float[] mPlaneMatrix = new float[16];
	    Matrix.setIdentityM(mPlaneMatrix, 0); 
//	    draw(mPlaneMatrix);
//	    if (true) return;

	   
 
      if ( indices != null )  {                       // IF Indices Exist
         indices.position( offset );                  // Set Index Buffer to Specified Offset
         glDrawElements( GL_TRIANGLES, numVertices, GLES20.GL_UNSIGNED_SHORT, indices );  // Draw Indexed
         GraphicsUtils.checkGlError("glDrawElements");
      }
      else  {                                         // ELSE No Indices Exist
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
       /*
      if ( hasColor )                                 // IF Vertices Have Color
         gl.glDisableClientState( GL10.GL_COLOR_ARRAY );  // Clear Color State

      if ( hasTexCoords )                             // IF Vertices Have Texture Coords
         gl.glDisableClientState( GL10.GL_TEXTURE_COORD_ARRAY );  // Clear Texture Coords State

      if ( hasNormals )                               // IF Vertices Have Normals
         gl.glDisableClientState( GL10.GL_NORMAL_ARRAY );  // Clear Normals State
         */
   }

   //--Draw Full--//
   // D: draw the vertices in the vertex/index buffers
   //    NOTE: unoptimized version! use bind()/draw()/unbind() for batches
   // A: primitiveType - the type of primitive to draw
   //    offset - the offset in the vertex/index buffer to start at
   //    numVertices - the number of vertices (indices) to draw
   // R: [none]
   /*
   public void drawFull(int primitiveType, int offset, int numVertices)  {
      gl.glEnableClientState( GL10.GL_VERTEX_ARRAY ); // Enable Position in Vertices
      vertices.position( 0 );                         // Set Vertex Buffer to Position
      gl.glVertexPointer( positionCnt, GL10.GL_FLOAT, vertexSize, vertices );  // Set Vertex Pointer

      if ( hasColor )  {                              // IF Vertices Have Color
         gl.glEnableClientState( GL10.GL_COLOR_ARRAY );  // Enable Color in Vertices
         vertices.position( positionCnt );            // Set Vertex Buffer to Color
         gl.glColorPointer( COLOR_CNT, GL10.GL_FLOAT, vertexSize, vertices );  // Set Color Pointer
      }

      if ( hasTexCoords )  {                          // IF Vertices Have Texture Coords
         gl.glEnableClientState( GL10.GL_TEXTURE_COORD_ARRAY );  // Enable Texture Coords in Vertices
         vertices.position( positionCnt + ( hasColor ? COLOR_CNT : 0 ) );  // Set Vertex Buffer to Texture Coords (NOTE: position based on whether color is also specified)
         gl.glTexCoordPointer( TEXCOORD_CNT, GL10.GL_FLOAT, vertexSize, vertices );  // Set Texture Coords Pointer
      }

      if ( indices != null )  {                       // IF Indices Exist
         indices.position( offset );                  // Set Index Buffer to Specified Offset
         gl.glDrawElements( primitiveType, numVertices, GL10.GL_UNSIGNED_SHORT, indices );  // Draw Indexed
      }
      else  {                                         // ELSE No Indices Exist
         gl.glDrawArrays( primitiveType, offset, numVertices );  // Draw Direct (Array)
      }

      if ( hasTexCoords )                             // IF Vertices Have Texture Coords
         gl.glDisableClientState( GL10.GL_TEXTURE_COORD_ARRAY );  // Clear Texture Coords State

      if ( hasColor )                                 // IF Vertices Have Color
         gl.glDisableClientState( GL10.GL_COLOR_ARRAY );  // Clear Color State
   }
   */

   //--Set Vertex Elements--//
   // D: use these methods to alter the values (position, color, textcoords, normals) for vertices
   //    WARNING: these do NOT validate any values, ensure that the index AND specified
   //             elements EXIST before using!!
   // A: x, y, z - the x,y,z position to set in buffer
   //    r, g, b, a - the r,g,b,a color to set in buffer
   //    u, v - the u,v texture coords to set in buffer
   //    nx, ny, nz - the x,y,z normal to set in buffer
   // R: [none]
   /*
   void setVtxPosition(int vtxIdx, float x, float y)  {
      int index = vtxIdx * vertexStride;              // Calculate Actual Index
      vertices.put( index + 0, Float.floatToRawIntBits( x ) );  // Set X
      vertices.put( index + 1, Float.floatToRawIntBits( y ) );  // Set Y
   }
   void setVtxPosition(int vtxIdx, float x, float y, float z)  {
      int index = vtxIdx * vertexStride;              // Calculate Actual Index
      vertices.put( index + 0, Float.floatToRawIntBits( x ) );  // Set X
      vertices.put( index + 1, Float.floatToRawIntBits( y ) );  // Set Y
      vertices.put( index + 2, Float.floatToRawIntBits( z ) );  // Set Z
   }
   void setVtxColor(int vtxIdx, float r, float g, float b, float a)  {
      int index = ( vtxIdx * vertexStride ) + positionCnt;  // Calculate Actual Index
      vertices.put( index + 0, Float.floatToRawIntBits( r ) );  // Set Red
      vertices.put( index + 1, Float.floatToRawIntBits( g ) );  // Set Green
      vertices.put( index + 2, Float.floatToRawIntBits( b ) );  // Set Blue
      vertices.put( index + 3, Float.floatToRawIntBits( a ) );  // Set Alpha
   }
   void setVtxColor(int vtxIdx, float r, float g, float b)  {
      int index = ( vtxIdx * vertexStride ) + positionCnt;  // Calculate Actual Index
      vertices.put( index + 0, Float.floatToRawIntBits( r ) );  // Set Red
      vertices.put( index + 1, Float.floatToRawIntBits( g ) );  // Set Green
      vertices.put( index + 2, Float.floatToRawIntBits( b ) );  // Set Blue
   }
   void setVtxColor(int vtxIdx, float a)  {
      int index = ( vtxIdx * vertexStride ) + positionCnt;  // Calculate Actual Index
      vertices.put( index + 3, Float.floatToRawIntBits( a ) );  // Set Alpha
   }
   void setVtxTexCoords(int vtxIdx, float u, float v)  {
      int index = ( vtxIdx * vertexStride ) + positionCnt + ( hasColor ? COLOR_CNT : 0 );  // Calculate Actual Index
      vertices.put( index + 0, Float.floatToRawIntBits( u ) );  // Set U
      vertices.put( index + 1, Float.floatToRawIntBits( v ) );  // Set V
   }
   void setVtxNormal(int vtxIdx, float x, float y, float z)  {
      int index = ( vtxIdx * vertexStride ) + positionCnt + ( hasColor ? COLOR_CNT : 0 ) + ( hasTexCoords ? TEXCOORD_CNT : 0 );  // Calculate Actual Index
      vertices.put( index + 0, Float.floatToRawIntBits( x ) );  // Set X
      vertices.put( index + 1, Float.floatToRawIntBits( y ) );  // Set Y
      vertices.put( index + 2, Float.floatToRawIntBits( z ) );  // Set Z
   }
   */
}
