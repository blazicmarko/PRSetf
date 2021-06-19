import Jama.Matrix;
import Jama.SingularValueDecomposition;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

import static jdk.nashorn.internal.objects.NativeMath.round;

public class GordonNewell {
    double[][] MatricaPbezI(int k) {
        double[][] M = new double[4 + k][4 + k];
        for(int i = 0 ; i < 4 ; i++){
            for(int j = 0 ; j < 4 ; j++){
                if(i == j){
                    M[i][j]=0.2;
                }
            }
        }
        M[0][1] = 0.15;
        M[0][2] = 0.1;
        M[0][3] = 0.05;
        M[1][0] = 0.3;
        M[2][0] = 0.3;
        M[3][0] = 0.3;
        for(int i = 0 ; i < 4 ; i++){
            for(int j = 4 ; j < 4+k ; j++){
                M[i][j] = 0.5/k;
            }
        }
        for(int i = 4 ; i < 4+k ; i++){
            M[i][0] = 1;
        }
        return M;
    }

    double[][] MatricaP(int k) {
        double[][] M = new double[4 + k][4 + k];
        for(int i = 0 ; i < 4 ; i++){
            for(int j = 0 ; j < 4 ; j++){
                if(i == j){
                    M[i][j]=0.2;
                }
            }
        }
        M[0][1] = 0.15;
        M[0][2] = 0.1;
        M[0][3] = 0.05;
        M[1][0] = 0.3;
        M[2][0] = 0.3;
        M[3][0] = 0.3;
        for(int i = 0 ; i < 4 ; i++){
            for(int j = 4 ; j < 4+k ; j++){
                M[i][j] = 0.5/k;
            }
        }
        for(int i = 4 ; i < 4+k ; i++){
            M[i][0] = 1;
        }
        for(int i = 0 ; i < 4+k ; i++){
            for(int j = 0 ; j < 4+k ; j++){
                if(i == j){
                    M[i][j]--;
                }
            }
        }
        return M;
    }

    double[][] MatricaU(int k){
        double s[] = new double[4+k];
        s[0]= 0.005;
        s[1]= 0.01;
        s[2]= 0.015;
        s[3]= s[2];
        for(int i = 4 ; i < 4+k ; i++){
            s[i]=0.025;
        }
        double u[][] = new double[4+k][4+k];
        for(int i = 0 ; i < 4+k ; i++){
            for(int j = 0 ; j < 4+k ; j++){
                if(i == j){
                    u[i][j] = 1/s[i];
                }
            }
        }
        return u;
    }

    double[][] MatricaI(int k){
        double a[][] = new double[4+k][4+k];
        for(int i = 0 ; i < 4+k ; i++){
            for(int j = 0 ; j < 4+k ; j++){
                if(i == j){
                    a[i][j] = 1;
                }
            }
        }
        return a;
    }

    double[] racunanjePotraznje(int k) {
        Matrix p = new Matrix(this.MatricaP(k));
        Matrix u = new Matrix(this.MatricaU(k));
        p = p.transpose();
        Matrix K=p.times(u);
        SingularValueDecomposition s = K.svd();
        //Racunanje V* matrice od V i I matrice
        Matrix i = new Matrix(this.MatricaI(k));
        Matrix ans = s.getV().solve(i);
        double[] x = new double[4+k];
        for(int w = 0 ; w< x.length ; w++){
            x[w]=ans.get(x.length-1,w)/ans.get(x.length-1,0);
        }
        return x;
    }

    void upisUtxtGN(){
        DecimalFormat df = new DecimalFormat("0.0000");
        File potraznje = new File("resenje\\potraznje_analiticki.txt");
        try {
            FileWriter myWriter = new FileWriter(potraznje);
            myWriter.write("***Potraznje***\n");
            for(int i = 2 ; i <= 10 ; i++){
                double x[] = racunanjePotraznje(i);
                myWriter.write("Za vrednost K = "+i+" potraznje su :\n");
                for(int j = 0 ; j< x.length; j++){
                    myWriter.write("x"+(j+1)+" = "+df.format(x[j])+" \n");
                }
                myWriter.write("*****\n");
            }
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    void upisUtxtGNexcel(){
        DecimalFormat df = new DecimalFormat("0.0000");
        File potraznje = new File("resenje\\potraznje_analiticki-excel.txt");
        try {
            FileWriter myWriter = new FileWriter(potraznje);
            for(int i = 2 ; i <= 10 ; i++){
                double x[] = racunanjePotraznje(i);
                myWriter.write("K = "+i+"\n");
                for(int j = 0 ; j< x.length; j++){
                    myWriter.write("x"+(j+1)+" = "+df.format(x[j])+" \n");
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

