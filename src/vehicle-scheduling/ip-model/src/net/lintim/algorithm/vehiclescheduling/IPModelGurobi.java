package net.lintim.algorithm.vehiclescheduling;

import gurobi.*;
import net.lintim.exception.LinTimException;
import net.lintim.exception.SolverGurobiException;
import net.lintim.model.Graph;
import net.lintim.model.VehicleSchedule;
import net.lintim.model.vehiclescheduling.TripConnection;
import net.lintim.model.vehiclescheduling.TripNode;
import net.lintim.util.LogLevel;
import net.lintim.util.SolverType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

/**
 * An implementation of an ip solver for the vehicle scheduling problem using Gurobi. Use
 * {@link IPModelSolver#getVehicleSchedulingIpSolver(SolverType)} for getting an actual instance of this class.
 */
public class IPModelGurobi extends IPModelSolver{

	@Override
	public VehicleSchedule solveVehicleSchedulingIPModel(Graph<TripNode, TripConnection> tripGraph, boolean useDepot,
	                                                     int timeLimit, Level logLevel) {
		try {
			GRBEnv env = new GRBEnv();
			GRBModel vsModel = new GRBModel(env);
			vsModel.set(GRB.IntAttr.ModelSense, GRB.MINIMIZE);
			double solverTimelimit = timeLimit == -1 ? GRB.INFINITY : timeLimit;
			logger.log(LogLevel.DEBUG, "Set Gurobi timelimit to " + solverTimelimit);
			vsModel.set(GRB.DoubleParam.TimeLimit, solverTimelimit);
			if (logLevel.equals(LogLevel.DEBUG)) {
				vsModel.set(GRB.IntParam.LogToConsole, 1);
				vsModel.set(GRB.StringParam.LogFile, "VSModelGurobi.log");
			} else {
				vsModel.set(GRB.IntParam.OutputFlag, 0);
			}

			// Add variables
			logger.log(LogLevel.DEBUG, "Add variables");
			HashMap<Integer, GRBVar> edgeVariables = new HashMap<>();
			for(TripConnection connection : tripGraph.getEdges()) {
				edgeVariables.put(connection.getId(), vsModel.addVar(0, 1, connection.getCost(), GRB.INTEGER, "v_"
						+ connection.getId()));
			}

			// Add constraints
			logger.log(LogLevel.DEBUG, "Add incoming connection constraints");
			GRBLinExpr incomingEdges;
			GRBLinExpr outgoingEdges;
			for(TripNode tripNode : tripGraph.getNodes()) {
				if(tripNode.isDepot()) {
					continue;
				}
				incomingEdges = new GRBLinExpr();
				for(TripConnection incomingConnection : tripGraph.getIncomingEdges(tripNode)) {
					incomingEdges.addTerm(1, edgeVariables.get(incomingConnection.getId()));
				}
				outgoingEdges = new GRBLinExpr();
				for(TripConnection outgoingConnection : tripGraph.getOutgoingEdges(tripNode)) {
					outgoingEdges.addTerm(1, edgeVariables.get(outgoingConnection.getId()));
				}
				vsModel.addConstr(incomingEdges, GRB.EQUAL, 1, "c_inc_" + tripNode.getId());
				vsModel.addConstr(outgoingEdges, GRB.EQUAL, 1, "c_out_" + tripNode.getId());
			}

			logger.log(LogLevel.DEBUG, "Start optimization");
			vsModel.write("vsModel.lp");
			vsModel.optimize();
			logger.log(LogLevel.DEBUG, "End optimization");

			int status = vsModel.get(GRB.IntAttr.Status);
			if (status == GRB.INFEASIBLE) {
				logger.log(LogLevel.ERROR, "The problem is infeasible");
				return null;
			}
			else if (status == GRB.OPTIMAL) {
				logger.log(LogLevel.DEBUG, "Optimal solution found");
			}
			else {
				logger.log(LogLevel.DEBUG, "No optimal solution found");
				if(vsModel.get(GRB.IntAttr.SolCount) == 0) {
					logger.log(LogLevel.WARN, "Could not find feasible solution");
					return null;
				}
			}
			// Create the vehicle schedule from the solution information
			List<TripConnection> usedTripConnections = new LinkedList<>();
			for(TripConnection connection : tripGraph.getEdges()) {
				// We are using a nested try-catch-exception here. We had the problem that
				// the get method would sometimes fail with a "variable not in model"-error,
				// altough the varible was contained. Adding an additional check solved
				// this problem. To avoid doing this in every step, we will only do it if
				// the first try fails.
				try {
					if (Math.round(edgeVariables.get(connection.getId()).get(GRB.DoubleAttr.X)) > 0) {
						usedTripConnections.add(connection);
					}
				}
				catch (GRBException e) {
					// Try again to find the variable. First check, if the variable is
					// contained in the model (and fail otherwise) and then query it again.
					GRBVar[] variables = vsModel.getVars();
					GRBVar variable = edgeVariables.get(connection.getId());
					if(Arrays.asList(variables).contains(variable)) {
						if (Math.round(edgeVariables.get(connection.getId()).get(GRB.DoubleAttr.X)) > 0) {
							usedTripConnections.add(connection);
						}
					}
					else {
						throw new LinTimException("Variable " + connection.getId() + " is not contained!");
					}
					throw new SolverGurobiException(e.getErrorCode() + ": " + e.toString());
				}
			}
			return computeSchedule(usedTripConnections, useDepot);

		} catch (GRBException e) {
			throw new SolverGurobiException(e.toString());
		}
	}
}