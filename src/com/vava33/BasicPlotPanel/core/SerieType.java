package com.vava33.BasicPlotPanel.core;

/**
 *
 * @author Oriol Vallcorba
 * Licence: GPLv3
 * 
 */
public enum SerieType {

    dat,
    peaks,
    bkg, 
    bkgEstimP, 
    obs,
    cal,
    diff,
    hkl,
    gr, 
    ref;
    
    public static SerieType getEnum(String n) {
        for (SerieType x: SerieType.values()) {
            if (n.equalsIgnoreCase(x.toString()))return x;
            if (n.equalsIgnoreCase(x.name()))return x;
        }
        return null;
    }
    
    
    public static String getStringAllNames(String separator) {
        StringBuilder sb = new StringBuilder();
        for (SerieType st:SerieType.values()) {
            sb.append(st.name()+separator);
        }
        return sb.toString().substring(0, sb.length()-2);
    }
    
}