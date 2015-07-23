package br.grupointegrado.ads.spaceInvaders.model;

/**
 * Created by Douglas on 23/07/2015.
 */
public class Explosao {

    private int estagio = 1;
    private float x;
    private float y;

    public void atualizar() {
        estagio = estagio + 1;
    }

    public int getEstagio() {
        return estagio;
    }

    public void setEstagio(int estagio) {
        this.estagio = estagio;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
