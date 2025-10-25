package edu.cwru.sepia.agent;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.action.ActionResult;
import edu.cwru.sepia.agent.Agent;
import edu.cwru.sepia.environment.model.history.HistoryView;
import edu.cwru.sepia.environment.model.state.ResourceNode.ResourceView;
import edu.cwru.sepia.environment.model.state.ResourceNode.Type;
import edu.cwru.sepia.environment.model.state.StateView;
import edu.cwru.sepia.environment.model.state.Unit.UnitView;

/**
 * ExecutionAgent is responsible for driving a {@link planner.Action} plan within the SEPIA
 * simulation. The agent translates high-level planner actions into primitive SEPIA commands and
 * issues them to a single peasant unit under its control.
 */
public class ExecutionAgent extends Agent implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<planner.Action> plan;
    private int currentStep;
    private Integer peasantId;
    private boolean planCompleted;
    private boolean completionAnnounced;
    private final Map<String, Integer> locationToId;
    private final Map<String, Point> locationToPoint;

    /**
     * Creates a new {@code ExecutionAgent} bound to the provided player number and plan.
     *
     * @param playerNum the SEPIA player identifier
     * @param plan      the high-level planner actions to execute
     */
    public ExecutionAgent(int playerNum, List<planner.Action> plan) {
        super(playerNum);
        this.plan = new ArrayList<>(Objects.requireNonNull(plan, "plan"));
        this.currentStep = 0;
        this.planCompleted = this.plan.isEmpty();
        this.completionAnnounced = false;
        this.locationToId = new HashMap<>();
        this.locationToPoint = new HashMap<>();
    }

    @Override
    public Map<Integer, Action> initialStep(StateView stateView, HistoryView historyView) {
        locationToId.clear();
        locationToPoint.clear();
        peasantId = null;

        for (UnitView unit : stateView.getUnits(getPlayerNumber())) {
            String name = unit.getTemplateView().getName();
            Point position = new Point(unit.getXPosition(), unit.getYPosition());
            locationToId.put(name, unit.getID());
            locationToPoint.put(name, position);

            if (peasantId == null && isPeasant(name)) {
                peasantId = unit.getID();
            }

            if (isTownhall(name)) {
                locationToId.put("Townhall", unit.getID());
                locationToPoint.put("Townhall", position);
            }
        }

        int goldIndex = 1;
        int woodIndex = 1;
        for (ResourceView resource : stateView.getResourceNodes()) {
            Point position = new Point(resource.getXPosition(), resource.getYPosition());
            String key;
            if (resource.getType() == Type.GOLD_MINE) {
                key = "Mine" + goldIndex++;
            } else if (resource.getType() == Type.TREE) {
                key = "Forest" + woodIndex++;
            } else {
                continue;
            }
            locationToId.put(key, resource.getID());
            locationToPoint.put(key, position);
        }

        if (peasantId == null) {
            throw new IllegalStateException("Expected to control a peasant but none was found.");
        }

        if (planCompleted) {
            logPlanCompletion();
        }

        return Collections.emptyMap();
    }

    @Override
    public Map<Integer, Action> middleStep(StateView stateView, HistoryView historyView) {
        if (planCompleted) {
            return Collections.emptyMap();
        }

        List<ActionResult> feedback = historyView.getCommandFeedback(getPlayerNumber(), peasantId);
        if (currentStep >= plan.size()) {
            if (feedback != null && !feedback.isEmpty()) {
                ActionResult last = feedback.get(feedback.size() - 1);
                if (last.getResult() == ActionResult.Result.INCOMPLETE) {
                    return Collections.emptyMap();
                }
            }
            logPlanCompletion();
            return Collections.emptyMap();
        }

        if (feedback != null && !feedback.isEmpty()) {
            ActionResult last = feedback.get(feedback.size() - 1);
            if (last.getResult() == ActionResult.Result.INCOMPLETE) {
                return Collections.emptyMap();
            }
            if (last.getResult() == ActionResult.Result.FAILED) {
                System.err.println("Warning: previous action reported failure for step " + currentStep);
            }
        }

        Action nextAction = buildSepiaAction(plan.get(currentStep));
        currentStep++;

        Map<Integer, Action> commands = new HashMap<>();
        commands.put(peasantId, nextAction);
        return commands;
    }

    @Override
    public void terminalStep(StateView stateView, HistoryView historyView) {
        logPlanCompletion();
    }

    @Override
    public void savePlayerData(java.io.OutputStream outputStream) {
        // No persistence required for this agent.
    }

    @Override
    public void loadPlayerData(java.io.InputStream inputStream) {
        // No persistence required for this agent.
    }

    private Action buildSepiaAction(planner.Action plannerAction) {
        if (plannerAction == null) {
            throw new IllegalArgumentException("plannerAction must not be null");
        }
        if (peasantId == null) {
            throw new IllegalStateException("Peasant identifier is not initialized.");
        }

        switch (plannerAction.type) {
            case MOVE:
                Point target = requireLocation(plannerAction.toLocation);
                return Action.createPrimitiveMove(peasantId, target.x, target.y);
            case HARVEST_GOLD:
            case HARVEST_WOOD:
                Integer resourceId = requireId(plannerAction.toLocation);
                return Action.createPrimitiveGather(peasantId, resourceId);
            case DEPOSIT_GOLD:
            case DEPOSIT_WOOD:
                Integer depositId = requireId(plannerAction.toLocation);
                return Action.createPrimitiveDeposit(peasantId, depositId);
            default:
                throw new UnsupportedOperationException(
                        "Unsupported planner action type: " + plannerAction.type);
        }
    }

    private Integer requireId(String locationName) {
        Integer id = locationToId.get(locationName);
        if (id == null) {
            throw new IllegalStateException(
                    "No unit or resource mapping for location '" + locationName + "'.");
        }
        return id;
    }

    private Point requireLocation(String locationName) {
        Point point = locationToPoint.get(locationName);
        if (point == null) {
            throw new IllegalStateException(
                    "No coordinate mapping for location '" + locationName + "'.");
        }
        return point;
    }

    private boolean isPeasant(String name) {
        return "Peasant".equalsIgnoreCase(name) || name.toLowerCase().contains("peasant");
    }

    private boolean isTownhall(String name) {
        return "TownHall".equalsIgnoreCase(name) || "Townhall".equalsIgnoreCase(name);
    }

    private void logPlanCompletion() {
        planCompleted = true;
        if (!completionAnnounced) {
            System.out.println("Plan execution finished.");
            completionAnnounced = true;
        }
    }
}
