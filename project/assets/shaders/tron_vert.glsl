#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 texCoord;
layout(location = 2) in vec3 normal;


//uniforms
// translation object to world
uniform mat4 model_matrix;
uniform mat4 view_matrix;
uniform mat4 projection_matrix;

uniform float tcMultiplier1;
uniform float tcMultiplier2;

uniform float shininess;

out struct VertexData
{
    vec3 position;
    vec3 normal;
    vec2 texCoord;
    vec2 tcMultiplier;
    float shininess;
} vertexData;

void main(){

    vertexData.texCoord = texCoord;

    vec4 pos = projection_matrix * view_matrix * model_matrix * vec4(position, 1.0f) ;

    gl_Position = pos;

    vertexData.position = pos.xyz;


    //camera normals
    vec4 norm = transpose(inverse(view_matrix * model_matrix)) * vec4(normal, 0.0f);
    vertexData.normal = norm.xyz;

    vertexData.shininess = shininess;

    vec2 tcMultiplier = vec2(tcMultiplier1,tcMultiplier2);

}
