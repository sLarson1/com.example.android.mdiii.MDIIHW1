/*
slow camera down
move fan closer
blades - rotate at origin, 
move a little to the left to simulate its at the end of the arm,
next multiply it by the previous arm matrix to put it
in the right spot
*/
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
	private final float[] topBladeMatrix = new float[16];
	private final float[] bottomBladeMatrix = new float[16];

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
//		blades = new Rectangle();
		blades = new Blades();
//		blades = new Ground();
		armInitialAngle = 45f;
		armAngle = armInitialAngle;
		bladeInitialAngle = armInitialAngle;
		bladeAngle = bladeInitialAngle;
		armRotationRate = .10f;
		bladeRotationRate = .075f;
		armMaxAngle = 400.0f * armRotationRate;
//FIXME - Remove this hack that limits downward movement - arm gets detached if you move too far down		
//		armMinAngle = -0.001f * armMaxAngle;
		armMinAngle = 0.5f * armMaxAngle;
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

		Matrix.setIdentityM(baseMatrix, 0);
        Matrix.setIdentityM(objectMatrix, 0);
        
        
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
	   
	   float[] offsetMatrix = new float[16];
	   float[] rotationMatrix = new float[16];
	   float[] translationMatrix = new float[16];
	   float[] tempMatrix = new float[16];
	   float[] temp2Matrix = new float[16];
	   float[] temp3Matrix = new float[16];
	   float[] temp4Matrix = new float[16];
	   float[] lastMatrix = new float[16];
	   

	   Matrix.setIdentityM(armMatrix, 0);
	   Matrix.setIdentityM(offsetMatrix, 0);
	   Matrix.setIdentityM(rotationMatrix, 0);
	   Matrix.setIdentityM(translationMatrix, 0);
	   Matrix.setIdentityM(tempMatrix, 0);
	   Matrix.setIdentityM(temp2Matrix, 0);
	   Matrix.setIdentityM(temp3Matrix, 0);
	   Matrix.setIdentityM(temp4Matrix, 0);
	   Matrix.setIdentityM(lastMatrix, 0);

//	   Matrix.translateM(offsetMatrix, 0, 0.5f, 0, 0 );
//	   Matrix.translateM(offsetMatrix, 0, 0.125f, 0, 0 );
	   Matrix.translateM(offsetMatrix, 0, 0.85f, 0, 0 );
	   
	   Matrix.rotateM(rotationMatrix, 0, armAngle, 0, 0, 1);
	   
	   Matrix.translateM(translationMatrix, 0, armCoord.getX(), armCoord.getY(), armCoord.getZ() );
	   
	   tempMatrix = armMatrix.clone();
	   Matrix.multiplyMM(armMatrix, 0, tempMatrix, 0,offsetMatrix, 0);
	   
	   temp2Matrix = armMatrix.clone();
	   Matrix.multiplyMM(armMatrix, 0, temp2Matrix, 0,rotationMatrix, 0);
	   
	   temp3Matrix = armMatrix.clone();
	   Matrix.multiplyMM(armMatrix, 0, temp3Matrix, 0,translationMatrix, 0);
	   
	   temp4Matrix = mMVPMatrix.clone();
	   Matrix.multiplyMM(mMVPMatrix, 0, temp4Matrix, 0,armMatrix, 0);
	   
	   arm.draw(mMVPMatrix);
/*
	   Matrix.multiplyMM(lastMatrix, 0, mMVPMatrix, 0,armMatrix, 0);
	   
	   arm.draw(lastMatrix);
*/	   
	   changeArmAngle();
	   
	   Log.i(TAG, "finished arm.draw()");
	   
//	   drawBlades(lastMatrix);
	   drawBlades(armMatrix);
   }	
   
   public void drawBlades(float[] previousRotationMatrix){
	   float[] rotationMatrix = new float[16];
	   float[] offsetMatrix = new float[16];
	   float[] tempMatrix = new float[16];
	   float[] temp1Matrix = new float[16];
	   float[] temp2Matrix = new float[16];
	   float[] temp3Matrix = new float[16];
	   float[] lastMatrix = new float[16];
	   
	   
	   Matrix.setIdentityM(topBladeMatrix, 0);
	   Matrix.setIdentityM(bottomBladeMatrix, 0);
	   Matrix.setIdentityM(rotationMatrix, 0);
	   Matrix.setIdentityM(offsetMatrix, 0);
	   Matrix.setIdentityM(tempMatrix, 0);
	   Matrix.setIdentityM(temp1Matrix, 0);
	   Matrix.setIdentityM(temp2Matrix, 0);
	   Matrix.setIdentityM(temp3Matrix, 0);
	   Matrix.setIdentityM(lastMatrix, 0);

	   
		//	make it vertical
       Matrix.rotateM(rotationMatrix, 0, 90.0f, 0, 0, 1);

       //	move vertically down a little to center it on arm shaft
       Matrix.translateM(offsetMatrix, 0, 0, -0.5f, 0 );	   
       
       // 	add rotation about new X axis
       //	its actually the original Y axis
       Matrix.rotateM(rotationMatrix, 0, bladeAngle, 0, 1, 0);

       temp1Matrix = topBladeMatrix.clone();
       Matrix.multiplyMM(topBladeMatrix, 0, temp1Matrix, 0,offsetMatrix, 0);
       
       tempMatrix = topBladeMatrix.clone();
       Matrix.multiplyMM(topBladeMatrix, 0, tempMatrix, 0,rotationMatrix, 0);
/*       
       Matrix.multiplyMM(temp3Matrix, 0, previousRotationMatrix, 0,topBladeMatrix, 0);
       Matrix.multiplyMM(lastMatrix, 0, mMVPMatrix, 0,temp3Matrix, 0);
*/       
	   temp2Matrix = mMVPMatrix.clone();
	   Matrix.multiplyMM(lastMatrix, 0, mMVPMatrix, 0,topBladeMatrix, 0);       

       blades.draw(lastMatrix);
       
       changeBladeAngle();
   }    

   public void drawBladesWorkingWithOneBlade(float[] previousRotationMatrix){
	   float[] rotationMatrix = new float[16];
	   float[] offsetMatrix = new float[16];
	   float[] tempMatrix = new float[16];
	   float[] temp1Matrix = new float[16];
	   float[] temp2Matrix = new float[16];
	   float[] lastMatrix = new float[16];
	   
	   
	   Matrix.setIdentityM(topBladeMatrix, 0);
	   Matrix.setIdentityM(bottomBladeMatrix, 0);
	   Matrix.setIdentityM(rotationMatrix, 0);
	   Matrix.setIdentityM(offsetMatrix, 0);
	   Matrix.setIdentityM(tempMatrix, 0);
	   Matrix.setIdentityM(temp1Matrix, 0);
	   Matrix.setIdentityM(temp2Matrix, 0);
	   Matrix.setIdentityM(lastMatrix, 0);

	   
		//	make it vertical
       Matrix.rotateM(rotationMatrix, 0, 90.0f, 0, 0, 1);

       //	move vertically down a little to center it on arm shaft
       Matrix.translateM(offsetMatrix, 0, 0, -0.5f, 0 );	   
       
       // 	add rotation about new X axis
       //	its actually the original Y axis
       Matrix.rotateM(rotationMatrix, 0, bladeAngle, 0, 1, 0);

       temp1Matrix = topBladeMatrix.clone();
       Matrix.multiplyMM(topBladeMatrix, 0, temp1Matrix, 0,offsetMatrix, 0);
       
       tempMatrix = topBladeMatrix.clone();
       Matrix.multiplyMM(topBladeMatrix, 0, tempMatrix, 0,rotationMatrix, 0);
       
	   temp2Matrix = mMVPMatrix.clone();
	   Matrix.multiplyMM(lastMatrix, 0, mMVPMatrix, 0,topBladeMatrix, 0);       
       
       blades.draw(lastMatrix);
       
       changeBladeAngle();
   }      
   
   public void drawBladesCenteredOnArmShaft(float[] previousRotationMatrix){
	   float[] rotationMatrix = new float[16];
	   float[] offsetMatrix = new float[16];
	   float[] tempMatrix = new float[16];
	   float[] temp1Matrix = new float[16];
	   float[] temp2Matrix = new float[16];
	   float[] lastMatrix = new float[16];
	   
	   
	   Matrix.setIdentityM(topBladeMatrix, 0);
	   Matrix.setIdentityM(rotationMatrix, 0);
	   Matrix.setIdentityM(offsetMatrix, 0);
	   Matrix.setIdentityM(tempMatrix, 0);
	   Matrix.setIdentityM(temp1Matrix, 0);
	   Matrix.setIdentityM(lastMatrix, 0);

	   
		//	make it vertical
       Matrix.rotateM(rotationMatrix, 0, 90.0f, 0, 0, 1);

       // move a little to the right so we rotate by its edge
       Matrix.translateM(offsetMatrix, 0, 0, -0.5f, 0 );	   

       
/*       
       // add rotation [about X axis]
       Matrix.rotateM(rotationMatrix, 0, bladeAngle, 0, 1, 0);
*/
       temp1Matrix = topBladeMatrix.clone();
       Matrix.multiplyMM(topBladeMatrix, 0, temp1Matrix, 0,offsetMatrix, 0);
       
       tempMatrix = topBladeMatrix.clone();
       Matrix.multiplyMM(topBladeMatrix, 0, tempMatrix, 0,rotationMatrix, 0);
       

	   temp2Matrix = mMVPMatrix.clone();
	   Matrix.multiplyMM(lastMatrix, 0, mMVPMatrix, 0,topBladeMatrix, 0);       
       
       blades.draw(lastMatrix);
       
       changeBladeAngle();
   }   
   

   public void drawBladesWorksButRotatesAboveFanArmAxis(float[] previousRotationMatrix){
	   float[] rotationMatrix = new float[16];
	   
	   Matrix.setIdentityM(topBladeMatrix, 0);
	   Matrix.setIdentityM(rotationMatrix, 0);

		//	make it vertical
       Matrix.rotateM(rotationMatrix, 0, 90.0f, 0, 0, 1);
       
       // add rotation [about X axis]
       Matrix.rotateM(rotationMatrix, 0, bladeAngle, 0, 1, 0);
       
       Matrix.multiplyMM(topBladeMatrix, 0, mMVPMatrix, 0,rotationMatrix, 0);

       blades.draw(topBladeMatrix);
       
       changeBladeAngle();
   }   
   
   
   public void drawBladesWorkingStill(float[] previousRotationMatrix){
	   float[] rotationMatrix = new float[16];
	   
	   Matrix.setIdentityM(topBladeMatrix, 0);
	   Matrix.setIdentityM(rotationMatrix, 0);

		//	make it vertical
       Matrix.rotateM(rotationMatrix, 0, 90.0f, 0, 0, 1);
       
       Matrix.multiplyMM(topBladeMatrix, 0, mMVPMatrix, 0,rotationMatrix, 0);

       blades.draw(topBladeMatrix);
       
   }   
   
   public void drawBlades_2(float[] previousRotationMatrix){
	   float[] rotationMatrix = new float[16];
	   float[] translationMatrix = new float[16];
	   float[] tempMatrix = new float[16];
	   float[] temp1Matrix = new float[16];

	   Matrix.setIdentityM(topBladeMatrix, 0);
	   Matrix.setIdentityM(rotationMatrix, 0);	   
	   Matrix.setIdentityM(translationMatrix, 0);
	   Matrix.setIdentityM(tempMatrix, 0);
	   Matrix.setIdentityM(temp1Matrix, 0);
	   
//	   Matrix.translateM(translationMatrix, 0, -armCoord.getX(), -armCoord.getY(), -armCoord.getZ() );
	   Matrix.translateM(translationMatrix, 0, 0, 0, 0 );

	   tempMatrix = topBladeMatrix.clone();
	   Matrix.multiplyMM(topBladeMatrix, 0, tempMatrix, 0, translationMatrix, 0);
	   
	   temp1Matrix = mMVPMatrix.clone();
	   Matrix.multiplyMM(mMVPMatrix, 0, temp1Matrix, 0, topBladeMatrix, 0);
	   
	   blades.draw(mMVPMatrix);
	   
	   changeBladeAngle();
	   
	   Log.i(TAG, "finished blade.draw()");
   }   
   
   public void drawBlades_old(float[] previousRotationMatrix){
	   float[] rotationMatrix = new float[16];
	   float[] tempMatrix = new float[16];
	   float[] temp1Matrix = new float[16];

	   Matrix.setIdentityM(topBladeMatrix, 0);
	   Matrix.setIdentityM(rotationMatrix, 0);
	   Matrix.setIdentityM(tempMatrix, 0);
	   Matrix.setIdentityM(temp1Matrix, 0);
	   
	   Matrix.rotateM(rotationMatrix, 0, bladeAngle, 0, 0, 1);
	   
	   tempMatrix = topBladeMatrix.clone();
	   Matrix.multiplyMM(topBladeMatrix, 0, tempMatrix, 0,rotationMatrix, 0);
	   
	   temp1Matrix = mMVPMatrix.clone();
	   Matrix.multiplyMM(mMVPMatrix, 0, temp1Matrix, 0,topBladeMatrix, 0);
	   
	   blades.draw(mMVPMatrix);
	   
	   changeBladeAngle();
	   
	   Log.i(TAG, "finished blade.draw()");
   }
   
	   @SuppressLint("NewApi")
	   public void drawArmWorksKindOf(float[] previousRotationMatrix){
		   
		   float[] rotationMatrix = new float[16];
		   float[] tempMatrix = new float[16];

		   Matrix.setIdentityM(rotationMatrix, 0);
		   Matrix.setIdentityM(tempMatrix, 0);
		   Matrix.setIdentityM(armMatrix, 0);
	
		   
		   Matrix.translateM(armMatrix, 0, armCoord.getX(), armCoord.getY(), armCoord.getZ() );
		   
		   Matrix.rotateM(rotationMatrix, 0, armAngle, 0, 0, 1);
		   
		   tempMatrix = armMatrix.clone();
		   Matrix.multiplyMM(armMatrix, 0, tempMatrix, 0,rotationMatrix, 0);
		   
		   tempMatrix = mMVPMatrix.clone();
		   Matrix.multiplyMM(mMVPMatrix, 0, tempMatrix, 0,armMatrix, 0);
		   
		   arm.draw(mMVPMatrix);
		   
		   changeArmAngle();
		   
		   Log.i(TAG, "finished arm.draw()");
	   }
	
	
   @SuppressLint("NewApi")
   public void drawArm2(float[] previousRotationMatrix){

      printMatrix(finalMatrix);
      float[] rotationMatrix = new float[16];
      float[] offsetMatrix = new float[16];
      float[] translationMatrix = new float[16];
      float[] tempMatrix = new float[16];
      float[] temp2Matrix = new float[16];
      

      Matrix.setIdentityM(rotationMatrix, 0);
      Matrix.setIdentityM(offsetMatrix, 0);
      Matrix.setIdentityM(translationMatrix, 0);
      Matrix.setIdentityM(tempMatrix, 0);
      Matrix.setIdentityM(temp2Matrix, 0);
      
      // move a little to the right so we rotate by its edge
      Matrix.translateM(offsetMatrix, 0, 0.5f, 0, 0 );
//      Matrix.translateM(rotationMatrix, 0, 0.5f, 0, 0 );
      
      //  move Arm to spot
      Matrix.translateM(translationMatrix, 0, armCoord.getX(), armCoord.getY(), armCoord.getZ() );    
      
      // rotate about Z
      Matrix.rotateM(rotationMatrix, 0, armAngle, 0, 0, 1);        
 
      Log.v(TAG, "drawArm(objectMatrix, 0,  coord.getX():" + armCoord.getX()+ ", baseCoordinate.getY():" + armCoord.getY() + ", baseCoordinate.getZ():" + armCoord.getZ() + " )");              
/**/
      //rotation origin as at arm - kind of works       
      Matrix.multiplyMM(tempMatrix, 0, offsetMatrix, 0,rotationMatrix, 0);
      Matrix.multiplyMM(temp2Matrix, 0, translationMatrix, 0,tempMatrix, 0);
      
      Matrix.multiplyMM(armMatrix, 0, mMVPMatrix, 0,temp2Matrix, 0);      

      
/*      
      Matrix.multiplyMM(tempMatrix, 0, offsetMatrix, 0,rotationMatrix, 0);      
      
      Matrix.multiplyMM(armMatrix, 0, mMVPMatrix, 0,tempMatrix, 0);
*/
      
/*      
      Matrix.multiplyMM(tempMatrix, 0, rotationMatrix, 0,offsetMatrix, 0);      
      
      Matrix.multiplyMM(armMatrix, 0, mMVPMatrix, 0,tempMatrix, 0);

      Matrix.multiplyMM(armMatrix, 0, mMVPMatrix, 0,rotationMatrix, 0);
*/      
      //  draw arm
      arm.draw(armMatrix);
      
      changeArmAngle();
   }
	
	
	@SuppressLint("NewApi")
	public void drawArmOld(float[] previousRotationMatrix){

		printMatrix(finalMatrix);
		float[] rotationMatrix = new float[16];
		float[] translationMatrix = new float[16];
		float[] offsetTranslationMatrix = new float[16];
		float[] destinationtranslationMatrix = new float[16];
		float[] temp1Matrix = new float[16];
		float[] temp2Matrix = new float[16];


		//rotate
		//move to spot

		//put at origin
		Matrix.setIdentityM(rotationMatrix, 0);
		Matrix.setIdentityM(translationMatrix, 0);
		
		Matrix.setIdentityM(offsetTranslationMatrix, 0);
		Matrix.setIdentityM(destinationtranslationMatrix, 0);

		Matrix.setIdentityM(temp1Matrix, 0);
		Matrix.setIdentityM(temp2Matrix, 0);

		//	move a little to the right so we rotate by its edge
		//Matrix.translateM(translationMatrix, 0, 0.5f, 0, 0 );
      //  move Arm to spot
      Matrix.translateM(translationMatrix, 0, armCoord.getX(), armCoord.getY(), armCoord.getZ() );		
      
		//	rotate about Z
        Matrix.rotateM(rotationMatrix, 0, armAngle, 0, 0, 1);        

        //	rotate about Y
//        Matrix.rotateM(rotationMatrix, 0, armAngle, 0, 1, 0);
  
        //	move Arm to spot
//        Matrix.translateM(destinationtranslationMatrix, 0, armCoord.getX(), armCoord.getY(), armCoord.getZ() );        
      
        Log.v(TAG, "drawArm(objectMatrix, 0,  coord.getX():" + armCoord.getX()+ ", baseCoordinate.getY():" + armCoord.getY() + ", baseCoordinate.getZ():" + armCoord.getZ() + " )");              

        
        Matrix.multiplyMM(temp1Matrix, 0, rotationMatrix, 0,offsetTranslationMatrix, 0);
        Matrix.multiplyMM(temp2Matrix, 0, destinationtranslationMatrix, 0,temp1Matrix, 0);
        Matrix.multiplyMM(armMatrix, 0, mMVPMatrix, 0,temp2Matrix, 0);
        
        //	draw arm
        arm.draw(armMatrix);

		changeArmAngle();
	}


	private void changeBladeAngle() {
		bladeAngle += 10 *  bladeRotationRate;
	}

	public void drawBlades(){
		Matrix.setIdentityM(topBladeMatrix, 0);
		
		//	rotate about Z
        Matrix.rotateM(topBladeMatrix, 0, bladeAngle, 0, 0, 1);
                
        Matrix.multiplyMM(finalMatrix, 0, mMVPMatrix, 0,topBladeMatrix, 0);
        
		blades.draw(finalMatrix);

		changeArmAngle();
		changeBladeAngle();        
	}

	private void changeArmAngle(){

		if( armRotationDirection.equals("up") ) {
			if(armAngle + armRotationRate > armMaxAngle ){
				armRotationDirection = "down";
				armAngle -= armRotationRate;
				Log.i(TAG, "arm switching direction to down.  armminAngle:"+armMaxAngle +" current angle:"+armAngle);
			}else{
				armAngle += armRotationRate;
			}
		}else if( armRotationDirection.equals("down")) {
			if(armAngle - armRotationRate < armMinAngle ){
				armRotationDirection = "up";
				Log.i(TAG, "arm switching direction to up.  armminAngle:"+armMinAngle +" current angle:"+armAngle);
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
		return false;
	}

	@Override
	public void draw(Coord coord, float[] mvpMatrix) {
		
	}
}