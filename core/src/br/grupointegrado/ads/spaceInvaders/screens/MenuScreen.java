package br.grupointegrado.ads.spaceInvaders.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.viewport.FillViewport;

import br.grupointegrado.ads.spaceInvaders.MainGame;
import br.grupointegrado.ads.spaceInvaders.util.Format;
import br.grupointegrado.ads.spaceInvaders.util.Preferencias;

/**
 * Created by Douglas on 28/07/2015.
 */
public class MenuScreen extends BaseScreen {

    private OrthographicCamera camera;
    private Stage cenario;
    private BitmapFont fontBotoes;
    private BitmapFont fontTitulo;
    private Texture botaoTextura;
    private Texture botaoPressionadoTextura;
    private ImageTextButton btnIniciar;
    private Label lbTitulo;
    private Label lbPontuacao;

    public MenuScreen(MainGame game) {
        super(game);
    }

    @Override
    public void show() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cenario = new Stage(new FillViewport(camera.viewportWidth, camera.viewportHeight, camera));
        Gdx.input.setInputProcessor(cenario);

        initFonts();
        initTexturas();
        initLabels();
        initBotoes();
    }

    private void initLabels() {
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = fontTitulo;
        lbTitulo = new Label("Space Invaders", labelStyle);
        cenario.addActor(lbTitulo);

        labelStyle = new Label.LabelStyle();
        labelStyle.font = fontBotoes;
        lbPontuacao = new Label("Pontuação: " + Format.format(Preferencias.getMaiorPontuacao()), labelStyle);
        cenario.addActor(lbPontuacao);
    }

    private void initFonts() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/roboto.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.size = MathUtils.roundPositive(32 * Gdx.graphics.getDensity());
        params.color = Color.BLACK;
        fontBotoes = generator.generateFont(params);

        params = new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.size = MathUtils.roundPositive(48 * Gdx.graphics.getDensity());
        params.color = new Color(.25f, .25f, .85f, 1);
        params.shadowOffsetX = 2;
        params.shadowOffsetY = 2;
        params.shadowColor = Color.BLACK;
        fontTitulo = generator.generateFont(params);

        generator.dispose();
    }

    private void initTexturas() {
        botaoTextura = new Texture(Gdx.files.internal("buttons/button.png"));
        botaoPressionadoTextura = new Texture(Gdx.files.internal("buttons/button-down.png"));
    }

    private void initBotoes() {
        ImageTextButton.ImageTextButtonStyle style = new ImageTextButton.ImageTextButtonStyle();
        style.font = fontBotoes;
        style.up = new SpriteDrawable(new Sprite(botaoTextura));
        style.down = new SpriteDrawable(new Sprite(botaoPressionadoTextura));

        btnIniciar = new ImageTextButton("  Iniciar  ", style);
        btnIniciar.addListener(new ClickListener() {
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
        lbTitulo.setPosition(camera.viewportWidth / 2 - lbTitulo.getPrefWidth() / 2, camera.viewportHeight - lbTitulo.getPrefHeight() - 50);

        btnIniciar.setPosition(camera.viewportWidth / 2 - btnIniciar.getPrefWidth() / 2, camera.viewportHeight / 2 - btnIniciar.getPrefHeight() / 2);

        lbPontuacao.setPosition(camera.viewportWidth / 2 - lbPontuacao.getPrefWidth() / 2, 100);
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
        fontBotoes.dispose();
        fontTitulo.dispose();
        botaoTextura.dispose();
        botaoPressionadoTextura.dispose();
    }
}
