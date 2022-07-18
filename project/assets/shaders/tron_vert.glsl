#version 330 core

//Vertex Attributes
layout(location = 0) in vec3 Position;
layout(location = 1) in vec2 TexCoord;
layout(location = 2) in vec3 Normal;

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
out vec3 ViewDirection;

//PointLights
struct PointLightStruct
{
    vec3 lightPos;
    vec3 lightColor;
    int attenuationType;
    float intensity;
};
uniform PointLightStruct PointLightArray[5];
uniform int PointLightArrayLength;
out vec3 PointLightDirArray[5];
out float PointLightDistArray[5];

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
uniform SpotLightStruct SpotLightArray[5];
uniform int SpotLightArrayLength;
out vec3 SpotLightDirArray[5];
out float SpotLightDistArray[5];
out vec3 SpotLightTargetDirection[5];



void main(){
    //transform vertices
    vec4 pos = Projection_matrix * View_matrix * Model_matrix * vec4(Position, 1.0f);
    gl_Position = pos;
    VertexData.position = pos.xyz;

    //camera normals
    vec4 norm = transpose(inverse(View_matrix * Model_matrix)) * vec4(Normal, 0.0f);
    VertexData.normal = norm.xyz;

    //texture coordinates
    VertexData.texCoord = TexCoord;

    FragmentPosition = (View_matrix * Model_matrix * vec4(Position, 1.0f));

    ViewDirection = (View_matrix * Model_matrix * vec4(-Position, 1.0f)).xyz; //toCamera

    //point light direction
    for (int i = 0 ; i < PointLightArrayLength ; i++)
    {
        vec4 pointLightPosition = View_matrix * vec4(PointLightArray[i].lightPos, 1.0);
        PointLightDirArray[i] = (vec4(PointLightArray[i].lightPos, 1.0) - FragmentPosition).xyz ;

        float pointLightDistance = length(pointLightPosition.xyz - (Model_matrix * vec4(Position, 0.0f)).xyz);
        PointLightDistArray[i] = pointLightDistance;
    }

    for (int j = 0 ; j < SpotLightArrayLength ; j++)
    {
        vec4 spotLightPosition = View_matrix * vec4(SpotLightArray[j].lightPos, 1.0);
        SpotLightDirArray[j] = (spotLightPosition - FragmentPosition).xyz;

        float spotLightDistance = length(SpotLightArray[j].lightPos - (Model_matrix * vec4(Position, 1.0f)).xyz);
        SpotLightDistArray[j] = spotLightDistance;

        SpotLightTargetDirection[j] = (View_matrix * vec4(SpotLightArray[j].direction, 0)).xyz;
    }
}
