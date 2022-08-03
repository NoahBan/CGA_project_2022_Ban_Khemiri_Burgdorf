#version 330 core

#define MAXSPOTLIGHTS 5
#define MAXPOINTLIGHTS 5
#define MAXDIRECTIONALLIGHTS 5
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
    float opacityMultiplier;
    int flatOpacity;
    float opacity;
    int movingMat;
    float movingU;
    float movingV;
    vec3 scalingColor;
    float colorScaling;
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
uniform PointLightStruct PointLights[MAXPOINTLIGHTS];
uniform int PointLightsLength;
in vec3 PointToPointlightDir[MAXPOINTLIGHTS];

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
uniform SpotLightStruct SpotLights[MAXSPOTLIGHTS];
uniform int SpotLightsLength;
in vec3 PointToSpotlightDir[MAXSPOTLIGHTS];

//DirectionalLight
struct DirectionalLight {

    vec3 direction;
    vec3 lightColor;
    float intensity;
};
uniform DirectionalLight DirectionalLights[MAXDIRECTIONALLIGHTS];
in vec3 varyDirectionalLight;

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
vec3 calcLightDiff(vec3 lightPos,vec3 pointTolightDir,vec3 lightColor, float intensity, int attenuationType, vec3 vertexNormal, vec3 matDiffuse){
    vec3 pointToPointlightDir = normalize(pointTolightDir);
    float cosa = max(0.0, dot(vertexNormal, pointToPointlightDir));
    vec3 diffuseTerm = matDiffuse * (lightColor * intensity);
    float attenuation = getAttenuation(attenuationType, VertexData.position,lightPos);
    return diffuseTerm * cosa / attenuation;
}
vec3 calcLightSpec(vec3 lightPos, vec3 pointTolightDir, vec3 lightColor, float intensity, int attenuationType,vec3 vertexNormal, vec3 matSpecular){
    vec3 V = normalize(PointToCamDir);
    vec3 R = normalize(reflect(-pointTolightDir, vertexNormal));

    float cosBeta = max(0.0,dot(R,V));
    float cosBetak = pow(cosBeta,Material.shininess);
    vec3 specularTerm = matSpecular * lightColor;
    float attenuation = getAttenuation(attenuationType, VertexData.position, lightPos);
    return specularTerm * cosBetak * intensity / attenuation;
}

//SPOT LIGHT CALCULATION
vec3 calcSpotLightDiff(vec3 lightPos,vec3 pointTolightDir,vec3 lightColor, float intensity, int attenuationType, vec3 spotLightDir, float cutOff, float outerCutOff, vec3 vertexNormal, vec3 matDiffuse){
    float theta = dot(normalize(pointTolightDir), normalize(spotLightDir));
    float epsilon = cutOff - outerCutOff;
    float softBorder = clamp((theta - outerCutOff) / epsilon, 0.0, 1.0);

    if (theta > outerCutOff){
        vec3 diffuse = calcLightDiff(lightPos, pointTolightDir, lightColor, intensity, attenuationType, vertexNormal, matDiffuse);
        return diffuse * softBorder;
    } else return vec3(0);
}

vec3 calcSpotLightSpec(vec3 lightPos,vec3 pointTolightDir,vec3 lightColor, float intensity, int attenuationType, vec3 spotLightDir, float cutOff, float outerCutOff, vec3 vertexNormal, vec3 matSpecular){
    float theta = dot(normalize(pointTolightDir), normalize(spotLightDir));
    float epsilon = cutOff - outerCutOff;
    float softBorder = clamp((theta - outerCutOff) / epsilon, 0.0, 1.0);

    if (theta > outerCutOff){
        vec3 specular = calcLightSpec(lightPos, pointTolightDir, lightColor, intensity, attenuationType, vertexNormal, matSpecular);
        return specular * softBorder;
    } else return vec3(0);
}

//DIRECTIONAL LIGHT CALCULATION
vec3 calcDirecLightDiff(vec3 direction,vec3 lightColor, float intensity, vec3 vertexNormal, vec3 matDiffuse){
    vec3 lightDirection = -normalize(direction);
    vec3 normal = normalize (vertexNormal);
    float cosa = max(0.0, dot(normal, lightDirection));
    vec3 diffuseTerm = matDiffuse * (lightColor * intensity);
    return diffuseTerm * cosa;
}
vec3 calcDirecLightSpec(vec3 direction,vec3 lightColor, float intensity, vec3 vertexNormal, vec3 matSpecular){
    vec3 lightDirection = normalize(-direction);
    vec3 normal = normalize (vertexNormal);
    vec3 V = normalize(PointToCamDir);
    vec3 R = normalize(reflect(-lightDirection, normal));
    float cosBeta = max(0.0,dot(R,V));
    float cosBetak = pow(cosBeta,Material.shininess);
    vec3 specularTerm = matSpecular * intensity;
    return specularTerm * cosBetak;
}
void main(){

    //get tex color on uv coordinate
    vec2 texCoord = VertexData.texCoord * Material.tcMultiplier;
    if (Material.movingMat == 1){
        texCoord[0] += Material.movingU;
        texCoord[1] += Material.movingV;
    };
    vec3 matEmissive = texture(Material.texEmit, texCoord).xyz;
    vec3 matDiffuse = texture(Material.texDiff,texCoord).xyz;
    vec3 matSpecular = texture(Material.texSpec,texCoord).xyz;
    toLinear(matDiffuse);
    toLinear(matEmissive);
    toLinear(matSpecular);

    vec3 vertexNormal = normalize(VertexData.normal);

    //variables for color
    vec3 diffuse = vec3(0);
    vec3 specular = vec3(0);
    vec3 emission = matEmissive * Material.emitMultiplier;
    vec3 ambient = AmbientColor * matDiffuse;
    vec4 colorAlpha;
    float alpha = texture(Material.texDiff,texCoord).a;
    float alphaMultiplier = Material.opacityMultiplier;

    //add point lights
//    calcLightDiff(vec3 lightPos,vec3 pointTolightDir,vec3 lightColor, float intensity, int attenuationType, vec3 vertexNormal, vec3 matDiffuse)
    for (int i = 0 ; i < PointLightsLength ; i++){
        diffuse += calcLightDiff(PointLights[i].lightPos,PointToPointlightDir[i],PointLights[i].lightColor, PointLights[i].intensity, PointLights[i].attenuationType, vertexNormal, matDiffuse);
        specular += calcLightSpec(PointLights[i].lightPos,PointToPointlightDir[i], PointLights[i].lightColor, PointLights[i].intensity, PointLights[i].attenuationType, vertexNormal, matSpecular);
    }

    for (int j = 0 ; j < SpotLightsLength ; j++){
        //diffuse += calcSpotLightDiff(j, vertexNormal, matDiffuse);
        diffuse += calcSpotLightDiff(SpotLights[j].lightPos,PointToSpotlightDir[j],SpotLights[j].lightColor, SpotLights[j].intensity, SpotLights[j].attenuationType, SpotLights[j].direction,SpotLights[j].cutOff,SpotLights[j].outerCutOff, vertexNormal, matDiffuse);
        specular += calcSpotLightSpec(SpotLights[j].lightPos,PointToSpotlightDir[j],SpotLights[j].lightColor, SpotLights[j].intensity, SpotLights[j].attenuationType, SpotLights[j].direction,SpotLights[j].cutOff,SpotLights[j].outerCutOff, vertexNormal, matSpecular);
    }
    diffuse += calcDirecLightDiff(varyDirectionalLight, DirectionalLights[0].lightColor,DirectionalLights[0].intensity,vertexNormal, matDiffuse);
    specular += calcDirecLightSpec(varyDirectionalLight, DirectionalLights[0].lightColor,DirectionalLights[0].intensity,vertexNormal, matSpecular);
    //add up material inputs
    vec3 result = emission + diffuse + specular + ambient;
    toSRGB(result);

    vec3 newResult =   vec3((result.x * Material.colorScaling) + Material.scalingColor.x * (1-Material.colorScaling),
                            (result.y * Material.colorScaling) + Material.scalingColor.y * (1-Material.colorScaling),
                            (result.z * Material.colorScaling) + Material.scalingColor.z * (1-Material.colorScaling));

    if(Material.flatOpacity == 1){
        colorAlpha = vec4(newResult,Material.opacity);
    }else{
        colorAlpha = vec4(newResult,alpha * alphaMultiplier);
    }

    color = colorAlpha;
//  color = vec4(PointLights[0].lightColor, 1.0);

}