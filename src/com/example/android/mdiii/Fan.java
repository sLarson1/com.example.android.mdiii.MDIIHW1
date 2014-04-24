
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
		armRotationRate = .05f;
		armAngle = armInitialAngle;
		firstRun = true;
		xOriginOffset= 0.25f;
		armCoordOffset = .5f;
		armCoord = new Coord(baseCoordinate.getX() + armCoordOffset, baseCoordinate.getY() + armCoordOffset, baseCoordinate.getZ() + armCoordOffset);
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
//        base.draw(finalMatrix);		
        
        printMatrix(finalMatrix);
        drawArm(finalMatrix);
	}
	
	public void drawArm(float[] previousRotationMatrix){

		printMatrix(finalMatrix);
		float[] rotationMatrix = new float[16];
//		float[] intermediateMatrix = new float[16];
		
		//put at origin
		//rotate
		//move to spot
		
		Matrix.setIdentityM(rotationMatrix, 0);

		//	move to origin
		if(firstRun){
			//	move to xoffset origin
			Log.d(TAG, "first run:0, 0.25f, 0, 0 ");
			Matrix.translateM(rotationMatrix, 0, 0.25f, 0, 0 );
			firstRun = false;
		}else{
			Log.d(TAG, "subsequent run:" +(-armCoord.getX()) +" "+(-armCoord.getY()) +" " +(-armCoord.getZ() ));
			//	move back to xoffset origin
			Matrix.translateM(rotationMatrix, 0, -armCoord.getX(), -armCoord.getY(), -armCoord.getZ() );
		}
		
		//	rotate about Z
        Matrix.rotateM(rotationMatrix, 0, armAngle, 0, 0, 1);
        
        //	move to spot
        Matrix.translateM(rotationMatrix, 0, armCoord.getX(), armCoord.getY(), armCoord.getZ() );

        
        Log.v(TAG, "drawArm(objectMatrix, 0,  coord.getX():" + armCoord.getX()+ ", baseCoordinate.getY():" + armCoord.getY() + ", baseCoordinate.getZ():" + armCoord.getZ() + " )");              

        Matrix.multiplyMM(finalMatrix, 0, mMVPMatrix, 0,rotationMatrix, 0);
        
        printMatrix(finalMatrix);
		arm.draw(finalMatrix);
		
		armAngle += armRotationRate;
	}

	public void drawBlades(){
		
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
