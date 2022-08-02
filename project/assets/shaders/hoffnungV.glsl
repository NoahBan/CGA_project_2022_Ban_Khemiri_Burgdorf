#version 330 core

layout (location = 0) in vec3 Vertex_Position;
layout (location = 1) in vec2 TexCoord;
layout (location = 2) in vec3 Normal;

//model transformation
uniform mat4 Model_matrix;

//camera
uniform mat4 View_matrix;
uniform mat4 Projection_matrix;

void main()
{
    gl_Position = Projection_matrix * View_matrix * Model_matrix * vec4(Vertex_Position, 1.0);
}