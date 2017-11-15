package com.roger.skybox;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.roger.skybox.objects.ParticleShooter;
import com.roger.skybox.objects.ParticleSystem;
import com.roger.skybox.objects.Skybox;
import com.roger.skybox.programs.ParticleShaderProgram;
import com.roger.skybox.programs.SkyboxShaderProgram;
import com.roger.skybox.util.Geometry;
import com.roger.skybox.util.Geometry.Point;
import com.roger.skybox.util.Geometry.Vector;
import com.roger.skybox.util.MatrixHelper;
import com.roger.skybox.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

/**
 * Created by Administrator on 2016/6/30.
 */
public class ParticlesRenderer implements GLSurfaceView.Renderer {

  private final Context context;

  private final float[] projectionMatrix = new float[16];
  private final float[] viewMatrix = new float[16];
  private final float[] viewProjectionMatrix = new float[16];

  private SkyboxShaderProgram skyboxProgram;
  private Skybox skybox;
    /*
    // Maximum saturation and value.
    private final float[] hsv = {0f, 1f, 1f};*/

  private ParticleShaderProgram particleProgram;
  private ParticleSystem particleSystem;
  private ParticleShooter redParticleShooter;
  private ParticleShooter greenParticleShooter;
  private ParticleShooter blueParticleShooter;
  /*private ParticleFireworksExplosion particleFireworksExplosion;
  private Random random;*/
  private long globalStartTime;
  private int skyboxTexture;
  private int particleTexture;

  private float xRotation, yRotation;

  public ParticlesRenderer(Context context) {
    this.context = context;
  }

  public void handleTouchDrag(float deltaX, float deltaY) {
    xRotation += deltaX / 16f;
    yRotation += deltaY / 16f;

    if (yRotation < -90) {
      yRotation = -90;
    } else if (yRotation > 90) {
      yRotation = 90;
    }
  }

  @Override public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
    glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

    skyboxProgram = new SkyboxShaderProgram(context);
    skybox = new Skybox();

    particleProgram = new ParticleShaderProgram(context);
    particleSystem = new ParticleSystem(10000);
    globalStartTime = System.nanoTime();

    final Vector particleDirection = new Geometry.Vector(0f, 0.5f, 0f);

    final float angleVarianceInDegrees = 5f;
    final float speedVariance = 1f;
    redParticleShooter =
        new ParticleShooter(new Point(-1f, 0f, 0f), particleDirection, Color.rgb(255, 50, 5),
            angleVarianceInDegrees, speedVariance);

    greenParticleShooter =
        new ParticleShooter(new Point(0f, 0f, 0f), particleDirection, Color.rgb(25, 255, 25),
            angleVarianceInDegrees, speedVariance);

    blueParticleShooter =
        new ParticleShooter(new Point(1f, 0f, 0f), particleDirection, Color.rgb(5, 50, 255),
            angleVarianceInDegrees, speedVariance);
    particleTexture = TextureHelper.loadTexture(context, R.drawable.particle_texture);
    skyboxTexture = TextureHelper.loadCubeMap(context, new int[] {
        R.drawable.left, R.drawable.right, R.drawable.bottom, R.drawable.top, R.drawable.front,
        R.drawable.back
    });
  }

  @Override public void onSurfaceChanged(GL10 gl10, int width, int height) {
    GLES20.glViewport(0, 0, width, height);
    MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width / (float) height, 1f, 10f);
  }

  @Override public void onDrawFrame(GL10 gl10) {
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

    drawSkybox();
    drawParticles();
  }

  private void drawSkybox() {
    setIdentityM(viewMatrix, 0);
    rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f);
    rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f);
    multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
    skyboxProgram.useProgram();
    skyboxProgram.setUniforms(viewProjectionMatrix, skyboxTexture);
    skybox.bindData(skyboxProgram);
    skybox.draw();
  }

  private void drawParticles() {
    float currentTime = (System.nanoTime() - globalStartTime) / 1000000000f;

    redParticleShooter.addParticles(particleSystem, currentTime, 1);
    greenParticleShooter.addParticles(particleSystem, currentTime, 1);
    blueParticleShooter.addParticles(particleSystem, currentTime, 1);

    setIdentityM(viewMatrix, 0);
    rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f);
    rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f);
    translateM(viewMatrix, 0, 0f, -1.5f, -5f);
    multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

    glEnable(GL_BLEND);
    glBlendFunc(GL_ONE, GL_ONE);

    particleProgram.useProgram();
    particleProgram.setUniforms(viewProjectionMatrix, currentTime, particleTexture);
    particleSystem.bindData(particleProgram);
    particleSystem.draw();

    glDisable(GL_BLEND);
  }
}
