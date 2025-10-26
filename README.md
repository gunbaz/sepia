# SEPIA Resource Collection Planner

Name: 

Student ID: 

This project provides a skeleton implementation for a SEPIA state-space planner assignment. It is structured as a Maven project and includes placeholders for the core planning components that must be implemented to complete the assignment.

## Project Overview

The assignment focuses on building an A* planner to solve a resource collection scenario. Key tasks include:

- Modeling the game state, resources, and peasant actions.
- Designing STRIPS-style actions for moving, harvesting, and depositing resources.
- Implementing the A* search algorithm with an appropriate heuristic.
- Integrating the planner with the SEPIA agent lifecycle.

## Project Structure

```
sepia/
├── lib/
│   └── sepia.jar (place the provided SEPIA library here)
├── src/
│   └── main/
│       └── java/
│           └── edu/
│               └── cwru/
│                   └── sepia/
│                       └── agent/
│                           ├── RCAgent.java
│                           └── planner/
│                               ├── AStarPlanner.java
│                               ├── GameState.java
│                               ├── StripsAction.java
│                               └── AStarNode.java
├── .gitignore
├── pom.xml
└── README.md
```

## Building the Project

This project uses Maven for dependency management and builds. Ensure that `sepia.jar` is placed in the `lib/` directory before building.

```
mvn clean package
```

## Next Steps

1. Implement the STRIPS actions required for the planner.
2. Define the heuristic function used by `AStarPlanner`.
3. Complete the logic in `GameState` for generating successor states.
4. Flesh out the `RCAgent` to integrate the planner with SEPIA's agent lifecycle.

Refer to the assignment specification for detailed requirements and grading criteria.
