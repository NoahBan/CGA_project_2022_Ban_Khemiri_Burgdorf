#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 textureC;
layout(location = 2) in vec3 normal;


//uniforms
// translation object to world
uniform mat4 model_matrix;
uniform mat4 view_matrix;
uniform mat4 projection_matrix;

uniform sampler2D texEmit;

uniform float tcMultiplier1;
uniform float tcMultiplier2;

out struct VertexData
{
    vec3 position;
    vec3 normal;
    sampler2D texEmit;
} vertexData;

void main(){

    vec4 pos = projection_matrix * view_matrix * model_matrix * vec4(position, 1.0f) ;

    gl_Position = pos;

    vertexData.position = pos.xyz;

    vec4 norm = transpose(inverse(view_matrix * model_matrix)) * vec4(normal, 0.0f);

    vertexData.normal = norm.xyz;

}
