#version 330 core

layout (location = 0) in vec3 Position;

out vec3 TexCoords;

uniform mat4 View_matrix;
uniform mat4 Projection_matrix;

void main()
{
    vec4 pos =  Projection_matrix * View_matrix * vec4(Position, 1.0);
    gl_Position = pos.xyww;
    TexCoords = vec3(Position.x,Position.y,-Position.z);

}

