

/*
 * try using 2,6,9

 * try printing out the difference in the values of all the matrix cells between runsg

what matrix cells does just doing yaw or pitch affect? 
try running it with both pitch and yaw commented out and see how it looks
Are we using the right indexes on the rotationMatrix when we do the rotation
maybe looking at the axises one at a time will help figure that out
Is he there on MOndays?
otherwise give up and ask for  more help and do something else
  */ 
   
/*
look on home computer for the MyGLRenderer code that had all
my work for the delta matrix
wrap all logging statements like this            if(Log.isLoggable(TAG, Log.VERBOSE))
   manually set the logger level for the project?

		 //nothing shows anymore
		 //why isn't the arm being drawn
		 //work on drawing matrix
		 /*
		  * update they way the plane points
		  * do the vent lift thing
		 diff with old project and do clean up
		 setup z correctly


		 try adjusting sensor frequency. for some reason it is zeroing out

		 is it right that the plane and camara alwasy center should the lookat be differnt
		 finish up hall stuff
		 work on making plane look good

		 use filter for pitch/yaw
		 use texture

		 check filter code
		 work on text overlay next
		 */
 package com.example.android.mdiii;

 import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.example.android.mdiii.text.GLText;

/**
* @author slarson
*
*/
public class MyGLRenderer implements GLSurfaceView.Renderer ,SensorEventListener{
/*
work on plane rotation and text stuff 
instead of switching actual halls why don't we just compare the currentZ wtih a halls measurements   
*/ 

    private static final String TAG = "MyGLRenderer";
    private Ground ground;
    //		 	private Plane plane;
    private com.example.android.mdiii.Drawable plane;
    private GLSurfaceView view;
    private HallManager hallManager;
    
    public float xDeltaThreshold;
    public float yDeltaThreshold;
    public volatile float mDx;
    public volatile float mDy;
    private float cameraSpeed;
    private float yawKludge;
    private float pitchKludge;
    private float lookX;
    private float lookY;
    private float lookZ;
    private float cameraX;
    private float cameraY;
    private float cameraZ;
    private float yaw;
    private float pitch;
    public String pitchMessage;
    private float directionX;
    private float directionY;
    private float directionZ;
    private float planeX;
    private float planeY;
    private float planeZ;
    private float planeWidth;
    private float planeHeight;
    private float wallXOffset;
    private float wallYOffset;
    private float drawableThreshold;
    private boolean collisionDetected;
    private GLText glText;
    public static Context context;	
    private CameraCoord[] delay;
    private boolean viewSonic;
    
    private int delayLength;
    private int head;
    private int tail;
    
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjMatrix = new float[16];
    private final float[] mVMatrix = new float[16];
    private final float[] objectMatrix = new float[16];
    private final float[] rotationMatrix = new float[16];
    private final float[] objectFinalMatrix = new float[16];
    private final float[] previousMatrix = new float[16];
    private float[] planeMatrix = new float[16];
    
    Canvas canvas;
    Drawable background;
    Paint textPaint;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private float[] linear_acceleration;
    private float[] gravity;
    private float idealPitch;
    private float pitchTolerance;
    private float accelerateRate;
    private float yIntercept;
    private int numberOfContents;
    private Contents contents;
    private Image image;
    
    public MyGLRenderer(GLSurfaceView view) {
    
    	this.view = view;
    	view.setKeepScreenOn(true);
    	context = this.view.getContext();
    	GraphicsUtils.context = context;		

    	cameraX = 0.0f;
    	cameraY = 0f;
    	cameraZ = 4.5f;
    	
    	directionX = 0f;
    	directionY = 0f;
    	directionZ = 1.0f;
    	
    	xDeltaThreshold = 0.1f;	// 20 % change
    	yDeltaThreshold = 0.1f;	// 20 % change
    
    	lookX = 0f;
    	lookY = 0f;
    	lookZ = 6.5f;
    
    	planeX = cameraX;
    	planeY = cameraY;
    	planeZ = cameraZ;
    	planeWidth = 0.25f;
    	planeHeight = 0.25f;
    	
    	wallXOffset = 1.0f;
    	wallYOffset = 1.0f;
    	
    	yaw = 0f;
    //		 		cameraSpeed = 0.01f;//1.25f;
    	cameraSpeed = 0.00004f;//1.25f;
    	yawKludge = 0.01f;
    	pitchKludge = .1f;	
    	collisionDetected = false;
    	
    	// initalize CameraCoords
    	head = 0;
    	tail = 1;
    	delayLength = 40;	//	10
    	delay  = new CameraCoord[delayLength];
    	for(int i = 0; i<delayLength; i++){
    	  delay[i] = new CameraCoord(0f,0f,0f,0f,0f,0f,0f,0f,0f);
    	}
    
    	idealPitch = 0.02f;
    	pitchTolerance =0.01f;
    	accelerateRate = 0.00005f;
    	//		 	      yIntercept = 0.065f;
    	yIntercept = 0.012f;
    	numberOfContents = 2;
    	pitchMessage = "";
    	drawableThreshold = 0.1f;
    
    	this.loadText();	
    	initialPlanePosition();
    	initializeSensor();
    	Matrix.setIdentityM(rotationMatrix, 0);
    }
    
    public void initializeSensor(){
    	gravity = new float[3];
    	linear_acceleration = new float[3];
    	mSensorManager   = (SensorManager) this.view.getContext().getSystemService(Context.SENSOR_SERVICE);
    	List<Sensor> list = mSensorManager.getSensorList(Sensor.TYPE_ALL);
    	Log.v(TAG,"Sensors:"+list.size());
    
    	for(Sensor sensor : list) {
    	 Log.v(TAG, "sensor:"+sensor.getName() +" vendor:"+sensor.getVendor() +"\n");
    	}
    	mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    	if(mSensor.getVendor().equals("ADI")){
    	  viewSonic = true;
    	}else{
    	  
    	}
    	  
    	if(mSensor==null){
    	  Log.v(TAG, "sensor was null exiting");
    	  System.exit(0);
    	}	
    }
    
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
    	//onSurfaceChanged width:1024 height:496 ratio:2.064516
    	
    	// Set the background frame color
    	GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    	GLES20.glClearDepthf(1.0f);
    	GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    	GLES20.glDepthFunc(GLES20.GL_LEQUAL);
    	GLES20.glDepthMask(true);
    	GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);	
    
    	
//    		 		plane = new Plane();
    	plane = new Ground();
    	hallManager = new HallManager(this, 3, 10.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 50.0f, this.objectMatrix, this.objectFinalMatrix, this.mMVPMatrix, this.wallXOffset, this.wallYOffset);
    	mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }
    
    
    
    
    @Override
    public void onDrawFrame(GL10 unused) {
    
    	//onSurfaceChanged width:1024 height:496 ratio:2.064516
    
    	GLES20.glClearColor(0.1f, 0.1f, 0.2f, 1.0f);
    	GLES20.glClearDepthf(1.0f);
    	GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);	
    
    	// Set the camera position (View matrix)
    	Log.v(TAG, "Matrix.setLookAtM(mVMatrix, 0, cameraX:" + cameraX+ ",cameraY:" + cameraY + ",cameraZ:" + cameraZ + ",lookX:"+ lookX + ",lookY:" + lookY + ",lookZ:" + lookZ + ")");
    	CameraCoord cameraCoord = readMeasurements();
    	Matrix.setLookAtM(mVMatrix, 0, cameraCoord.cameraX, cameraCoord.cameraY, cameraCoord.cameraZ, cameraCoord.lookX, cameraCoord.lookY, cameraCoord.lookZ, cameraCoord.upX, cameraCoord.upY, cameraCoord.upZ);
    
    	// Calculate the projection and view transformation
    	Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
    
    	hallManager.draw(cameraZ);
    
//    	drawPlane(objectMatrix, objectFinalMatrix);
//FIXME put plane back
//    	drawPlane(objectMatrix, rotationMatrix, objectFinalMatrix);
    	drawTexture();
    	
    	updateDirection();
    	updateCameraPosition();
    	updateLookAt();
    	updatePlanePosition();
//FIXME put back gravity    	
//    	updateGravity();
    	recordMeasurements();
    
    	resetMessages();
//    	image.draw(planeMatrix);
    }
    
    
    private void resetMessages() {
    	pitchMessage = "";
    }
    
    protected void drawTexture(){
    	// draw texture
    	GLES20.glDisable( GLES20.GL_DEPTH_TEST );
    	GLES20.glEnable( GLES20.GL_BLEND );                   // Enable Alpha Blend
    	GLES20.glBlendFunc( GLES20.GL_SRC_ALPHA, GLES20.GL_ZERO );  // Set Alpha Blend Function
    
    	glText.begin( 1.0f, 1.0f, 1.0f, 1.0f );         // Begin Text Rendering (Set Color WHITE)
    	  
//    	glText.draw("Z:" +this.cameraZ +" pitch:"+pitch +" " +pitchMessage +"\nSpeed:"+cameraSpeed, 50, 50 );         
    	glText.draw(pitchMessage, 50, 50 );         
//    	glText.draw("PUT BACK HALL CONTENTS!  Also TURN ON COLLISION DETECTION, Turn ON GRAVITY", 50, 50 );         
    
    	glText.end();     // End Text Rendering
		 		

//    	image.draw(objectFinalMatrix);
//    	if(hallManager.contentsMap.)
		for(Entry<Coord, com.example.android.mdiii.Drawable> entry : hallManager.contentsMap.entrySet()){
			/*
			if the drawable is of type ground use theh draw method in this class
			if the drawable is of type Fan then use the draw method in fan
			*/
			com.example.android.mdiii.Drawable drawable = (com.example.android.mdiii.Drawable) entry.getValue();
			if(drawable instanceof Shooter){
				Shooter shooter = (Shooter) drawable;
				if(shooter.isFinished() && shooter.hitPlane()) {
					    	
					try {
						Thread.sleep(250);
						image.draw(planeMatrix);
						Thread.sleep(250);
						image.draw(planeMatrix);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					
					resetCamera();
				}

			}
			
		}    	

    
    	GLES20.glDisable( GLES20.GL_BLEND );  
    	GLES20.glEnable( GLES20.GL_DEPTH_TEST );		
    }
    
    private void recordMeasurements() {
    	CameraCoord cameraCoord = new CameraCoord(cameraX, cameraY, cameraZ, lookX, lookY, lookZ, 0f, 1f, 0f);
    	delay[head] = cameraCoord;
    }
    
    private CameraCoord readMeasurements(){
    	
    	CameraCoord cameraCoord = delay[tail];
    	
    	tail++;
    	head++;
    	if(tail > delayLength - 1){
    		tail = 0;
    	}
    	if(head > delayLength - 1){
    		head = 0;
    	}
    	
    	return cameraCoord;
    }
    
    private void updateYaw() {
       Log.v(TAG, "updateYaw: mDx" + mDx + "Original Angle [Degrees]:" +Math.toDegrees(mDx) +" New Angle [Degrees]:" +Math.toDegrees(mDx * yawKludge));
//    	yaw = (mDx * yawKludge);
    	yaw += (mDx * yawKludge);    	
    }
    
    
    
    private void updatePitch() {	
       Log.v(TAG, "updatePitch: mDy" + mDy + "Original Angle [Degrees]:" +pitch +" New Angle [Degrees]:" +(mDy * pitchKludge));
//       pitch = (mDy * pitchKludge); 	   
       pitch += (mDy * pitchKludge); 	   
    
    }
    
    private void updateDirection() {	  
    
    	Log.v(TAG, "updateDirection() - original -  yaw:"+yaw +" pitch:" +pitch +" directionX:" + directionX + " directionY:"+ directionY +" directionZ:"+directionZ);
    	directionX = 0f;
    	directionY = 0f;
    	directionZ = 1.0f;
    	rotateX();
    	rotateY();
    	
    	Log.e(TAG, "updateDirection() - complete - yaw:"+yaw +" pitch:" +pitch +" directionX:" + directionX + " directionY:"+ directionY +" directionZ:"+directionZ);
    }
    
    private void rotateX(){
       
    	Log.v(TAG, "rotateX() pitch:" +pitch +" yaw:" +yaw +" Y: directionX:"+directionX +" (directionY:"+directionY +" * Math.cos(pitch):"+Math.cos(pitch) +") + (directionZ:"+directionZ + "* Math.sin(pitch):"+Math.sin(pitch ) +" : "+( (directionY * Math.cos(pitch)) + (directionZ * Math.sin(pitch))));
    	Log.v(TAG, "rotateX() pitch:" +pitch +" yaw:" +yaw +"  Z: directionX:"+directionX +" (-directionY:"+directionY +" * Math.sin(pitch):"+Math.sin(pitch) +") + (directionZ:"+directionZ + "* Math.cos(pitch):"+Math.cos(pitch ) +" : "+( (-directionY * Math.sin(pitch)) + (directionZ * Math.cos(pitch)))  );
    	
    	directionY = (float) ( (directionY * Math.cos(pitch)) + (directionZ * Math.sin(pitch)));  
    	directionZ = (float) ( (-directionY * Math.sin(pitch)) + (directionZ * Math.cos(pitch)) );
    }
    
    private void rotateY(){
       Log.v(TAG, "rotateY()  pitch:" +pitch +" yaw:" +yaw +" X: directionY:"+directionY +" (directionX:"+directionX +" * Math.cos(yaw):"+Math.cos(yaw) +") + (directionZ:"+directionZ + "* Math.sin(yaw):"+Math.sin(yaw) +" : "+( (directionX * Math.cos(yaw)) + (directionZ * Math.sin(yaw)))  );
       Log.v(TAG, "rotateY()  pitch:" +pitch +" yaw:" +yaw +" Z: directionY:"+directionY +" (-directionX:"+directionX +" * Math.sin(yaw):"+Math.sin(yaw) +") + (directionZ:"+directionZ + "* Math.cos(yaw):"+Math.cos(yaw) +" : "+( (-directionX * Math.sin(yaw)) + (directionZ * Math.cos(yaw)))  );
    	
       directionX = (float) ( (directionX * Math.cos(yaw))  + (directionZ * Math.sin(yaw)) );
    	//directionY = directionY;	no change
    	directionZ = (float) ( (-directionX * Math.sin(yaw)) + (directionZ * Math.cos(yaw)) );
    }
    
    /**
     * 
     * 	C = C + D*cameraDistancePerFrame
     */
    private void updateCameraPosition() {
    
    	Log.v(TAG, "updateCameraPosition() cameraX:"+ (cameraX + (directionX * cameraSpeed)) + " = cameraX"+ cameraX + " + (directionX * cameraSpeed): (" + directionX+ "*" + cameraSpeed + ")[" + (directionX * cameraSpeed) + "]");
    	Log.v(TAG, "updateCameraPosition() cameraY:"+ (cameraY + (directionY * cameraSpeed)) + " = cameraY"+ cameraY + " + (directionY * cameraSpeed): (" + directionY+ "*" + cameraSpeed + ")[" + (directionY * cameraSpeed) + "]");
    	Log.v(TAG, "updateCameraPosition() cameraZ:"+ (cameraZ + (directionZ * cameraSpeed)) + " = cameraZ"+ cameraZ + " + (directionZ * cameraSpeed): (" + directionZ+ "*" + cameraSpeed + ")[" + (directionZ * cameraSpeed) + "]");
    	
    	cameraX = cameraX + (directionX * cameraSpeed);
    	cameraY = cameraY + (directionY * cameraSpeed);
    	cameraZ = cameraZ + (directionZ * cameraSpeed);
    }	
    
    /**
     * 	LookAt = C + D
     */
    private void updateLookAt() {
    	
    	Log.v(TAG, "updateLookAt() lookX=" + (cameraX + directionX)+ " cameraX:" + cameraX + " directionX:" + directionX);
    	Log.v(TAG, "updateLookAt() lookY=" + (cameraY + directionY)+ " cameraY:" + cameraY + " directionY:" + directionY);
    	Log.v(TAG, "updateLookAt() lookZ=" + (cameraZ + directionZ)+ " cameraZ:" + cameraZ + " directionZ:" + directionZ);
    	
    	lookX = cameraX + directionX;
    	lookY = cameraY + directionY;
    	lookZ = cameraZ + directionZ;
    //		 		lookZ = cameraZ + directionZ + 2.0f;
    }
    
    private void initialPlanePosition(){
    	planeX = lookX;
    	planeY = lookY * .75f;
//    	planeZ = lookZ + .75f;		
    	planeZ = lookZ + 0f;		
    }
    
    private void updatePlanePosition(){
    
    	planeX += (directionX * cameraSpeed);
    	planeY += (directionY * cameraSpeed);
    	planeZ += (directionZ * cameraSpeed);
    	
    	updateVents();
    	
    	if(detectCollision()){
    		resetCamera();
    	}
    	Log.e(TAG, "updatePlanePosition() planeX:"+planeX+" planeY:"+planeY +" planeZ:"+planeZ);
    }
    
    //TODO make this more oo
    private void updateVents(){
    	
    	for(Contents contents : this.hallManager.getContents()){
    		for(Coord coord : contents.getMapCoordToDrawable().keySet()){
    			if( (planeZ >( coord.getZ() - drawableThreshold) 
    					&& planeZ < ( coord.getZ() + drawableThreshold ))
    				&&				
//TODO add enum or type to Ground so we know if its a vent or a Shooter or whatever or simply subclass ground    			
    				(planeY > coord.getY())
    					){
    				Log.i(TAG, "updateVents() - intercepted vent:"+coord.getZ() +" planeZ:"+planeZ);
//    				pitchMessage = " VENTS!!!!!!!!!!!!!!!!!!!!!!!!!";
    				planeY = planeY * 1.13f;
    			}else{
    				Log.i(TAG, "updateVents() - NO vent:"+coord.getZ() +" planeZ:"+planeZ);
    			}
    		}
    	}
    }
    
    private boolean detectCollision(){
    	String side = null;		
    	
    	if(planeX + planeWidth >= wallXOffset)
    		side = "right";
    	else if(planeX  - planeWidth <= -1.0f * wallXOffset)
    		side = "left";
    	else if(planeY + planeHeight >= wallYOffset)
    		side = "top";
    	else if(planeY - planeHeight <= -1.0f * wallYOffset)
    		side = "bottom";
    	else 
    		collisionDetected = false;
    	
    	if(side !=null)
    		collisionDetected = true;
    	
    	if(collisionDetected){
    		Log.d(TAG, "\n\nCOLLISION DETECTED!!! EXITING GAME");
    		pauseExecution();
    		resetCamera();
    	}
    	
    	Log.v(TAG, "\n\ndetectCollision() = "+collisionDetected +" side:"+side +" planeWidth" +planeWidth +"  - planeX:"+planeX +" planeY:"+planeY
    			+" wallXOffset:"+wallXOffset +" -wallXOffset:"+-1.0f * wallXOffset
    			+" wallYOffset:"+wallYOffset +" -wallYOffset:"+-1.0f * wallYOffset);
    	return collisionDetected;
    }
    
    public void setmDx(float mDx) {
    	Log.v(TAG, "setmDx() oldDmx:" + this.mDx + " new mDx:" + mDx);
    	this.mDx = mDx;
    	this.updateYaw();
    }
    
    public void filtermDx(float mDx){
    	float alpha = 0.8f;
    	float tmp;
    	tmp = (alpha * this.mDx) + (1.0f - alpha) * mDx;
    //		 		see if this works, if not create a float[] to track last 5 values and then do a wieghted sum to get current mDx
    }
    
    public void filtermDy(float mDy){
    	float alpha = 0.8f;
    	float tmp;
    	tmp = (alpha * this.mDy) + (1.0f - alpha) * mDy;
    
    }
    
    public void setmDy(float mDy) {
    	Log.v(TAG, "setmDy() oldDmy:" + this.mDy + " new mDy:" + mDy);
    	this.mDy = mDy;
    	this.updatePitch();
    }	
    
    protected void resetCamera(){
    	Log.d(TAG, "resetCamera");
    	
    	cameraX = 0.0f;
    	cameraY = 0f;
    	cameraZ = 4.5f;
    	cameraSpeed = 0.015f;		
    	
    	lookX = 0f;
    	lookY = 0f;
    	lookZ = 6.5f;
    	
    	yaw = 0f;
    	pitch = 0f;
    	
    	initialPlanePosition();
    	
    	setmDx(0);
    	setmDy(0);
    	collisionDetected = false;
    	
    	hallManager = new HallManager(this, 3, 10.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 50.0f, this.objectMatrix, this.objectFinalMatrix, this.mMVPMatrix, this.wallXOffset, this.wallYOffset);    	
    }
    
    	
    private void drawPlane(float[] objectMatrix, float[] finalWallMatrix){
    	
    	final float[] intermediateMatrix = new float[16];
    	final float[] deltaMatrix = new float[16];
    	
    	 // Set the camera position (View matrix)
    //		         Matrix.setIdentityM(wallMatrix, 0);
    	Matrix.setIdentityM(intermediateMatrix, 0);
    	Matrix.setIdentityM(deltaMatrix, 0);
    
    	 //   Move the Object of interest on the Screen
    	  Matrix.translateM(objectMatrix, 0,  planeX, planeY, planeZ);
    	  
    
    	  //	roll - good - 8, 9, 10 best combo		          
    	  Matrix.rotateM(deltaMatrix, 0, (yaw * 70.0f), rotationMatrix[8], rotationMatrix[9], rotationMatrix[10]);
    
    	  // bad
    //		          Matrix.rotateM(deltaMatrix, 0, (yaw * 70.0f), rotationMatrix[0], rotationMatrix[1], rotationMatrix[2]);
    	  //worse
    //		          Matrix.rotateM(deltaMatrix, 0, (yaw * 70.0f), rotationMatrix[4], rotationMatrix[5], rotationMatrix[6]);
    	  
    /*
    	  //	pitch - bad
    	  Matrix.rotateM(deltaMatrix, 0, pitch * -85.0f, rotationMatrix[0], rotationMatrix[1], rotationMatrix[2]);
    
    
    	  //	yaw
    	  Matrix.rotateM(deltaMatrix, 0, (yaw * -80.0f), rotationMatrix[4], rotationMatrix[5], rotationMatrix[6]);
    */          
    //	#3	same as #2 except more vertical?		          
    	  Matrix.multiplyMM(rotationMatrix, 0, deltaMatrix,  0, rotationMatrix, 0);
    	  Matrix.multiplyMM(intermediateMatrix, 0, rotationMatrix,  0, objectMatrix, 0);
    //	#2 works better than 0 or 1		          
    //		          Matrix.multiplyMM(rotationMatrix, 0, rotationMatrix,  0, deltaMatrix, 0);
    //		          Matrix.multiplyMM(intermediateMatrix, 0, rotationMatrix,  0, objectMatrix, 0);
    	  
    // #0
    //		          Matrix.multiplyMM(rotationMatrix, 0, deltaMatrix,  0, rotationMatrix, 0);
    //	          	  Matrix.multiplyMM(intermediateMatrix, 0, objectMatrix,  0, rotationMatrix, 0);		          
    	  
    /*#1 doesn't move
    * 		          Matrix.multiplyMM(rotationMatrix, 0, rotationMatrix,  0, deltaMatrix, 0);
    	  Matrix.multiplyMM(intermediateMatrix, 0, objectMatrix,  0, rotationMatrix, 0);
    * 		          
    */
    
    	  // Combine the Object of interest matrix with the projection and camera view
    	  Matrix.multiplyMM(finalWallMatrix, 0, mMVPMatrix, 0, intermediateMatrix, 0);
    
    	// Draw Ground
    	  plane.draw(finalWallMatrix);      
    }
    
	private void drawPlane(float[] wallMatrix, float[] rotationMatrix, float[] finalWallMatrix){

        float[] deltaMatrix = new float[16];
		final float[] intermediateMatrix = new float[16];
		planeMatrix = new float[16];

		 // Set the camera position (View matrix)
        Matrix.setIdentityM(wallMatrix, 0);
        Matrix.setIdentityM(rotationMatrix, 0); 
        Matrix.setIdentityM(planeMatrix, 0);
        Matrix.setIdentityM(intermediateMatrix, 0);
        Matrix.setIdentityM(deltaMatrix, 0);
/*         
         //	pitch
         Matrix.rotateM(wallMatrix, 0, pitch * -85.0f, 1, 0, 0);         
         //	roll
         Matrix.rotateM(wallMatrix, 0, (yaw * 50.0f), 0, 0, 1);
         //	yaw
         Matrix.rotateM(wallMatrix, 0, (yaw * -80.0f), 0, 1, 0);
*/         
         //   Move the Object of interest on the Screen
         Matrix.translateM(wallMatrix, 0,  planeX, planeY, planeZ);         

         //	roll
         Matrix.rotateM(deltaMatrix, 0, (yaw * 70.0f), 0, 0, 1);
         
         //	pitch
         Matrix.rotateM(deltaMatrix, 0, pitch * -85.0f, rotationMatrix[0], rotationMatrix[1], rotationMatrix[2]);         

         //	yaw
         Matrix.rotateM(deltaMatrix, 0, (yaw * -80.0f), rotationMatrix[4], rotationMatrix[5], rotationMatrix[6]);
         
//         Matrix.multiplyMM(intermediateMatrix, 0, rotationMatrix, 0, wallMatrix, 0);
//         Matrix.multiplyMM(intermediateMatrix, 0, wallMatrix,  0, rotationMatrix, 0);



	// Combine the Object of interest matrix with the projection and camera view
//         Matrix.multiplyMM(finalWallMatrix, 0, mMVPMatrix, 0, intermediateMatrix, 0);
         Matrix.multiplyMM(rotationMatrix, 0, deltaMatrix,  0, rotationMatrix, 0);
         Matrix.multiplyMM(intermediateMatrix, 0, rotationMatrix,  0, wallMatrix, 0);         
         Matrix.multiplyMM(planeMatrix, 0, mMVPMatrix, 0, intermediateMatrix, 0);
 
       // Draw Ground
         plane.draw(planeMatrix);         
//       ground.draw(finalWallMatrix);		
	}    
    
    protected void updateGravity(){
    	updateCameraSpeed();
    	setTilt();		
    }
    	
    protected void updateCameraSpeed(){
    	Log.v(TAG, "updateCameraSpeed() pitch:"+pitch +" idealPitch:" +idealPitch +" pitchTolerance: "+pitchTolerance);		
    	if( pitch >  (idealPitch + pitchTolerance) ){
    		Log.v(TAG, "updateCameraSpeed() Slow Down  - oldCameraSpeed:"+cameraSpeed +" newCameraSpeed:" +(cameraSpeed - accelerateRate));
    		cameraSpeed -= accelerateRate;
//    		pitchMessage = "Slowing Down";
    	}else if(pitch < (idealPitch - pitchTolerance)){
    		Log.v(TAG, "updateCameraSpeed() Speed Up -  oldCameraSpeed:"+cameraSpeed +" newCameraSpeed:" +(cameraSpeed + accelerateRate));
    		cameraSpeed += accelerateRate;
//    		pitchMessage = "Speeding Up";
    	}else{
    		Log.v(TAG, "updateCameraSpeed() no change: pitch:"+pitch +" idealPitch:" +idealPitch +" pitchTolerance: "+pitchTolerance);
//    		pitchMessage = "No Speed Change";
    	}
    }
    
    protected void setTilt(){
    
    	//	find min speed based upon current pitch
    	float minSpeed = (float) (Math.sin(pitch) - yIntercept);
    	Log.v(TAG, "setTilt() cameraSpeed:"+cameraSpeed +" minSpeed = "+ minSpeed +" = pitch:"+pitch + "  sin(pitch):" +Math.sin(pitch) +"- yIntercept:"+yIntercept);
    	
    	//	find ideal pitch based upon intersection of current speed and min speed line
    	if(cameraSpeed < minSpeed){
    		float sinIdealPitch = cameraSpeed + yIntercept;
    		float idealPitch = (float) Math.asin(sinIdealPitch);
    		Log.v(TAG, "setTilt() idealPitch:"+idealPitch +" sinIdealPitch = "+(cameraSpeed + yIntercept) +" = cameraSpeed:"+cameraSpeed +" + yIntercept:"+yIntercept);  
    		pitch = idealPitch;
    	}
    }
    
    @Override
    public void onSensorChanged(SensorEvent event) {
    	  // In this example, alpha is calculated as t / (t + dT),
    	  // where t is the low-pass filter's time-constant and
    	  // dT is the event delivery rate.
    
    	  final float alpha = 0.8f;
    
    	  if(!viewSonic){
    	// Isolate the force of gravity with the low-pass filter.
    	  gravity[0] = alpha * gravity[0] /* previous X gravity */ + (1 - alpha) * event.values[0];
    	  gravity[1] = alpha * gravity[1] /* previous Y gravity */ + (1 - alpha) * event.values[1];
    	  gravity[2] = alpha * gravity[2] /* previous Y gravity */ + (1 - alpha) * event.values[2];
    
    	  // Remove the gravity contribution with the high-pass filter.
    	  linear_acceleration[0] = event.values[0] - gravity[0];
    	  linear_acceleration[1] = event.values[1] - gravity[1];
    	  linear_acceleration[2] = event.values[2] - gravity[2];
    	  }else{
    		  
    			// Isolate the force of gravity with the low-pass filter.
    		  gravity[0] = alpha * gravity[0] /* previous X gravity */ + (1 - alpha) * event.values[0];
    		  gravity[1] = alpha * gravity[1] /* previous Y gravity */ + (1 - alpha) * event.values[1];
    		  gravity[2] = alpha * gravity[2] /* previous Y gravity */ + (1 - alpha) * event.values[2];
    
    		  // Remove the gravity contribution with the high-pass filter.
    		  linear_acceleration[0] = event.values[0] - gravity[0];
    		  linear_acceleration[1] = event.values[1] - gravity[1];
    		  linear_acceleration[2] = event.values[2] - gravity[2];			  
    /*			  
    		  gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
    		  gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
    		  gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
    
    		  linear_acceleration[0] = (-event.values[1]) - gravity[0];
    		  linear_acceleration[1] = event.values[0] - gravity[1];
    		  linear_acceleration[2] = (-event.values[2]) - gravity[2];
    */			  
    	  }
    	  /* forward backward front back tilt*/
    	  Log.v(TAG, "onSensorChanged() - linear_acceleration[0]:"+linear_acceleration[0] + " gravity[0]:"+gravity[0] +" event.values[0]:" +event.values[0]);
    	  /*side to side side to side tilt */
    	  Log.v(TAG, "onSensorChanged() - linear_acceleration[1]:"+linear_acceleration[1] + " gravity[1]:"+gravity[1] +" event.values[1]:" +event.values[1]);
    	  /* up down fronttilt maby*/
    	  Log.v(TAG, "onSensorChanged() - linear_acceleration[2]:"+linear_acceleration[2] + " gravity[2]:"+gravity[2] +" event.values[2]:" +event.values[2]);
    	  
    	//	set yaw
    	setmDx(linear_acceleration[1] * .90f);
    //		 		setmDx(linear_acceleration[1] );
    	// set pitch
    	setmDy(linear_acceleration[0] * .10f);
    
    	
    }	
    
    private void loadText(){
    	
    	// Create an empty, mutable bitmap
    	Bitmap bitmap = Bitmap.createBitmap(400, 250, Bitmap.Config.ARGB_4444);
    	// get a canvas to paint over the bitmap
    	canvas = new Canvas(bitmap);
    	bitmap.eraseColor(0);
    
    	// get a background image from resources
    	// note the image format must match the bitmap format
    	background = this.view.getContext().getResources().getDrawable(R.drawable.background_small);
    	background.setBounds(0, 0, 400, 256);
    	background.draw(canvas); // draw the background to our bitmap
    
    	// Draw the text
    	textPaint = new Paint();
    	textPaint.setTextSize(32);
    	textPaint.setAntiAlias(true);
    	textPaint.setARGB(0xff, 0x00, 0x00, 0x00);
    	
    }
    
    private void renderText(){
    	GLES20.glDisable( GLES20.GL_DEPTH_TEST );
    	GLES20.glEnable( GLES20.GL_BLEND );                   // Enable Alpha Blend
    	GLES20.glBlendFunc( GLES20.GL_SRC_ALPHA, GLES20.GL_ZERO );  // Set Alpha Blend Function
    	
    //		 		do stuff
    	
    	GLES20.glDisable( GLES20.GL_BLEND );  
    	GLES20.glEnable( GLES20.GL_DEPTH_TEST );
    /*
    	// draw the text centered
    	canvas.drawText("Hello World", 16,112, textPaint);
    
    	int[] textureIds = new int[1];
    	//Generate one texture pointer...
    	gl.glGenTextures(1, textureIds, 0);
    	//...and bind it to our array
    	gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIds[0]);
    
    	//Create Nearest Filtered Texture
    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
    
    	//Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);
    
    	//Use the Android GLUtils to specify a two-dimensional texture image from our bitmap
    	GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
    
    	//Clean up
    	bitmap.recycle();		
    	
    */	
    }
    
    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
    /**/
    	 glText = new GLText( context.getAssets(), width, height );
    	 glText.load( "Roboto-Regular.ttf", 14, 2, 2 );
    	 
    	image = new Image();
    	//image.loadTexture(context);
    	
    	// Adjust the viewport based on geometry changes,
    	// such as screen rotation
    	GLES20.glViewport(0, 0, width, height);
    	Log.v(TAG, "onSurfaceChanged width:" + width + " height:" + height +" ratio:"+((float) width / height));
    	float ratio = (float) width / height;
    	// this projection matrix is applied to object coordinates
    	// in the onDrawFrame() method
    	// Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 3, 1000);
    	Matrix.frustumM(mProjMatrix, 0, -ratio / 3.0f, ratio / 3.0f,-1.0f / 3.0f, 1.0f / 3.0f, 1, 1000);
    	Log.v(TAG, "Matrix.frustumM(mProjMatrix, " + 0 + "," + -ratio / 3.0f+ "," + ratio / 3.0f + "," + -1.0f / 3.0f + "," + 1.0f / 3.0f+ "," + 1 + "," + 1000);
    
    }
    
    public static int loadShader(int type, String shaderCode) {
    
    	// create a vertex shader type (GLES20.GL_VERTEX_SHADER)
    	// or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
    	int shader = GLES20.glCreateShader(type);
    	
    	// add the source code to the shader and compile it
    	GLES20.glShaderSource(shader, shaderCode);
    	GLES20.glCompileShader(shader);
    	
    	// Get the compilation status.
    	final int[] compileStatus = new int[1];
    	GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
    
    	// If the compilation failed, delete the shader.
    	if (compileStatus[0] == 0) 
    	{
    		Log.v(TAG, "Error compiling shader: " + GLES20.glGetShaderInfoLog(shader));
    		GLES20.glDeleteShader(shader);
    		shader = 0;
    	}else{
    		Log.v(TAG, "Successful compilation of shader:"+shader +" code:"+shaderCode);
    	}
    
    	return shader;
    }
    
    private void pauseExecution() {
    	Log.v(TAG, "pauseExecution()");					
    	
    	try {
    		Thread.sleep(250);
    	} catch (InterruptedException e) {
    		e.printStackTrace();
    	}
    
    }
    
    /**
     * Utility method for debugging OpenGL calls. Provide the name of the call
     * just after making it:
     * 
     * <pre>
     * mColorHandle = GLES20.glGetUniformLocation(mProgram, &quot;vColor&quot;);
     * MyGLRenderer.checkGlError(&quot;glGetUniformLocation&quot;);
     * </pre>
     * 
     * If the operation is not successful, the check throws an error.
     * 
     * @param glOperation
     *            - Name of the OpenGL call to check.
     */
    public static void checkGlError(String glOperation) {
    	int error;
    	String errors = "";		 	
    	while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
    		errors += error +",";//GLUtils.getEGLErrorString(error);
    		//Log.e(TAG, glOperation + ": glError " + error +":"+errorString);
    		Log.e(TAG, glOperation + ": glError " + error);
    //		 			throw new RuntimeException(glOperation + ": glError " + error +":"+errorString);
    		
    	}
    	if(!errors.isEmpty())
    		throw new RuntimeException(glOperation + ": glError " + errors);
    }
    
    public float getmDx() {
    	return mDx;
    }
    
    public float getmDy() {
    	return mDy;
    }
    
    public float getPlaneY() {
		return planeY;
	}

	public float getPlaneX() {
		return planeX;
	}

	public float getPlaneZ() {
		return planeZ;
	}

	public float getPlaneWidth() {
		return planeWidth;
	}

	public float getPlaneHeight() {
		return planeHeight;
	}

	@Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
    	
    }
    
    private void copyMatrix(float[] input, float[] output){
    	for(int i =0 ;i<input.length; i++){
    		output[i] = input[i];
    		if(Log.isLoggable(TAG, Log.VERBOSE))
    			Log.v(TAG, "copyMatrix input["+i+"]="+input[i] +"output[i"+i +"]" +output[i]);
    	}
    }

}
