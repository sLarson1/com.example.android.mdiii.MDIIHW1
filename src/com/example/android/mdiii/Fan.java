//why does the second arm work and not the first?
/**
 * started blade work
   create the actual blade drawable
tighten up the rotation of the fan
 * 
 */
package com.example.android.mdiii;

import java.util.Arrays;

import android.annotation.SuppressLint;
import android.opengl.Matrix;
import android.util.Log;

/**
 * @author slarson
 *
 */
public class Fan implements Drawable {

	private final float[] baseMatrix = new float[16];
	private final float[] armMatrix = new float[16]; 
	private final float[] bladeMatrix = new float[16];

	private final float[] objectMatrix;
	private final float[] mMVPMatrix;
	private final float[] finalMatrix;
	private final Coord baseCoordinate;
	private float armCoordOffset;
	private float bladeCoordOffset;
	private final Coord armCoord;
	private final Coord bladeCoord;
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
	private float bladeInitialAngle;
	private float bladeRotationRate;
	private float bladeAngle;



	public Fan(Coord rootCoordinate, float[] objectMatrix, float[] mMVPMatrix, float[] finalMatrix) {
		this.objectMatrix = objectMatrix;
		this.mMVPMatrix = mMVPMatrix;
		this.finalMatrix = finalMatrix;
		this.baseCoordinate = rootCoordinate;
		base = new Ground();
		arm = new Ground();
//		blades = new Blades();
		blades = new Ground();
		armInitialAngle = 0f;
		armAngle = armInitialAngle;
		bladeInitialAngle = armInitialAngle;
		bladeAngle = bladeInitialAngle;
		armRotationRate = .10f;
		bladeRotationRate = .025f;
		armMaxAngle = 400.0f * armRotationRate;
		armMinAngle = -armMaxAngle;
		armRotationDirection = "up";
		firstRun = true;
		xOriginOffset= 0.25f;
		armCoordOffset = .5f;
//		armCoord = new Coord(baseCoordinate.getX() + armCoordOffset, baseCoordinate.getY() +( 1 *armCoordOffset), baseCoordinate.getZ() + armCoordOffset);
		armCoord = new Coord(baseCoordinate.getX() + armCoordOffset, baseCoordinate.getY() +( 2 *armCoordOffset), baseCoordinate.getZ() + armCoordOffset);
		bladeCoord = new Coord(armCoord.getX() + bladeCoordOffset, armCoord.getY() +( 1 *bladeCoordOffset), armCoord.getZ() + bladeCoordOffset);
		//armCoord.setX(armCoord.getX() + baseCoordinate.getX());
	}

	public void drawBase(){
		printMatrix(baseMatrix);

        Matrix.setIdentityM(objectMatrix, 0);
        Matrix.setIdentityM(baseMatrix, 0);
        
		//	make it vertical
        Matrix.rotateM(objectMatrix, 0, 90.0f, 0, 0, 1);

        Matrix.translateM(objectMatrix, 0, baseCoordinate.getX(), baseCoordinate.getY(), baseCoordinate.getZ() );
        
        Log.v(TAG, "drawBase(objectMatrix, 0,  coord.getX():" + baseCoordinate.getX()+ ", baseCoordinate.getY():" + baseCoordinate.getY() + ", baseCoordinate.getZ():" + baseCoordinate.getZ() + " )");              
        Matrix.multiplyMM(baseMatrix, 0, mMVPMatrix, 0,objectMatrix, 0);
        
        Log.v(TAG , "drawing entity:"+base);
        base.draw(baseMatrix);		
        
        printMatrix(baseMatrix);
        drawArm(baseMatrix);
	}

	@SuppressLint("NewApi")
	public void drawArm(float[] previousRotationMatrix){

		printMatrix(finalMatrix);
		float[] rotationMatrix = new float[16];
		float[] offsetTranslationMatrix = new float[16];
		float[] destinationtranslationMatrix = new float[16];
		float[] temp1Matrix = new float[16];
		float[] temp2Matrix = new float[16];


		//rotate
		//move to spot

		//put at origin
		Matrix.setIdentityM(rotationMatrix, 0);
		Matrix.setIdentityM(offsetTranslationMatrix, 0);
		Matrix.setIdentityM(destinationtranslationMatrix, 0);
		Matrix.setIdentityM(armMatrix, 0);
		Matrix.setIdentityM(temp1Matrix, 0);
		Matrix.setIdentityM(temp2Matrix, 0);

		//	move a little to the right so we rotate by its edge
//		Matrix.translateM(rotationMatrix, 0, 0.5f, 0, 0 );
		Matrix.translateM(offsetTranslationMatrix, 0, 0.5f, 0, 0 );
      
		//	rotate about Z
        Matrix.rotateM(rotationMatrix, 0, armAngle, 0, 0, 1);        

        //	rotate about Y
//        Matrix.rotateM(rotationMatrix, 0, armAngle, 0, 1, 0);
  
        //	move Arm to spot
//        Matrix.translateM(rotationMatrix, 0, armCoord.getX(), armCoord.getY(), armCoord.getZ() );
        Matrix.translateM(destinationtranslationMatrix, 0, armCoord.getX(), armCoord.getY(), armCoord.getZ() );        
      
        Log.v(TAG, "drawArm(objectMatrix, 0,  coord.getX():" + armCoord.getX()+ ", baseCoordinate.getY():" + armCoord.getY() + ", baseCoordinate.getZ():" + armCoord.getZ() + " )");              

        
        Matrix.multiplyMM(temp1Matrix, 0, rotationMatrix, 0,offsetTranslationMatrix, 0);
        Matrix.multiplyMM(temp2Matrix, 0, destinationtranslationMatrix, 0,temp1Matrix, 0);
        Matrix.multiplyMM(armMatrix, 0, mMVPMatrix, 0,temp2Matrix, 0);
        
        //	draw arm
        arm.draw(armMatrix);

/*
        Matrix.multiplyMM(finalMatrix, 0, mMVPMatrix, 0,bladeMatrix, 0);
        
		blades.draw(finalMatrix);
*/
		changeArmAngle();
	}


	private void changeBladeAngle() {
		bladeAngle += 10 *  bladeRotationRate;
	}

	public void drawBlades(){
		Matrix.setIdentityM(bladeMatrix, 0);
		
		//	rotate about Z
        Matrix.rotateM(bladeMatrix, 0, bladeAngle, 0, 0, 1);
                
        Matrix.multiplyMM(finalMatrix, 0, mMVPMatrix, 0,bladeMatrix, 0);
        
		blades.draw(finalMatrix);

		changeArmAngle();
		changeBladeAngle();        
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

	@Override
	public boolean isFinished() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void draw(Coord coord, float[] mvpMatrix) {
		// TODO Auto-generated method stub
		
	}
}