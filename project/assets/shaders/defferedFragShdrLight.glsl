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

struct DirectionalLight {
    vec3 direction;
    vec3 lightColor;
    float intensity;
};
uniform DirectionalLight DirectionalLights[MAXDIRECTIONALLIGHTS];

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

vec3 calcLightDiff(vec3 lightPos, int attenuationType, vec3 lightColor, float intensity, vec3 FragPos, vec3 FragNormal, vec3 FragDiffuse){
    vec3 outDiff = vec3(0.0);

    vec3 fragToLightDir = normalize(lightPos - FragPos);
    float attenuation = getAttenuation(attenuationType, FragPos, lightPos);
    outDiff = max(dot(FragNormal, fragToLightDir), 0.0) * FragDiffuse * lightColor * intensity / attenuation;

    return outDiff;
}

vec3 calcLightSpec(vec3 lightPos, int attenuationType, vec3 lightColor, float intensity, vec3 fragToCamDir, vec3 FragPos, vec3 FragNormal, float FragSpecular){
    vec3 outSpec = vec3(0.0);
//
//    vec3 fragToLightDir = (lightPos - FragPos);
//
//    vec3 V = fragToCamDir;
//    vec3 R = normalize(reflect(-fragToLightDir, FragNormal));
//
//    float cosBeta = max(0.0,dot(R,V));
//    float cosBetak = pow(cosBeta,60f);
//
//    vec3 specularTerm = FragSpecular * lightColor;
//    float attenuation = getAttenuation(attenuationType, FragPos, lightPos);
//
//    outSpec = specularTerm * cosBetak * intensity / attenuation;


    vec3 fragToLightDir = normalize(lightPos - FragPos);
    float attenuation = getAttenuation(attenuationType, FragPos, lightPos);

    vec3 halfwayDir = normalize(fragToLightDir + fragToCamDir);
    float specAngle = pow(max(dot(FragNormal, halfwayDir), 0.0), 16.0);
    outSpec = lightColor * specAngle * FragSpecular * intensity / attenuation;

    return outSpec;
}

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


    vec3 fragToCamDir = normalize(CameraPosition - FragPos);

    for (int i = 0 ; i < 1 ; i++){
        vec3 lightDirection = normalize(-DirectionalLights[i].direction);
        vec3 diffuseTerm = max(dot(FragNormal, lightDirection), 0.0) * FragDiffuse * DirectionalLights[i].lightColor * DirectionalLights[i].intensity;
        outDiffuse += diffuseTerm;
    }

    for (int i = 0 ; i < PointLightsLength ; i++){
        outDiffuse += calcLightDiff(PointLights[i].lightPos, PointLights[i].attenuationType, PointLights[i].lightColor, PointLights[i].intensity, FragPos, FragNormal, FragDiffuse);
        outSpecular += calcLightSpec(PointLights[i].lightPos, PointLights[i].attenuationType, PointLights[i].lightColor, PointLights[i].intensity, fragToCamDir, FragPos, FragNormal, FragSpecular);
    }


    vec3 result = outDiffuse + outEmission + outAmbient + outSpecular;
    toSRGB(result);

    color = vec4(result,1.0);
}

