#version 420 core

#define MAXPOINTLIGHTS 26
#define MAXSPOTLIGHTS 1
#define MAXDIRECTIONALLIGHTS 1

out vec4 color;

in vec2 vertexTexCoord;

layout (binding = 0) uniform sampler2D gPosition;
layout (binding = 1) uniform sampler2D gNormal;
layout (binding = 2) uniform sampler2D gColorSpec;
layout (binding = 3) uniform sampler2D gEmission;

uniform vec3 AmbientColor;
struct PointLightStruct
{
    vec3 lightPos;
    vec3 lightColor;
    int attenuationType;
    float intensity;
};
uniform PointLightStruct PointLights[MAXPOINTLIGHTS];
uniform int PointLightsLength;

//camera
uniform vec3 CameraPosition;


void toSRGB (inout vec3 linearCol){
    linearCol = pow(linearCol, vec3 (2.2));
}
void toLinear (inout vec3 srgbCol){
    srgbCol = pow(srgbCol, vec3(1.0/2.2));
}

float getAttenuation(int inAttenuationType,vec3 fragPosition,vec3 lightPos){
    float attenuation = 1;
    float distance = length(fragPosition - lightPos);
    int attenuationType = int(inAttenuationType);
    if (attenuationType == 1) attenuation *= distance;
    if (attenuationType == 2) attenuation *= distance * distance;
    if (attenuationType == 3) attenuation *= distance * distance * distance;
    return attenuation;
}

//vec3 calcLightDiff(){
//
//
//    vec3 pointToPointlightDir = normalize(pointTolightDir);
//    float cosa = max(0.0, dot(vertexNormal, pointToPointlightDir));
//    vec3 diffuseTerm = matDiffuse * (lightColor * intensity);
//    float attenuation = getAttenuation(attenuationType, VertexData.position,lightPos);
//    return diffuseTerm * cosa / attenuation;
//}



void main(){
    vec3 FragPos = texture(gPosition, vertexTexCoord).rgb;
    vec3 FragNormal = texture(gNormal, vertexTexCoord).rgb;
    vec3 FragDiffuse = texture(gColorSpec, vertexTexCoord).rgb;
    float FragSpecular = texture(gColorSpec, vertexTexCoord).a;
    vec3 FragEmission = texture(gEmission, vertexTexCoord).rgb;

    toLinear(FragDiffuse);
    toLinear(FragEmission);

    vec3 outDiffuse = vec3(0.0);
    vec3 outSpecular = vec3(0.0);
    vec3 outEmission = FragEmission;
    vec3 outAmbient = AmbientColor * FragDiffuse;


    vec3 fragToViewDir = normalize(CameraPosition - FragPos);

    for (int i = 0 ; i < PointLightsLength ; i++){
        vec3 fragToLightDir = normalize(PointLights[i].lightPos - FragPos);
        float attenuation = getAttenuation(PointLights[i].attenuationType, FragPos, PointLights[i].lightPos);
        vec3 diffuseTerm = max(dot(FragNormal, fragToLightDir), 0.0) * FragDiffuse * PointLights[i].lightColor * PointLights[i].intensity / attenuation;

//
//
//        outDiffuse += diffuseTerm();
//        specular += calcLightSpec(PointLights[i].lightPos,PointToPointlightDir[i], PointLights[i].lightColor, PointLights[i].intensity, PointLights[i].attenuationType, vertexNormal, matSpecular);

        outDiffuse += diffuseTerm;
    }


    vec3 result = outDiffuse + outEmission + outAmbient;
    toSRGB(result);

    color = vec4(result,1.0);
}