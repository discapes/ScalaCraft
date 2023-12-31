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
layout(binding=1) uniform sampler2D tex_spec;

layout (std140, binding=0) uniform Lighting {
    uint nPointLights;
    uint nDirLights;
    uint nSpotLights;
    //-
    
    PointLight pointLights[10];
    DirLight dirLights[10];
    SpotLight spotLights[10];
};

uniform vec3 camPos;
uniform vec3 ambientLight;

vec4 calcDirLightColor(DirLight light) {
  vec3 normal = normalize(frag.rawNormal);
  vec3 lightDir = normalize(-light.dir);

  float diffPower = max(dot(normal, lightDir), 0);
  vec4 diffuse = vec4(light.color, 1) * texture(tex, frag.uv) * diffPower;

  vec3 reflection = reflect(-lightDir, normal);
  float shine = 32;
  float specPower = pow(max(dot(normalize(camPos - frag.pos), reflection), 0), shine);
  vec4 specular = vec4(light.color, 1) * texture(tex_spec, frag.uv) * specPower;

  return diffuse + specular;
}

vec4 calcPointLightColor(PointLight light) {
  vec3 normal = normalize(frag.rawNormal);
  vec3 lightDir = normalize(light.pos - frag.pos);

  float diffPower = max(dot(normal, lightDir), 0);
  vec4 diffuse = vec4(light.color, 1) * texture(tex, frag.uv) * diffPower;

  vec3 reflection = reflect(-lightDir, normal);
  float shine = 100;
  float specPower = pow(max(dot(normalize(camPos - frag.pos), reflection), 0), shine);
  vec4 specular = vec4(light.color, 1) * texture(tex_spec, frag.uv) * specPower;

  float dist = length(light.pos - frag.pos);
  float constant = 1;
  float attenuation = constant + light.linear * dist + light.quadratic * (dist * dist);
  float luminosity = 1.0 / attenuation;

  return (diffuse + specular) * luminosity;
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