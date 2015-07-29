package br.grupointegrado.ads.spaceInvaders.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.compression.lzma.Base;
import com.badlogic.gdx.utils.viewport.FillViewport;

import br.grupointegrado.ads.spaceInvaders.MainGame;

/**
 * Created by Douglas on 28/07/2015.
 */
public class MenuScreen extends BaseScreen {

    private OrthographicCamera camera;
    private Stage cenario;
    private BitmapFont font;
    private TextButton btnIniciar;
    private Label lbTitulo;

    public MenuScreen(MainGame game) {
        super(game);
    }

    @Override
    public void show() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cenario = new Stage(new FillViewport(camera.viewportWidth, camera.viewportHeight, camera));
        Gdx.input.setInputProcessor(cenario);

        initBotoes();
    }

    private void initBotoes() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/roboto.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.size = 24;
        params.color = Color.BLACK;
        font = generator.generateFont(params);
        generator.dispose();

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        lbTitulo = new Label("Space Invaders", labelStyle);
        cenario.addActor(lbTitulo);

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        btnIniciar = new TextButton("Iniciar", style);
        btnIniciar.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new PlayScreen(game));
            }
        });
        cenario.addActor(btnIniciar);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        atualizarBotoes();

        cenario.act(delta);
        cenario.draw();
    }

    private void atualizarBotoes() {
        lbTitulo.setPosition(camera.viewportWidth / 2 - lbTitulo.getWidth() / 2, camera.viewportHeight - 100);

        btnIniciar.setPosition(camera.viewportWidth / 2 - btnIniciar.getWidth() / 2, camera.viewportHeight / 2 - btnIniciar.getPrefHeight() / 2);
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        camera.update();
        cenario.getViewport().setScreenSize(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        cenario.dispose();
        font.dispose();
    }
}
