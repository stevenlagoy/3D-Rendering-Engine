package org.engine.core.rendering;

import org.engine.core.Camera;
import org.engine.core.entity.Model;
import org.engine.core.lighting.PointLight;
import org.engine.core.lighting.SpotLight;
import org.engine.core.lighting.DirectionalLight;

public interface IRenderer<T> {
    public void init() throws Exception;

    public void render(Camera camera, PointLight[] pointLights, SpotLight[] spotLights, DirectionalLight directionalLight);

    abstract void bind(Model model);

    public void unbind();
    
    public void prepare(T t, Camera camera);

    public void cleanup();
}
