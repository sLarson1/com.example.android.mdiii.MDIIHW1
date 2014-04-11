
package com.example.android.mdiii;

import android.app.Activity;
import android.content.Context;
import android.nfc.Tag;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

public class MDIIHW1 extends Activity {

    private GLSurfaceView mGLView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity
        mGLView = new MyGLSurfaceView(this);
 
        setContentView(mGLView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.
        mGLView.onPause();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        mGLView.onResume();
    }
}

class MyGLSurfaceView extends GLSurfaceView {
	
	MyGLRenderer render;
    String TAG = "MyGLSurfaceView";
    float x;
    float y;
    float xDelta;
    float yDelta;
    long lastTouchTime;
    long doubleTapDuration;
    long holdDuration;
	
    public MyGLSurfaceView(Context context) {
        super(context);

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);
        //Uncomment line below to run on device?
//        setEGLConfigChooser(8 , 8, 8, 8, 16, 0);
        // Set the Renderer for drawing on the GLSurfaceView
        render = new MyGLRenderer(this);
        setRenderer(render);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        doubleTapDuration = 500;
        holdDuration = 50;
    }

	@Override
	public boolean onTouchEvent(MotionEvent e) {
//	   use pressure to tell game to start over or recenter?
		Log.d(TAG, "TouchOldX:"+x +" TouchNewX:"+e.getX());
		Log.d(TAG, "TouchOldY:"+y +" TouchNewY:"+e.getY());
		
		xDelta = (Math.abs( x - e.getX()) ) / x;
		yDelta = (Math.abs( y - e.getY()) ) / y;
		
//		if(xDelta > render.xDeltaThreshold || yDelta > render.yDeltaThreshold){
			Log.d(TAG, "xDelta:"+xDelta +" xDeltaThreshold:" +render.xDeltaThreshold +" or yDelta:" +yDelta +" yDeltaThreshold:" +render.yDeltaThreshold);
			x = e.getX();
			y = e.getY();
			
			switch(e.getAction()){
			case MotionEvent.ACTION_MOVE:
				long now = System.currentTimeMillis();
				long duration = (now - lastTouchTime);
				if(duration < doubleTapDuration ){
					if(duration > holdDuration){
						Log.d(TAG, "\n\n\nDoubleTap!!!\n\n");
						render.resetCamera();
					}else{
						Log.d(TAG, "tapHOLD");
					}
				}else{
					Log.d(TAG, "regular tap");
				}
				lastTouchTime = now;
			    Log.d(TAG, "MyGLSurfaceView() Height:"+getHeight()+" Width:"+getWidth());
//			    render.setmDx( (x - (getWidth() / 2.0f) ) / (getWidth()/20f) );
//				render.setmDx( (x - (getWidth() / 2.0f) ) / (getWidth()/4.5f) );
				render.setmDx( (x - (getWidth() / 2.0f) ) / (getWidth()/2.0f) );
//				render.setmDy( (y - (getHeight() / 2.0f) ) / (getHeight()/20f) );
				render.setmDy( (y - (getHeight() / 2.0f) ) / (getHeight()/2.0f) );
				break;
				default:
			}			
//		}
		
		return true;
	}
    
    
}
