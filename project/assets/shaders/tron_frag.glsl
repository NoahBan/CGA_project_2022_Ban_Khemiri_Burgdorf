#version 330 core

uniform sampler2D texEmit;

//input from vertex shader
in struct VertexData
{
    vec3 position;
    vec3 normal;
    vec2 texCoord;
    vec2 tcMultiplier;
    float shininess;
} vertexData;

//fragment shader output
out vec4 color;


void main(){

  //    color = vec4(vertexData.textureC[0],vertexData.textureC[1],1,1);

    color = texture2D(texEmit,vertexData.texCoord);

//      color = normalize(vec4(abs(vertexData.normal), 1.0f));

}