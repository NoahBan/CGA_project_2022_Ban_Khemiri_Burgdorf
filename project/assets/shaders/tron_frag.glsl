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
    int attenuationType;
    float intensity;
};
uniform pointLight pointLightArray[10];
uniform int pointLightArrayLength;
in vec3 pointLightDirArray[10];
in float pointLightDistArray[10];

//pixel color out
out vec4 color;

float getAttenuation(in int inAttenuationType,in float inDistance){
    float attenuation = 1;
    int attenuationType = int(inAttenuationType);
    if (attenuationType == 1) attenuation *= inDistance;
    if (attenuationType == 2) attenuation *= inDistance * inDistance;
    if (attenuationType == 3) attenuation *= inDistance * inDistance * inDistance;
    return attenuation;
}

void toSRGB (inout vec3 linearCol){
    linearCol = pow(linearCol, vec3 (1.0/2.2));
}

void toLinear (inout vec3 srgbCol){
    srgbCol = pow(srgbCol, vec3(2.2));
}

void main(){

    //get tex color on uv coordinate
    vec3 matEmissive = texture(texEmit, vertexData.texCoord * tcMultiplier).xyz;
    vec3 matDiffuse = texture(texDiff,vertexData.texCoord * tcMultiplier).xyz;
    toLinear(matDiffuse);
    toLinear(matEmissive);

    vec3 vertexNormal = normalize(vertexData.normal);

    //variable for diffuse color
    vec3 diffuse = vec3(0);

    //add point lights
    for (int i = 0 ; i < pointLightArrayLength ; i++){
        vec3 lightDirection = normalize(pointLightDirArray[i]);
        float lightDistance = pointLightDistArray[i];
        float cosAlpha = max(dot(vertexNormal, lightDirection), 0.0);

        float attenuation = getAttenuation(pointLightArray[i].attenuationType, lightDistance);
        int attenuationType = pointLightArray[i].attenuationType;

        diffuse += matDiffuse * pointLightArray[i].lightColor * pointLightArray[i].intensity * cosAlpha / attenuation;
    }

    //add up material inputs
    vec3 result = matEmissive + diffuse + (ambientColor * matDiffuse);
    toSRGB(result);

    color = vec4(result, 1.0);
}
