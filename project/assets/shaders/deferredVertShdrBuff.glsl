#version 330 core

//Vertex Attributes
layout(location = 0) in vec3 Vertex_Position;
layout(location = 1) in vec2 TexCoord;
layout(location = 2) in vec3 Normal;

out vec3 vertexPosition;
out vec3 vertexNormal;
out vec2 vertexTexCoord;

//model transformation
uniform mat4 Model_matrix;

//camera
uniform mat4 View_matrix;
uniform mat4 Projection_matrix;

void main(){
    vec4 worldPos = Model_matrix * vec4(Vertex_Position, 1.0f);

    vertexPosition = worldPos.xyz;
    vertexTexCoord = TexCoord;

    vec4 n = vec4(Normal,0);
    mat4 normalMat = transpose(inverse(View_matrix * Model_matrix));
    //VertexData.normal = (normalMat * n).xyz;
    vertexNormal = Normal;

    gl_Position = Projection_matrix * View_matrix * worldPos;
}
