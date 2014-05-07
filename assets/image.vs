	uniform mat4 uMVPMatrix;
	
	attribute vec4 a_Position;
	
	attribute vec4 a_Color;
	
	attribute vec2 a_TexCoord;
	
	varying vec4 v_Color;
	
	varying vec2 v_TexCoord;
	
	
	void main() {
	  // the matrix must be included as a modifier of gl_Position
	  gl_Position = uMVPMatrix * a_Position;
	  v_Color = a_Color;
	  v_TexCoord = a_TexCoord;
	}