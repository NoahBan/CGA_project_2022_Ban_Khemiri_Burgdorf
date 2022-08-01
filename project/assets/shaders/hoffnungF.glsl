#version 330 core

layout (location = 0) out vec3 gPosition;
layout (location = 1) out vec3 gNormal;
layout (location = 2) out vec4 gColorSpec;
layout (location = 3) out vec3 gEmission;

//input from vertex shader
in vec3 vertexPosition;
in vec3 vertexNormal;
in vec2 vertexTexCoord;

//material
struct MaterialStruct {
    sampler2D texEmit;
    sampler2D texDiff;
    sampler2D texSpec;
    vec2 tcMultiplier;
    float shininess;
    vec3 emitMultiplier;
    int movingMat;
    float movingU;
    float movingV;
    int skySphere;
};
uniform MaterialStruct Material;

out vec4 color;

void toSRGB (inout vec3 linearCol){
    linearCol = pow(linearCol, vec3 (2.2));
}
void toLinear (inout vec3 srgbCol){
    srgbCol = pow(srgbCol, vec3(1.0/2.2));
}

void main(){
    vec2 texCoord = vertexTexCoord * Material.tcMultiplier;
    if (Material.movingMat == 1){
        texCoord[0] += Material.movingU;
        texCoord[1] += Material.movingV;
    };

    gPosition = vertexPosition;
    gNormal = normalize(vertexNormal);
    gEmission = texture(Material.texEmit, texCoord).rgb;
    gColorSpec.rgb = texture(Material.texDiff, texCoord).rgb;
    gColorSpec.a = texture(Material.texSpec, texCoord).r;
}