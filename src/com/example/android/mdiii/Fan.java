started blade work
   create the actual blade drawable
tighten up the rotation of the fan
//why does the second arm work and not the first?
/**
 * 
 */
package com.example.android.mdiii;

import android.opengl.Matrix;
import android.util.Log;

/**
 * @author slarson
 *
 */
public class Fan implements Drawable {
	
	private final float[] baseMatrix = new float[16];
	private final float[] armlMatrix = new float[16]; 
	private final float[] bladeMatrix = new float[16]; 
	private final float[] objectMatrix;
	private final float[] mMVPMatrix;
	private final float[] finalMatrix;
	private final Coord baseCoordinate;
	private float armCoordOffset;
	private final Coord armCoord;
	private Drawable base;
	private Drawable arm;
	private Drawable blades;
	private float armInitialAngle;
	private float armAngle;
	private float armMaxAngle;
	private float armMinAngle;
	private String armRotationDirection;
	private float armRotationRate;
	private boolean firstRun;
	private float xOriginOffset;
	//private 
	private String TAG = "Fan";

	

	public Fan(Coord rootCoordinate, float[] objectMatrix, float[] mMVPMatrix, float[] finalMatrix) {
		this.objectMatrix = objectMatrix;
		this.mMVPMatrix = mMVPMatrix;
		this.finalMatrix = finalMatrix;
		this.baseCoordinate = rootCoordinate;
		base = new Ground();
		arm = new Ground();
		blades = new Ground();
		armInitialAngle = 0f;
		armRotationRate = .025f;
		armMaxAngle = 400.0f * armRotationRate;
		armMinAngle = -armMaxAngle;
		armAngle = armInitialAngle;
		armRotationDirection = "up";
		firstRun = true;
		xOriginOffset= 0.25f;
		armCoordOffset = .5f;
		armCoord = new Coord(baseCoordinate.getX() + armCoordOffset, baseCoordinate.getY() +( 1 *armCoordOffset), baseCoordinate.getZ() + armCoordOffset);
		//armCoord.setX(armCoord.getX() + baseCoordinate.getX());
	}

	public void drawBase(){
		printMatrix(finalMatrix);
		
        Matrix.setIdentityM(objectMatrix, 0);
        
		//	make it vertical
        Matrix.rotateM(objectMatrix, 0, 90.0f, 0, 0, 1);

        Matrix.translateM(objectMatrix, 0, baseCoordinate.getX(), baseCoordinate.getY(), baseCoordinate.getZ() );
        
        Log.v(TAG, "drawBase(objectMatrix, 0,  coord.getX():" + baseCoordinate.getX()+ ", baseCoordinate.getY():" + baseCoordinate.getY() + ", baseCoordinate.getZ():" + baseCoordinate.getZ() + " )");              
        Matrix.multiplyMM(finalMatrix, 0, mMVPMatrix, 0,objectMatrix, 0);
        
        Log.v(TAG , "drawing entity:"+base);
        base.draw(finalMatrix);		
        
        printMatrix(finalMatrix);
        drawArm(finalMatrix);
	}
	
	public void drawArm(float[] previousRotationMatrix){

		printMatrix(finalMatrix);
		float[] rotationMatrix = new float[16];
		
		
		//rotate
		//move to spot

		//put at origin
		Matrix.setIdentityM(rotationMatrix, 0);

		//	move a little to the right so we rotate by its edge
		Matrix.translateM(rotationMatrix, 0, 0.5f, 0, 0 );
      
		//	rotate about Z
        Matrix.rotateM(rotationMatrix, 0, armAngle, 0, 0, 1);
        
        //	rotate about Y
        Matrix.rotateM(rotationMatrix, 0, armAngle, 0, 1, 0);
        
        is this the right way to copy a matrix?
        used .equals() to find out
        float[] bladeMatrix = rotationMatrix;         
        
        // rotate blades about X
        Matrix.rotateM(bladeMatrix, 0, bladeAngle, 1, 0, 0);      
        
        //	move Arm to spot
        Matrix.translateM(rotationMatrix, 0, armCoord.getX(), armCoord.getY(), armCoord.getZ() );
        
        //  move blade to spot
        Matrix.translateM(bladeMatrix, 0, bladeCoord.getX(), bladeCoord.getY(), bladeCoord.getZ() );
        
        Log.v(TAG, "drawArm(objectMatrix, 0,  coord.getX():" + armCoord.getX()+ ", baseCoordinate.getY():" + armCoord.getY() + ", baseCoordinate.getZ():" + armCoord.getZ() + " )");              

        Matrix.multiplyMM(finalMatrix, 0, mMVPMatrix, 0,rotationMatrix, 0);
        
        multiply bladematrix
        Matrix.multiplyMM(finalMatrix, 0, mMVPMatrix, 0,bladeMatrix, 0);
        
//        printMatrix(finalMatrix);
		arm.draw(finalMatrix);
		blade.draw(finalMatrix)
				
		changeArmAngle();
		changeBladeAngle();
	}
	
	public void turnArm(){
		printMatrix(finalMatrix);
		float[] rotationMatrix = new float[16];
		
		//put at origin
		//rotate
		//move to spot
		
		Matrix.setIdentityM(rotationMatrix, 0);

		Matrix.translateM(rotationMatrix, 0, 0.5f, 0, 0 );
      
		//	rotate about Z
        Matrix.rotateM(rotationMatrix, 0, armAngle, 0, 0, 1);
        
        //	move to spot
        Matrix.translateM(rotationMatrix, 0, armCoord.getX(), armCoord.getY(), armCoord.getZ() );
        
        Log.v(TAG, "drawArm(objectMatrix, 0,  coord.getX():" + armCoord.getX()+ ", baseCoordinate.getY():" + armCoord.getY() + ", baseCoordinate.getZ():" + armCoord.getZ() + " )");              

        Matrix.multiplyMM(finalMatrix, 0, mMVPMatrix, 0,rotationMatrix, 0);
        
//        printMatrix(finalMatrix);
		arm.draw(finalMatrix);
		
		changeArmAngle();
	}

	public void drawBlades(){
		
	}

	private void changeArmAngle(){
		
		if( armRotationDirection.equals("up") ) {
			if(armAngle + armRotationRate > armMaxAngle ){
				armRotationDirection = "down";
				armAngle -= armRotationRate;
			}else{
				armAngle += armRotationRate;
			}
		}else if( armRotationDirection.equals("down")) {
			if(armAngle - armRotationRate < armMinAngle ){
				armRotationDirection = "up";
				armAngle += armRotationRate;
			}else{
				armAngle -= armRotationRate;
			}			
			
		}		
	}

	/* (non-Javadoc)
	 * @see com.example.android.mdiii.Drawable#draw(float[])
	 */
	@Override
	public void draw(float[] mvpMatrix) {
		drawBase();
	}
	
	private void printMatrix(float[] matrix){
		StringBuilder sb = new StringBuilder();
		sb.append("[" +matrix[0] +"," +matrix[1] +"," +matrix[2] +"," +matrix[3] +",");
		Log.d(TAG, sb.toString());
	}
}
