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
	private Drawable base;
	private Drawable arm;
	private Drawable blades;
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
	}

	public void drawBase(){
		printMatrix(finalMatrix);
		
        Matrix.setIdentityM(objectMatrix, 0);

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
		float[] intermediateMatrix = new float[16];
		
		Matrix.setIdentityM(rotationMatrix, 0);

		Matrix.translateM(rotationMatrix, 0, -baseCoordinate.getX(), -baseCoordinate.getY(), -baseCoordinate.getZ() );
		
        //	roll
        Matrix.rotateM(rotationMatrix, 0, 45.0f, 0, 1, 0);
        
        Matrix.translateM(rotationMatrix, 0, 2.0f * baseCoordinate.getX(), 2.0f * baseCoordinate.getY(), 2.0f * baseCoordinate.getZ() );

        
        Log.v(TAG, "drawArm(objectMatrix, 0,  coord.getX():" + baseCoordinate.getX()+ ", baseCoordinate.getY():" + baseCoordinate.getY() + ", baseCoordinate.getZ():" + baseCoordinate.getZ() + " )");              
        Matrix.multiplyMM(intermediateMatrix, 0, previousRotationMatrix, 0,rotationMatrix, 0);
        
        Matrix.multiplyMM(finalMatrix, 0, mMVPMatrix, 0,intermediateMatrix, 0);
        		
        printMatrix(finalMatrix);
		arm.draw(finalMatrix);
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
