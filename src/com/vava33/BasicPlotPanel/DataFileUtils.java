package com.vava33.BasicPlotPanel;

/**
 * Operations with Data Files
 * 
 * @author Oriol Vallcorba
 * Licence: GPLv3
 * 
 */

import java.io.File;
import java.util.Scanner;

import com.vava33.jutils.FileUtils;
import com.vava33.ovPlot.SerieType;


public final class DataFileUtils {

    public static BasicSerie readDAT(File datFile) { 
        boolean firstLine = true;
        boolean readed = true;
        
        //creem un DataSerie_Pattern
        BasicSerie dsP = new BasicSerie(datFile.getName(),SerieType.dat);
        
        //FIRST CHECK ENCODING
        String enc = FileUtils.getEncodingToUse(datFile);
        Scanner sf = null;

        try {
            sf = new Scanner(datFile,enc);
            while (sf.hasNextLine()){
                String line = sf.nextLine();
                if (firstLine) {
                    firstLine=false;
                    if (!line.isEmpty()) {
                        if ((int)line.charAt(0)==65279) { //BOM character
                            line = line.substring(1);
                        }
                    }
                }
                if (isComment(line.trim())){
                    continue;
                }
                if (line.trim().isEmpty()){
                    continue;
                }

                String values[] = line.trim().split("\\s+");

                //afegim comprovacio de que la primera linea pot ser no comentada (cas xye), TODO:revisar
                double t2 = 0.0;
                double inten = 0.0;
                try {
                    t2 = Double.parseDouble(values[0]);
                    inten = Double.parseDouble(values[1]);
                }catch(Exception ex) {
                    System.out.println(String.format("Error reading (t2 Intensity) in line: %s",line));
                    continue;
                }
                double sdev = 0.0;
                try{
                    sdev = Double.parseDouble(values[2]);
                }catch(Exception ex){
//                    log.fine("error parsing sdev");
                }
                dsP.addPoint(new BasicPoint(t2,inten,0.0,sdev,dsP));

            }
            if (dsP.getNPoints()<=0)return null;
            dsP.setName(datFile.getName());

        }catch(Exception e){
            e.printStackTrace();
            readed = false;
        }finally {
            if(sf!=null)sf.close();
        }
        if (readed){
            return dsP;
        }else{
            return null;
        }
    }

    private static boolean isComment(String ln){
        if (ln.trim().startsWith("#"))return true;
        if (ln.trim().startsWith("!"))return true;
        if (ln.trim().startsWith("/"))return true;
        if (ln.trim().startsWith("$"))return true;
        return false;
    }
}
