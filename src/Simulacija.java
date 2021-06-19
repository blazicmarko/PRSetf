import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.Random;

import static java.lang.Math.pow;

public class Simulacija {

    class Promena{
        int id;
        int endTime;
        int startTime;

        public int getStartTime() {
            return startTime;
        }

        public int getId() {
            return id;
        }

        public int getEndTime() {
            return endTime;
        }

        Promena(int i, int t,int st){
            this.id = i;
            this.endTime = t;
            this.startTime =st;
        }
    }

    class Resurs{
        int brPoslova;
        double vremeRada;
        boolean radi;
        double prosecanBrP;

        public Resurs(int brPoslova, int vremeRada, boolean radi, double pBp) {
            this.brPoslova = brPoslova;
            this.vremeRada = vremeRada;
            this.radi = radi;
            this.prosecanBrP =pBp;
        }

        public int getBrPoslova() {
            return brPoslova;
        }

        public void setBrPoslova(int brPoslova) {
            this.brPoslova = brPoslova;
        }

        public double getVremeRada() {
            return vremeRada;
        }

        public void setVremeRada(double vremeRada) {
            this.vremeRada = vremeRada;
        }

        public boolean isRadi() {
            return radi;
        }

        public void setRadi(boolean radi) {
            this.radi = radi;
        }

        public double getProsecanBrP() {
            return prosecanBrP;
        }

        public void setProsecanBrP(double prosecanBrP) {
            this.prosecanBrP = prosecanBrP;
        }
    }

    void addInList(Promena p, LinkedList<Promena> list){
        int index=0;
        for(int i = 0 ; i < list.size() ; i++){
            if(p.getEndTime() <= list.get(i).getEndTime()) {
                index = i;
                list.add(index,p);
                return;
            }
            index = i+1;
        }
        list.add(index,p);
    }

    Resurs[] pokreniSimulaciju(int k, int n, int vreme){
        Random r = new Random();
        GordonNewell gordonNewell = new GordonNewell();
        Buzen buzen = new Buzen();
        double[] svr= buzen.vratiS(k);
        double[][] matricaVerovatnoca = gordonNewell.MatricaPbezI(k);
        int [] redovi = new int[4+k];

        for(int i = 0 ; i < n ; i++){
            int s=i%(4+k);
            redovi[s]++;
        }
        Resurs[] resursi = new Resurs[4+k];
        for(int i = 0 ; i<resursi.length; i++){
            resursi[i]= new Resurs(0,0,false,0);
        }
        LinkedList<Promena> vremenaPromeneStanja = new LinkedList<>();
        for(int i = 0 ; i< redovi.length; i++){
            if(redovi[i] >0) {
                double u = r.nextDouble();
                int x = (int)((-svr[i]*1000) * Math.log(1-u));
                if(x == 0){
                    x=1;
                }
                addInList(new Promena(i, x, 0),vremenaPromeneStanja);
                redovi[i]--;
                resursi[i].setRadi(true);
            }
        }
        int stariBrojac = 1;
        for(int brojac = 1; brojac < vreme*60*1000 ; brojac=vremenaPromeneStanja.getFirst().getEndTime()){
                    int id =vremenaPromeneStanja.getFirst().getId();
                    resursi[id].brPoslova++;
                    resursi[id].vremeRada+= vremenaPromeneStanja.getFirst().getEndTime()-vremenaPromeneStanja.getFirst().getStartTime();
                    int s =0;
                    double rand = r.nextDouble();
                    double old = 0;
                    for(int i = 0 ; i < matricaVerovatnoca[id].length ; i++){
                        s=i;
                        if(rand<old+matricaVerovatnoca[id][i]){
                            break;
                        }
                        else{
                            old+=matricaVerovatnoca[id][i];
                        }
                    }

                    if(resursi[s].isRadi()) {
                        redovi[s]++;
                    }
                    else{
                        resursi[s].radi=true;
                        double u = r.nextDouble();
                        int x = (int)((-svr[s]*1000) * Math.log(1-u));
                        if(x == 0){
                            x=1;
                        }
                        addInList(new Promena(s, x+brojac, brojac),vremenaPromeneStanja);
                    }

                    if(redovi[id] == 0){
                        resursi[id].setRadi(false);
                    }
                    else{
                        redovi[id]--;
                        double u = r.nextDouble();
                        int x = (int)((-svr[id]*1000) * Math.log(1-u));
                        if(x == 0){
                            x=1;
                        }
                        addInList(new Promena(id, x+brojac, brojac),vremenaPromeneStanja);
                    }
                    int sum = 0;
                    for(int i = 0 ; i < redovi.length ; i++){
                        sum+=redovi[i];
                        if(resursi[i].isRadi())
                            sum++;
                    }
                    if(sum != n){
                        System.out.println("GRESKA");
                    }
                    vremenaPromeneStanja.remove(0);
                    for(int i = 0 ; i < resursi.length ; i++){
                        int trBrojPoslova = resursi[i].isRadi() ? 1+redovi[i] : redovi[i];
                        double ukupanBrPoslova =resursi[i].getProsecanBrP()*stariBrojac+ trBrojPoslova*(brojac-stariBrojac);
                        resursi[i].setProsecanBrP(ukupanBrPoslova/(double) brojac);
                    }
                    stariBrojac = brojac;
                }
        return resursi;
        }



    void upisUtxtSim(int time){
        DecimalFormat df = new DecimalFormat("0.0000");
        File simulacija = new File("resenje\\rezultati-simulacije.txt");
        try {
            FileWriter myWriter = new FileWriter(simulacija);
            myWriter.write("***Simulacija***\n");
            for (int n = 10; n <= 20; n += 5) {
                myWriter.write("***N=" + n +"***\n");
                for(int k = 2 ; k <= 10 ; k++) {
                    myWriter.write("**Za vrednost K = "+k+" rezultati za resurse su :**\n");
                    Resurs resursi[] = pokreniSimulaciju(k,n,time);
                    int vremeSvihPoslova = time*60*1000;
                    for(int j = 0 ; j<resursi.length ; j++) {
                        myWriter.write("Resurs " + (j + 1) + ":\n");
                        double u = resursi[j].getVremeRada()/vremeSvihPoslova;
                        double x = (double) resursi[j].getBrPoslova()/vremeSvihPoslova*1000;

                        myWriter.write("Iskoriscenje resursa U" + (j + 1) + " = " + df.format(u * 100) + "%  \n");
                        myWriter.write("Protok kroz resurse X" + (j + 1) + " = " + df.format(x) + " posla/s \n");
                        myWriter.write("Prosecan broj poslova n" + (j + 1) + " = " + df.format(resursi[j].getProsecanBrP()) + " posla \n");
                        myWriter.write("\n");
                    }
                    myWriter.write("*****\n");
                }
            }
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    void upisUtxtSimexcel(int time){
        DecimalFormat df = new DecimalFormat("0.0000");
        File simulacija = new File("resenje\\rezultati-simulacije-excel.txt");
        try {
            FileWriter myWriter = new FileWriter(simulacija);
            for (int n = 10; n <= 20; n += 5) {
                myWriter.write("N=" + n + "\n");
                for (int k = 2; k <= 10; k++) {
                    myWriter.write("K = " + k + "\n");
                    Resurs resursi[] = pokreniSimulaciju(k, n, time);
                    int vremeSvihPoslova = time * 60 * 1000;
                    for (int j = 0; j < resursi.length; j++) {
                        myWriter.write("Resurs " + (j + 1) + ":\n");
                        double u = resursi[j].getVremeRada() / vremeSvihPoslova;
                        double x = (double) resursi[j].getBrPoslova() / vremeSvihPoslova * 1000;

                        myWriter.write("U" + (j + 1) + " = " + df.format(u * 100) + "\n");
                        myWriter.write("X" + (j + 1) + " = " + df.format(x) + "\n");
                        myWriter.write("N" + (j + 1) + " = " + df.format(resursi[j].getProsecanBrP()) + "\n");
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

    void upisUtxtUsrednjeno(int time){
        DecimalFormat df = new DecimalFormat("0.0000");
        File usrednjeno = new File("resenje\\rezultati_simulacija_usrednjeno.txt");
        try {
            FileWriter myWriter = new FileWriter(usrednjeno);
            int vremeSvihPoslova = time*60*1000;
            myWriter.write("***Usrednjeno***\n");
            myWriter.write("***N=" + 10 +"***\n");
            for (int k = 2 ; k <= 10 ; k++) {
                int duzina = k+4;
                double uSum[] = new double[duzina];
                double xSum[] = new double[duzina];
                double prBrSum[] = new double[duzina];
                for(int n = 0; n < 10; n ++) {
                    Resurs resursi[] = pokreniSimulaciju(k,10,time);
                    for(int l = 0 ; l <resursi.length ; l++){
                        uSum[l] += resursi[l].getVremeRada()/vremeSvihPoslova;
                        xSum[l] += (double) resursi[l].getBrPoslova()/vremeSvihPoslova*1000;
                        prBrSum[l] += resursi[l].getProsecanBrP();
                    }
                }
                for(int l = 0 ; l <duzina ; l++){
                    uSum[l] /= 10;
                    xSum[l] /= 10;
                    prBrSum[l] /= 10;
                }
                myWriter.write("**Za vrednost K = "+k+" rezultati za resurse su :**\n");


                for(int j = 0 ; j<duzina ; j++) {
                    myWriter.write("Resurs " + (j + 1) + ":\n");

                    myWriter.write("Iskoriscenje resursa U" + (j + 1) + " = " + df.format(uSum[j]*100) + "%  \n");
                    myWriter.write("Protok kroz resurse X" + (j + 1) + " = " + df.format(xSum[j]) + " posla/s \n");
                    myWriter.write("Prosecan broj poslova n" + (j + 1) + " = " + df.format(prBrSum[j]) + " posla \n");
                    myWriter.write("\n");
                    myWriter.write("*****\n");
                }
            }
            myWriter.write("***N=" + 15 +"***\n");
            for (int k = 2 ; k <= 10 ; k++) {
                int duzina = k+4;
                double uSum[] = new double[duzina];
                double xSum[] = new double[duzina];
                double prBrSum[] = new double[duzina];
                for(int n = 0; n < 25; n ++) {
                    Resurs resursi[] = pokreniSimulaciju(k,15,time);
                    for(int l = 0 ; l <resursi.length ; l++){
                        uSum[l] += resursi[l].getVremeRada()/vremeSvihPoslova;
                        xSum[l] += (double) resursi[l].getBrPoslova()/vremeSvihPoslova*1000;
                        prBrSum[l] += resursi[l].getProsecanBrP();
                    }
                }
                for(int l = 0 ; l <duzina ; l++){
                    uSum[l] /= 25;
                    xSum[l] /= 25;
                    prBrSum[l] /= 25;
                }
                myWriter.write("**Za vrednost K = "+k+" rezultati za resurse su :**\n");


                for(int j = 0 ; j<duzina ; j++) {
                    myWriter.write("Resurs " + (j + 1) + ":\n");

                    myWriter.write("Iskoriscenje resursa U" + (j + 1) + " = " + df.format(uSum[j]*100) + "%  \n");
                    myWriter.write("Protok kroz resurse X" + (j + 1) + " = " + df.format(xSum[j]) + " posla/s \n");
                    myWriter.write("Prosecan broj poslova n" + (j + 1) + " = " + df.format(prBrSum[j]) + " posla \n");
                    myWriter.write("\n");
                    myWriter.write("*****\n");
                }
            }
            myWriter.write("***N=" + 20 +"***\n");
            for (int k = 2 ; k <= 10 ; k++) {
                int duzina = k+4;
                double uSum[] = new double[duzina];
                double xSum[] = new double[duzina];
                double prBrSum[] = new double[duzina];
                for(int n = 0; n < 100; n ++) {
                    Resurs resursi[] = pokreniSimulaciju(k,20,time);
                    for(int l = 0 ; l <resursi.length ; l++){
                        uSum[l] += resursi[l].getVremeRada()/vremeSvihPoslova;
                        xSum[l] += (double) resursi[l].getBrPoslova()/vremeSvihPoslova*1000;
                        prBrSum[l] += resursi[l].getProsecanBrP();
                    }
                }
                for(int l = 0 ; l <duzina ; l++){
                    uSum[l] /= 100;
                    xSum[l] /= 100;
                    prBrSum[l] /= 100;
                }
                myWriter.write("**Za vrednost K = "+k+" rezultati za resurse su :**\n");


                for(int j = 0 ; j<duzina ; j++) {
                    myWriter.write("Resurs " + (j + 1) + ":\n");

                    myWriter.write("Iskoriscenje resursa U" + (j + 1) + " = " + df.format(uSum[j]*100) + "%  \n");
                    myWriter.write("Protok kroz resurse X" + (j + 1) + " = " + df.format(xSum[j]) + " posla/s \n");
                    myWriter.write("Prosecan broj poslova n" + (j + 1) + " = " + df.format(prBrSum[j]) + " posla \n");
                    myWriter.write("\n");
                    myWriter.write("*****\n");
                }
            }

            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    void upisUtxtUsrednjenoexcel(int time){
        DecimalFormat df = new DecimalFormat("0.0000");
        File usrednjeno = new File("resenje\\rezultati_simulacija_usrednjeno_excel.txt");
        try {
            FileWriter myWriter = new FileWriter(usrednjeno);
            int vremeSvihPoslova = time*60*1000;
            myWriter.write("N=" + 10 +"\n");
            for (int k = 2 ; k <= 10 ; k++) {
                int duzina = k+4;
                double uSum[] = new double[duzina];
                double xSum[] = new double[duzina];
                double prBrSum[] = new double[duzina];
                for(int n = 0; n < 10; n ++) {
                    Resurs resursi[] = pokreniSimulaciju(k,10,time);
                    for(int l = 0 ; l <duzina ; l++){
                        uSum[l] += resursi[l].getVremeRada()/vremeSvihPoslova;
                        xSum[l] += (double) resursi[l].getBrPoslova()/vremeSvihPoslova*1000;
                        prBrSum[l] += resursi[l].getProsecanBrP();
                    }
                }
                for(int l = 0 ; l <duzina ; l++){
                    uSum[l] /= 10;
                    xSum[l] /= 10;
                    prBrSum[l] /= 10;
                }
                myWriter.write("K = "+k+"\n");


                for(int j = 0 ; j<duzina ; j++) {
                    myWriter.write("Resurs " + (j + 1) + ":\n");

                    myWriter.write("U" + (j + 1) + " = " + df.format(uSum[j]*100) + "\n");
                    myWriter.write("X" + (j + 1) + " = " + df.format(xSum[j]) + "\n");
                    myWriter.write("N" + (j + 1) + " = " + df.format(prBrSum[j]) + "\n");
                }
            }
            myWriter.write("N=" + 15 +"\n");
            for (int k = 2 ; k <= 10 ; k++) {
                int duzina = k+4;
                double uSum[] = new double[duzina];
                double xSum[] = new double[duzina];
                double prBrSum[] = new double[duzina];
                for(int n = 0; n < 25; n ++) {
                    Resurs resursi[] = pokreniSimulaciju(k,15,time);
                    for(int l = 0 ; l <duzina ; l++){
                        uSum[l] += resursi[l].getVremeRada()/vremeSvihPoslova;
                        xSum[l] += (double) resursi[l].getBrPoslova()/vremeSvihPoslova*1000;
                        prBrSum[l] += resursi[l].getProsecanBrP();
                    }
                }
                for(int l = 0 ; l <duzina ; l++){
                    uSum[l] /= 25;
                    xSum[l] /= 25;
                    prBrSum[l] /= 25;
                }
                myWriter.write("K = "+k+"\n");


                for(int j = 0 ; j<duzina ; j++) {
                    myWriter.write("Resurs " + (j + 1) + ":\n");

                    myWriter.write("U" + (j + 1) + " = " + df.format(uSum[j]*100) + "\n");
                    myWriter.write("X" + (j + 1) + " = " + df.format(xSum[j]) + "\n");
                    myWriter.write("N" + (j + 1) + " = " + df.format(prBrSum[j]) + "\n");
                }
            }
            myWriter.write("N=" + 20 +"\n");
            for (int k = 2 ; k <= 10 ; k++) {
                int duzina = k+4;
                double uSum[] = new double[duzina];
                double xSum[] = new double[duzina];
                double prBrSum[] = new double[duzina];
                for(int n = 0; n < 100; n ++) {
                    Resurs resursi[] = pokreniSimulaciju(k,20,time);
                    for(int l = 0 ; l <duzina ; l++){
                        uSum[l] += resursi[l].getVremeRada()/vremeSvihPoslova;
                        xSum[l] += (double) resursi[l].getBrPoslova()/vremeSvihPoslova*1000;
                        prBrSum[l] += resursi[l].getProsecanBrP();
                    }
                }
                for(int l = 0 ; l <duzina ; l++){
                    uSum[l] /= 100;
                    xSum[l] /= 100;
                    prBrSum[l] /= 100;
                }
                myWriter.write("K = "+k+"\n");


                for(int j = 0 ; j<duzina ; j++) {
                    myWriter.write("Resurs " + (j + 1) + ":\n");

                    myWriter.write("U" + (j + 1) + " = " + df.format(uSum[j]*100) + "\n");
                    myWriter.write("X" + (j + 1) + " = " + df.format(xSum[j]) + "\n");
                    myWriter.write("N" + (j + 1) + " = " + df.format(prBrSum[j]) + "\n");
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
