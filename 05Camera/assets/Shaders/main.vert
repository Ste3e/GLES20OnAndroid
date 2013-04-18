attribute vec3 pos;
attribute vec2 coords;
uniform vec4 proj;
uniform vec3 mPos;
uniform vec3 cPos;
uniform mat3 cMat;

varying vec2 texCoords;

void main(){
  texCoords = coords;
  vec3 eyePos = cMat * (pos + mPos - cPos);
  vec4 clip;
  clip.x = eyePos.x * proj.x;
  clip.y = eyePos.y * proj.y;
  clip.z = eyePos.z * proj.z + proj.w;
  clip.w = -eyePos.z;
  gl_Position = clip;
}
