#version 440

out vec4 color;

uniform sampler2D gPosition;
uniform sampler2D gNormal;
uniform sampler2D gColorSpec;
uniform sampler2D gEmission;

in vec2 vertexTexCoord;

void main(){

    vec3 FragPos = texture(gPosition, vertexTexCoord).rgb;
    vec3 Normal = texture(gNormal, vertexTexCoord).rgb;
    vec3 Diffuse = texture(gColorSpec, vertexTexCoord).rgb;
    float Specular = texture(gColorSpec, vertexTexCoord).a;
    vec3 Emission = texture(gEmission, vertexTexCoord).rgb;

    color = vec4(FragPos, 1.0);

}