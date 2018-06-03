package org.mesutormanli.CWLPTestCaseGenerator;

import java.util.Random;

public class TestCaseForCWLP {

    private int [] a; //Maximum capacity (in units) for each of the warehouses.
    private int [] d; //Demands (in units) for each of the customers.
    private double [][] c; //Cost of transporting one unit of good from warehouse i to customer j.
    private double [] f;  //Fixed cost of operating warehouse i if it is opened.

    Random random = new Random();

    public TestCaseForCWLP(int numberOfWarehouses, int numberOfCustomers, int[] aInterval, int[] dInterval, double[] cInterval, double[] fInterval) {
        generateA(numberOfWarehouses, aInterval);
        generateD(numberOfCustomers, dInterval);
        generateC(numberOfWarehouses, numberOfCustomers, cInterval);
        generateF(numberOfWarehouses, fInterval);
    }

    public void generateA (int numberOfWarehouses, int[] aInterval){
        setA(random.ints(numberOfWarehouses, aInterval[0], aInterval[1]+1).toArray());
    }

    public void generateD (int numberOfCustomers, int[] dInterval){
        //TODO: check constraint
        setD(random.ints(numberOfCustomers, dInterval[0], dInterval[1]+1).toArray());
    }

    public void generateC (int numberOfWarehouses, int numberOfCustomers, double[] cInterval){
        double [][] candidate = new double[numberOfWarehouses][numberOfCustomers];
        for(int i = 0; i < numberOfWarehouses; i++){
            candidate[i] = random.doubles(numberOfCustomers, cInterval[0], cInterval[1]+1).toArray();
        }
        setC(candidate);

    }

    public void generateF (int numberOfWarehouses, double[] fInterval ){
        setF(random.doubles(numberOfWarehouses, fInterval[0], fInterval[1]+1).toArray());
    }

    public int[] getA() {
        return a;
    }

    public void setA(int[] a) {
        this.a = a;
    }

    public int[] getD() {
        return d;
    }

    public void setD(int[] d) {
        this.d = d;
    }

    public double[][] getC() {
        return c;
    }

    public void setC(double[][] c) {
        this.c = c;
    }

    public double[] getF() {
        return f;
    }

    public void setF(double[] f) {
        this.f = f;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();

        sb.append("a:\n");
        for (int aValue : a) {
            sb.append("["+ aValue +"], ");
        }
        sb.append("\n");
        sb.append("c:\n");
        for(int i = 0; i<c.length; i++){
            for (int j=0; j<c[0].length; j++){
                sb.append("["+ c[i][j] +"], ");
            }
        }
        sb.append("\n");
        sb.append("d:\n");
        for (int dValue : d) {
            sb.append("["+ dValue +"], ");
        }
        sb.append("\n");
        sb.append("f:\n");
        for (double fValue : f) {
            sb.append("["+ fValue +"], ");
        }
        sb.append("\n");

        return sb.toString();

    }
}
