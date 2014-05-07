package com.example.android.mdiii;

public interface Drawable {


	public boolean isFinished();
	
	public void draw(float[] mvpMatrix);
	
	public void draw(Coord coord, float[] mvpMatrix);
}
