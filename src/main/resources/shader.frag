#version 450 core

in VStoFS {
    vec3 pos;
    vec3 rawNormal;
    vec2 uv;
} frag;

out vec4 color;

struct DirLight {
  vec3 color;
  // pad1

  vec3 dir;
  // pad1
};

struct PointLight {
  vec3 color;
  float linear;
  
  vec3 pos;
  float quadratic;
};

struct SpotLight {
  vec3 color;
  // pad1
  
  vec3 pos;
  // pad1

  vec3 dir;
  float linear;

  float quadratic;
  float innerCutoff;
  float outerCutoff;
  // pad1
};

layout(binding=0) uniform sampler2D tex;

layout (std140, binding=0) uniform Lighting {
    vec3 ambientLight;
    uint nPointLights;
    
    uint nDirLights;
    uint nSpotLights;
    //-
    //-
    
    PointLight pointLights[10];
    DirLight dirLights[10];
    SpotLight spotLights[10];
};


vec4 calcDirLightColor(DirLight light) {
  return vec4(0, 0, 0, 0);
}

vec4 calcPointLightColor(PointLight light) {
  vec3 normal = normalize(frag.rawNormal);
  vec3 lightDir = normalize(light.pos - frag.pos);
  float diffPower = max(dot(normal, lightDir), 0);
  vec4 diffuse = vec4(light.color, 1) * texture(tex, frag.uv) * diffPower;
  return diffuse;
}

vec4 calcSpotLightColor(SpotLight light) {
  return vec4(0, 0, 0, 0);
}

void main()
{
  color = texture(tex, frag.uv) * vec4(ambientLight, 1);
  
  for (int i = 0; i < nPointLights; i++)
      color += calcPointLightColor(pointLights[i]);
  
  for (int i = 0; i < nDirLights; i++)
      color += calcDirLightColor(dirLights[i]);
    
  for (int i = 0; i < nSpotLights; i++)
      color += calcSpotLightColor(spotLights[i]);
  
}