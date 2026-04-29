#version 430

layout(location = 0) out vec4 fragcolor;

uniform int redColor[21];
uniform int greenColor[21];
uniform int blueColor[21];

flat in int placeMarker;

uniform sampler2D diffuse_color;
in vec2 tex_coord;
uniform int pass;

void main(void)
{
	if(pass == 1)
	{
		if(gl_PointCoord.y > 0.9)
		{
			if(redColor[placeMarker] == 0 && greenColor[placeMarker] == 0 && blueColor[placeMarker] == 0)
			{
				fragcolor = vec4(1, 1, 1, 1);
			}
			else
			{
				fragcolor = vec4(redColor[placeMarker]+0.2, greenColor[placeMarker]+0.2, blueColor[placeMarker]+0.2, 1);
			}
		}
	}
	else
	{
		fragcolor = texture(diffuse_color, tex_coord);
	}
}


























