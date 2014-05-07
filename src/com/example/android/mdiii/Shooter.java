/**
 * 
 */
package com.example.android.mdiii;

/**
 * @author slarson@twia.org
 * @since May 7, 2014
 *
 */
public class Shooter implements Drawable {

	private HallManager hallManager;
	private boolean isFinished;
	private Coord position;
	private Coord movementRate;
	private float collisionPadding;
	
	
	

	public Shooter(HallManager hallManager, Coord position, Coord movementRate) {
		super();
		this.hallManager = hallManager;
		this.position = position;
		this.movementRate = movementRate;
	}




	/* (non-Javadoc)
	 * @see com.example.android.mdiii.Drawable#draw(com.example.android.mdiii.Coord, float[])
	 */
	@Override
	public void draw(Coord coord, float[] mvpMatrix) {
		if(!hitWall()) {
			
		}

	}

	private boolean hitWall() {
		
		if(
			((movementRate.getX() < 0.0) 
				&& (position.getX() + collisionPadding) <= -hallManager.getWallXOffset())
			||
			((movementRate.getX() > 0.0) 
					&& (position.getX() - collisionPadding) >= hallManager.getWallXOffset())
				){
			isFinished = true;
			return isFinished;			
		}else {
			return false;
		}
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
