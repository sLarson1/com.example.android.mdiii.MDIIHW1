
attribute vec4 vPosition;

attribute vec2 aTexPos;

varying vec2 vTexPos;

void main() {

  gl_Position = vPosition ;
  gl_Position.x = 2.0 * (vPosition.x - 0.5) ;
  gl_Position.y = 2.0 * (vPosition.y - 0.5) ;
  
//  vTexPos.x = 0.0;
//  vTexPos.y = 0.0;
  vTexPos = aTexPos;

}
