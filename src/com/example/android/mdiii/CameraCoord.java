package com.example.android.mdiii;

class CameraCoord extends Coord{
	public float cameraX;
	public float cameraY;
	public float cameraZ;
	public float lookX;
	public float lookY;
	public float lookZ;
	public float upX;
	public float upY;
	public float upZ;
	
	
	
	public CameraCoord(float cameraX, float cameraY, float cameraZ, float lookX,
			float lookY, float lookZ, float upX, float upY, float upZ) {
		super();
		this.cameraX = cameraX;
		this.cameraY = cameraY;
		this.cameraZ = cameraZ;
		this.lookX = lookX;
		this.lookY = lookY;
		this.lookZ = lookZ;
		this.upX = upX;
		this.upY = upY;
		this.upZ = upZ;
	}
	public float getCameraX() {
		return cameraX;
	}
	public void setCameraX(float cameraX) {
		this.cameraX = cameraX;
	}
	public float getCameraY() {
		return cameraY;
	}
	public void setCameraY(float cameraY) {
		this.cameraY = cameraY;
	}
	public float getCameraZ() {
		return cameraZ;
	}
	public void setCameraZ(float cameraZ) {
		this.cameraZ = cameraZ;
	}
	public float getLookX() {
		return lookX;
	}
	public void setLookX(float lookX) {
		this.lookX = lookX;
	}
	public float getLookY() {
		return lookY;
	}
	public void setLookY(float lookY) {
		this.lookY = lookY;
	}
	public float getLookZ() {
		return lookZ;
	}
	public void setLookZ(float lookZ) {
		this.lookZ = lookZ;
	}
	public float getUpX() {
		return upX;
	}
	public void setUpX(float upX) {
		this.upX = upX;
	}
	public float getUpY() {
		return upY;
	}
	public void setUpY(float upY) {
		this.upY = upY;
	}
	public float getUpZ() {
		return upZ;
	}
	public void setUpZ(float upZ) {
		this.upZ = upZ;
	}

}