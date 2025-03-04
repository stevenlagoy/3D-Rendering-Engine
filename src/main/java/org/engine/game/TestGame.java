package org.engine.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.engine.core.Camera;
import org.engine.core.ILogic;
import org.engine.core.MouseInput;
import org.engine.core.ObjectLoader;
import org.engine.core.WindowManager;
import org.engine.core.entity.Entity;
import org.engine.core.entity.Material;
import org.engine.core.entity.Model;
import org.engine.core.entity.SceneManager;
import org.engine.core.entity.Texture;
import org.engine.core.entity.terrain.Terrain;
import org.engine.core.lighting.DirectionalLight;
import org.engine.core.lighting.PointLight;
import org.engine.core.lighting.SpotLight;
import org.engine.core.rendering.RenderManager;
import org.engine.utils.Consts;

public class TestGame implements ILogic {

    private final RenderManager renderer;
    private final ObjectLoader loader;
    private final WindowManager window;
    private final SceneManager sceneManager;

    private Camera camera;
    Vector3f cameraInc;

    public TestGame() {
        renderer = new RenderManager();
        window = Launcher.getWindow();
        loader = new ObjectLoader();
        sceneManager = new SceneManager(-90);
        camera = new Camera();
        cameraInc = new Vector3f(0, 0, 0);
    }

    @Override
    public void init() throws Exception {
        renderer.init();

        Model model = loader.loadOBJModel("/models/cube.obj");
        model.setTexture(new Texture(loader.loadTexture("textures/obamaface.png")), 1f);

        Terrain terrain = new Terrain(new Vector3f(0, -1, -800), loader, new Material(new Texture(loader.loadTexture("textures/terrain.png")), 0.1f));
        Terrain terrain2 = new Terrain(new Vector3f(-800, -1, -800), loader, new Material(new Texture(loader.loadTexture("textures/flowers.png")), 0.1f));
        sceneManager.addTerrain(terrain); sceneManager.addTerrain(terrain2);

        Random rand = new Random();
        int factor = 265;
        for (int i = 0; i < 2 * factor; i++) {
            float x = rand.nextFloat() * factor - (factor / 2);
            float y = rand.nextFloat() * factor - (factor / 2);
            float z = rand.nextFloat() * (-3 * factor);
            sceneManager.addEntity(new Entity(
                model,
                new Vector3f(x, 2, z),
                new Vector3f(0, 0, 0),
                3f)
            );
        }
        sceneManager.addEntity(new Entity(model, new Vector3f(0, 0, -2f), new Vector3f(0, 0, 0), 1));

        float lightIntensity;
        Vector3f lightPosition, lightColor;

        // Point Lights
        lightIntensity = 50000f;
        lightPosition = new Vector3f(0f, -50f, 0f);
        lightColor = new Vector3f(1, 1, 1);
        PointLight pointLight = new PointLight(lightColor, lightPosition, lightIntensity, 0, 0, 1);

        // Spot Lights
        Vector3f coneDirection = new Vector3f(0, -50, 0);
        float cutoff = (float) Math.cos(Math.toRadians(1));
        lightIntensity = 50000f;
        lightColor = new Vector3f(0, 0.25f, 0);
        lightPosition = new Vector3f(1f, 50f, -5f);
        SpotLight spotLight0 = new SpotLight(new PointLight(lightColor, lightPosition, lightIntensity, 0, 0, 1), coneDirection, cutoff);
        lightColor = new Vector3f(0.25f, 0, 0);
        lightPosition = new Vector3f(1f, 50f, -5f);
        SpotLight spotLight1 = new SpotLight(new PointLight(lightColor, lightPosition, lightIntensity, 0, 0, 1), coneDirection, cutoff);
        spotLight1.getPointLight().setPosition(new Vector3f(0.5f, 0.5f, -3.6f));

        // Directional Light
        lightIntensity = 1;
        lightPosition = new Vector3f(-1, 0, 0);
        lightColor = new Vector3f(1, 1, 1);
        sceneManager.setDirectionalLight(new DirectionalLight(lightColor, lightPosition, lightIntensity));

        sceneManager.setPointLights(new PointLight[] {pointLight});
        sceneManager.setSpotLights(new SpotLight[] {spotLight0, spotLight1});
    }

    @Override
    public void input() {
        cameraInc.set(0, 0, 0);
        if(window.isKeyPressed(GLFW.GLFW_KEY_W))
            cameraInc.z = -10;
        if(window.isKeyPressed(GLFW.GLFW_KEY_S))
            cameraInc.z = 10;
            if(window.isKeyPressed(GLFW.GLFW_KEY_A))
            cameraInc.x = -10;
        if(window.isKeyPressed(GLFW.GLFW_KEY_D))
            cameraInc.x = 10;
        if(window.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT))
            cameraInc.y = -10;
        if(window.isKeyPressed(GLFW.GLFW_KEY_SPACE))
            cameraInc.y = 10;
        if(window.isKeyPressed(GLFW.GLFW_KEY_Q))
            camera.moveRotation(0.0f, 0.0f, -0.5f);
        if(window.isKeyPressed(GLFW.GLFW_KEY_E))
            camera.moveRotation(0.0f, 0.0f, 0.5f);
        if(window.isKeyPressed(GLFW.GLFW_KEY_R))
            camera.setRotation(camera.getRotation().x, camera.getRotation().y, 0);
    }

    @Override
    public void update(float interval, MouseInput mouse) {
        camera.movePosition(cameraInc.x * Consts.CAMERA_MOVE_SPEED, cameraInc.y * Consts.CAMERA_MOVE_SPEED, cameraInc.z * Consts.CAMERA_MOVE_SPEED);

        if (mouse.isRightButtonPress()) {
            Vector2f rotVec = mouse.getDisplVec();
            camera.moveRotation(rotVec.x * Consts.MOUSE_SENSITIVITY, rotVec.y * Consts.MOUSE_SENSITIVITY, 0);
        }

        sceneManager.incSpotAngle(0.15f);
        sceneManager.setSpotAngle(sceneManager.getSpotInc() * 0.15f);
        if (sceneManager.getSpotAngle() > 4) {
            sceneManager.setSpotInc(-1);
        }
        else if (sceneManager.getSpotAngle() < 1) {
            sceneManager.setSpotInc(1);
        }

        double spotAngleRad = Math.toRadians(sceneManager.getSpotAngle());
        Vector3f coneDir = sceneManager.getSpotLights()[0].getPointLight().getPosition();
        coneDir.x = (float) Math.sin(spotAngleRad);

        coneDir = sceneManager.getSpotLights()[1].getPointLight().getPosition();
        coneDir.z = (float) Math.cos(spotAngleRad);

        sceneManager.incLightAngle(1.1f);
        if(sceneManager.getLightAngle() > 90) {
            sceneManager.getDirectionalLight().setIntensity(0);
            if (sceneManager.getLightAngle() >= 360)
                sceneManager.setLightAngle(-90);
        }
        else if (sceneManager.getLightAngle() <= -80 || sceneManager.getLightAngle() >= 80) {
            float factor = 1 - (Math.abs(sceneManager.getLightAngle()) - 80) / 10.0f;
            sceneManager.getDirectionalLight().setIntensity(factor);
            sceneManager.getDirectionalLight().getColor().y = Math.max(factor, 0.9f);
            sceneManager.getDirectionalLight().getColor().z = Math.max(factor, 0.5f);
        }
        else {
            sceneManager.getDirectionalLight().setIntensity(1);
            sceneManager.getDirectionalLight().getColor().x = 1;
            sceneManager.getDirectionalLight().getColor().y = 1;
            sceneManager.getDirectionalLight().getColor().z = 1;        
        }
        double angRad = Math.toRadians(sceneManager.getLightAngle());
        sceneManager.getDirectionalLight().getDirection().x = (float)Math.sin(angRad);
        sceneManager.getDirectionalLight().getDirection().y = (float)Math.cos(angRad);

        for (Entity entity : sceneManager.getEntities()) {
            renderer.processEntity(entity);
        }
        
        for (Terrain terrain : sceneManager.getTerrains()) {
            renderer.processTerrain(terrain);
        }
    }

    @Override
    public void render() {
        renderer.render(camera, sceneManager);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        loader.cleanup();
    }
    

    
}
