#include <windows.h>

#include <GL/glew.h>

#include <GL/freeglut.h>

#include <GL/gl.h>
#include <GL/glext.h>

#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>
#include <glm/gtx/transform.hpp>
#include <glm/gtc/type_ptr.hpp>

#include <iostream>

#include "InitShader.h"
#include "imgui_impl_glut.h"
#include "VideoMux.h"
#include "DebugCallback.h"
#include "LoadTexture.h"
#include "LoadMesh.h"

#include <iostream>

GLuint meshVao;

GLuint texture_id = -1;

static const std::string mesh_name = "grass_low_poly.obj";
static const std::string texture_name = "low_poly_grassDark.png";

//names of the shader files to load
static const std::string vertex_shader("tfb_vs.glsl");
static const std::string fragment_shader("tfb_fs.glsl");

GLuint shader_program = -1;

//Ping-pong pairs of objects and buffers since we don't have simultaneous read/write access to VBOs.
GLuint vao[2] = {-1, -1};
GLuint vbo[2] = {-1, -1};
const int num_particles = 20000;
GLuint tfo[2] = { -1, -1 }; //transform feedback objects

//These indices get swapped every frame to perform the ping-ponging
int Read_Index = 0;  //initially read from VBO_ID[0]
int Write_Index = 1; //initially write to VBO_ID[1]

float time_sec = 0.0f;
float angle = 0.0f;
bool recording = false;

int newFirework = 0;
float startPoint = 0;
static int numberTails[20];
static int redColor[21];
static int greenColor[21];
static int blueColor[21];
int numberFireworks = 0;

MeshData mesh_data;

//Draw the user interface using ImGui
void draw_gui()
{
   ImGui_ImplGlut_NewFrame();

   const int filename_len = 256;
   static char video_filename[filename_len] = "capture.mp4";

   ImGui::InputText("Video filename", video_filename, filename_len);
   ImGui::SameLine();
   if (recording == false)
   {
      if (ImGui::Button("Start Recording"))
      {
         const int w = glutGet(GLUT_WINDOW_WIDTH);
         const int h = glutGet(GLUT_WINDOW_HEIGHT);
         recording = true;
         start_encoding(video_filename, w, h); //Uses ffmpeg
      }
   }
   else
   {
      if (ImGui::Button("Stop Recording"))
      {
         recording = false;
         finish_encoding(); //Uses ffmpeg
      }
   }

   //create a slider to change the angle variables
   ImGui::SliderFloat("View angle", &angle, -3.141592f, +3.141592f);

   static float slider = 0.5f;
   if(ImGui::SliderFloat("Slider", &slider, 0.0f, 1.0f))
   {
      glUniform1f(2, slider);
   }

   //static bool show = false;
   //ImGui::ShowTestWindow(&show);
   ImGui::Render();
 }

std::vector<glm::mat4> getCoordinates() {
	std::vector<glm::mat4> coordinates;
	for (int i = 0; i < 1000; i++) 
	{
		for (int j = 0; j < 100; j++)
		{
			coordinates.push_back(glm::translate(glm::mat4(), glm::vec3(-1.0 + i * 0.002f + 0.0001*(round((float(rand() % 10 + 1)) / 10.0)) 
				+ 0.0001*(round((float(rand() % 10 + 1)) / 10.0)), -1.2f + j * 0.01f, 0.0f)));
		}
	}
	return coordinates;
}

GLuint create_transform_vbo() {

	std::vector<glm::mat4> surf_tra = getCoordinates();

	GLuint vbo;
	glGenBuffers(1, &vbo); //Generate vbo to hold vertex attributes for triangle.
	glBindBuffer(GL_ARRAY_BUFFER, vbo); //Specify the buffer where vertex attribute data is stored.

	//Upload from main memory to gpu memory.
	glBufferData(GL_ARRAY_BUFFER, sizeof(glm::mat4) * surf_tra.size(), surf_tra.data(), GL_STATIC_DRAW);

	int mat_loc = 10;
	int mat_loc1 = mat_loc + 0;
	int mat_loc2 = mat_loc + 1;
	int mat_loc3 = mat_loc + 2;
	int mat_loc4 = mat_loc + 3;
	glEnableVertexAttribArray(mat_loc1);
	glEnableVertexAttribArray(mat_loc2);
	glEnableVertexAttribArray(mat_loc3);
	glEnableVertexAttribArray(mat_loc4);
	glBindBuffer(GL_ARRAY_BUFFER, vbo);
	glVertexAttribPointer(mat_loc1, 4, GL_FLOAT, GL_FALSE, sizeof(glm::mat4), (void*)(sizeof(glm::vec4) * 0));
	glVertexAttribPointer(mat_loc2, 4, GL_FLOAT, GL_FALSE, sizeof(glm::mat4), (void*)(sizeof(glm::vec4) * 1));
	glVertexAttribPointer(mat_loc3, 4, GL_FLOAT, GL_FALSE, sizeof(glm::mat4), (void*)(sizeof(glm::vec4) * 2));
	glVertexAttribPointer(mat_loc4, 4, GL_FLOAT, GL_FALSE, sizeof(glm::mat4), (void*)(sizeof(glm::vec4) * 3));
	glVertexAttribDivisor(mat_loc1, 1);
	glVertexAttribDivisor(mat_loc2, 1);
	glVertexAttribDivisor(mat_loc3, 1);
	glVertexAttribDivisor(mat_loc4, 1);

	return vbo;
}

void drawGrass()
{
	const int pass = 2;
	int pass_loc = glGetUniformLocation(shader_program, "pass");
	if (pass_loc != -1)
	{
		glUniform1i(pass_loc, pass);
	}

	const int w = glutGet(GLUT_WINDOW_WIDTH);
	const int h = glutGet(GLUT_WINDOW_HEIGHT);
	const float aspect_ratio = float(w) / float(h);

	glm::mat4 mGrass = glm::rotate(angle, glm::vec3(0.0f, 1.0f, 0.0f))*glm::scale(glm::vec3(mesh_data.mScaleFactor));
	glm::mat4 vGrass = glm::lookAt(glm::vec3(0.0f, 1.0f, 2.0f), glm::vec3(0.0f, 0.0f, 0.0f), glm::vec3(0.0f, 1.0f, 0.0f));
	glm::mat4 pGrass = glm::perspective(3.141592f / 2.0f, 5.0f*aspect_ratio, 0.1f, 100.0f);
	glm::mat4 sGrass = glm::scale(glm::mat4(), glm::vec3(0.05, 0.05, 1.0));

	glActiveTexture(GL_TEXTURE0);
	glBindTexture(GL_TEXTURE_2D, texture_id);
	int tex_loc = glGetUniformLocation(shader_program, "diffuse_color");
	if (tex_loc != -1)
	{
		glUniform1i(tex_loc, 0); 
	}

	int pvmGrass_loc = glGetUniformLocation(shader_program, "pvmGrass");
	if (pvmGrass_loc != -1)
	{
		glm::mat4 pvmGrass = pGrass*vGrass*mGrass*sGrass;
		glUniformMatrix4fv(pvmGrass_loc, 1, false, glm::value_ptr(pvmGrass));
	}

	glPrimitiveRestartIndex(0xFFFF);

	glBindVertexArray(meshVao);
	mesh_data.DrawMeshInstanced(100000);
}

void drawFireworks() 
{
	const int pass = 1;
	int pass_loc = glGetUniformLocation(shader_program, "pass");
	if (pass_loc != -1)
	{
		glUniform1i(pass_loc, pass);
	}

	const int w = glutGet(GLUT_WINDOW_WIDTH);
	const int h = glutGet(GLUT_WINDOW_HEIGHT);
	const float aspect_ratio = float(w) / float(h);

	glm::mat4 M = glm::rotate(angle, glm::vec3(0.0f, 1.0f, 0.0f));
	glm::mat4 V = glm::lookAt(glm::vec3(0.0f, 1.0f, 2.0f), glm::vec3(0.0f, 0.0f, 0.0f), glm::vec3(0.0f, 1.0f, 0.0f));
	glm::mat4 P = glm::perspective(3.141592f / 4.0f, aspect_ratio, 0.1f, 100.0f);

	int PVM_loc = glGetUniformLocation(shader_program, "PVM");
	if (PVM_loc != -1)
	{
		glm::mat4 PVM = P * V*M;
		glUniformMatrix4fv(PVM_loc, 1, false, glm::value_ptr(PVM));
	}

	int newFirework_loc = glGetUniformLocation(shader_program, "newFirework");
	if (newFirework_loc != -1)
	{
		glUniform1i(newFirework_loc, newFirework);
	}
	newFirework = 0;

	int numberTails_loc = glGetUniformLocation(shader_program, "numberTails");
	if (numberTails_loc != -1)
	{
		glUniform1iv(numberTails_loc, 20, numberTails);
	}

	int startPoint_loc = glGetUniformLocation(shader_program, "startPoint");
	if (startPoint_loc != -1)
	{
		glUniform1f(startPoint_loc, startPoint);
	}

	int numberFireworks_loc = glGetUniformLocation(shader_program, "numberFireworks");
	if (numberFireworks_loc != -1)
	{
		glUniform1i(numberFireworks_loc, numberFireworks);
	}

	int redColor_loc = glGetUniformLocation(shader_program, "redColor");
	if (redColor_loc != -1)
	{
		glUniform1iv(redColor_loc, 20, redColor);
	}

	int greenColor_loc = glGetUniformLocation(shader_program, "greenColor");
	if (greenColor_loc != -1)
	{
		glUniform1iv(greenColor_loc, 20, greenColor);
	}

	int blueColor_loc = glGetUniformLocation(shader_program, "blueColor");
	if (blueColor_loc != -1)
	{
		glUniform1iv(blueColor_loc, 20, blueColor);
	}

	const bool TFO_SUPPORTED = true;

	if (TFO_SUPPORTED == true)
	{
		//Bind the current write transform feedback object.
		glBindTransformFeedback(GL_TRANSFORM_FEEDBACK, tfo[Write_Index]);
	}
	else
	{
		//Binding the transform feedback object recalls the buffer range state shown below. If
		//your system does not support transform feedback objects you can uncomment the following lines.
		const GLint pos_varying = 0;
		const GLint vel_varying = 1;
		const GLint age_varying = 2;

		glBindBufferBase(GL_TRANSFORM_FEEDBACK_BUFFER, pos_varying, vbo[Write_Index]);
		glBindBufferBase(GL_TRANSFORM_FEEDBACK_BUFFER, vel_varying, vbo[Write_Index]);
		glBindBufferBase(GL_TRANSFORM_FEEDBACK_BUFFER, age_varying, vbo[Write_Index]);
	}

	glDepthMask(GL_FALSE);

	//Prepare the pipeline for transform feedback
	glBeginTransformFeedback(GL_POINTS);
	glBindVertexArray(vao[Read_Index]);
	glDrawArrays(GL_POINTS, 0, num_particles);
	glEndTransformFeedback();

	if (TFO_SUPPORTED == true)
	{
		//Bind the current write transform feedback object.
		glBindTransformFeedback(GL_TRANSFORM_FEEDBACK, 0);
	}

	//Ping-pong the buffers.
	Read_Index = 1 - Read_Index;
	Write_Index = 1 - Write_Index;

	glDepthMask(GL_TRUE);
}


// glut display callback function.
// This function gets called every time the scene gets redisplayed 
void display()
{
   //clear the screen
   glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
   glUseProgram(shader_program);

   const int w = glutGet(GLUT_WINDOW_WIDTH);
   const int h = glutGet(GLUT_WINDOW_HEIGHT);

   //glViewport(0, 0, w, h); //Render to the full viewport
   glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); //Clear the back buffer

   drawFireworks();
   drawGrass();

   glutSwapBuffers();

   draw_gui();

   glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); //Clear the back buffer

   if (recording == true)
   {
	   glFinish();

	   glReadBuffer(GL_BACK);
	   read_frame_to_encode(&rgb, &pixels, w, h);
	   encode_frame(rgb);
   }
}

// glut idle callback.
//This function gets called between frames
void idle()
{
	glutPostRedisplay();

   const int time_ms = glutGet(GLUT_ELAPSED_TIME);
   time_sec = 0.001f*time_ms;

   glUniform1f(1, time_sec);
}

void reload_shader()
{
   GLuint new_shader = InitShader(vertex_shader.c_str(), fragment_shader.c_str());

   if(new_shader == -1) // loading failed
   {
      glClearColor(1.0f, 0.0f, 1.0f, 0.0f);
   }
   else
   {
      glClearColor(0.1f, 0.05f, 0.1f, 0.0f);

      if(shader_program != -1)
      {
         glDeleteProgram(shader_program);
      }
      shader_program = new_shader;
      
      //You need to specify which varying variables will capture transform feedback values.
      const char *vars[] = { "pos_out", "vel_out", "age_out", "exploded_out", "down_out" };
      glTransformFeedbackVaryings(shader_program, 5, vars, GL_INTERLEAVED_ATTRIBS);

      //Must relink the program after specifying transform feedback varyings.
      glLinkProgram(shader_program);
      int status;
      glGetProgramiv(shader_program, GL_LINK_STATUS, &status);
      if (status == GL_FALSE)
      {
         printProgramLinkError(shader_program);
      }
   }
}

// Display info about the OpenGL implementation provided by the graphics driver.
// Your version should be > 4.0 for CGT 521 
void printGlInfo()
{
   std::cout << "Vendor: "       << glGetString(GL_VENDOR)                    << std::endl;
   std::cout << "Renderer: "     << glGetString(GL_RENDERER)                  << std::endl;
   std::cout << "Version: "      << glGetString(GL_VERSION)                   << std::endl;
   std::cout << "GLSL Version: " << glGetString(GL_SHADING_LANGUAGE_VERSION)  << std::endl;
}

#define BUFFER_OFFSET( offset )   ((GLvoid*) (offset))

void initOpenGl()
{
   //Initialize glew so that new OpenGL function names can be used
   glewInit();

   RegisterCallback();

   glEnable(GL_DEPTH_TEST);

   //Enable alpha blending
   glEnable(GL_BLEND);
   glBlendFunc(GL_SRC_ALPHA, GL_ONE); //additive alpha blending
   //glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA); //semitransparent alpha blending

   //Allow setting point size in fragment shader
   glEnable(GL_PROGRAM_POINT_SIZE);

   //reload shader is modified to specify transform feedback varyings
   reload_shader();
   
   //create TFOs
   glGenTransformFeedbacks(2, tfo);
  
   //all attribs are initially zero
   float zeros[9*num_particles] = {0.0f}; //particle positions, velocities, ages

   const int stride = 9*sizeof(float);
   const int pos_offset = 0;
   const int vel_offset = sizeof(glm::vec3);
   const int age_offset = 2*sizeof(glm::vec3);
   const int exploded_offset = 2 * sizeof(glm::vec3) + sizeof(float);
   const int down_offset = 2 * sizeof(glm::vec3) + 2 * sizeof(float);
   const int pos_size = num_particles * sizeof(glm::vec3);
   const int vel_size = num_particles * sizeof(glm::vec3);
   const int age_size = num_particles * sizeof(float);
   const int exploded_size = num_particles * sizeof(float);
   const int down_size = num_particles * sizeof(float);

   //These are the indices in the array passed to glTransformFeedbackVaryings (const char *vars[] = { "pos_out", "vel_out", "age_out" };)
   const GLint pos_varying = 0;
   const GLint vel_varying = 1;
   const GLint age_varying = 2;
   const GLint exploded_varying = 3;
   const GLint down_varying = 4;

   //create VAOs and VBOs
   glGenVertexArrays(2, vao);
   glGenBuffers(2, vbo);   

   //These are the attribute locations we specify in the shader.
   const GLint pos_loc = 0;
   const GLint vel_loc = 1;
   const GLint age_loc = 2;
   const GLint exploded_loc = 3;
   const GLint down_loc = 4;

   for(int i=0; i<2; i++)
   {
      //Create VAO and VBO with interleaved attributes
      glBindVertexArray(vao[i]); 
      glBindBuffer(GL_ARRAY_BUFFER, vbo[i]); 
      glBufferData(GL_ARRAY_BUFFER, sizeof(zeros), zeros, GL_DYNAMIC_COPY);

      glEnableVertexAttribArray(pos_loc); 
      glVertexAttribPointer(pos_loc, 3, GL_FLOAT, false, stride, BUFFER_OFFSET(pos_offset)); 

      glEnableVertexAttribArray(vel_loc); 
      glVertexAttribPointer(vel_loc, 3, GL_FLOAT, false, stride, BUFFER_OFFSET(vel_offset));            
                                                                                    
      glEnableVertexAttribArray(age_loc); 
      glVertexAttribPointer(age_loc, 1, GL_FLOAT, false, stride, BUFFER_OFFSET(age_offset));

	  glEnableVertexAttribArray(exploded_loc);
	  glVertexAttribPointer(exploded_loc, 1, GL_FLOAT, false, stride, BUFFER_OFFSET(exploded_offset));

	  glEnableVertexAttribArray(down_loc);
	  glVertexAttribPointer(down_loc, 1, GL_FLOAT, false, stride, BUFFER_OFFSET(down_offset));

      //Tell the TFO where each varying variable should be written in the VBO.
      glBindTransformFeedback(GL_TRANSFORM_FEEDBACK, tfo[i]);

      //Specify VBO to write into
      glBindBufferBase(GL_TRANSFORM_FEEDBACK_BUFFER, pos_varying, vbo[i]);
      glBindBufferBase(GL_TRANSFORM_FEEDBACK_BUFFER, vel_varying, vbo[i]);
      glBindBufferBase(GL_TRANSFORM_FEEDBACK_BUFFER, age_varying, vbo[i]);
	  glBindBufferBase(GL_TRANSFORM_FEEDBACK_BUFFER, exploded_varying, vbo[i]);
	  glBindBufferBase(GL_TRANSFORM_FEEDBACK_BUFFER, down_varying, vbo[i]);

      glBindTransformFeedback(GL_TRANSFORM_FEEDBACK, 0);
      glBindVertexArray(0); //unbind vao
      glBindBuffer(GL_ARRAY_BUFFER, 0); //unbind vbo
   }

   mesh_data = LoadMesh(mesh_name); //Helper function: Uses Open Asset Import library.
   texture_id = LoadTexture(texture_name.c_str()); //Helper function: Uses FreeImage library

   meshVao = mesh_data.mVao;
   //Generate vao id to hold the mapping from attrib variables in shader to memory locations in vbo
   //glGenVertexArrays(0, &meshVao);

   //Binding vao means that bindbuffer, enablevertexattribarray and vertexattribpointer state will be remembered by vao
   glBindVertexArray(meshVao);

   create_transform_vbo();
   glBindVertexArray(0);
}

// glut callbacks need to send keyboard and mouse events to imgui
void keyboard(unsigned char key, int x, int y)
{
   ImGui_ImplGlut_KeyCallback(key);
   std::cout << "key : " << key << ", x: " << x << ", y: " << y << std::endl;

   switch(key)
   {
      case 'r':
      case 'R':
         reload_shader();     
      break;

	  case 'n':
	  case 'N':
		  newFirework = 1;
		  startPoint = (rand() % 10 - 4.5)/3;
		  if (numberFireworks == 20)
		  {
			  numberFireworks = 1;
		  }
		  else 
		  {
			  numberFireworks = numberFireworks + 1;
		  }
		  numberTails[numberFireworks-1] = round(rand() % 10) / 2 + 5;
		  redColor[numberFireworks - 1] = round((float(rand() % 10 + 1)) / 10.0);
		  greenColor[numberFireworks - 1] = round((float(rand() % 10 + 1)) / 10.0);
		  blueColor[numberFireworks - 1] = round((float(rand() % 10 + 1)) / 10.0);
		  break;
   }
}

void keyboard_up(unsigned char key, int x, int y)
{
   ImGui_ImplGlut_KeyUpCallback(key);
}

void special_up(int key, int x, int y)
{
   ImGui_ImplGlut_SpecialUpCallback(key);
}

void passive(int x, int y)
{
   ImGui_ImplGlut_PassiveMouseMotionCallback(x,y);
}

void special(int key, int x, int y)
{
   ImGui_ImplGlut_SpecialCallback(key);
}

void motion(int x, int y)
{
   ImGui_ImplGlut_MouseMotionCallback(x, y);
}

void mouse(int button, int state, int x, int y)
{
   ImGui_ImplGlut_MouseButtonCallback(button, state);
}


int main (int argc, char **argv)
{
   //Configure initial window state using freeglut

#if _DEBUG
   glutInitContextFlags(GLUT_DEBUG);
#endif
   glutInitContextVersion(4, 3);

   glutInit(&argc, argv); 
   glutInitDisplayMode (GLUT_DOUBLE | GLUT_RGBA | GLUT_DEPTH);
   glutInitWindowPosition (5, 5);
   glutInitWindowSize (1280, 720);
   int win = glutCreateWindow ("Transform Feedback Demo");

   printGlInfo();

   //Register callback functions with glut. 
   glutDisplayFunc(display); 
   glutKeyboardFunc(keyboard);
   glutSpecialFunc(special);
   glutKeyboardUpFunc(keyboard_up);
   glutSpecialUpFunc(special_up);
   glutMouseFunc(mouse);
   glutMotionFunc(motion);
   glutPassiveMotionFunc(motion);
   glutIdleFunc(idle);

   initOpenGl();
   ImGui_ImplGlut_Init(); // initialize the imgui system

   //Enter the glut event loop.
   glutMainLoop();
   glutDestroyWindow(win);
   return 0;		
}


