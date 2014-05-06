package com.example.android.mdiii.text;

import android.opengl.GLES20;

public class SpriteBatch {

   //--Constants--//
   final static int VERTEX_SIZE = 5;                  // Vertex Size (in Components) ie. (X,Y,Z,U,V)
   final static int VERTICES_PER_SPRITE = 4;           // Vertices Per Sprite
   final static int INDICES_PER_SPRITE = 6;            // Indices Per Sprite

   //--Members--//
   Vertices vertices;                                 // Vertices Instance Used for Rendering
   float[] vertexBuffer;                              // Vertex Buffer
   int bufferIndex;                                   // Vertex Buffer Start Index
   int maxSprites;                                    // Maximum Sprites Allowed in Buffer
   int numSprites;                                    // Number of Sprites Currently in Buffer


   /**
    * prepare the sprite batcher for specified maximum number of sprites
    * @param maxSprites - the maximum allowed sprites per batch
    */
   public SpriteBatch(int maxSprites)  {
	   // Create Vertex Buffer 
      this.vertexBuffer = new float[maxSprites * VERTICES_PER_SPRITE * VERTEX_SIZE];  
      this.vertices = new Vertices( maxSprites * VERTICES_PER_SPRITE, maxSprites * INDICES_PER_SPRITE, false, true, false );  // Create Rendering Vertices
      this.bufferIndex = 0;                           // Reset Buffer Index
      this.maxSprites = maxSprites;                   // Save Maximum Sprites
      this.numSprites = 0;                            // Clear Sprite Counter

      // Create Temp Index Buffer
      short[] indices = new short[maxSprites * INDICES_PER_SPRITE];  
      int len = indices.length;                       // Get Index Buffer Length
      short j = 0;                                    // Counter
      
      // FOR Each Index Set (Per Sprite)
      for ( int i = 0; i < len; i+= INDICES_PER_SPRITE, j += VERTICES_PER_SPRITE )  {  
         indices[i + 0] = (short)( j + 0 );           // Calculate Index 0
         indices[i + 1] = (short)( j + 1 );           // Calculate Index 1
         indices[i + 2] = (short)( j + 2 );           // Calculate Index 2
         indices[i + 3] = (short)( j + 2 );           // Calculate Index 3
         indices[i + 4] = (short)( j + 3 );           // Calculate Index 4
         indices[i + 5] = (short)( j + 0 );           // Calculate Index 5
      }
      vertices.setIndices( indices, 0, len );         // Set Index Buffer for Rendering
   }
   
   /**
    * signal the start of a batch. set the texture and clear buffer
    * NOTE: the overloaded (non-texture) version assumes that the texture is already bound!
    * @param textureId
    */
   public void beginBatch(int textureId)  {
      numSprites = 0;                                 // Empty Sprite Counter
      bufferIndex = 0;                                // Reset Buffer Index (Empty)
   }
   
   public void beginBatch()  {
      numSprites = 0;                                 // Empty Sprite Counter
      bufferIndex = 0;                                // Reset Buffer Index (Empty)
   }

   
   /**
    * signal the end of a batch. render the batched sprites
    * 
    */
   public void endBatch()  {
	  // IF Any Sprites to Render	   
      if ( numSprites > 0 )  {                        
    	 // Set Vertices from Buffer 
         vertices.setVertices( vertexBuffer, 0, bufferIndex );
         // Bind Vertices
         vertices.bind();                             
         // Render Batched Sprites
         vertices.draw( GLES20.GL_TRIANGLES, 0, numSprites * INDICES_PER_SPRITE );
         // Unbind Vertices
         vertices.unbind();                           
      }
   }


   /**
    * batch specified sprite to batch. adds vertices for sprite to vertex buffer
   		NOTE: MUST be called after beginBatch(), and before endBatch()!
   		NOTE: if the batch overflows, this will render the current batch, restart it,
   		and then batch this sprite.
    * @param x - the x position of the sprite (center)
    * @param y - the y position of the sprite (center)
    * @param width - the width of the sprite
    * @param height - the height of the sprite
    * @param region- the texture region to use for sprite
    */
   public void drawSprite(float x, float y, float width, float height, TextureRegion region)  {
	   
      if ( numSprites == maxSprites )  {              // IF Sprite Buffer is Full
         endBatch();                                  // End Batch
         // NOTE: leave current texture bound!!
         numSprites = 0;                              // Empty Sprite Counter
         bufferIndex = 0;                             // Reset Buffer Index (Empty)
      }

      float halfWidth = width / 2.0f;                 // Calculate Half Width
      float halfHeight = height / 2.0f;               // Calculate Half Height
      float x1 = x - halfWidth;                       // Calculate Left X
      float y1 = y - halfHeight;                      // Calculate Bottom Y
      float x2 = x + halfWidth;                       // Calculate Right X
      float y2 = y + halfHeight;                      // Calculate Top Y
      float z = 0.0f;

      vertexBuffer[bufferIndex++] = x1;               // Add X for Vertex 0
      vertexBuffer[bufferIndex++] = y1;               // Add Y for Vertex 0
      vertexBuffer[bufferIndex++] = z;                
      vertexBuffer[bufferIndex++] = region.u1;        // Add U for Vertex 0
      vertexBuffer[bufferIndex++] = region.v2;        // Add V for Vertex 0

      vertexBuffer[bufferIndex++] = x2;               // Add X for Vertex 1
      vertexBuffer[bufferIndex++] = y1;               // Add Y for Vertex 1
      vertexBuffer[bufferIndex++] = z;                
      vertexBuffer[bufferIndex++] = region.u2;        // Add U for Vertex 1
      vertexBuffer[bufferIndex++] = region.v2;        // Add V for Vertex 1

      vertexBuffer[bufferIndex++] = x2;               // Add X for Vertex 2
      vertexBuffer[bufferIndex++] = y2;               // Add Y for Vertex 2
      vertexBuffer[bufferIndex++] = z;                
      vertexBuffer[bufferIndex++] = region.u2;        // Add U for Vertex 2
      vertexBuffer[bufferIndex++] = region.v1;        // Add V for Vertex 2

      vertexBuffer[bufferIndex++] = x1;               // Add X for Vertex 3
      vertexBuffer[bufferIndex++] = y2;               // Add Y for Vertex 3
      vertexBuffer[bufferIndex++] = z;                
      vertexBuffer[bufferIndex++] = region.u1;        // Add U for Vertex 3
      vertexBuffer[bufferIndex++] = region.v1;        // Add V for Vertex 3

      numSprites++;                                   // Increment Sprite Count
   }
}
