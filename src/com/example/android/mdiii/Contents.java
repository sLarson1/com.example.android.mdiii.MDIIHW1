/**
 * 
 */
package com.example.android.mdiii;

import java.util.Map;
import java.util.Map.Entry;

import android.opengl.Matrix;
import android.util.Log;

/**
 * @author 4678603
 *
 */
public class Contents {

	private HallManager hallManager;
	private Map<Coord, Drawable> mapCoordToDrawable;
	private Ground ground;
	private static float padding = 0.1f;
	private static float lookAhead = 2.0f;
	private float[] objectMatrix = new float[16];
	private final String TAG = "Contents";
	private float[] finalWallMatrix;
	private float[] mMVPMatrix;	
	
	
	
	public Contents(HallManager hallManager, Map<Coord, Drawable> map,
			float[] objectMatrix, float[] mMVPMatrix, float[] finalWallMatrix) {
		super();
		Log.v(TAG, "Contents()");
		this.hallManager = hallManager;
		this.mapCoordToDrawable = map;
		this.objectMatrix = objectMatrix;
		this.mMVPMatrix = mMVPMatrix;
		this.finalWallMatrix = finalWallMatrix;
		this.ground = new Ground();
	}


/*
	public void draw(Coord coord){
		Log.v(TAG, "draw("+coord+")");
		for(Map.Entry<Coord, Drawable> entry : mapCoordToDrawable.entrySet()){
		//	fix this if
			if(entry.getKey().getZ() < coord.getZ() + padding){
				Log.v(TAG, "draw("+coord+") time to draw:"+entry.getKey().getZ() +" padding:"+padding +" coords:" +coord);
//				hallManager.renderer.pitchMessage =  hallManager.renderer.pitchMessage.concat(" CONTENTS!!!");
				
		        Matrix.setIdentityM(objectMatrix, 0);

//		        Matrix.translateM(objectMatrix, 0, coord.getX(), coord.getY(), coord.getZ() );
		        Matrix.translateM(objectMatrix, 0, coord.getX(), coord.getY(), (coord.getZ() + lookAhead) );
		        
		        Log.v(TAG, "Contents.draw(objectMatrix, 0,  coord.getX():" + coord.getX()+ ", coord.getY():" + coord.getY() + ", coord.getZ():" + (coord.getZ() + lookAhead)+ " )");              
		        Matrix.multiplyMM(finalWallMatrix, 0, mMVPMatrix, 0,objectMatrix, 0);
		        
		        // Draw Wall
		        //Log.v(TAG, "drawing entity:"+entry.getValue());
//		        entry.getValue().draw(finalWallMatrix);
		        Log.v(TAG, "drawing entity:"+ground);
		        ground.draw(finalWallMatrix);
		        
				hallManager.getInteractiveObjectMap().put(entry.getKey(), entry.getValue());
			}else{
				Log.v(TAG, "NO draw("+coord+") :"+entry.getKey().getZ() +" padding:"+padding +" coords:" +coord);
			}
				
		}
	}
*/
	
	private void draw(Coord coord, Drawable drawable){
		Log.v(TAG, "draw("+coord+"," +drawable +" )");
				Log.v(TAG, "draw("+coord+") time to draw:"+" padding:"+padding +" coords:" +coord);
//				hallManager.renderer.pitchMessage =  hallManager.renderer.pitchMessage.concat(" CONTENTS!!!");
				
		        Matrix.setIdentityM(objectMatrix, 0);

		        Matrix.translateM(objectMatrix, 0, coord.getX(), coord.getY(), (coord.getZ() + lookAhead) );
		        
		        Log.v(TAG, "Contents.draw(objectMatrix, 0,  coord.getX():" + coord.getX()+ ", coord.getY():" + coord.getY() + ", coord.getZ():" + (coord.getZ() + lookAhead)+ " )");              
		        Matrix.multiplyMM(finalWallMatrix, 0, mMVPMatrix, 0,objectMatrix, 0);
		        
		        Log.v(TAG, "drawing entity:"+ground);
		        drawable.draw(finalWallMatrix);
	}
	
	public void draw(){
		Log.v(TAG, "draw()");
		for(Entry<Coord, Drawable> entry : mapCoordToDrawable.entrySet()){
			this.draw(entry.getKey(), entry.getValue());
		}
	}


	public Map<Coord, Drawable> getMapCoordToDrawable() {
		return mapCoordToDrawable;
	}
	
	
}
