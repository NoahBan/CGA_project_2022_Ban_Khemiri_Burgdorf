#version 420 core

//Vertex Attributes
layout (location = 0) in vec2 Vertex_Position;

out vec2 vertexTexCoord;

void main(){
    vertexTexCoord = Vertex_Position * 0.5 + 0.5;
    gl_Position = vec4(Vertex_Position, 0.0, 1.0);
}
