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
in vec3 ViewDirection;

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

//PointLightArrays
uniform PointLightStruct PointLightArray[5];
uniform int PointLightArrayLength;
in vec3 PointLightDirArray[5];
in float PointLightDistArray[5];

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
uniform SpotLightStruct SpotLightArray[5];
uniform int SpotLightArrayLength;
in vec3 SpotLightDirArray[5];
in float SpotLightDistArray[5];
in vec3 SpotLightTargetDirection[5];

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

struct CalcLightDataStruct{
    vec3 diffuse;
    vec3 specular;
};



//POINT LIGHT CALCULATION
CalcLightDataStruct calcPointLight(int index, vec3 viewDirection, vec3 vertexNormal, vec3 matDiffuse, vec3 matSpecular){
    vec3 diffuse = vec3(0.0);
    vec3 specular = vec3(0);
    CalcLightDataStruct calcLightData;

    vec3 lightDirection = normalize(PointLightDirArray[index]);
//    vec3 halfwayDirection = normalize(lightDirection + viewDirection);

    float lightDistance = PointLightDistArray[index];
    float cosAlpha = max(dot(vertexNormal, lightDirection), 0.0);

    float attenuation = getAttenuation(PointLightArray[index].attenuationType, lightDistance);
    int attenuationType = PointLightArray[index].attenuationType;

    calcLightData.diffuse = matDiffuse * PointLightArray[index].lightColor * PointLightArray[index].intensity * cosAlpha / attenuation;

    vec3 R = normalize(reflect(-lightDirection,vertexNormal));
    float cosBeta = max(0.0, dot (R,viewDirection));
    float cosBetak = pow(cosBeta, Material.shininess);

    vec3 specularTerm = matSpecular * PointLightArray[index].lightColor / attenuation;

//    float spec = pow(max(dot(vertexNormal, halfwayDirection), 0.0), material.shininess);
    calcLightData.specular = specularTerm * cosBetak;

    return calcLightData;
}



//SPOT LIGHT CALCULATION
CalcLightDataStruct calcSpotLight(int index, vec3 viewDirection, vec3 vertexNormal, vec3 matDiffuse, vec3 matSpecular, bool blinn){
    vec3 diffuse = vec3(0.0);
    vec3 specular = vec3(0);
    CalcLightDataStruct calcLightData;

    vec3 lightDirection = normalize(SpotLightDirArray[index]);

    float theta = dot(lightDirection, normalize(SpotLightTargetDirection[index]));

    float epsilon = SpotLightArray[index].cutOff - SpotLightArray[index].outerCutOff;

    float softCone = clamp((SpotLightArray[index].outerCutOff - theta) / epsilon, 0.0, 1.0);
    if(blinn){
        vec3 halb_vektor = normalize( lightDirection+ viewDirection);
        float spec_blinn = pow(max(dot(normalize(vertexNormal),halb_vektor),0.0),Material.shininess);
        calcLightData.specular = SpotLightArray[index].lightColor * spec_blinn;
    }
    if(theta > SpotLightArray[index].cutOff){

        float lightDistance = SpotLightDistArray[index];
        float cosAlpha = max(dot(vertexNormal, lightDirection), 0.0);

        float attenuation = getAttenuation(SpotLightArray[index].attenuationType, lightDistance);
        int attenuationType = SpotLightArray[index].attenuationType;

        calcLightData.diffuse = matDiffuse * SpotLightArray[index].lightColor * SpotLightArray[index].intensity * softCone * cosAlpha / attenuation;

        if(blinn) return calcLightData; //Specular-Calculation is finished

        vec3 R = normalize(reflect(-lightDirection, vertexNormal)); //If not Blinn then calculate specular
        float cosBeta = max(0.0, dot (R, viewDirection));
        float cosBetak = pow(cosBeta, Material.shininess);
        vec3 specularTerm = matSpecular * SpotLightArray[index].lightColor * SpotLightArray[index].intensity * softCone / attenuation;
        calcLightData.specular = specularTerm * cosBetak;

    }
    return calcLightData;
}




void main(){

    //get tex color on uv coordinate
    vec3 matEmissive = texture(Material.texEmit, VertexData.texCoord * Material.tcMultiplier).xyz;
    vec3 matDiffuse = texture(Material.texDiff,VertexData.texCoord * Material.tcMultiplier).xyz;
    vec3 matSpecular = texture(Material.texSpec,VertexData.texCoord * Material.tcMultiplier).xyz;
    toLinear(matDiffuse);
    toLinear(matEmissive);
    toLinear(matSpecular);

    vec3 vertexNormal = normalize(VertexData.normal);
    vec3 viewDirection = normalize(ViewDirection);

    //variables for color
    vec3 diffuse = vec3(0);
    vec3 specular = vec3(0);
    vec3 emission = matEmissive * Material.emitMultiplier;
    vec3 ambient = AmbientColor * matDiffuse;

    //add point lights
    for (int i = 0 ; i < PointLightArrayLength ; i++){
        CalcLightDataStruct calcPointLightData = calcPointLight(i, viewDirection, vertexNormal, matDiffuse, matSpecular);
        diffuse += calcPointLightData.diffuse;
        specular += calcPointLightData.specular;
    }

    for (int j = 0 ; j < SpotLightArrayLength ; j++){
        CalcLightDataStruct calcSpotLightData = calcSpotLight(j, viewDirection, vertexNormal, matDiffuse, matSpecular, false);
        diffuse += calcSpotLightData.diffuse;
        specular += calcSpotLightData.specular;
    }

    //add up material inputs
    vec3 result = emission + diffuse + specular + ambient;
    toSRGB(result);
    color = vec4(result, 1.0);

}