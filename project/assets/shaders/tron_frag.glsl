#version 330 core

uniform sampler2D texEmit;
uniform sampler2D texDiff;
uniform sampler2D texSpec;

//light
uniform mat4 lightMatrix;
uniform vec3 lightColor;

//material
uniform vec2 tcMultiplier;
uniform float shininess;


//input from vertex shader
in struct VertexData
{
     vec3 position;
     vec3 normal;
     vec2 texCoord;
} vertexData;

//fragment shader output
out vec4 color;


void main(){

    color = texture(texEmit, vertexData.texCoord * tcMultiplier);

}