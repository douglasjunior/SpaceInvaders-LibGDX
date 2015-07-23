package br.grupointegrado.ads.spaceInvaders.model;

/**
 * Created by Douglas on 23/07/2015.
 */
public class Explosao {

    private float acumulado = 0;
    private float intervalo = 1f / 17f;
    private int estagio = 1;
    private float x;
    private float y;

    public void atualizar(float delta) {
        // acumula o tempo decorrido entre um quadro e outro
        acumulado += delta;
        // verifica se o acumulado é maior ou igual ao intervalo configurado entre as trocas
        if (acumulado >= intervalo) {
            acumulado = 0;
            estagio = estagio + 1;
        }
    }

    public int getEstagio() {
        return estagio;
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
