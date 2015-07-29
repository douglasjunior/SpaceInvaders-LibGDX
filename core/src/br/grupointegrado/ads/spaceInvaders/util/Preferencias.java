package br.grupointegrado.ads.spaceInvaders.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Created by Douglas on 28/07/2015.
 */
public class Preferencias {

    private static Preferences prefs;

    public static int getMaiorPontuacao(){
        return getPrefs().getInteger("maior_pontuacao");
    }

    public static void setMaiorPontuacao(int pontuacao){
        getPrefs().putInteger("maior_pontuacao", pontuacao);
        getPrefs().flush();
    }

    private static Preferences getPrefs() {
        if (prefs == null) {
            prefs = Gdx.app.getPreferences("SpaceInvaders");
        }
        return prefs;
    }
}
