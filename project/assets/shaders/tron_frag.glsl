#version 330 core

//input from vertex shader
in struct VertexData
{
    vec3 position;
    vec3 normal;
} vertexData;

//fragment shader output
out vec4 color;



void main(){

    color = normalize(vec4(abs(vertexData.normal), 1.0f));

}