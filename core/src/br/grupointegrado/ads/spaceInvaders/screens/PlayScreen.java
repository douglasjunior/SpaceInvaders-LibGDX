package br.grupointegrado.ads.spaceInvaders.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;

import java.util.List;

/**
 * Created by Douglas on 21/07/2015.
 */
public class PlayScreen extends BaseScreen {


    private OrthographicCamera camera; // camera para criar a sensação de 2D
    private Stage cenario; // Stage (Palco) é o cenário do jogo
    private Image player; // Image é uma implementação de Actor, que são os atores do jogo.
    private Texture playerTextura;
    private Texture playerTexturaEsquerda;
    private Texture playerTexturaDireita;
    private Texture asteroidTextura1;
    private Texture asteroidTextura2;
    private Array<Image> shoots = new Array<Image>();
    private Array<Image> asteroids = new Array<Image>();
    private Texture shootTexture;
    private float velocidadePlayer = 200;
    private float velocidadeShoot = 250;
    private float velocidadeAsteroid1 = 100;
    private float velocidadeAsteroid2 = 150;
    private int maxAsteroids = 5;
    private boolean indoEsquerda = false;
    private boolean indoDireita = false;

    /**
     * Chamado quando a Screen é exbida a primeira vez
     */
    @Override
    public void show() {
        camera = new OrthographicCamera();
        cenario = new Stage(new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera));
        shootTexture = new Texture("sprites/shot.png");
        asteroidTextura1 = new Texture("sprites/enemie-1.png");
        asteroidTextura2 = new Texture("sprites/enemie-2.png");
        initPlayer();
    }

    private void initPlayer() {
        playerTextura = new Texture("sprites/player.png");
        playerTexturaEsquerda = new Texture("sprites/player-left.png");
        playerTexturaDireita = new Texture("sprites/player-right.png");
        player = new Image(playerTextura);
        player.setPosition((camera.viewportWidth / 2) - player.getWidth() / 2, 10);
        cenario.addActor(player);
    }

    /**
     * Chamado a todo Quadro (Frame)
     *
     * @param delta
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(.15f, .15f, .25f, 1);

        capturaTeclas(delta);
        atualizaAsteroides(delta);
        atualizarTiros(delta);
        detectarColisoes(delta);

        if (indoDireita) {
            player.setDrawable(new SpriteDrawable(new Sprite(playerTexturaDireita)));
        } else if (indoEsquerda) {
            player.setDrawable(new SpriteDrawable(new Sprite(playerTexturaEsquerda)));
        } else {
            player.setDrawable(new SpriteDrawable(new Sprite(playerTextura)));
        }

        cenario.act(delta);
        cenario.draw();
    }

    private void detectarColisoes(float delta) {
        Rectangle boundsAsteroid = new Rectangle();
        Rectangle boundsShoot = new Rectangle();
        for (Image asteroid : asteroids) {
            boundsAsteroid.set(asteroid.getX(), asteroid.getY(), asteroid.getWidth(), asteroid.getHeight());
            for (Image shoot : shoots) {
                boundsShoot.set(shoot.getX(), shoot.getY(), shoot.getWidth(), shoot.getHeight());
                // verifica colisão
                if (boundsAsteroid.overlaps(boundsShoot)) {
                    asteroid.remove();
                    asteroids.removeValue(asteroid, true);
                    shoot.remove();
                    shoots.removeValue(shoot, true);
                }
            }

        }
    }

    private void atualizaAsteroides(float delta) {
        for (Image asteroid : asteroids) {
            // verifica o nome do asteroid para decidir a velocidade
            float velocidade;
            if ("1".equals(asteroid.getName())) {
                velocidade = velocidadeAsteroid1;
            } else {
                velocidade = velocidadeAsteroid2;
            }
            // movimenta o asteroid em direção ao player
            float x = asteroid.getX();
            float y = asteroid.getY() - velocidade * delta;
            asteroid.setPosition(x, y);
            // remove o asteroid que já saiu da tela
            if (asteroid.getY() + asteroid.getHeight() < 0) {
                asteroid.remove();
                asteroids.removeValue(asteroid, true);
            }
        }
        // cria novos asteroids
        if (asteroids.size < maxAsteroids) {
            Image asteroid;
            if (MathUtils.random(1, 3) == 1) {
                asteroid = new Image(asteroidTextura1);
                asteroid.setName("1");
            } else {
                asteroid = new Image(asteroidTextura2);
                asteroid.setName("2");
            }
            //configura posições aleatórias para os asteroids
            asteroid.setX(MathUtils.random(0, camera.viewportWidth - asteroid.getWidth()));
            asteroid.setY(MathUtils.random(camera.viewportHeight, camera.viewportHeight * 2));
            asteroids.add(asteroid);
            cenario.addActor(asteroid);
        }
    }

    private void atualizarTiros(float delta) {
        for (Image shoot : shoots) {
            // movimenta o tiro em direção ao topo da tela
            float x = shoot.getX();
            float y = shoot.getY() + velocidadeShoot * delta;
            shoot.setPosition(x, y);
            // verifica se o tiro já saiu da tela
            if (shoot.getY() > camera.viewportHeight) {
                shoot.remove();
                shoots.removeValue(shoot, true);
            }
        }
    }

    private void capturaTeclas(float delta) {
        indoDireita = false;
        indoEsquerda = false;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            if (player.getX() > 0) {
                player.setX(player.getX() - (velocidadePlayer * delta));
            }
            indoEsquerda = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if (player.getX() + player.getWidth() < camera.viewportWidth) {
                player.setX(player.getX() + (velocidadePlayer * delta));
            }
            indoDireita = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            atirar();
        }
    }

    private long ultimoTiro = 0;

    private void atirar() {
        if (System.currentTimeMillis() - ultimoTiro >= 400) {
            Image shoot = new Image(shootTexture);
            float x = player.getX() + player.getWidth() / 2 - shoot.getWidth() / 2;
            float y = player.getY() + player.getHeight();
            shoot.setPosition(x, y);
            shoots.add(shoot);
            cenario.addActor(shoot);
            ultimoTiro = System.currentTimeMillis();
        }
    }

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
        playerTextura.dispose();
        playerTexturaDireita.dispose();
        playerTexturaEsquerda.dispose();
        shootTexture.dispose();
    }
}
