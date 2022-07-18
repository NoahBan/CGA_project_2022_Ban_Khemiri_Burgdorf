#version 330 core

//input from vertex shader
in struct VertexDataStruct
{
    vec3 position;
    vec3 normal;
    vec2 texCoord;
} VertexData;

in vec4 FragmentPosition;

//model transformation
uniform mat4 Model_matrix;

//camera
uniform mat4 View_matrix;
uniform mat4 Projection_matrix;
in vec3 PointToCamDir;

//material
struct MaterialStruct {
    sampler2D texEmit;
    sampler2D texDiff;
    sampler2D texSpec;
    vec2 tcMultiplier;
    float shininess;
    vec3 emitMultiplier;
};
uniform MaterialStruct Material;

//ambient color/light
uniform vec3 AmbientColor;
//PointLightStruct
struct PointLightStruct
{
    vec3 lightPos;
    vec3 lightColor;
    int attenuationType;
    float intensity;
};
uniform PointLightStruct PointLights[5];
uniform int PointLightsLength;
in vec3 PointToPointlightDir[5];

//SpotLights
struct SpotLightStruct
{
    vec3 lightPos;
    vec3 lightColor;
    int attenuationType;
    float intensity;
    vec3 direction;
    float cutOff;
    float outerCutOff;
};
uniform SpotLightStruct SpotLights[5];
uniform int SpotLightsLength;
in vec3 PointToSpotlightDir[5];

out vec4 color;

float getAttenuation(int inAttenuationType,vec3 fragPosition,vec3 lightPos){
    float attenuation = 1;
    float distance = length(fragPosition - lightPos);
    int attenuationType = int(inAttenuationType);
    if (attenuationType == 1) attenuation *= distance;
    if (attenuationType == 2) attenuation *= distance * distance;
    if (attenuationType == 3) attenuation *= distance * distance * distance;
    return attenuation;
}

void toSRGB (inout vec3 linearCol){
    linearCol = pow(linearCol, vec3 (2.2));
}
void toLinear (inout vec3 srgbCol){
    srgbCol = pow(srgbCol, vec3(1.0/2.2));
}

//POINT LIGHT CALCULATION
vec3 calcPointLightDiff(int index, vec3 vertexNormal, vec3 matDiffuse){
    vec3 pointToPointlightDir = normalize(PointToPointlightDir[index]);
    float cosa = max(0.0, dot(vertexNormal, pointToPointlightDir));
    vec3 diffuseTerm = matDiffuse * (PointLights[index].lightColor * PointLights[index].intensity);
    float attenuation = getAttenuation(PointLights[index].attenuationType, VertexData.position,PointLights[index].lightPos);
    return diffuseTerm * cosa / attenuation;
}
vec3 calcPointLightSpec(int index,vec3 vertexNormal, vec3 matSpecular){
    vec3 V = normalize(PointToCamDir);
    vec3 R = normalize(reflect(-PointToPointlightDir[index], vertexNormal));

    float cosBeta = max(0.0,dot(R,V));
    float cosBetak = pow(cosBeta,Material.shininess);
    vec3 specularTerm = matSpecular * PointLights[index].lightColor;
    float attenuation = getAttenuation(PointLights[index].attenuationType, VertexData.position, PointLights[index].lightPos);
    return specularTerm * cosBetak / attenuation;
}


//SPOT LIGHT CALCULATION
vec3 calcSpotLightDiff(int index, vec3 vertexNormal, vec3 matDiffuse){

    vec3 pointToSpotlightDir = normalize(PointToSpotlightDir[index]);

    float theta = dot(normalize(SpotLights[index].direction), pointToSpotlightDir);
    float epsilon = SpotLights[index].outerCutOff - SpotLights[index].cutOff;
    float softBorder = clamp((theta - SpotLights[index].outerCutOff) / epsilon, 1.0,0.0);
    
//    if (theta > SpotLights[index].cutOff){
        float cosa = max(0.0, dot(vertexNormal, pointToSpotlightDir));
        vec3 diffuseTerm = matDiffuse * (SpotLights[index].lightColor * SpotLights[index].intensity);
        float attenuation = getAttenuation(SpotLights[index].attenuationType, VertexData.position,SpotLights[index].lightPos);
        return diffuseTerm * softBorder / attenuation;
//    } else{
//        return vec3(0);
    }

//    float cosa = max(0.0, dot(vertexNormal, pointToSpotlightDir));
//    vec3 diffuseTerm = matDiffuse * (SpotLights[index].lightColor * SpotLights[index].intensity);
//    float attenuation = getAttenuation(SpotLights[index].attenuationType, VertexData.position,SpotLights[index].lightPos);
//    return diffuseTerm * cosa / attenuation;
//}

void main(){

    //get tex color on uv coordinate
    vec3 matEmissive = texture(Material.texEmit, VertexData.texCoord * Material.tcMultiplier).xyz;
    vec3 matDiffuse = texture(Material.texDiff,VertexData.texCoord * Material.tcMultiplier).xyz;
    vec3 matSpecular = texture(Material.texSpec,VertexData.texCoord * Material.tcMultiplier).xyz;
    toLinear(matDiffuse);
    toLinear(matEmissive);
    toLinear(matSpecular);

    vec3 vertexNormal = normalize(VertexData.normal);

    //variables for color
    vec3 diffuse = vec3(0);
    vec3 specular = vec3(0);
    vec3 emission = matEmissive * Material.emitMultiplier;
    vec3 ambient = AmbientColor * matDiffuse;

    //add point lights
    for (int i = 0 ; i < PointLightsLength ; i++){
        diffuse += calcPointLightDiff(i, vertexNormal, matDiffuse);
        specular += calcPointLightSpec(i, vertexNormal, matSpecular);
    }

    for (int j = 0 ; j < SpotLightsLength ; j++){
        diffuse += calcSpotLightDiff(j, vertexNormal, matDiffuse);
//        specular += calcSpotLightData.specular;
    }

    //add up material inputs
    vec3 result = emission + diffuse + specular + ambient;
    toSRGB(result);
    color = vec4(result, 1.0);

//    color = vec4(PointLights[0].lightColor, 1.0);

}