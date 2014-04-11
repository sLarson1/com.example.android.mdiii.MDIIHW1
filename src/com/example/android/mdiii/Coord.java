/**
 * 
 */
package com.example.android.mdiii;

/**
 * @author 4678603
 *
 */
public class Coord {

	private float x;
	private float y;
	private float z;
	
	
	public Coord(){
		
	}
	
	
	public Coord(float x, float y, float z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}


	public float getX() {
		return x;
	}


	public void setX(float x) {
		this.x = x;
	}


	public float getY() {
		return y;
	}


	public void setY(float y) {
		this.y = y;
	}


	public float getZ() {
		return z;
	}


	public void setZ(float z) {
		this.z = z;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Coord [x=");
		builder.append(x);
		builder.append(", y=");
		builder.append(y);
		builder.append(", z=");
		builder.append(z);
		builder.append("]");
		return builder.toString();
	}
	
	

}
