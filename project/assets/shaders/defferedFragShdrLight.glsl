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

struct PointLightStruct
{
    vec3 lightPos;
    vec3 lightColor;
    int attenuationType;
    float intensity;
};
uniform PointLightStruct PointLights[MAXPOINTLIGHTS];
uniform int PointLightsLength;

struct DirectionalLight {

    vec3 direction;
    vec3 lightColor;
    float intensity;
};
uniform DirectionalLight DirectionalLights[MAXDIRECTIONALLIGHTS];

void main(){
    vec3 FragPos = texture(gPosition, vertexTexCoord).rgb;
    vec3 Normal = texture(gNormal, vertexTexCoord).rgb;
    vec3 Diffuse = texture(gColorSpec, vertexTexCoord).rgb;
    float Specular = texture(gColorSpec, vertexTexCoord).a;
    vec3 Emission = texture(gEmission, vertexTexCoord).rgb;

    color = vec4(Diffuse,1.0);
}