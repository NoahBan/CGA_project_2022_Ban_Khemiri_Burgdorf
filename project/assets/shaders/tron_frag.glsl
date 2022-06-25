#version 330 core

struct Light
{
    vec3 lightPos;
    vec3 lightColor;
};
uniform Light lightsArray[10];
uniform int lightsArrayLength;

uniform vec3 texAmbi;

//light
uniform vec3 lightPos;
uniform vec3 lightColor;

uniform vec3 ambientColor;

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

//TESTING
in vec3 lightDir ;

//input from vertex shader
in struct VertexData
{
     vec3 position;
     vec3 normal;
     vec2 texCoord;
} vertexData;



//fragment shader output
out vec4 color;


void main(){

    //color = texture(texEmit, vertexData.texCoord * tcMultiplier);

        vec3 matEmissive = texture(texEmit, vertexData.texCoord * tcMultiplier).xyz;
        vec3 matDiffuse = texture(texDiff,vertexData.texCoord * tcMultiplier).xyz;

        vec3 vertexNormal = normalize(vertexData.normal);
        vec3 lightDirection = normalize(lightDir);

        vec4 fragmentPos = (view_matrix * model_matrix * vec4(vertexData.position, 1.0f));

        vec3 lightDir = (vec4(lightPos,1.0) - fragmentPos).xyz;

    //Diffuse
        float cosAlpha = max(dot(vertexNormal,lightDirection), 0.0);
        vec3 diffuse = (matDiffuse * lightColor) * cosAlpha;


    //All together
        vec3 result = matEmissive + (diffuse * matDiffuse) + (ambientColor * matDiffuse);

        color = vec4(result, 1.0);

    //Emissive und Ambient
       // vec3 AmbientEmissiveTerm = matEmissive + matAmbient * lightColorAmbient;
       // color += vec4(AmbientEmissiveTerm, 0.0);

}




//void main(){
//
//    //color = texture(texEmit, vertexData.texCoord * tcMultiplier);
//
//    vec3 matEmissive = texture(texEmit, vertexData.texCoord * tcMultiplier).xyz;
//    vec3 matDiffuse = texture(texDiff,vertexData.texCoord * tcMultiplier).xyz;
//
//    vec4 fragmentPos = (view_matrix * model_matrix * vec4(vertexData.position, 1.0f));
//
//    vec3 lightDir = (vec4(lightsArray[0].lightPos,1.0) - fragmentPos).xyz;
//
//
//    vec3 vertexNormal = normalize(vertexData.normal);
//    vec3 lightDirection = normalize(lightDir);
//
//    //Diffuse
//    float cosAlpha = max(dot(vertexNormal,lightDirection), 0.0);
//    vec3 diffuse = (matDiffuse * lightsArray[0].lightColor) * cosAlpha;
//
//
//    //All together
//    vec3 result = matEmissive + (diffuse * matDiffuse) + (ambientColor * matDiffuse);
//
//    color = vec4(result, 1.0);
//
//
//    //Emissive und Ambient
//    // vec3 AmbientEmissiveTerm = matEmissive + matAmbient * lightColorAmbient;
//    // color += vec4(AmbientEmissiveTerm, 0.0);
//
//}

//void main(){
//
//    vec3 matEmissive = texture(texEmit, vertexData.texCoord * tcMultiplier).xyz;
//    vec3 matDiffuse = texture(texDiff,vertexData.texCoord * tcMultiplier).xyz;
//
//    vec4 fragmentPos = (view_matrix * model_matrix * vec4(vertexData.position, 1.0f));
//
//    vec3 diffuse = vec3(0);
//    for (int i = 0 ; i < lightsArrayLength ; i++)
//    {
//        vec3 lightDir = (vec4(lightsArray[i].lightPos,1.0) - fragmentPos).xyz;
//
//        vec3 vertexNormal = normalize(vertexData.normal);
//
//        vec3 lightDirection = normalize(lightDir);
//
//        float cosAlpha = max(dot(vertexNormal,lightDirection), 0.0);
//
//        diffuse += (matDiffuse * lightsArray[i].lightColor) * cosAlpha;
//    }
//
//
//    //All together
//    vec3 result = matEmissive + (diffuse * matDiffuse) + (ambientColor * matDiffuse);
//
//    color = vec4(result, 1.0);
//
//    //Emissive und Ambient
//    // vec3 AmbientEmissiveTerm = matEmissive + matAmbient * lightColorAmbient;
//    // color += vec4(AmbientEmissiveTerm, 0.0);
//
//}