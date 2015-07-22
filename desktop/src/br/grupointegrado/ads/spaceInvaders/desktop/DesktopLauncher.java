package br.grupointegrado.ads.spaceInvaders.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import br.grupointegrado.ads.spaceInvaders.MainGame;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.backgroundFPS = 60;
        config.foregroundFPS = 60;
        config.height = 640;
        config.width = 480;
        new LwjglApplication(new MainGame(), config);
    }
}
