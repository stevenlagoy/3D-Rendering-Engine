package org.engine.game;

import org.engine.core.EngineManager;
import org.engine.core.WindowManager;
import org.engine.utils.Consts;
import org.joml.Vector4f;

public class Launcher {

    private static WindowManager window;
    private static TestGame game;

    public static void main(String[] args) {
        window = new WindowManager(Consts.TITLE, 0, 0, false);
        game = new TestGame();
        EngineManager engine = new EngineManager();

        try {
            engine.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static WindowManager getWindow() {
        return window;
    }
    
    public static TestGame getGame() {
        return game;
    }

}
