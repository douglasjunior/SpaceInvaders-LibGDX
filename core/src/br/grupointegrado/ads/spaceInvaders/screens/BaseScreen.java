package br.grupointegrado.ads.spaceInvaders.screens;

import com.badlogic.gdx.Screen;

/**
 * Created by Douglas on 21/07/2015.
 */
public abstract class BaseScreen implements Screen {

    @Override
    public void hide() {
        dispose();
    }

}
