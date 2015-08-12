package br.grupointegrado.ads.spaceInvaders.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;

import br.grupointegrado.ads.spaceInvaders.MainGame;
import br.grupointegrado.ads.spaceInvaders.model.Explosao;
import br.grupointegrado.ads.spaceInvaders.util.Format;
import br.grupointegrado.ads.spaceInvaders.util.Preferencias;

/**
 * Created by Douglas on 21/07/2015.
 */
public class PlayScreen extends BaseScreen {

    private BitmapFont font;
    private SpriteBatch batch;
    private OrthographicCamera camera; // camera para criar a sensação de 2D
    private Stage cenario; // Stage (Palco) é o cenário do jogo
    private Stage informacoes; // Stage para imprimir informações na tela
    private Image jogador; // Image é uma implementação de Actor, que são os atores do jogo.
    private Label lbPontuacao;
    private Label lbBonus;
    private Label lbGameOver;
    private Label lbMaiorPontuacao;
    private Label lbPausado;
    private Texture jogadorTextura;
    private Texture jogadorTexturaEsquerda;
    private Texture jogadorTexturaDireita;
    private Texture asteroideTextura1;
    private Texture asteroideTextura2;
    private Texture tiroTextura;
    private Texture bonusTextura;
    private Array<Texture> explosoesTexturas = new Array<Texture>();
    private Array<Image> tiros = new Array<Image>();
    private Array<Image> asteroides = new Array<Image>();
    private Array<Explosao> explosoes = new Array<Explosao>();
    private Array<Image> bonuses = new Array<Image>();
    private Music musicaFundo;
    private Music musicaBonus;
    private Sound somExplosao;
    private Sound somGameover;
    private Sound somTiro;
    private Music somBobus;
    private float velocidadeJogador = 200;
    private float velocidadeTiro = 250;
    private float velocidadeBonus = 100;
    private float velocidadeAsteroide1 = 100;
    private float velocidadeAsteroide2 = 150;
    private int maxAsteroides = 10;
    private int intervaloTiros = 350;
    private boolean indoEsquerda = false;
    private boolean indoDireita = false;
    private boolean atirando = false;
    private boolean gameOver = false;
    private boolean pausado = false;
    private int pontuacao = 0;
    private float TEMPO_POR_BONUS = 10;
    private float tempoBonus = 0;

    public PlayScreen(MainGame game) {
        super(game);
    }

    /**
     * Chamado quando a Screen é exbida a primeira vez
     */
    @Override
    public void show() {
        camera = new OrthographicCamera();
        batch = new SpriteBatch();
        cenario = new Stage(new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera));
        informacoes = new Stage(new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera));

        initTexturas();
        initPlayer();
        initInformacoes();
        initSons();
    }

    private void initTexturas() {
        tiroTextura = new Texture("sprites/shot.png");
        asteroideTextura1 = new Texture("sprites/enemie-1.png");
        asteroideTextura2 = new Texture("sprites/enemie-2.png");
        bonusTextura = new Texture("sprites/bonus.png");
        for (int i = 1; i <= 17; i++) {
            Texture explosao = new Texture("sprites/explosion-" + i + ".png");
            explosoesTexturas.add(explosao);
        }
    }

    private void initSons() {
        musicaFundo = Gdx.audio.newMusic(Gdx.files.internal("sounds/background.mp3"));
        musicaFundo.setLooping(true);
        musicaFundo.play();
        musicaBonus = Gdx.audio.newMusic(Gdx.files.internal("sounds/bonus-background.mp3"));
        musicaBonus.setLooping(true);
        somExplosao = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion.mp3"));
        somGameover = Gdx.audio.newSound(Gdx.files.internal("sounds/gameover.mp3"));
        somTiro = Gdx.audio.newSound(Gdx.files.internal("sounds/shoot.mp3"));
        somBobus = Gdx.audio.newMusic(Gdx.files.internal("sounds/bonus.mp3"));
        somBobus.setOnCompletionListener(new Music.OnCompletionListener() {
            @Override
            public void onCompletion(Music music) {
                musicaFundo.stop();
                musicaBonus.play();
            }
        });
    }

    private void initInformacoes() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/roboto.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.size = MathUtils.roundPositive(32 * Gdx.graphics.getDensity());
        params.shadowOffsetX = 2;
        params.shadowOffsetY = 2;
        font = generator.generateFont(params);
        generator.dispose();

        // cria um estilo para o Label
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = font;

        lbPontuacao = new Label("0 pontos", style);
        informacoes.addActor(lbPontuacao);

        lbGameOver = new Label("Game Over!", style);
        lbGameOver.setVisible(false);
        informacoes.addActor(lbGameOver);

        lbPausado = new Label("Em pausa!", style);
        lbPausado.setVisible(true);
        informacoes.addActor(lbPausado);

        lbMaiorPontuacao = new Label("Maior pontuacao: 0", style);
        lbMaiorPontuacao.setVisible(false);
        informacoes.addActor(lbMaiorPontuacao);

        lbBonus = new Label("Tempo bonus: 0", style);
        lbBonus.setVisible(false);
        informacoes.addActor(lbBonus);
    }

    private void initPlayer() {
        jogadorTextura = new Texture("sprites/player.png");
        jogadorTexturaEsquerda = new Texture("sprites/player-left.png");
        jogadorTexturaDireita = new Texture("sprites/player-right.png");
        jogador = new Image(jogadorTextura);
        jogador.setPosition((camera.viewportWidth / 2) - jogador.getWidth() / 2, 10);
        cenario.addActor(jogador);
    }

    /**
     * Chamado a todo Quadro (Frame)
     *
     * @param delta
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.15f, .15f, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        capturarTeclas(delta);
        if (!gameOver) {
            if (!pausado) {
                atualizarSons();
                capturarTeclasJogo(delta);
                atualizarJogador(delta);
                atualizarAsteroides(delta);
                atualizarTiros(delta);
                atualizarBonus(delta);
                detectarColisoes(delta);
            }
        }
        atualizarExplosoes(delta);
        atualizarInformacoes(delta);

        // desenha o cenário na tela
        cenario.act(delta);
        cenario.draw();

        // desenha as explosoes
        batch.begin();
        batch.setProjectionMatrix(camera.combined);
        for (Explosao exp : explosoes) {
            int i = exp.getEstagio() - 1;
            batch.draw(explosoesTexturas.get(i), exp.getX(), exp.getY());
        }
        batch.end();

        // desenha as informações na tela
        informacoes.act(delta);
        informacoes.draw();
    }

    private void atualizarSons() {
        if (tempoBonus == 0) {
            if (musicaBonus.isPlaying()) {
                musicaBonus.stop();
            }
            if (!musicaFundo.isPlaying()) {
                musicaFundo.play();
            }
        }
    }

    private int bonusMeta = 100;
    private int bonusLevel = 0;

    private void atualizarBonus(float delta) {
        // atualiza o tempo de bonus
        tempoBonus -= delta;
        if (tempoBonus < 0)
            tempoBonus = 0;

        for (Image bonus : bonuses) {
            // movimenta o tiro em direção ao topo da tela
            float x = bonus.getX();
            float y = bonus.getY() - velocidadeBonus * delta;
            bonus.setPosition(x, y);
            // verifica se o tiro já saiu da tela
            if (bonus.getY() + bonus.getHeight() < 0) {
                bonus.remove();
                bonuses.removeValue(bonus, true);
            }
        }
        // calcula o level atual da pontuação do usuário
        int bonusLevel = (int) ((float) pontuacao / (float) bonusMeta);
        // verifica se o level atual é maior que o último level atingido
        if (bonusLevel > this.bonusLevel) {
            this.bonusLevel = bonusLevel;
            Image bonus = new Image(bonusTextura);
            setPosicaoAleatoria(bonus);
            cenario.addActor(bonus);
            bonuses.add(bonus);
        }
    }

    private void setPosicaoAleatoria(Actor actor) {
        float y = MathUtils.random(camera.viewportHeight, camera.viewportHeight * 2);
        float x = MathUtils.random(0, camera.viewportWidth - actor.getWidth());
        actor.setPosition(x, y);
    }

    private void capturarTeclas(float delta) {
        // verifica se o botão PAUSE foi pressionado
        if (!gameOver && Gdx.input.isKeyJustPressed(Input.Keys.CONTROL_LEFT)) {
            pausado = !pausado;
        }
        // verifica se o ENTER foi pressionado para reiniciar o Jogo
        if (gameOver && (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.justTouched())) {
            reiniciarJogo();
        }
    }

    private void reiniciarJogo() {
        game.setScreen(new MenuScreen(game));
    }

    /**
     * Ataliza informações escritas na tela
     *
     * @param delta
     */
    private void atualizarInformacoes(float delta) {
        lbPontuacao.setText(Format.format(pontuacao) + " pontos");
        lbPontuacao.setPosition(10, camera.viewportHeight - lbPontuacao.getPrefHeight() - 10);

        lbGameOver.setVisible(gameOver);
        lbGameOver.setPosition(camera.viewportWidth / 2 - lbGameOver.getWidth() / 2, camera.viewportHeight / 2 - lbGameOver.getHeight() / 2);

        lbPausado.setVisible(pausado);
        lbPausado.setPosition(camera.viewportWidth / 2 - lbPausado.getWidth() / 2, camera.viewportHeight / 2 - lbPausado.getHeight() / 2);

        lbMaiorPontuacao.setText("Maior pontuação: " + Format.format(Preferencias.getMaiorPontuacao()));
        lbMaiorPontuacao.setVisible(gameOver);
        lbMaiorPontuacao.setPosition(camera.viewportWidth / 2 - lbMaiorPontuacao.getPrefWidth() / 2, lbGameOver.getY() - 100);

        lbBonus.setVisible(tempoBonus > 0);
        lbBonus.setText("Tempo bonus: " + (int) tempoBonus);
        lbBonus.setPosition(camera.viewportWidth - lbBonus.getPrefWidth() - 10, lbPontuacao.getY());
    }

    private void atualizarExplosoes(float delta) {
        for (Explosao exp : explosoes) {
            exp.atualizar(delta);
            // verifica se a explosão já passou todos os estágios
            if (exp.getEstagio() > 17) {
                explosoes.removeValue(exp, true);
            }
        }
    }

    private void atualizarJogador(float delta) {
        if (indoDireita) {
            // impede que o jogador saia da tela
            if (jogador.getX() + jogador.getWidth() < camera.viewportWidth) {
                jogador.setX(jogador.getX() + (velocidadeJogador * delta));
            }
            jogador.setDrawable(new SpriteDrawable(new Sprite(jogadorTexturaDireita)));
        } else if (indoEsquerda) {
            // impede que o jogador saia da tela
            if (jogador.getX() > 0) {
                jogador.setX(jogador.getX() - (velocidadeJogador * delta));
            }
            jogador.setDrawable(new SpriteDrawable(new Sprite(jogadorTexturaEsquerda)));
        } else {
            jogador.setDrawable(new SpriteDrawable(new Sprite(jogadorTextura)));
        }
        // atualiza o bonus do jogador
        if (tempoBonus == 0 && jogador.hasActions()) {
            jogador.clearActions();
            jogador.addAction(Actions.alpha(1));
        }
        if (tempoBonus > 0 && !jogador.hasActions()) {
            jogador.addAction(Actions.forever(Actions.sequence(Actions.fadeOut(0.2f), Actions.fadeIn(0.2f))));
        }
    }

    private void detectarColisoes(float delta) {
        Rectangle boundsAsteroid = new Rectangle();
        Rectangle boundsShoot = new Rectangle();
        Rectangle boundsBonus = new Rectangle();
        Rectangle boundsJogador = new Rectangle(jogador.getX(), jogador.getY(), jogador.getWidth(), jogador.getHeight());
        for (Image asteroid : asteroides) {
            boundsAsteroid.set(asteroid.getX(), asteroid.getY(), asteroid.getWidth(), asteroid.getHeight());
            if (boundsAsteroid.overlaps(boundsJogador)) {
                asteroid.remove();
                asteroides.removeValue(asteroid, true);
                if (tempoBonus == 0) {
                    criarExplosao(jogador.getX() + jogador.getWidth() / 2, jogador.getY() + jogador.getHeight() / 2);
                    jogador.setVisible(false);
                    somGameover.play();
                    musicaFundo.stop();
                    musicaBonus.stop();
                    gameOver();
                    return;
                } else {
                    criarExplosao(asteroid.getX() + asteroid.getWidth() / 2, asteroid.getY() + asteroid.getHeight() / 2);
                    incrementaPontuacao(asteroid);
                    somExplosao.play();
                }
            }
            for (Image tiro : tiros) {
                boundsShoot.set(tiro.getX(), tiro.getY(), tiro.getWidth(), tiro.getHeight());
                // verifica colisão
                if (boundsAsteroid.overlaps(boundsShoot)) {
                    asteroid.remove();
                    asteroides.removeValue(asteroid, true);
                    tiro.remove();
                    tiros.removeValue(tiro, true);
                    criarExplosao(tiro.getX(), tiro.getY());
                    incrementaPontuacao(asteroid);
                    somExplosao.play();
                }
            }
        }
        for (Image bonus : bonuses) {
            boundsBonus.set(bonus.getX(), bonus.getY(), bonus.getWidth(), bonus.getHeight());
            if (boundsBonus.overlaps(boundsJogador)) {
                bonus.remove();
                bonuses.removeValue(bonus, true);
                tempoBonus += TEMPO_POR_BONUS;
                somBobus.stop();
                somBobus.play();
                musicaBonus.pause();
                musicaFundo.stop();
            }
        }
    }

    private void gameOver() {
        gameOver = true;
        if (pontuacao > Preferencias.getMaiorPontuacao()) {
            Preferencias.setMaiorPontuacao(pontuacao);
        }
    }

    private void incrementaPontuacao(Image asteroid) {
        // verifica o tipo do asteroide para incrementar a pontuação
        if ("1".equals(asteroid.getName())) {
            pontuacao += 5;
        } else {
            pontuacao += 10;
        }
    }

    private void criarExplosao(float x, float y) {
        // a imagem da explosão mede 96 por 96 pixels
        Explosao explosao = new Explosao();
        explosao.setX(x - 96 / 2);
        explosao.setY(y - 96 / 2);
        explosoes.add(explosao);
    }

    private void atualizarAsteroides(float delta) {
        for (Image asteroid : asteroides) {
            float velocidade;
            // verifica o tipo do asteroid para decidir a velocidade
            if ("1".equals(asteroid.getName())) {
                velocidade = velocidadeAsteroide1;
            } else {
                velocidade = velocidadeAsteroide2;
            }
            // movimenta o asteroid em direção ao jogador
            float x = asteroid.getX();
            float y = asteroid.getY() - velocidade * delta;
            asteroid.setPosition(x, y);
            // remove o asteroid que já saiu da tela
            if (asteroid.getY() + asteroid.getHeight() < 0) {
                asteroid.remove();
                asteroides.removeValue(asteroid, true);
                decrementaPontuacao(asteroid);
            }
        }
        // cria novos asteroides se necessário
        if (asteroides.size < maxAsteroides) {
            Image asteroid;
            // decide aleatoriamente entre criar asteroide tipo 1 ou 2
            int tipo = MathUtils.random(1, 3);
            if (tipo == 1) {
                asteroid = new Image(asteroideTextura1);
                asteroid.setName("1");
            } else {
                asteroid = new Image(asteroideTextura2);
                asteroid.setName("2");
            }
            // configura posições aleatórias para os asteroides
            setPosicaoAleatoria(asteroid);
            asteroides.add(asteroid);
            cenario.addActor(asteroid);
        }
    }


    private void decrementaPontuacao(Image asteroid) {
        // verifica o tipo do asteroide para decrementar a pontuação
        if ("1".equals(asteroid.getName())) {
            pontuacao -= 15;
        } else {
            pontuacao -= 30;
        }
    }

    private void atualizarTiros(float delta) {
        for (Image tiro : tiros) {
            // movimenta o tiro em direção ao topo da tela
            float x = tiro.getX();
            float y = tiro.getY() + velocidadeTiro * delta;
            tiro.setPosition(x, y);
            // verifica se o tiro já saiu da tela
            if (tiro.getY() > camera.viewportHeight) {
                tiro.remove();
                tiros.removeValue(tiro, true);
            }
        }
        // cria novos tiros se necessário
        if (atirando) {
            // verifica se o último tiro foi disparado a 400 milisegundos atrás
            if (System.currentTimeMillis() - ultimoTiro >= intervaloTiros) {
                Image tiro = new Image(tiroTextura);
                float x = jogador.getX() + jogador.getWidth() / 2 - tiro.getWidth() / 2;
                float y = jogador.getY() + jogador.getHeight();
                tiro.setPosition(x, y);
                tiros.add(tiro);
                cenario.addActor(tiro);
                ultimoTiro = System.currentTimeMillis();
                somTiro.play();
            }
        }
    }

    private void capturarTeclasJogo(float delta) {
        indoDireita = false;
        indoEsquerda = false;
        atirando = false;
        // verifica se a seta para esquerda está pressionada
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || toqueEsquerda()) {
            indoEsquerda = true;
        }
        // verifica se a seta para a direita está pressionada
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || toqueDireita()) {
            indoDireita = true;
        }
        // verifica se o espaço está pressionado
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.app.getType() == Application.ApplicationType.Android) {
            atirando = true;
        }
    }

    private boolean toqueDireita() {
        return Gdx.input.isTouched() && Gdx.input.getX() > camera.viewportWidth / 2;
    }

    private boolean toqueEsquerda() {
        return Gdx.input.isTouched() && Gdx.input.getX() < camera.viewportWidth / 2;
    }

    private long ultimoTiro = 0;

    /**
     * Chamado sempre que a Screen muda de tamanho (rotação da tela)
     *
     * @param width
     * @param height
     */
    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        camera.update();
    }

    /**
     * Chamado quando a Screen é minimizado, ou levado para segundo plano
     */
    @Override
    public void pause() {

    }

    /**
     * Chamado quando a Screen é restaurada ou retorna para o primeiro plano
     */
    @Override
    public void resume() {

    }

    /**
     * Chamado quando a Screen é fechada (destuida)
     */
    @Override
    public void dispose() {
        cenario.dispose();
        jogadorTextura.dispose();
        jogadorTexturaDireita.dispose();
        jogadorTexturaEsquerda.dispose();
        tiroTextura.dispose();
        asteroideTextura1.dispose();
        asteroideTextura2.dispose();
        bonusTextura.dispose();
        for (Texture t : explosoesTexturas) {
            t.dispose();
        }
        informacoes.dispose();
        musicaFundo.dispose();
        musicaBonus.dispose();
        somExplosao.dispose();
        somGameover.dispose();
        somTiro.dispose();
        somBobus.dispose();
    }
}
