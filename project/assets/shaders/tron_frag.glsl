#version 330 core

//input from vertex shader
in struct VertexData
{
     vec3 position;
     vec3 normal;
     vec2 textureC;
     sampler2D texEmit;
     vec2 tcMultiplier;
} vertexData;

//fragment shader output
out vec4 color;



void main(){

    //color = texture2D(texEmit,textureC*tcMultiplier);

    color = normalize(vec4(abs(vertexData.normal), 1.0f));

}