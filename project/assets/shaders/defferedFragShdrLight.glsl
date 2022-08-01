#version 330 core

out vec4 color;

layout (location = 0) out vec3 gPosition;
layout (location = 1) out vec3 gNormal;
layout (location = 2) out vec4 gColorSpec;
layout (location = 3) out vec3 gEmission;

uniform sampler2D gPosition;
uniform sampler2D gNormal;
uniform sampler2D gColorSpec;
uniform sampler2D gEmission;

in vec2 vertexTexCoord;

void main(){

    vec3 FragPos = texture(gPosition, TexCoords).rgb;
    vec3 Normal = texture(gNormal, TexCoords).rgb;
    vec3 Diffuse = texture(gColorSpec, TexCoords).rgb;
    float Specular = texture(gColorSpec, TexCoords).a;
    vec3 Emission = texture(gEmission, TexCoords).rgb;

    gPosition = vec4(Diffuse,1.0);

}