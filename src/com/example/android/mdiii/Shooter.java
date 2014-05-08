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
	private boolean isFinished;
	private Coord position;
	private Coord movementRate;
	private float collisionPadding;
	private Drawable drawable;
	private float[] mMVPMatrix;
	
	
	
	

	public Shooter(HallManager hallManager, Coord position, Coord movementRate, float[] mMVPMatrix) {
		super();
		this.hallManager = hallManager;
		this.position = position;
		this.movementRate = movementRate;		
		this.mMVPMatrix = mMVPMatrix;
		drawable = new Ground();
	}


	/* (non-Javadoc)
	 * @see com.example.android.mdiii.Drawable#draw(com.example.android.mdiii.Coord, float[])
	 */
	@Override
	public void draw(Coord coord, float[] mvpMatrix) {
		Log.e(TAG , "drawing entity:"+drawable);
		
		if(!hitWall()) {	        
			float[] movementMatrix= new float[16];
			Matrix.setIdentityM(movementMatrix, 0);

			float[] shooterMatrix = new float[16];
			Matrix.setIdentityM(shooterMatrix, 0);
			
	        Matrix.translateM(movementMatrix, 0, position.getX(), position.getY(), position.getZ() );
	        
	        Log.e(TAG, "draw(objectMatrix, 0,  position.getX():" + position.getX()+ ", position.getY():" + position.getY() + ", position.getZ():" + position.getZ() + " )");              
	        Matrix.multiplyMM(shooterMatrix, 0, mMVPMatrix, 0,movementMatrix, 0);
	        
	        drawable.draw(shooterMatrix);		
			move();
			this.hallManager.renderer.pitchMessage = "DRAW SHOOTER!";
		}else{
			Log.e(TAG, "draw failed - hit wall");
			this.hallManager.renderer.pitchMessage = "SHOOTER HIT WALL";
		}

	}

	private boolean hitWall() {
		Log.e(TAG, "hitWall() movementRate.getX():"
				+movementRate.getX()+" collisionPadding:"+collisionPadding
				+" hallManager.getWallXOffset():"+hallManager.getWallXOffset());		
		if(
			((movementRate.getX() < 0.0) 
				&& (position.getX() + collisionPadding) <= -hallManager.getWallXOffset())
			||
			((movementRate.getX() > 0.0) 
					&& (position.getX() - collisionPadding) >= hallManager.getWallXOffset())
				){
			Log.e(TAG, "collision detected!");
			isFinished = true;
			return isFinished;			
		}else {
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
		return isFinished;
	}




	/**
	 * @param isFinished the isFinished to set
	 */
	public void setFinished(boolean isFinished) {
		this.isFinished = isFinished;
	}
	
	
	
}
