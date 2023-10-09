#version 450 core

in vec2 f_uv;
out vec4 color;


struct DirLight {
  vec3 color;
  vec3 dir;
}

struct PointLight {
  vec3 color;
  vec3 pos;
  float linear;
  float quadratic;
}

struct SpotLight {
  vec3 color;
  vec3 pos;
  vec3 dir;
  float linear;
  float quadratic;
  float innerCutoff;
  float outerCutoff;
}

layout(binding=0) uniform sampler2D tex;

layout (std430, binding = 0) buffer Lighting {
    vec3 ambientLight;

    uint nDirLights;
    DirLight dirLights[];

    uint nPointLights;
    PointLight pointLights[];

    uint nSpotLights;
    SpotLight spotLights[];
};


vec4 calcDirLightColor(DirLight light) {


}

vec4 calcPointLightColor(PointLight light) {


}

vec4 calcSpotLightColor(SpotLight light) {


}

void main()
{
	color = texture(tex, f_uv) * vec4(ambientLight, 1);
      
    for (int i = 0; i < nPointLights; i++)
        color += calcPointLightColor(pointLights[i]);
    
    for (int i = 0; i < nDirLights; i++)
        color += calcDirLightColor(dirLights[i]);
      
    for (int i = 0; i < nPointLights; i++)
        color += calcPointLightColor(pointLights[i]);
    
    for (int i = 0; i < nSpotLights; i++)
        color += calcSpotLightColor(spotLights[i]);
    
}