
/**
 * 
move drawfirstwall and subsequent wall into Hall inner class - 
setcurrentwall will be in the manager class and control the current hall we are going to draw
calculation- minimum distance travelled + zLookAheadDistance -  measurement to the end of the wall[we want to draw the next wall segment before we reach the end]

 */
package com.example.android.mdiii;

import android.opengl.Matrix;
import android.util.Log;

/**
 * @author Anna
 *
 */
public class CopyOfHallManager_3_20_2014_1953 {
	

	
	
	private final String TAG = "HallManager";
	private int yIncrement;
	private int zIncrement;
	private float tempgYfloat;
	private float tempgZfloat;
	private Wall wall;
	private float startX;
	private float endX;
	private float startY;
	private float endY;
	private float startZ;
	private float endZ;
	private float[] wallMatrix;
	private float[] finalWallMatrix;
	private float[] mMVPMatrix;
	private float wallXOffset;
	private int xIncrement;
	private Ground ground;
	private float tempgXfloat;
	private float wallYOffset;
	private Hall[] halls;
	private Hall currentHall;
	private int numberOfHalls;
	private float hallGap;
	private float hallMinLength;
	private float deltaZMinimum;
	private float lastZ;
	private boolean firstWallsdrawn;
	private float endPadding;
	
	
	public CopyOfHallManager_3_20_2014_1953(int numberOfHalls, float endPadding, float startX, float endX, float startY, float endY, float startZ, float endZ, float[] wallMatrix, float[] finalWallMatrix, float[] mMVPMatrix, float wallXOffset, float wallYOffset ){
		this.numberOfHalls = numberOfHalls;  
		this.endPadding = endPadding;
		this.startX = startX;
		this.endX = endX;
		this.startY = startY;
		this.endY = endY;
		this.startZ = startZ;
		this.endZ = endZ;
		this.wallMatrix = wallMatrix;
		this.finalWallMatrix = finalWallMatrix;
		this.mMVPMatrix = mMVPMatrix; 
		this.wallXOffset = wallXOffset;		
		this.wallYOffset = wallYOffset;
		yIncrement = 10;
		zIncrement = 10;
		xIncrement = 10;
		wall = new Wall();
		ground = new Ground();
		hallMinLength = 10.0f;
		deltaZMinimum = 1.0f;
		////Log.v(TAG, "HallManager(numberOfHalls:"+this.numberOfHalls +" endgap:" +endPadding +" hallgap:"+hallGap +"startX:"+this.startX +" endX:"+this.endX +"startY:"+this.startY +" endY:"+this.endY+"startZ:"+this.startZ +" endZ:"+this.endZ);
		init();
	}

	private void init(){
		float hallLength = (endZ - startZ)/numberOfHalls;
		////Log.v(TAG, "init() halllength"+hallLength);
		this.halls = new Hall[numberOfHalls];
		for(int i=0; i< numberOfHalls; i++){
			if(i==0){
				halls[i] = new Hall(startX, endX, startY, endY, startZ, startZ + hallLength);
			}else{
				float nextStartZ = halls[i-1].endZ + this.hallGap;
				float nextEndz = nextStartZ + hallLength;
				halls[i] = new Hall(startX, endX, startY, endY, nextStartZ, nextEndz);
			}
		}
	}	
	
	
	protected boolean draw(float currentZ){
		for(int i = 0; i<this.numberOfHalls ; i++){
	
			boolean drawGood = halls[i].draw(currentZ);
			if(drawGood){
				////Log.v(TAG, "draw() - GOOD! hallIndex:"+i +" currentZ:"+currentZ);
//			if(halls[i].draw(currentZ)){
				return true;
			}else{
				////Log.v(TAG, "draw() - BAD! hallIndex:"+i +" currentZ:"+currentZ); 
			}
				
		}
		////Log.v(TAG, "draw("+currentZ +") DID NOT DRAW!!");
		return false;
	}
	

	
	private void setCurrentHall(float currentZ) {
		// TODO Auto-generated method stub
		for(Hall hall : halls){
			
		}
	}
	
	public class Hall {	
	
	private static final String TAG = "Hall";
	private float startZ;
	private float endZ;
	private float startY;
	private float endY;
	private float startX;
	private float endX;
	

	public Hall(float startX, float endX,float startY, float endY, float startZ, float endZ) {
		super();
		this.startZ = startZ;
		this.endZ = endZ;
		this.startY = startY;
		this.endY = endY;
		this.startX = startX;
		this.endX = endX;
		////Log.v(TAG, "Hall(startX:"+this.startX +" endX:"+this.endX +"startY:"+this.startY +" endY:"+this.endY+"startZ:"+this.startZ +" endZ:"+this.endZ);
	}

	protected boolean draw(float currentZ){
      return drawEndlessVerticalWall(currentZ, CopyOfHallManager_3_20_2014_1953.this.wallXOffset)
            && drawEndlessVerticalWall(currentZ, -1.0f * CopyOfHallManager_3_20_2014_1953.this.wallXOffset)
            && drawEndlessHorizontalWall(currentZ, CopyOfHallManager_3_20_2014_1953.this.wallYOffset)
            && drawEndlessHorizontalWall(currentZ, -1.0f * CopyOfHallManager_3_20_2014_1953.this.wallYOffset);		
	}		
	
	protected boolean drawEndlessVerticalWall(float currentZ, float wallOffset){
      int startY = (int) (this.startY * 10.0f) ;
      int startZ = (int) (this.startZ * 10.0f) ; 
      int endY = (int) (this.endY * 10.0f) ;
      int endZ = (int) ( (currentZ + CopyOfHallManager_3_20_2014_1953.this.endPadding) * 10.0f) ;
      //Log.v(TAG, "drawEndlessVerticalWall: currentZ:" +currentZ +" lastZ:" +lastZ +" deltaZMinimum:" +deltaZMinimum +" firstWallsdrawn:" +firstWallsdrawn +" startY:" +startY +" endY:"+endY +" startZ:"+startZ +" endZ:"+endZ +" gap:"+HallManager.this.hallGap +" HallManager.this.endPadding:"+HallManager.this.endPadding);               
      for (int gy = startY; gy <= endY; gy+=yIncrement) {
         for (int gz = startZ; gz <= endZ; gz+=zIncrement) {

            float gyFloat = gy / 10.0f;
            float gzFloat = gz / 10.0f;
            tempgYfloat = gyFloat;
            tempgZfloat = gzFloat;

//          //Log.v(TAG, "drawWall:gz:"+gz +" gy:"+gy +" gyFloat:"+gyFloat +" gzFloat:"+gzFloat +" wallXOffset:"+wallXOffset);
            
            // Set the camera position (View matrix)
            Matrix.setIdentityM(wallMatrix, 0);

            // move wall alittle
            Matrix.translateM(wallMatrix, 0, wallOffset, gyFloat, gzFloat);
            //Log.v(TAG, "Matrix.translateM(objectMatrix, 0,  gxfloat:" + wallOffset+ ", gyFloat:" + tempgYfloat + ", gzFloat:" + tempgZfloat+ " )");              
            Matrix.multiplyMM(finalWallMatrix, 0, mMVPMatrix, 0,wallMatrix, 0);
            
            // Draw Wall
            wall.draw(finalWallMatrix);
         }
      }
      lastZ = hallMinLength;
      firstWallsdrawn = true;
      return true;   
		
	}	


	public boolean drawHorizontal(float currentZ, float wallYOffset){
		if(this.startZ < currentZ &&  currentZ < this.endZ ){
			
			int startX = (int) (this.startX * 10.0f) ;
			int endX = (int) (this.endX * 10.0f) ;
			int startZ = (int) (this.startZ * 10.0f) ; 
			int endZ = (int) (this.endZ * 10.0f) ;
			//Log.v(TAG, "drawHorizontalPlanes: startY:"+startY +" endY:"+endY +" startZ:"+startZ +" endZ:"+endZ+" gap:"+HallManager.this.hallGap);		
		for (int gx = startX; gx <= endX; gx+=xIncrement) {		
//		for (int gx = -1 * groundX; gx <= groundX; gx++) {
			for (int gz = startZ; gz <= endZ; gz+=zIncrement) {			

		       float gxFloat = gx / 10.0f;
		       float gzFloat = gz / 10.0f;
		       tempgXfloat = gxFloat;
		       tempgZfloat = gzFloat;		       
		
		       // Set the camera position (View matrix)
		       Matrix.setIdentityM(wallMatrix, 0);
		
		       //	Move the Object of interest on the Screen
		        Matrix.translateM(wallMatrix, 0,  gxFloat, wallYOffset, gzFloat);
//		        //Log.v(TAG, "Matrix.translateM(wallMatrix, 0,  gxfloat:"+gxFloat +", gyFloat:"+wallXOffset +", gzFloat:"+gzFloat +" )");
		
		        // Combine the Object of interest matrix with the projection and camera view
		        Matrix.multiplyMM(finalWallMatrix, 0, mMVPMatrix, 0, wallMatrix, 0);
		
		    	// Draw Ground
		    	ground.draw(finalWallMatrix);
    		}
		}				
		//Log.v(TAG, "drawHorizontalPlanes:gxFloat:" +tempgXfloat +" gZFloat:" +tempgZfloat +"wallXOffset:"+wallXOffset+" gap:"+HallManager.this.hallGap);			
		
		return true;
	}else{
		//Log.v(TAG, "\n\ndrawHorizontalPlanes failed - currentZ:"+currentZ+" startZ:"+startZ +" endZ:"+endZ+" gap:"+HallManager.this.hallGap);
		return false;
	}
		
	}	
	
   protected boolean drawEndlessHorizontalWall(float currentZ, float wallOffset){

      int startX = (int) (this.startX * 10.0f) ;
      int endX = (int) (this.endX * 10.0f) ;
      int startZ = (int) (this.startZ * 10.0f) ;       
      int endZ = (int) ( (currentZ + CopyOfHallManager_3_20_2014_1953.this.endPadding) * 10.0f) ;     
      //Log.v(TAG, "drawEndlessHorizontalWall: currentZ:" +currentZ +" lastZ:" +lastZ +" deltaZMinimum:" +deltaZMinimum +" firstWallsdrawn:" +firstWallsdrawn +" startX:" +startX +" endX:"+endX +" startZ:"+startZ +" endZ:"+endZ +" gap:"+HallManager.this.hallGap +" HallManager.this.endPadding:"+HallManager.this.endPadding);               
      for (int gx = startX; gx <= endX; gx+=xIncrement) {   
         for (int gz = startZ; gz <= endZ; gz+=zIncrement) {

            float gxFloat = gx / 10.0f;
            float gzFloat = gz / 10.0f;
            tempgXfloat = gxFloat;
            tempgZfloat = gzFloat;     

//          //Log.v(TAG, "drawWall:gz:"+gz +" gy:"+gy +" gyFloat:"+gyFloat +" gzFloat:"+gzFloat +" wallXOffset:"+wallXOffset);
            
            // Set the camera position (View matrix)
            Matrix.setIdentityM(wallMatrix, 0);

            // move wall alittle
            Matrix.translateM(wallMatrix, 0,  gxFloat, wallOffset, gzFloat);
            //Log.v(TAG, "Matrix.translateM(objectMatrix, 0,  gxfloat:" + tempgXfloat+ ", gyFloat:" + wallOffset + ", gzFloat:" + tempgZfloat+ " )");              
            Matrix.multiplyMM(finalWallMatrix, 0, mMVPMatrix, 0,wallMatrix, 0);
            
            // Draw Ground
            ground.draw(finalWallMatrix);
         }
      }
      lastZ = hallMinLength;
      firstWallsdrawn = true;
      return true;         
      
   }  	
	
	}
	
}
