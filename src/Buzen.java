import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

import static java.lang.Math.pow;

public class Buzen {
    GordonNewell gordonNewell = new GordonNewell();
    int br = 20;
    double[] vratiS(int k){
        double s[] = new double[4+k];
        s[0]= 0.005;
        s[1]= 0.01;
        s[2]= 0.015;
        s[3]= s[2];
        for(int i = 4 ; i < 4+k ; i++){
            s[i]=0.025;
        }
        return s;
    }

    double[][] racunanjeMatriceBuzen(int k) {
        double[] niz=gordonNewell.racunanjePotraznje(k);
        int n = 5+k;
        double [][] matrica = new double[br+1][n];
        for(int j = 1 ; j < n ; j++){
            matrica[0][j] = 1;
        }
        for(int j = 1; j< n ; j++){
            for(int i = 1; i<=br ; i++){
                matrica[i][j]= matrica[i][j-1] + niz[j-1]*matrica[i-1][j];
            }
        }
        return matrica;
    }

    void upisUtxtBuzen(){
        GordonNewell gordonNewell = new GordonNewell();
        DecimalFormat df = new DecimalFormat("0.0000");
        File rezultati = new File("resenje\\rezultati_analiticki.txt");
        try {
            FileWriter myWriter = new FileWriter(rezultati);
            myWriter.write("****Rezultati****\n");
            for(int n = 10 ; n <=20 ; n=n+5){
                myWriter.write("***N=" + n +"***\n");
                for(int i = 2 ; i <= 10 ; i++){
                    double mat[][] = racunanjeMatriceBuzen(i);
                    double buzen[]=new double[br+1];
                    double s[] = vratiS(i);
                    for(int j = 0 ; j <=br ; j++){
                        buzen[j]=mat[j][i+4];
                    }
                    double x[] = gordonNewell.racunanjePotraznje(i);
                    double kritRes = 0;
                    for(int q = 0 ; q<x.length ; q++){
                        if (x[q] > kritRes){
                            kritRes = x[q];
                        }
                    }
                    myWriter.write("**Za vrednost K = "+i+" rezultati za resurse su :**\n");
                    double r =0;
                    for(int j = 0 ; j< x.length; j++){
                        myWriter.write("Resurs "+(j+1)+":\n");
                        double u =x[j]*buzen[n-1]/buzen[n];
                        myWriter.write("Iskoriscenje resursa U"+(j+1)+" = "+df.format(u*100)+"%  \n");
                        myWriter.write("Protok kroz resurse X"+(j+1)+" = "+df.format(u/s[j])+" posla/s \n");
                        if(j == 0){
                            r = n/((u/s[j])*0.2)*1000;
                        }
                        double prosecanBr =0;
                        for(int w = 1; w<n ; w++){
                            prosecanBr+= pow(x[j],w)*buzen[n-w]/buzen[n];
                        }
                        myWriter.write("Prosecan broj poslova n"+(j+1)+" = "+df.format(prosecanBr)+" posla \n");
                        if(x[j] == kritRes){
                            myWriter.write("OVAJ RESURS JE KRITICAN!\n");
                        }
                        myWriter.write("\n");
                    }
                    myWriter.write("Vreme odziva sistema je : "+df.format(r)+" ms\n");
                    myWriter.write("*****\n");
                }
                myWriter.write("***** *****\n");
            }
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    void upisUtxtBuzenexcel(){
        GordonNewell gordonNewell = new GordonNewell();
        DecimalFormat df = new DecimalFormat("0.0000");
        File rezultati = new File("resenje\\rezultati_analiticki_excel.txt");
        try {
            FileWriter myWriter = new FileWriter(rezultati);
            for(int n = 10 ; n <=20 ; n=n+5){
                myWriter.write("N=" + n +"\n");
                for(int i = 2 ; i <= 10 ; i++){
                    double mat[][] = racunanjeMatriceBuzen(i);
                    double buzen[]=new double[br+1];
                    double s[] = vratiS(i);
                    for(int j = 0 ; j <=br ; j++){
                        buzen[j]=mat[j][i+4];
                    }
                    double x[] = gordonNewell.racunanjePotraznje(i);
                    double kritRes = 0;
                    for(int q = 0 ; q<x.length ; q++){
                        if (x[q] > kritRes){
                            kritRes = x[q];
                        }
                    }
                    myWriter.write("K = "+i+"\n");
                    double r =0;
                    for(int j = 0 ; j< x.length; j++){
                        myWriter.write("Resurs "+(j+1)+":\n");
                        double u =x[j]*buzen[n-1]/buzen[n];
                        myWriter.write("U"+(j+1)+" = "+df.format(u*100)+"\n");
                        myWriter.write("X"+(j+1)+" = "+df.format(u/s[j])+"\n");
                        if(j == 0){
                            r = u/s[j]*0.2;
                        }
                        double prosecanBr =0;
                        for(int w = 1; w<n ; w++){
                            prosecanBr+= pow(x[j],w)*buzen[n-w]/buzen[n];
                        }
                        myWriter.write("N"+(j+1)+" = "+df.format(prosecanBr)+"\n");
                    }
                }
            }
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

}
