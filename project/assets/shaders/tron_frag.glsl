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



    color = texture2D(texEmit,vertexData.texCoord * vertexData.tcMultiplier);

//      color = normalize(vec4(abs(vertexData.normal), 1.0f));

}