#version 330 core

uniform sampler2D texEmit;
uniform sampler2D texDiff;
uniform sampler2D texSpec;
uniform vec3 texAmbi;

//light
uniform vec3 lightPos;
uniform vec3 lightColor;
uniform vec3 lightColAmbient;

//material
uniform vec2 tcMultiplier;
uniform float shininess;


//input from vertex shader
in struct VertexData
{
     vec3 position;
     vec3 normal;
     vec2 texCoord;
} vertexData;

in struct LightData
{

    vec3 toLight;
} lightData;

//fragment shader output
out vec4 color;


void main(){

    //color = texture(texEmit, vertexData.texCoord * tcMultiplier);


        vec3 matEmissive = texture(texEmit, vertexData.texCoord * tcMultiplier).xyz;
        vec3 matDiffuse = texture(texDiff,vertexData.texCoord * tcMultiplier).xyz;

        vec3 N = normalize(vertexData.normal);
        vec3 L = normalize(lightData.toLight);

    //Diffuse
        float cos_alpha = max(0.0, dot(N,L));
        vec3 DiffuseTerm = matDiffuse * lightColor;
        color = vec4(DiffuseTerm * cos_alpha, 1.0);

    //Emissive und Ambient
       // vec3 AmbientEmissiveTerm = matEmissive + matAmbient * lightColorAmbient;
       // color += vec4(AmbientEmissiveTerm, 0.0);

}