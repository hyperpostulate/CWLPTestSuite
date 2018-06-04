package org.mesutormanli.CWLPTestCaseGenerator;

import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;

public class CWLPTestCaseGenerator {

    static final int [] aInterval = {3000, 21000}; //Interval for test case parameter a.
    static final int [] dInterval = {10, 110}; //Interval for test case parameter d.
    static final double [] cInterval = {0.2, 1}; //Interval for test case parameter c.
    static final double [] fInterval = {3600, 7200}; //Interval for test case parameter f.

    static final int [] parametersForSmallInstances = {10, 1000};
    static final int [] parametersForMediumInstances = {20, 2000};
    static final int [] parametersForLargeInstances = {50, 5000};


    public static void main(String[] args) {
        Instant start = Instant.now();

        TestCaseForCWLP [] smallInstances;
        TestCaseForCWLP [] mediumInstances;
        TestCaseForCWLP [] largeInstances;

        smallInstances = generateSmallInstances();
        mediumInstances = generateMediumInstances();
        largeInstances = generateLargeInstances();

        printInstances("small", smallInstances);
        printInstances("medium", mediumInstances);
        printInstances("large", largeInstances);

        Instant end = Instant.now();
        System.out.println("Elapsed time: " + Duration.between(start, end));

    }

    public static TestCaseForCWLP [] generateSmallInstances(){
        TestCaseForCWLP [] smallInstances = new TestCaseForCWLP[30];
        for (int i = 0, smallInstancesLength = smallInstances.length; i < smallInstancesLength; i++) {
            smallInstances[i] = new TestCaseForCWLP(parametersForSmallInstances[0], parametersForSmallInstances[1], aInterval, dInterval, cInterval, fInterval);
        }
        return smallInstances;

    }

    public static TestCaseForCWLP [] generateMediumInstances(){
        TestCaseForCWLP [] mediumInstances = new TestCaseForCWLP[30];
        for (int i = 0, mediumInstancesLength = mediumInstances.length; i < mediumInstancesLength; i++) {
            mediumInstances[i] = new TestCaseForCWLP(parametersForMediumInstances[0], parametersForMediumInstances[1], aInterval, dInterval, cInterval, fInterval);
        }
        return mediumInstances;
    }

    public static TestCaseForCWLP [] generateLargeInstances(){
        TestCaseForCWLP [] largeInstances = new TestCaseForCWLP[30];
        for (int i = 0, largeInstancesLength = largeInstances.length; i < largeInstancesLength; i++) {
            largeInstances[i] = new TestCaseForCWLP(parametersForLargeInstances[0], parametersForLargeInstances[1], aInterval, dInterval, cInterval, fInterval);
        }
        return largeInstances;
    }

    public static void printInstances(String type, TestCaseForCWLP [] instances){
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("./target/test-case-outputs/" + type + "-instances-output.txt", "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i=0; i<instances.length; i++){
            writer.println("-----Test Case [" + i + "] For "+ type.toUpperCase() +" Instances-----");
            writer.println(instances[i].toString());
        }

    }






}
