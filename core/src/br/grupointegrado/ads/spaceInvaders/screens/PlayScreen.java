package br.grupointegrado.ads.spaceInvaders.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;

import br.grupointegrado.ads.spaceInvaders.model.Explosao;

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
    private Label lbGameOver;
    private Label lbPausado;
    private Texture jogadorTextura;
    private Texture jogadorTexturaEsquerda;
    private Texture jogadorTexturaDireita;
    private Texture asteroideTextura1;
    private Texture asteroideTextura2;
    private Texture tiroTexture;
    private Array<Texture> explosoesTexturas = new Array<Texture>();
    private Array<Image> tiros = new Array<Image>();
    private Array<Image> asteroides = new Array<Image>();
    private Array<Explosao> explosoes = new Array<Explosao>();
    private float velocidadeJogador = 200;
    private float velocidadeTiro = 250;
    private float velocidadeAsteroide1 = 100;
    private float velocidadeAsteroide2 = 150;
    private int maxAsteroides = 10;
    private int intervaloTiros = 400;
    private boolean indoEsquerda = false;
    private boolean indoDireita = false;
    private boolean atirando = false;
    private boolean gameOver = false;
    private boolean pausado = false;
    private int pontuacao = 0;

    /**
     * Chamado quando a Screen é exbida a primeira vez
     */
    @Override
    public void show() {
        camera = new OrthographicCamera();
        batch = new SpriteBatch();
        font = new BitmapFont();
        cenario = new Stage(new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera));
        informacoes = new Stage(new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera));
        tiroTexture = new Texture("sprites/shot.png");
        asteroideTextura1 = new Texture("sprites/enemie-1.png");
        asteroideTextura2 = new Texture("sprites/enemie-2.png");
        initPlayer();
        initExplosoes();
        initInformacoes();
    }

    private void initInformacoes() {
        // cria um estilo para o Label
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = font;

        lbPontuacao = new Label("", style);
        informacoes.addActor(lbPontuacao);

        lbGameOver = new Label("Game Over!", style);
        lbGameOver.setVisible(false);
        informacoes.addActor(lbGameOver);

        lbPausado = new Label("Em pausa!", style);
        lbPausado.setVisible(true);
        informacoes.addActor(lbPausado);
    }

    private void initExplosoes() {
        for (int i = 1; i <= 17; i++) {
            Texture explosao = new Texture("sprites/explosion-" + i + ".png");
            explosoesTexturas.add(explosao);
        }
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
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        capturaTeclas(delta);
        if (!gameOver) {
            if (!pausado) {
                capturaTeclasJogo(delta);
                atualizaJogador(delta);
                atualizaAsteroides(delta);
                atualizarTiros(delta);
                atualizaExplosoes(delta);
                detectarColisoes(delta);
            }
        }
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

    private void capturaTeclas(float delta) {
        // verifica se o botão PAUSE foi pressionado
        if (!gameOver && Gdx.input.isKeyJustPressed(Input.Keys.CONTROL_LEFT)) {
            pausado = !pausado;
        }
        // verifica se o ENTER foi pressionado para reiniciar o Jogo
        if (gameOver && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            pontuacao = 0;
            gameOver = false;
            pausado = false;
            for (Image ast : asteroides) {
                ast.remove();
            }
            asteroides.clear();
            for (Image tiro : tiros) {
                tiro.remove();
            }
            tiros.clear();
            explosoes.clear();
            jogador.setX(camera.viewportWidth / 2 - jogador.getWidth() / 2);
        }
    }

    /**
     * Ataliza informações escritas na tela
     * @param delta
     */
    private void atualizarInformacoes(float delta) {
        lbPontuacao.setText(pontuacao + " pontos");
        lbPontuacao.setPosition(10, camera.viewportHeight - lbPontuacao.getHeight() - 10);

        lbGameOver.setVisible(gameOver);
        lbGameOver.setPosition(camera.viewportWidth / 2 - lbGameOver.getWidth() / 2, camera.viewportHeight / 2 - lbGameOver.getHeight() / 2);

        lbPausado.setVisible(pausado);
        lbPausado.setPosition(camera.viewportWidth / 2 - lbPausado.getWidth() / 2, camera.viewportHeight / 2 - lbPausado.getHeight() / 2);
    }

    private void atualizaExplosoes(float delta) {
        for (Explosao exp : explosoes) {
            exp.atualizar(delta);
            // verifica se a explosão já passou todos os estágios
            if (exp.getEstagio() > 17) {
                explosoes.removeValue(exp, true);
            }
        }
    }

    private void atualizaJogador(float delta) {
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
    }

    private void detectarColisoes(float delta) {
        Rectangle boundsAsteroid = new Rectangle();
        Rectangle boundsShoot = new Rectangle();
        Rectangle boundsJogador = new Rectangle(jogador.getX(), jogador.getY(), jogador.getWidth(), jogador.getHeight());
        for (Image asteroid : asteroides) {
            boundsAsteroid.set(asteroid.getX(), asteroid.getY(), asteroid.getWidth(), asteroid.getHeight());
            if (boundsAsteroid.overlaps(boundsJogador)) {
                gameOver = true;
                return;
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
                }
            }
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

    private void atualizaAsteroides(float delta) {
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
            float x = MathUtils.random(0, camera.viewportWidth - asteroid.getWidth());
            float y = MathUtils.random(camera.viewportHeight, camera.viewportHeight * 2);
            asteroid.setPosition(x, y);
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
                Image tiro = new Image(tiroTexture);
                float x = jogador.getX() + jogador.getWidth() / 2 - tiro.getWidth() / 2;
                float y = jogador.getY() + jogador.getHeight();
                tiro.setPosition(x, y);
                tiros.add(tiro);
                cenario.addActor(tiro);
                ultimoTiro = System.currentTimeMillis();
            }
        }
    }

    private void capturaTeclasJogo(float delta) {
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
        tiroTexture.dispose();
        for (Texture t : explosoesTexturas) {
            t.dispose();
        }
        informacoes.dispose();
    }
}
