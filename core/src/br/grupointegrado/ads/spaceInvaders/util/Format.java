package br.grupointegrado.ads.spaceInvaders.util;

import java.text.DecimalFormat;

/**
 * Created by Douglas on 30/07/2015.
 */
public class Format {

    private static DecimalFormat format = new DecimalFormat("###,###,###,##0");

    /**
     * Recebe um número Inteiro e converte para String adicionado formatação.
     * @param number
     * @return
     */
    public static String format(int number){
        return format.format(number);
    }
}
