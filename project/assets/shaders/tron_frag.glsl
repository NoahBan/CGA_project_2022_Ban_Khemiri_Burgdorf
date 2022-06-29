#version 330 core

//input from vertex shader
in struct VertexData
{
    vec3 position;
    vec3 normal;
    vec2 texCoord;
} vertexData;

in vec4 fragmentPosition;

//model transformation
uniform mat4 model_matrix;

//camera
uniform mat4 view_matrix;
uniform mat4 projection_matrix;
in vec3 ViewDirection;

//material
struct Material{
    sampler2D texEmit;
    sampler2D texDiff;
    sampler2D texSpec;
    vec2 tcMultiplier;
    float shininess;
    vec3 emitMultiplier;
};
uniform Material material;

//ambient color/light
uniform vec3 ambientColor;
//PointLightStruct
struct PointLight
{
    vec3 lightPos;
    vec3 lightColor;
    int attenuationType;
    float intensity;
};

//PointLightArrays
uniform PointLight pointLightArray[10];
uniform int pointLightArrayLength;
in vec3 pointLightDirArray[10];
in float pointLightDistArray[10];

//SpotLightStruct
struct spotLight
{
    vec3 lightPos;
    vec3 lightColor;
    float intensity;
    int attenuationType;
    vec3 direction;
    float cutOff;
};
//SpotLightArrays
uniform spotLight spotLightArray[10];
uniform int spotLightArrayLength;
in vec3 spotLightDirArray[10];
in vec3 spotLightTargetDirArray[10] ;
in float spotLightDistArray[10];

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

struct CalcLightData{
    vec3 diffuse;
    vec3 specular;
};

//POINT LIGHT CALCULATION
CalcLightData calcPointLight(int index, vec3 viewDirection, vec3 vertexNormal, vec3 matDiffuse, vec3 matSpecular){
    vec3 diffuse = vec3(0.0);
    vec3 specular = vec3(0);
    CalcLightData calcLightData;

    vec3 lightDirection = normalize(pointLightDirArray[index]);
//    vec3 halfwayDirection = normalize(lightDirection + viewDirection);

    float lightDistance = pointLightDistArray[index];
    float cosAlpha = max(dot(vertexNormal, lightDirection), 0.0);

    float attenuation = getAttenuation(pointLightArray[index].attenuationType, lightDistance);
    int attenuationType = pointLightArray[index].attenuationType;
    calcLightData.diffuse = matDiffuse * pointLightArray[index].lightColor * pointLightArray[index].intensity * cosAlpha / attenuation;

    vec3 R = normalize(reflect(-lightDirection,vertexNormal));
    float cosBeta = max(0.0, dot (R,viewDirection));
    float cosBetak = pow(cosBeta, material.shininess);

    vec3 specularTerm = matSpecular * pointLightArray[index].lightColor / attenuation;

//    float spec = pow(max(dot(vertexNormal, halfwayDirection), 0.0), material.shininess);
    calcLightData.specular = specularTerm * cosBetak;

    return calcLightData;
}

//CalcLightData calcSpotLight(int index, vec3 viewDirection, vec3 vertexNormal, vec3 matDiffuse, vec3 matSpecular){
//
//    vec3 diffuse = vec3(0.0);
//    vec3 specular = vec3(0.0);
//    CalcLightData calcLightData;
//
//    vec3 lightDirection = normalize(spotLightDirArray[index]);
////    vec3 halfwayDirection = normalize(lightDirection + viewDirection);
//
//    float lightDistance = (spotLightDistArray[index]);
//
//    vec3 theta = (spotLightTargetDirArray[index]);
//
//    // float theta = dot(lightDirection, normalize(-spotLightTargetDirArray[index]));
//
//
//    return calcLightData;
//}

void main(){

    //get tex color on uv coordinate
    vec3 matEmissive = texture(material.texEmit, vertexData.texCoord * material.tcMultiplier).xyz;
    vec3 matDiffuse = texture(material.texDiff,vertexData.texCoord * material.tcMultiplier).xyz;
    vec3 matSpecular = texture(material.texSpec,vertexData.texCoord * material.tcMultiplier).xyz;
    toLinear(matDiffuse);
    toLinear(matEmissive);
    toLinear(matSpecular);

    vec3 vertexNormal = normalize(vertexData.normal);
    vec3 viewDirection = normalize(ViewDirection);

    //variable for diffuse color
    vec3 diffuse = vec3(0);
    vec3 specular = vec3(0);

    //add point lights
    for (int i = 0 ; i < pointLightArrayLength ; i++){
        CalcLightData calcLightData = calcPointLight(i, viewDirection, vertexNormal, matDiffuse, matSpecular);
        diffuse += calcLightData.diffuse;
        specular += calcLightData.specular;
    }
//    for (int j = 0 ; j < 0 ; j++){
//        CalcLightData calcLightData = calcSpotLight(j, viewDirection, vertexNormal, matDiffuse, matSpecular);
//        diffuse += calcLightData.diffuse;
//        specular += calcLightData.specular;
//    }

    //add up material inputs
    vec3 result = matEmissive*material.emitMultiplier + diffuse + specular + (ambientColor * matDiffuse);
//    vec3 result = matEmissive + diffuse + specular + (ambientColor * matDiffuse);
    toSRGB(result);

    color = vec4(result, 1.0);
}