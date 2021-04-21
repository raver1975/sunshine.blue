package com.klemstinegroup.sunshineblue.engine.util;

public class Lagrange {

    public static void main(String[] argv) {

//        Parameters p = new Parameters();
//        NumberVariable rp = new DoubleVariable();
//        Writer out = new FileWriter("Lagrange.out");
//        String s = "";

        double x, xinc = 5.0;
        int n = 9;
        int l, lend;
        double interpolation;


        //
        //Input data arrays
        //

        double[] xin = {0., 25., 50., 75., 100., 125., 150., 175., 200.};
        double[] yin = {10.6, 16.0, 45.0, 83.5, 52.8, 19.9, 10.8, 8.25, 4.7};

        lend = (int) (xin[8] / xinc);

        //
        //Loop through all requested x values
        //

        for (l = 0; l <= lend; l++) {
            x = l * xinc;
            interpolation = inter(xin, yin, x, n);
            System.out.println(x+"\t" + interpolation);
//   	s = Format.sprintf( "%f\t%f\n", p.add(x).add(interpolation) );
//   	out.write(s);
        }

//   System.out.println("Data written to Langrange.out");
//   out.close();
    }


    public static double inter(double xin[], double yin[], double x, int n) {
        double lambda[] = new double[10];         //array for weights
        double interp = 0;
        int i, j;
        n = n - 1;

        for (i = 0; i <= n; i++)                     //loop over all x inputs
        {
            lambda[i] = 1.0;

            for (j = 0; j <= n; j++)              //compute weights
            {
                if (i != j) {
                    lambda[i] = lambda[i] * ((x - xin[j]) / (xin[i] - xin[j]));
                }

            }
            interp = interp + (yin[i] * lambda[i]);   //the interpolated function
        }

        return interp;
    } //end inter method

}  