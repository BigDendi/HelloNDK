package com.roger.livewallpaper.programs;

import android.content.Context;
import android.opengl.GLES20;

import com.roger.livewallpaper.util.ShaderHelper;
import com.roger.livewallpaper.util.TextResourceReader;

/**
 * Created by Administrator on 2016/7/5.
 */
public class ShaderProgram {

  protected static final String U_MATRIX = "u_Matrix";
  protected static final String U_COLOR = "u_Color";
  protected static final String U_TEXTURE_UNIT = "u_TextureUnit";
  protected static final String U_TIME = "u_Time";
  protected static final String U_VECTOR_TO_LIGHT = "u_VectorToLight";
  protected static final String U_MV_MATRIX = "u_MVMatrix";
  protected static final String U_IT_MV_MATRIX = "u_IT_MVMatrix";
  protected static final String U_MVP_MATRIX = "u_MVPMatrix";
  protected static final String U_POINT_LIGHT_POSITIONS =
      "u_PointLightPositions";
  protected static final String U_POINT_LIGHT_COLORS = "u_PointLightColors";

  // Attribute constants
  protected static final String A_POSITION = "a_Position";
  protected static final String A_COLOR = "a_Color";
  protected static final String A_NORMAL = "a_Normal";
  protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";

  protected static final String A_DIRECTION_VECTOR = "a_DirectionVector";
  protected static final String A_PARTICLE_START_TIME = "a_ParticleStartTime";

  // Shader program
  protected final int program;

  protected ShaderProgram(Context context, int vertexShaderResourceId,
      int fragmentShaderResourceId) {
    // Compile the shaders and link the program.
    program = ShaderHelper.buildProgram(
        TextResourceReader
            .readTextFileFromResource(context, vertexShaderResourceId),
        TextResourceReader
            .readTextFileFromResource(context, fragmentShaderResourceId));
  }

  public void useProgram() {
    // Set the current OpenGL shader program to this program.
    GLES20.glUseProgram(program);
  }
}
