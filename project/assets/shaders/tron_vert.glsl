#version 330 core

//Vertex Attributes
layout(location = 0) in vec3 position;
layout(location = 1) in vec2 texCoord;
layout(location = 2) in vec3 normal;

//vertex data to fragment shader
out struct VertexData
{
    vec3 position;
    vec3 normal;
    vec2 texCoord;
} vertexData;

out vec4 fragmentPosition;

//model transformation
uniform mat4 model_matrix;

//camera
uniform mat4 view_matrix;
uniform mat4 projection_matrix;
out vec3 ViewDirection;

//PointLights
struct PointLight
{
    vec3 lightPos;
    vec3 lightColor;
    int attenuationType;
    float intensity;
};
uniform PointLight pointLightArray[10];
uniform int pointLightArrayLength;
out vec3 pointLightDirArray[10];
out float pointLightDistArray[10];

//directLights
struct spotLight
{
    vec3 lightPos;
    vec3 lightColor;
    float intensity;
    vec3 direction;
    float cutOff;
};
uniform spotLight spotLightArray[10];
uniform int spotLightArrayLength;
out vec3 spotLightDirArray[10];
out vec3 spotLightTargetDirArray[10];
out float spotLightDistArray[10];


void main(){
    //transform vertices
    vec4 pos = projection_matrix * view_matrix * model_matrix * vec4(position, 1.0f);
    gl_Position = pos;
    vertexData.position = pos.xyz;

    //camera normals
    vec4 norm = transpose(inverse(view_matrix * model_matrix)) * vec4(normal, 0.0f);
    vertexData.normal = norm.xyz;

    //texture coordinates
    vertexData.texCoord = texCoord;

    fragmentPosition = (view_matrix * model_matrix * vec4(position, 1.0f));

    ViewDirection = (view_matrix * model_matrix * vec4(-position, 1.0f)).xyz;

    //point light direction
    for (int i = 0 ; i < pointLightArrayLength ; i++)
    {
        vec4 lightPosition = view_matrix * vec4(pointLightArray[i].lightPos, 1.0);
        pointLightDirArray[i] = (lightPosition - fragmentPosition).xyz ;

        float distance = length(pointLightArray[i].lightPos - (model_matrix * vec4(position, 1.0f)).xyz);
        pointLightDistArray[i] = distance;
    }

    for (int j = 0 ; j < spotLightArrayLength ; j++)
    {
        vec4 lightPosition = view_matrix * vec4(spotLightArray[j].lightPos, 1.0);
        spotLightDirArray[j] = (lightPosition - fragmentPosition).xyz;

        float distance = length(spotLightArray[j].lightPos - (model_matrix * vec4(position, 1.0f)).xyz);
        spotLightDistArray[j] = distance;

        spotLightTargetDirArray[j] = (view_matrix * vec4(spotLightArray[j].direction, 1)).xyz;
    }
}
