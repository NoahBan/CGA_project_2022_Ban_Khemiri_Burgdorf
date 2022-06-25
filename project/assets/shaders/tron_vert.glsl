#version 330 core

struct Light
{
    vec3 lightPos;
    vec3 lightColor;
};
uniform Light lightsArray[10];
uniform int lightsArrayLength;

//vertexData
layout(location = 0) in vec3 position;
layout(location = 1) in vec2 texCoord;
layout(location = 2) in vec3 normal;

//model transformation
uniform mat4 model_matrix;

//camera
uniform mat4 view_matrix;
uniform mat4 projection_matrix;

//light
uniform vec3 lightPos;


out vec3 lightDir;

//output to fragment shader
out struct VertexData
{
    vec3 position;
    vec3 normal;
    vec2 texCoord;
} vertexData;

out vec3 lightDirArray[10];

void main(){
    //transform vertices
    vec4 pos = projection_matrix * view_matrix * model_matrix * vec4(position, 1.0f);
    gl_Position = pos;
    vertexData.position = pos.xyz;

    //camera normals
    vec4 norm = transpose(inverse(view_matrix * model_matrix)) * vec4(normal, 0.0f);
    vertexData.normal = norm.xyz;

    vertexData.texCoord = texCoord;

//    vec4 lp = view_matrix * vec4(lightPos, 1.0);
//    vec4 p = (view_matrix * model_matrix * vec4(position, 1.0f));
//    lightDir = (lp - p).xyz ;


    for (int i = 0 ; i < lightsArrayLength ; i++)
    {
        vec4 lp = view_matrix * vec4(lightsArray[i].lightPos, 1.0);
        vec4 p = (view_matrix * model_matrix * vec4(position, 1.0f));
        lightDirArray[i] = (lp - p).xyz ;
    }
}
