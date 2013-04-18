attribute vec3 pos;
attribute vec2 coords;
varying vec2 texCoords;

void main(){
  texCoords = coords;

  gl_Position = vec4(pos, 1);
}