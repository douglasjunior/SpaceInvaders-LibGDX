package br.grupointegrado.ads.spaceInvaders;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import br.grupointegrado.ads.spaceInvaders.screens.PlayScreen;

public class MainGame extends Game {

    @Override
    public void create() {
        setScreen(new PlayScreen());
    }
}
