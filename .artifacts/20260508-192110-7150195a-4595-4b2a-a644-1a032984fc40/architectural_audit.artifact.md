# Architectural Audit: Current State vs. Living Digital Organism

## 1. Executive Summary
The project has a robust MVVM + Clean Architecture foundation. It uses Hilt for DI and Room for persistence. The simulation is decoupled from the UI, but currently follows a **linear, deterministic path** rather than an **emergent, stochastic one**.

## 2. Component Analysis

### Core Simulation (`SimulationEngine`)
*   **Existing**: Linear decay of stats based on elapsed time.
*   **Gap**: No influence from psychology (e.g., stress doesn't affect hunger decay). No feedback loops.
*   **Risk**: High predictability leads to player boredom.

### Emotional System (`MoodEngine`)
*   **Existing**: Threshold-based mood derivation (e.g., `hunger < 15 -> ANGRY`).
*   **Gap**: Lacks **Emotional Inertia**. Mood switches instantly. No persistence of historical trauma or joy.
*   **Risk**: Creature feels like a finite-state machine rather than a living being.

### AI Behavior (`PetInteractionSystem`)
*   **Existing**: Direct command-response (e.g., `feed()` always works).
*   **Gap**: No **Autonomy**. The creature has no agency to refuse or initiate.
*   **Risk**: Creature feels like a tool/toy rather than a companion.

### Atmosphere & Immersion (`AtmosphereManager`)
*   **Existing**: Basic lighting/particle shifts based on time and weather.
*   **Gap**: No connection to **Psychology**. The world doesn't reflect the creature's internal state.
*   **Risk**: Low emotional resonance.

## 3. Implementation Priorities (The Director's Pipeline)

1.  **Memory Integration**: Inject a `Memory interpretation` layer before mood resolution.
2.  **Desire Evaluation**: Introduce `DesireState` to the `SimulationEngine` as a motivational pressure.
3.  **BrainEngine Utility Scoring**: Replace direct UI-to-Simulation commands with `BehaviorIntent` requests.
4.  **Emotional Resolution**: Implement `EmotionalInertia` to slow down mood transitions.

## 4. Technical Debt & Risks
*   **Room Schema**: Migrating `PetEntity` to include complex JSON fields (Memories/Desires) will increase save/load overhead. Need to ensure `Json` serialization is optimized.
*   **Recomposition**: The `PetSprite` animation loop is efficient, but adding more complex behavior-based animations could pressure the UI thread.
*   **Coroutine Chaos**: Autonomous behavior will require carefully managed `Job` hierarchies to prevent leaks during screen transitions.
