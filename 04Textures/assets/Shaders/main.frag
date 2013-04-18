precision mediump float;
varying vec2 texCoords;
uniform sampler2D cMap;

void main(){
  gl_FragColor = texture2D( cMap, texCoords);
  //gl_FragColor = texture2D( cMap, texCoords * vec2(2, 2));
}