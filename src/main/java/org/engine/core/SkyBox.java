package org.engine.core;

import org.engine.core.entity.Material;
import org.engine.core.entity.Model;
import org.engine.core.entity.Texture;

public class SkyBox {

    private Texture texture;

    public SkyBox(Texture texture) {
        this.texture = texture;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }
}
