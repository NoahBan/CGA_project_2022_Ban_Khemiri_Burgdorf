#version 330 core

//Vertex Attributes
layout (location = 0) in vec3 Vertex_Position;
layout (location = 1) in vec2 TexCoord;

out vec2 vertexTexCoord;

void main(){
    vertexTexCoord = TexCoord;
    gl_Position = vec4(Vertex_Position, 1.0);
}
