package org.mesutormanli.CWLPSolver;

import gurobi.*;
import org.mesutormanli.CWLPTestCaseGenerator.CWLPTestCaseGenerator;
import org.mesutormanli.CWLPTestCaseGenerator.TestCaseForCWLP;

import java.io.PrintWriter;

public class CWLPSolverWithGurobi {
    public static void main(String[] args) {
        //Create PrintWriters for output
        PrintWriter smallInstancesWriter = null;
        PrintWriter mediumInstancesWriter = null;
        PrintWriter largeInstancesWriter = null;
        try {
            smallInstancesWriter = new PrintWriter("./target/gurobi-solver-outputs/small-instances-output.txt", "UTF-8");
            mediumInstancesWriter = new PrintWriter("./target/gurobi-solver-outputs/medium-instances-output.txt", "UTF-8");
            largeInstancesWriter = new PrintWriter("./target/gurobi-solver-outputs/large-instances-output.txt", "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Run solver for small instances
        TestCaseForCWLP[] smallInstances = CWLPTestCaseGenerator.generateSmallInstances();
        smallInstancesWriter.println("Gurobi Solver Started for SMALL Instances");
        smallInstancesWriter.println("------------------------------");
        for (int i = 0; i < smallInstances.length; i++) {
            solveWithGurobi(smallInstances[i], smallInstancesWriter, i);
        }

        //Run solver for medium instances
        TestCaseForCWLP[] mediumInstances = CWLPTestCaseGenerator.generateMediumInstances();
        mediumInstancesWriter.println("Gurobi Solver Started for MEDIUM Instances");
        mediumInstancesWriter.println("------------------------------");
        for (int i = 0; i < mediumInstances.length; i++) {
            solveWithGurobi(mediumInstances[i], mediumInstancesWriter, i);
        }

        //Run solver for large instances
        TestCaseForCWLP[] largeInstances = CWLPTestCaseGenerator.generateLargeInstances();
        largeInstancesWriter.println("Gurobi Solver Started for LARGE Instances");
        largeInstancesWriter.println("------------------------------");
        for (int i = 0; i < largeInstances.length; i++) {
            solveWithGurobi(largeInstances[i], largeInstancesWriter, i);
        }

        //Close writers
        smallInstancesWriter.close();
        mediumInstancesWriter.close();
        largeInstancesWriter.close();

    }

    public static void solveWithGurobi(TestCaseForCWLP testCase, PrintWriter writer, int indexOfInstance) {

        try {

            // Customer demand in units
            int Demand[] = testCase.getD();

            // Warehouse capacity in units
            int Capacity[] = testCase.getA();

            // Fixed costs for each warehouse
            double FixedCosts[] = testCase.getF();

            // Transportation costs
            double TransCosts[][] = testCase.getC();

            // Number of warehouses and customers
            int nWarehouses = testCase.getNumberOfWarehouses();
            int nCustomers = testCase.getNumberOfCustomers();

            // Model
            GRBEnv env = new GRBEnv();
            GRBModel model = new GRBModel(env);
            model.set(GRB.StringAttr.ModelName, "Capacitated Warehouse Location Problem");

            // Warehouse open decision variables: open[w] == 1 if warehouse w is open.
            GRBVar[] open = new GRBVar[nWarehouses];
            for (int w = 0; w < nWarehouses; w++) {
                open[w] = model.addVar(0, 1, FixedCosts[w], GRB.BINARY, "Open" + w);
            }

            // Transportation decision variables: how much to transport from
            // a warehouse w to a customer c
            GRBVar[][] transport = new GRBVar[nWarehouses][nCustomers];
            for (int w = 0; w < nWarehouses; w++) {
                for (int c = 0; c < nCustomers; c++) {
                    transport[w][c] =
                            model.addVar(0, GRB.INFINITY, TransCosts[w][c], GRB.CONTINUOUS,
                                    "Trans" + w + "." + c);
                }
            }

            // The objective is to minimize the total fixed and variable costs
            model.set(GRB.IntAttr.ModelSense, GRB.MINIMIZE);

            // Production constraints
            // Note that the right-hand limit sets the production to zero if
            // the warehouse is closed
            for (int w = 0; w < nWarehouses; w++) {
                GRBLinExpr ptot = new GRBLinExpr();
                for (int c = 0; c < nCustomers; c++) {
                    ptot.addTerm(1.0, transport[w][c]);
                }
                GRBLinExpr limit = new GRBLinExpr();
                limit.addTerm(Capacity[w], open[w]);
                model.addConstr(ptot, GRB.LESS_EQUAL, limit, "Capacity" + w);
            }

            // Demand constraints
            for (int c = 0; c < nCustomers; c++) {
                GRBLinExpr dtot = new GRBLinExpr();
                for (int w = 0; w < nWarehouses; w++) {
                    dtot.addTerm(1.0, transport[w][c]);
                }
                model.addConstr(dtot, GRB.EQUAL, Demand[c], "Demand" + c);
            }

            // Guess at the starting point: close the warehouse with the highest
            // fixed costs; open all others

            // First, open all warehouses
            for (int w = 0; w < nWarehouses; w++) {
                open[w].set(GRB.DoubleAttr.Start, 1.0);
            }

            // Now close the warehouse with the highest fixed cost
            writer.println("Initial guess:");
            double maxFixed = -GRB.INFINITY;
            for (int w = 0; w < nWarehouses; w++) {
                if (FixedCosts[w] > maxFixed) {
                    maxFixed = FixedCosts[w];
                }
            }
            for (int w = 0; w < nWarehouses; w++) {
                if (FixedCosts[w] == maxFixed) {
                    open[w].set(GRB.DoubleAttr.Start, 0.0);
                    writer.println("Closing warehouse " + w + "\n");
                    break;
                }
            }

            // Use barrier to solve root relaxation
            model.set(GRB.IntParam.Method, GRB.METHOD_BARRIER);

            // Solve
            model.optimize();

            // Print solution
            writer.println("Gurobi Output for Instance[" + indexOfInstance + "]: ");
            writer.println("------------------------------");
            writer.println("TOTAL COSTS: " + model.get(GRB.DoubleAttr.ObjVal));
            writer.println("SOLUTION:");
            for (int w = 0; w < nWarehouses; w++) {
                if (open[w].get(GRB.DoubleAttr.X) > 0.99) {
                    writer.println("Warehouse " + w + " open:");
                    for (int c = 0; c < nCustomers; c++) {
                        if (transport[w][c].get(GRB.DoubleAttr.X) > 0.0001) {
                            writer.println("  Transport " +
                                    transport[w][c].get(GRB.DoubleAttr.X) +
                                    " units to customer " + c);
                        }
                    }
                } else {
                    writer.println("Warehouse " + w + " closed!");
                }
            }

            // Dispose of model and environment
            model.dispose();
            env.dispose();

            //Handle exceptions, if any
        } catch (GRBException e) {
            writer.println("Error code: " + e.getErrorCode() + ". " +
                    e.getMessage());
        }


    }
}
