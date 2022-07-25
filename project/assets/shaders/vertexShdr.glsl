#version 330 core

//Vertex Attributes
layout(location = 0) in vec3 Vertex_Position;
layout(location = 1) in vec2 TexCoord;
layout(location = 2) in vec3 Normal;


#define MAXPOINTLIGHTS 5
#define MAXSPOTLIGHTS 5
#define MAXDIRECTIONALLIGHTS 5

//vertex data to fragment shader
out struct VertexDataStruct
{
    vec3 position;
    vec3 normal;
    vec2 texCoord;
} VertexData;

out vec4 FragmentPosition;

//model transformation
uniform mat4 Model_matrix;

//camera
uniform mat4 View_matrix;
uniform mat4 Projection_matrix;
out vec3 PointToCamDir;

//PointLights
struct PointLightStruct
{
    vec3 lightPos;
    vec3 lightColor;
    int attenuationType;
    float intensity;
};
uniform PointLightStruct PointLights[MAXPOINTLIGHTS];
uniform int PointLightsLength;
out vec3 PointToPointlightDir[MAXPOINTLIGHTS];


//SpotLights
struct SpotLightStruct
{
    vec3 lightPos;
    vec3 lightColor;
    int attenuationType;
    float intensity;
    vec3 direction;
    float cutOff;
    float outerCutOff;
};
uniform SpotLightStruct SpotLights[MAXSPOTLIGHTS];
uniform int SpotLightsLength;

//DirectionalLights
out vec3 PointToSpotlightDir[MAXSPOTLIGHTS];
//DirectionalLight
struct DirectionalLight {

    vec3 direction;
    vec3 lightColor;
    float intensity;
};
uniform DirectionalLight DirectionalLights[MAXDIRECTIONALLIGHTS];

void main(){
    //transform vertices
    gl_Position = Projection_matrix * View_matrix * Model_matrix * vec4(Vertex_Position, 1.0f);

    VertexData.position = (View_matrix * Model_matrix * vec4(Vertex_Position, 1.0f)).xyz;

    //camera normals
    vec4 n = vec4(Normal,0);
    mat4 normalMat = transpose(inverse(View_matrix * Model_matrix));
    VertexData.normal = (normalMat * n).xyz;

    //view direction
    PointToCamDir = -(View_matrix * Model_matrix * vec4(Vertex_Position, 1.0f)).xyz;

    //texture coordinates
    VertexData.texCoord = TexCoord;

    vec4 vertexpos = View_matrix * Model_matrix * vec4(Vertex_Position, 1.0f);
    //point light direction
    for (int i = 0 ; i < MAXPOINTLIGHTS ; i++)
    {
        PointToPointlightDir[i] = (PointLights[i].lightPos - vertexpos.xyz);
    }

    for (int j = 0 ; j < MAXSPOTLIGHTS ; j++)
    {
        PointToSpotlightDir[j] = (SpotLights[j].lightPos - vertexpos.xyz);
    }
    //DirectionalLights[0].direction = (DirectionalLights[0].direction);
}
