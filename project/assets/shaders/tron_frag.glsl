#version 330 core

//input from vertex shader
in struct VertexData
{
    vec3 position;
    vec3 normal;
    vec2 texCoord;
} vertexData;

//model transformation
uniform mat4 model_matrix;

//camera
uniform mat4 view_matrix;
uniform mat4 projection_matrix;

//material
uniform sampler2D texEmit;
uniform sampler2D texDiff;
uniform sampler2D texSpec;
uniform vec2 tcMultiplier;
uniform float shininess;

//ambient color/light
uniform vec3 ambientColor;
//PointLights
struct pointLight
{
    vec3 lightPos;
    vec3 lightColor;
};
uniform pointLight pointLightArray[10];
uniform int pointLightArrayLength;
in vec3 pointLightDirArray[10];

//pixel color out
out vec4 color;


void main(){

    //get tex color on uv coordinate
    vec3 matEmissive = texture(texEmit, vertexData.texCoord * tcMultiplier).xyz;
    vec3 matDiffuse = texture(texDiff,vertexData.texCoord * tcMultiplier).xyz;

    vec3 vertexNormal = normalize(vertexData.normal);

    //variable for diffuse color
    vec3 diffuse = vec3(0);

    //add point lights
    for (int i = 0 ; i < pointLightArrayLength ; i++){
        vec3 lightDirection = normalize(pointLightDirArray[i]);

        float cosAlpha = max(dot(vertexNormal, lightDirection), 0.0);
        diffuse += (matDiffuse * pointLightArray[i].lightColor) * cosAlpha;
    }

    //add up material inputs
    vec3 result = matEmissive + (diffuse) + (ambientColor * matDiffuse);

    color = vec4(result, 1.0);

}
