package agent;

import java.awt.Point;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.action.ActionFeedback;
import edu.cwru.sepia.action.ActionResult;
import edu.cwru.sepia.agent.Agent;
import edu.cwru.sepia.environment.model.history.History.HistoryView;
import edu.cwru.sepia.environment.model.state.ResourceNode.ResourceView;
import edu.cwru.sepia.environment.model.state.ResourceNode.Type;
import edu.cwru.sepia.environment.model.state.State.StateView;
import edu.cwru.sepia.environment.model.state.Unit.UnitView;
import planner.Planner;

/**
 * ExecutionAgent is responsible for driving a {@link planner.Action} plan within the SEPIA
 * simulation. The agent translates high-level planner actions into primitive SEPIA commands and
 * issues them to a single peasant unit under its control.
 */
public class ExecutionAgent extends Agent {

    private static final long serialVersionUID = 1L;

    private List<planner.Action> plan;
    private int currentStep;
    private Integer peasantId;
    private boolean planCompleted;
    private boolean completionAnnounced;
    private final Map<String, Integer> locationToId;
    private final Map<String, Point> locationToPoint;
    private int lastProcessedTurn;
    private boolean awaitingCommand;
    private int requiredGold;
    private int requiredWood;

    /**
     * Creates a new {@code ExecutionAgent} with required resources to collect
     *
     * @param args the arguments passed from SEPIA config
     */
    public ExecutionAgent(int playerNum, String[] args) {
        super(playerNum);
        
        if (args == null || args.length != 2) {
            throw new IllegalArgumentException("Expected 2 arguments: requiredGold and requiredWood");
        }

        try {
            this.requiredGold = Integer.parseInt(args[0]);
            this.requiredWood = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Arguments must be integers: " + e.getMessage());
        }

        System.out.println("Creating ExecutionAgent: playerNum=" + playerNum + 
                        ", requiredGold=" + this.requiredGold + 
                        ", requiredWood=" + this.requiredWood);
                        
        this.currentStep = 0;
        this.planCompleted = false;
        this.completionAnnounced = false;
        this.locationToId = new HashMap<>();
        this.locationToPoint = new HashMap<>();
        this.lastProcessedTurn = 0;
        this.awaitingCommand = false;
        
        // Plan will be created after the agent is fully initialized
        this.plan = null;
    }

    @Override
    public Map<Integer, Action> initialStep(StateView stateView, HistoryView historyView) {
        System.out.println("Initializing agent...");
        refreshEnvironmentMappings(stateView);
        lastProcessedTurn = stateView.getTurnNumber();
        awaitingCommand = false;

        // Create initial plan if not already created
        if (plan == null) {
            System.out.println("Creating initial plan...");
            Planner planner = new Planner(requiredGold, requiredWood);
            plan = planner.plan();
            if (plan == null || plan.isEmpty()) {
                planCompleted = true;
                System.err.println("Warning: Initial planning failed to produce a valid plan");
            } else {
                System.out.println("Plan created successfully with " + plan.size() + " steps");
            }
        }

        if (planCompleted) {
            announcePlanCompletion();
        }

        return Collections.emptyMap();
    }

    @Override
    public Map<Integer, Action> middleStep(StateView stateView, HistoryView historyView) {
        refreshEnvironmentMappings(stateView);
        processFeedback(historyView, stateView.getTurnNumber());

        if (!awaitingCommand && currentStep >= plan.size()) {
            announcePlanCompletion();
        }

        if (planCompleted || awaitingCommand) {
            return Collections.emptyMap();
        }

        Action nextAction = buildSepiaAction(plan.get(currentStep));
        currentStep++;
        awaitingCommand = true;

        Map<Integer, Action> commands = new HashMap<>();
        commands.put(peasantId, nextAction);
        return commands;
    }

    @Override
    public void terminalStep(StateView stateView, HistoryView historyView) {
        announcePlanCompletion();
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
        if (planCompleted) {
            throw new IllegalStateException("Plan is already completed.");
        }
        if (plannerAction == null) {
            throw new IllegalArgumentException("plannerAction must not be null");
        }
        if (peasantId == null) {
            throw new IllegalStateException("Peasant identifier is not initialized.");
        }

        switch (plannerAction.type) {
            case MOVE:
                Point target = requireLocation(plannerAction.toLocation);
                return Action.createCompoundMove(peasantId, target.x, target.y);
            case HARVEST_GOLD:
            case HARVEST_WOOD:
                String resourceName = "";
                if (plannerAction.type == planner.Action.Type.HARVEST_GOLD) {
                    resourceName = "Mine" + (plannerAction.resourceIndex + 1);
                } else {
                    resourceName = "Forest" + (plannerAction.resourceIndex + 1);
                }
                Integer resourceId = requireId(resourceName);
                return Action.createCompoundGather(peasantId, resourceId);
            case DEPOSIT_GOLD:
            case DEPOSIT_WOOD:
                Integer depositId = requireId("Townhall");
                return Action.createCompoundDeposit(peasantId, depositId);
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

    private void refreshEnvironmentMappings(StateView stateView) {
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
        List<ResourceView> goldNodes = stateView.getResourceNodes(Type.GOLD_MINE);
        if (goldNodes != null) {
            for (ResourceView resource : goldNodes) {
                Point position = new Point(resource.getXPosition(), resource.getYPosition());
                String key = "Mine" + goldIndex++;
                locationToId.put(key, resource.getID());
                locationToPoint.put(key, position);
            }
        }

        int woodIndex = 1;
        List<ResourceView> woodNodes = stateView.getResourceNodes(Type.TREE);
        if (woodNodes != null) {
            for (ResourceView resource : woodNodes) {
                Point position = new Point(resource.getXPosition(), resource.getYPosition());
                String key = "Forest" + woodIndex++;
                locationToId.put(key, resource.getID());
                locationToPoint.put(key, position);
            }
        }

        if (peasantId == null) {
            throw new IllegalStateException("Expected to control a peasant but none was found.");
        }
    }

    private void processFeedback(HistoryView historyView, int currentTurn) {
        for (int turn = lastProcessedTurn; turn < currentTurn; turn++) {
            Map<Integer, ActionResult> feedback = historyView.getCommandFeedback(getPlayerNumber(), turn);
            if (feedback == null) {
                continue;
            }

            ActionResult result = feedback.get(peasantId);
            if (result == null) {
                continue;
            }

            ActionFeedback feedbackType = result.getFeedback();
            if (feedbackType == null) {
                continue;
            }

            if (feedbackType == ActionFeedback.INCOMPLETE
                    || feedbackType == ActionFeedback.INCOMPLETEMAYBESTUCK) {
                awaitingCommand = true;
            } else {
                awaitingCommand = false;
                if (feedbackType == ActionFeedback.FAILED) {
                    System.err.println("Warning: previous action reported failure on turn " + turn);
                }
            }
        }
        lastProcessedTurn = Math.max(lastProcessedTurn, currentTurn);
    }

    private boolean isPeasant(String name) {
        return "Peasant".equalsIgnoreCase(name) || name.toLowerCase().contains("peasant");
    }

    private boolean isTownhall(String name) {
        return "TownHall".equalsIgnoreCase(name) || "Townhall".equalsIgnoreCase(name);
    }

    private void announcePlanCompletion() {
        if (!planCompleted) {
            planCompleted = true;
        }
        if (!completionAnnounced) {
            System.out.println("Plan execution finished.");
            completionAnnounced = true;
        }
    }
}
