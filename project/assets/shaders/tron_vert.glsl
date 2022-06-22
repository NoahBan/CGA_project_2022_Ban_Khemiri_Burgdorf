#version 330 core

//vertexData
layout(location = 0) in vec3 position;
layout(location = 1) in vec2 texCoord;
layout(location = 2) in vec3 normal;

//model transformation
uniform mat4 model_matrix;

//camera
uniform mat4 view_matrix;
uniform mat4 projection_matrix;

//output to fragment sher
out struct VertexData
{
    vec3 position;
    vec3 normal;
    vec2 texCoord;
} vertexData;


void main(){
    //transform vertices
    vec4 pos = projection_matrix * view_matrix * model_matrix * vec4(position, 1.0f) ;
    gl_Position = pos;
    vertexData.position = pos.xyz;

    //camera normals
    vec4 norm = transpose(inverse(view_matrix * model_matrix)) * vec4(normal, 0.0f);
    vertexData.normal = norm.xyz;

    vertexData.texCoord = texCoord;
}
