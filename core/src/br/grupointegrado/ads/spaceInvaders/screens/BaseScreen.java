package br.grupointegrado.ads.spaceInvaders.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.compression.lzma.Base;

import br.grupointegrado.ads.spaceInvaders.MainGame;

/**
 * Created by Douglas on 21/07/2015.
 */
public abstract class BaseScreen implements Screen {

    protected final MainGame game;

    public BaseScreen(MainGame game){
        this.game = game;
    }

    @Override
    public void hide() {
        dispose();
    }

}
