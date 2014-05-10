

/**
 * 
move drawfirstwall and subsequent wall into Hall inner class - 
setcurrentwall will be in the manager class and control the current hall we are going to draw
calculation- minimum distance travelled + zLookAheadDistance -  measurement to the end of the wall[we want to draw the next wall segment before we reach the end]

 */
package com.example.android.mdiii;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.opengl.Matrix;
import android.util.Log;

/**
 * @author slarson
 *
 */
public class HallManager {
	
	
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
	private float currentHallZ;
	private int currentHallIndex;
	private int numberOfHalls;
	private float hallGap;
	private float hallMinLength;
	private float deltaZMinimum;
	private float lastZ;
	private boolean firstWallsdrawn;
	private float endPadding;
	private Map<Coord, Drawable> interactiveObjectMap;
	private List<Contents> contentsList;
	public MyGLRenderer renderer;
	public HashMap<Coord, Drawable> contentsMap;
	
	
	public HallManager(MyGLRenderer renderer, int numberOfHalls, float endPadding, float startX, float endX, float startY, float endY, float startZ, float endZ, float[] wallMatrix, float[] finalWallMatrix, float[] mMVPMatrix, float wallXOffset, float wallYOffset ){
		this.renderer = renderer;
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
		interactiveObjectMap = new HashMap<Coord, Drawable>();
		Log.v(TAG, "HallManager(numberOfHalls:"+this.numberOfHalls +" endgap:" +endPadding +" hallgap:"+hallGap +"startX:"+this.startX +" endX:"+this.endX +"startY:"+this.startY +" endY:"+this.endY+"startZ:"+this.startZ +" endZ:"+this.endZ);
		init();
	}

	@Deprecated
	//move block below into Cotents Factory and give initialize method
	private void init(){
		float hallLength = (endZ - startZ)/numberOfHalls;
		Log.v(TAG, "init() halllength"+hallLength);		
		this.halls = new Hall[numberOfHalls];
		currentHallIndex = 0;
		currentHallZ = 0f;

{
		contentsMap = new HashMap<Coord, Drawable>();
		Coord coord = new Coord(0f, -0.3f, 12.0f );
		Coord coord1 = new Coord(0f, 0f, 10f );
//		Coord coord2 = new Coord(-.250f, 0f, 9.0f );
		Coord coord2 = new Coord(0f, 0f, 9.0f );
		Coord coord3 = new Coord(0f, 0f, 19.0f );
		Coord coord4 = new Coord(0f, 0f, 25.0f );
		Coord coord5 = new Coord(0f, 0f, 30.0f );
		//	vent
		Drawable obj = new Ground();
		
		Drawable obj1 = new Shooter(this, coord1, new Coord(0.0020f, 0, 0),this.mMVPMatrix);
		Drawable obj2 = new Fan(coord2, this.wallMatrix, this.mMVPMatrix, this.finalWallMatrix);
		Drawable obj3 = new Fan(coord3, this.wallMatrix, this.mMVPMatrix, this.finalWallMatrix);
		Drawable obj4 = new Fan(coord4, this.wallMatrix, this.mMVPMatrix, this.finalWallMatrix);
		Drawable obj5 = new Fan(coord5, this.wallMatrix, this.mMVPMatrix, this.finalWallMatrix);
//FIXME put back contents		
		contentsMap.put(coord1, obj1);
		contentsMap.put(coord, obj);
		contentsMap.put(coord2, obj2);
/*		contentsMap.put(coord3, obj3);
	    
	    contentsMap.put(coord4, obj4);
	    contentsMap.put(coord5, obj5);
*/
		contentsList = new ArrayList<Contents>();
		contentsList.add(new Contents(this, contentsMap, wallMatrix, mMVPMatrix, finalWallMatrix));
}

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
					if(true)
		{

//FIXME put back walls							
/*					   	
		if (drawEndlessVerticalWall(currentZ, HallManager.this.wallXOffset) 
				&& drawEndlessVerticalWall(currentZ, -1.0f * HallManager.this.wallXOffset)
	            && drawEndlessHorizontalWall(currentZ, HallManager.this.wallYOffset)
	            && drawEndlessHorizontalWall(currentZ, -1.0f * HallManager.this.wallYOffset)){
*/
						
			boolean results = false;
			// Now draw hall content
			for(int i = 0; i<this.numberOfHalls ; i++){
		
    			boolean drawGood = halls[i].drawHallContents(currentZ);
    			if(drawGood){
    				Log.v(TAG, "draw() - GOOD! hallIndex:"+i +" currentZ:"+currentZ);
    //			if(halls[i].draw(currentZ)){
    				results = true;
    				break;
    			}else{
    				Log.v(TAG, "draw() - BAD! hallIndex:"+i +" currentZ:"+currentZ); 
    			}
    				
			}
			
			if(results){
				Log.d(TAG, "try drawing contents....");
				drawContents();
			}
			Log.v(TAG, "draw("+currentZ +") DID NOT DRAW!!");
			return results;
		}else{
			return false;
		}
	}		
	

	public List<Contents> getContents() {
		return contentsList;
	}
	
	private void drawContents(){
		for(Contents contents : contentsList){
			contents.draw();
		}
	}
	
	protected boolean drawEndlessVerticalWall(float currentZ, float wallOffset){
      int startY = (int) (this.startY * 10.0f) ;
      int startZ = (int) (this.startZ * 10.0f) ; 
      int endY = (int) (this.endY * 10.0f) ;
      int endZ = (int) ( (currentZ + HallManager.this.endPadding) * 10.0f) ;
      Log.v(TAG, "drawEndlessVerticalWall: currentZ:" +currentZ +" lastZ:" +lastZ +" deltaZMinimum:" +deltaZMinimum +" firstWallsdrawn:" +firstWallsdrawn +" startY:" +startY +" endY:"+endY +" startZ:"+startZ +" endZ:"+endZ +" gap:"+HallManager.this.hallGap +" HallManager.this.endPadding:"+HallManager.this.endPadding);               
      for (int gy = startY; gy <= endY; gy+=yIncrement) {
         for (int gz = startZ; gz <= endZ; gz+=zIncrement) {

            float gyFloat = gy / 10.0f;
            float gzFloat = gz / 10.0f;
            tempgYfloat = gyFloat;
            tempgZfloat = gzFloat;

//	          Log.v(TAG, "drawWall:gz:"+gz +" gy:"+gy +" gyFloat:"+gyFloat +" gzFloat:"+gzFloat +" wallXOffset:"+wallXOffset);
            
            // Set the camera position (View matrix)
            Matrix.setIdentityM(wallMatrix, 0);

            // move wall alittle
            Matrix.translateM(wallMatrix, 0, wallOffset, gyFloat, gzFloat);
            Log.v(TAG, "Matrix.translateM(objectMatrix, 0,  gxfloat:" + wallOffset+ ", gyFloat:" + tempgYfloat + ", gzFloat:" + tempgZfloat+ " )");              
            Matrix.multiplyMM(finalWallMatrix, 0, mMVPMatrix, 0,wallMatrix, 0);
            
            // Draw Wall
            wall.draw(finalWallMatrix);
         }
      }
      lastZ = hallMinLength;
      firstWallsdrawn = true;
      return true;   
			
	}	


   protected boolean drawEndlessHorizontalWall(float currentZ, float wallOffset){

      int startX = (int) (this.startX * 10.0f) ;
      int endX = (int) (this.endX * 10.0f) ;
      int startZ = (int) (this.startZ * 10.0f) ;       
      int endZ = (int) ( (currentZ + HallManager.this.endPadding) * 10.0f) ;     
      Log.v(TAG, "drawEndlessHorizontalWall: currentZ:" +currentZ +" lastZ:" +lastZ +" deltaZMinimum:" +deltaZMinimum +" firstWallsdrawn:" +firstWallsdrawn +" startX:" +startX +" endX:"+endX +" startZ:"+startZ +" endZ:"+endZ +" gap:"+HallManager.this.hallGap +" HallManager.this.endPadding:"+HallManager.this.endPadding);               
      for (int gx = startX; gx <= endX; gx+=xIncrement) {   
    	  for (int gz = startZ; gz <= endZ; gz+=zIncrement) {

	        float gxFloat = gx / 10.0f;
	        float gzFloat = gz / 10.0f;
	        tempgXfloat = gxFloat;
	        tempgZfloat = gzFloat;     
	
	//		          Log.v(TAG, "drawWall:gz:"+gz +" gy:"+gy +" gyFloat:"+gyFloat +" gzFloat:"+gzFloat +" wallXOffset:"+wallXOffset);
	        
	        // Set the camera position (View matrix)
	        Matrix.setIdentityM(wallMatrix, 0);
	
	        // move wall alittle
	        Matrix.translateM(wallMatrix, 0,  gxFloat, wallOffset, gzFloat);
	        Log.v(TAG, "Matrix.translateM(objectMatrix, 0,  gxfloat:" + tempgXfloat+ ", gyFloat:" + wallOffset + ", gzFloat:" + tempgZfloat+ " )");              
	        Matrix.multiplyMM(finalWallMatrix, 0, mMVPMatrix, 0,wallMatrix, 0);
	        
	        // Draw Ground
	        ground.draw(finalWallMatrix);
	     }
	  }
      
      Log.d(TAG, "draw contents for real");
//      contents.draw(new Coord(tempgXfloat, tempgYfloat, tempgZfloat));
	  lastZ = hallMinLength;
	  firstWallsdrawn = true;
	  return true;         
      
   }  		
	
	
	public Map<Coord, Drawable> getInteractiveObjectMap() {
		return interactiveObjectMap;
	}
   
	/**
	 * @return the wallXOffset
	 */
	public float getWallXOffset() {
		return wallXOffset;
	}

	/**
	 * @return the wallYOffset
	 */
	public float getWallYOffset() {
		return wallYOffset;
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
		Log.v(TAG, "Hall(startX:"+this.startX +" endX:"+this.endX +"startY:"+this.startY +" endY:"+this.endY+"startZ:"+this.startZ +" endZ:"+this.endZ);
	}

	protected boolean drawHallContents(float currentZ){
		return true;
	}		
	
	}
	
}
