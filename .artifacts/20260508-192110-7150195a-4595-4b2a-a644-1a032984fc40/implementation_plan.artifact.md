# Soul Through Systems: Building a Believable Lifeform

This plan outlines the systemic shift from "Maintenance Simulation" to "Relationship Simulation," focusing on autonomy, continuity, and invisible UI.

## 1. The Temperament Engine (Beyond Stats)
We are moving away from flat 0-100 bars towards **Biased Personality Vectors**.

### [NEW] [Temperament.kt](file:///C:/Users/stanislav/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/domain/model/Temperament.kt)
- **Personality Vectors**: `Independence` (0-1), `Curiosity` (0-1), `Affection` (0-1), `Laziness` (0-1).
- **Influence**: These values don't just decay; they act as multipliers for the `BrainEngine`.
  - *High Laziness*: Creature sleeps longer and refuses to play even if energy is > 50%.
  - *High Curiosity*: Creature wanders around the screen more frequently during `Idle` state.

## 2. Narrative Memory Loop (Continuity)
The creature will anchor emotions to objects and times, creating "Soul."

### [NEW] [MemoryAnchor.kt](file:///C:/Users/stanislav/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/domain/model/MemoryAnchor.kt)
- **Object Attachment**: Tracks `InteractionCount` with specific items.
- **Narrative Effect**: If a creature has high attachment to a "Soft Bear" plushie, it gains +10 `Happiness` just by being near it in the room.

## 3. The 70/30 Autonomy Rule (BrainEngine)
The creature should feel like it has a life independent of the player.

### [BrainEngine.kt](file:///C:/Users/stanislav/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/domain/simulation/BrainEngine.kt)
- **Utility Scoring**:
  - `AutonomousUtility`: Calculated every 5-15 seconds.
  - `PlayerInputUtility`: Calculated when the player taps.
- **The Conflict**: If `AutonomousUtility(Sleep)` is 0.9 and the player calls for `Play`, the creature will **hesitate** or **refuse**, emphasizing its own will.

## 4. Empathy Over UI (Invisible HUD)
We will begin the transition to world-space cues.

### [HomeScreen.kt](file:///C:/Users/stanislav/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/ui/screen/home/HomeScreen.kt)
- **Reduction**: Fade out the `Stats Card` when the creature is `Happy` and `Stable`.
- **World-Space Cues**: If `Hunger` is high, the creature will move towards the "FEED" button area (the fridge) rather than just showing a badge.

## 5. Persistence (The Ghost in the Machine)
The creature lives while the app is closed.

### [SimulationEngine.kt](file:///C:/Users/stanislav/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/domain/simulation/SimulationEngine.kt)
- **Offline Resolution**: When the player returns, the engine doesn't just decay stats; it **generates a Narrative Summary** (e.g., "Buddy missed you and spent the afternoon looking at the window").

---

## 6. Verification Plan

### Automated
- `TemperamentTest`: Verify that a `Lazy` creature has a different energy recovery curve.
- `MemoryAnchorTest`: Verify attachment levels increase with specific item usage.

### Manual
- **Autonomy Watch**: Observe the creature for 60 seconds without input. It should transition between at least 3 distinct "Autonomous Intents" (e.g., Pacing -> Staring -> Resting).
- **Refusal Test**: Drain the creature's energy to 5% and try to initiate a "Play" action. It should display a "Refusal" animation instead of starting the game.
