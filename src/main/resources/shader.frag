#version 450 core

in vec2 f_uv;

out vec4 color;

layout(binding=0) uniform sampler2D tex;

void main()
{

	color = texture(tex, f_uv);
}