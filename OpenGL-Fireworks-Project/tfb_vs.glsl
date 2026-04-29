#version 440            

layout(location = 0) uniform mat4 PVM;
layout(location = 1) uniform float time;
layout(location = 2) uniform float slider = 0.5;  

layout(location = 0) in vec3 pos_attrib;
layout(location = 1) in vec3 vel_attrib;
layout(location = 2) in float age_attrib;
layout(location = 3) in float exploded_attrib;
layout(location = 4) in float down_attrib;

layout(xfb_offset = 0, xfb_stride = 36) out vec3 pos_out; 
layout(xfb_offset = 12, xfb_stride = 36) out vec3 vel_out; 
layout(xfb_offset = 24, xfb_stride = 36) out float age_out;
layout(xfb_offset = 28, xfb_stride = 36) out float exploded_out;
layout (xfb_offset = 32, xfb_stride = 36) out float down_out;

uniform int newFirework;
uniform int numberTails[20];
uniform float startPoint;
uniform int numberFireworks;

flat out int placeMarker;

vec3 v0(vec3 p);
float rand(vec2 co);
vec3 calculateVelOut(vec3 posAttr, float angle, int num);
vec3 calculatePosOut(vec3 posAttr, vec3 velOut);

uniform mat4 pvmGrass;
in vec2 tex_coord_attrib;
in vec3 normal_attrib;
out vec2 tex_coord;

uniform int pass;

out vec3 fragPos;

layout(location=10) in mat4 model_matrix;

void main(void)
{
	if (pass == 1)
	{
		gl_Position = PVM*vec4(pos_attrib.x, pos_attrib.y, pos_attrib.z, 1.0);
		gl_PointSize = 5.0 + 0.001*age_attrib + rand(vec2(float(gl_Position.y), time));

		if (gl_VertexID > numberFireworks * 1000)
		{
			pos_out = vec3(-10, -10, -10);
		}
		else if (pos_attrib.y < 0.5 && exploded_attrib == 0.0)
		{
			vel_out = v0(pos_attrib) + vec3(0, 10, 0);
			pos_out = pos_attrib + 0.001*vel_out;
			exploded_out = 0.0;
			down_out = 0.0;
			placeMarker = 20;
		}
		else
		{
			for (int j = 0; j < numberFireworks; j++)
			{
				for (int i = 1; i < numberTails[j] + 1; i++)
				{
					if (gl_VertexID < round((numberFireworks-1)*1000 + (1000/numberTails[j])*i))
					{
						placeMarker = int(floor(gl_VertexID/1000));
						vel_out = rand(vec2(pos_attrib.x, pos_attrib.y)) * calculateVelOut(pos_attrib, round(6.283/numberTails[placeMarker]), i);
						pos_out = calculatePosOut(pos_attrib, vel_out);
						exploded_out = 1.0;
						down_out = down_attrib - 0.001;
						break;
					}
				}
				break;
			}
		}

		age_out = age_attrib - 3.5 + 0.3*abs(vel_out.x) + 3.5*rand(vec2(vel_out.y, vel_out.z));

		if(newFirework == 1 && gl_VertexID < 1000*numberFireworks && gl_VertexID > 1000*(numberFireworks-1))
		{
			vec2 seed = vec2(float(gl_VertexID), time); 
			pos_out = vec3(startPoint, -0.2, 0) + vec3(0.01*rand(seed.xx), 0.01*rand(seed.yy), 0.01*rand(seed.xy));
			age_out = 0;
			exploded_out = 0.0;
			down_out = 0.0;
		}
		else if (age_out < -5000 )
		{
			pos_out = vec3(0, 0, 0);
		}
	}
	else
	{
		gl_Position = model_matrix*pvmGrass*vec4(pos_attrib, 1.0);
		tex_coord = tex_coord_attrib;
		fragPos = vec3(model_matrix * vec4(pos_attrib, 1.0));
	}
}

vec3 calculateVelOut(vec3 posAttr, float angle, int num)
{
	return 0.2*v0(posAttr) + vec3(cos(angle*num), down_attrib, sin(angle*num));
}

vec3 calculatePosOut(vec3 posAttr, vec3 velOut)
{
	return posAttr + 0.0005*velOut;
}

vec3 v0(vec3 p)
{
	return vec3(sin(p.y*10.0+time-10.0), -sin(p.x*10.0+9.0*time+10.0), +cos(2.4*p.z+2.0*time));
}

float rand(vec2 co)
{
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

