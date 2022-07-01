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
//PointLightArrays
uniform PointLightStruct PointLightArray[10];
uniform int PointLightArrayLength;
out vec3 PointLightDirArray[10];
out float PointLightDistArray[10];

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
//SpotLightArrays
uniform SpotLightStruct SpotLightArrayTest;
out vec3 SpotLightDirArrayTest;
out float SpotLightDistArrayTest;
out vec3 SpotLightTargetDirectionTest;

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

    ViewDirection = (View_matrix * Model_matrix * vec4(-Position, 1.0f)).xyz;

    //point light direction
    for (int i = 0 ; i < PointLightArrayLength ; i++)
    {
        vec4 pointLightPosition = View_matrix * vec4(PointLightArray[i].lightPos, 1.0);
        PointLightDirArray[i] = (pointLightPosition - FragmentPosition).xyz ;

        float pointLightDistance = length(PointLightArray[i].lightPos - (Model_matrix * vec4(Position, 1.0f)).xyz);
        PointLightDistArray[i] = pointLightDistance;
    }
    vec4 spotLightPosition = View_matrix * vec4(SpotLightArrayTest.lightPos, 1.0);
    SpotLightDirArrayTest = (spotLightPosition - FragmentPosition).xyz ;

    float spotLightDistance = length(SpotLightArrayTest.lightPos - (Model_matrix * vec4(Position, 1.0f)).xyz);
    SpotLightDistArrayTest = spotLightDistance;

    SpotLightTargetDirectionTest = (View_matrix * vec4(SpotLightArrayTest.direction, 0)).xyz;
}
