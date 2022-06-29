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
uniform PointLightStruct PointLightArray[10];
uniform int PointLightArrayLength;
out vec3 PointLightDirArray[10];
out float PointLightDistArray[10];


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
        vec4 lightPosition = View_matrix * vec4(PointLightArray[i].lightPos, 1.0);
        PointLightDirArray[i] = (lightPosition - FragmentPosition).xyz ;

        float distance = length(PointLightArray[i].lightPos - (Model_matrix * vec4(Position, 1.0f)).xyz);
        PointLightDistArray[i] = distance;
    }
}
