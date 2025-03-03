import org.joml.Vector4f;

public class Main {
    
    public static final Vector4f DEFAULT_COLOR = new Vector4f(1, 1, 1, 1);

    private static WindowManager window;
    private static TestGame game;

    public static void main(String[] args) {
        window = new WindowManager("ENGINE", 0, 0, false);
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
