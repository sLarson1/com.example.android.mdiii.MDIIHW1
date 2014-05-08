/*
for collision detection
draw line from center of plane to center of shooter
if length(line) > radius of plane + radius of shooter
then
	no collision
else there is a collision
change shape to blade or cube from Lesson 4?
fix fan
make plane the plane and not ground
*/
/**
 * 
 */
package com.example.android.mdiii;

import android.opengl.Matrix;
import android.util.Log;

/**
 * @author slarson@twia.org
 * @since May 7, 2014
 *
 */
public class Shooter implements Drawable {

	private final static String TAG = "Shooter";
	private HallManager hallManager;
	private Drawable drawable;
	private float[] mMVPMatrix;
	private Coord position;
	private Coord movementRate;
	private float collisionPadding;
	private float shooterHeight;
	private float shooterWidth;
	//TODO - THIS SHOULD REALLY BE A RECTANGLE CALCULATION TOO.
	/**How close are the Z centers of both objects */
	private float zTolerance;
	private boolean isFinished;
	private float xTolerance;
	
	
	
	

	public Shooter(HallManager hallManager, Coord position, Coord movementRate, float[] mMVPMatrix) {
		super();
		this.hallManager = hallManager;
		this.position = position;
		this.movementRate = movementRate;		
		this.mMVPMatrix = mMVPMatrix;
		collisionPadding = 0.0f;
		drawable = new Ground();
		
		shooterWidth = 0.25f;
		shooterHeight = 0.25f;
		zTolerance = 0.50f;
		xTolerance = 0.50f;
	}


	/* (non-Javadoc)
	 * @see com.example.android.mdiii.Drawable#draw(com.example.android.mdiii.Coord, float[])
	 */
	@Override
	public void draw(Coord coord, float[] mvpMatrix) {
		Log.i(TAG , "drawing entity:"+drawable);		
		
		if(!isFinished()) {	        
			float[] movementMatrix= new float[16];
			float[] shooterMatrix = new float[16];

			Matrix.setIdentityM(movementMatrix, 0);
			Matrix.setIdentityM(shooterMatrix, 0);
			
	        Matrix.translateM(movementMatrix, 0, position.getX(), position.getY(), position.getZ() );
	        
	        Log.e(TAG, "draw(objectMatrix, 0,  position.getX():" + position.getX()+ ", position.getY():" + position.getY() + ", position.getZ():" + position.getZ() + " )");              
	        Matrix.multiplyMM(shooterMatrix, 0, mMVPMatrix, 0,movementMatrix, 0);
	        
	        drawable.draw(shooterMatrix);		
			move();
		}

		//TODO Just for debugging		
		if(hitPlane()){
			String message = "draw failed - hit wall! x:"+position.getX() +" y:"+position.getY() +" z:"+position.getZ();
			Log.i(TAG, message);
			this.hallManager.renderer.pitchMessage = message;
//			System.exit(0);
		}

	}
	
	//TODO move collision detection into its own class:CollisionDetection - it handles all collision detection
	//TODO In collision class have 3 options: bounding box and spherical(Use plane equation) and capsule
	private boolean hitWall() {
		Log.i(TAG, "hitWall() movementRate.getX():"
				+movementRate.getX()+" collisionPadding:"+collisionPadding
				+" hallManager.getWallXOffset():"+hallManager.getWallXOffset());
		
		if(
			(	
    			((movementRate.getX() < 0.0) 
    				&& (position.getX() - collisionPadding - shooterWidth) <= (-hallManager.getWallXOffset()))
    			||
    			((movementRate.getX() > 0.0) 
    					&& (position.getX() + collisionPadding + shooterWidth) >= hallManager.getWallXOffset())
    		)

			
		){
			String message = "collision detected! x:"+position.getX() +" y:"+position.getY() +" z:"+position.getZ() +" left wall:"+(-hallManager.getWallXOffset() +" right wall:"+hallManager.getWallXOffset() );
			Log.i(TAG, message);
			hallManager.renderer.pitchMessage = message;
			return true;			
		}else {
			return false;
		}
	}

	public boolean hitPlane(){

		float planeX = hallManager.renderer.getPlaneX();
		float planeY = hallManager.renderer.getPlaneY();
		float planeHeight = hallManager.renderer.getPlaneHeight();
		float planeWidth = hallManager.renderer.getPlaneWidth();
		float planeZ = hallManager.renderer.getPlaneZ();
		boolean planeHit = false;
		
		if(
			(
    			//	X collision
    			Math.abs(position.getX() - planeX) < xTolerance					
			)	
			&&
    		( 
    			//	Y collision	
    			((position.getY() + shooterHeight) > (planeY - planeHeight) )&& ( (position.getY() + shooterHeight) < (planeY + planeHeight) )
    			||
    			((position.getY() - shooterHeight) > ( planeY - planeHeight) ) && ( (position.getY() - shooterHeight) < (planeY + planeHeight) )
    		)
    		&&
    		(
    			//	Z collision
    			Math.abs(position.getZ() - planeZ) < zTolerance	
    		)
		){
			String message = "Plane collision: X:"+position.getX() +" planeX:" +planeX 
							+" Y:" +position.getY() +" planeY:"+planeY 
							+" Z:" +position.getZ() +" planeY:"+planeZ;
			Log.i(TAG, message);
			hallManager.renderer.pitchMessage = message;
			return true;
		}else{
			return false;
		}
	}
	
	private void move(){
		position.setX(position.getX() + movementRate.getX());
		position.setY(position.getY() + movementRate.getY());
		position.setZ(position.getZ() + movementRate.getZ());
	}
	/* (non-Javadoc)
	 * @see com.example.android.mdiii.Drawable#draw(float[])
	 */
	@Override
	public void draw(float[] mvpMatrix) {
		// TODO Auto-generated method stub

	}
	
	
	/* (non-Javadoc)
	 * @see com.example.android.mdiii.Drawable#isFinished()
	 */
	@Override
	public boolean isFinished() {
		// TODO Auto-generated method stub
		if(!isFinished){
			isFinished = hitWall() || hitPlane();
		}
		
		return isFinished;
	}




	/**
	 * @param isFinished the isFinished to set
	 */
	public void setFinished(boolean isFinished) {
		this.isFinished = isFinished;
	}
	
	
	
}
