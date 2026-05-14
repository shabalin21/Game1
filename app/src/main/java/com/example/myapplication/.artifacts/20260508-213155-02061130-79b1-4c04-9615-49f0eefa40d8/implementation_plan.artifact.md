# Redesign: Immersive Cargo, Market, and Debug System

## User Review Required
- **Buff/Debuff Visuals**: I plan to add small floating icons around the Buddy to represent active buffs (e.g., a coffee cup for caffeine). Does this match the vision?
- **Cheat Menu Access**: I propose a long-press on the version number in the Settings screen to unlock the hidden Debug menu.
- **FPS Counter Location**: Defaulting to the top-left corner.

## Proposed Changes

### Core Models & Logic
Update item models to support complex effects and integrate into the simulation engine.

#### [ItemModel.kt](file:///C:/Users/stanislav/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/domain/model/ItemModel.kt)
- Add `buffType` to `StatEffect` (e.g., CAFFEINE, SUGAR_RUSH).
- Add `sideEffects` StatEffect for energy crashes.

#### [SimulationEngine.kt](file:///C:/Users/stanislav/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/domain/simulation/SimulationEngine.kt)
- Update `updateState` to handle side effects after buff expiration.
- Implement specific logic for buffs like "Hyperfocus" (+Performance, -Happiness).

---

### UI Components
Premium futuristic components for a cohesive cyber aesthetic.

#### [GlassCard.kt](file:///C:/Users/stanislav/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/ui/component/GlassCard.kt)
- Enhance glassmorphism with better blur and neon borders.

#### [CyberButton.kt](file:///C:/Users/stanislav/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/ui/component/CyberButton.kt) [NEW]
- Animated neon buttons with haptic feedback.

#### [ItemDetailDialog.kt](file:///C:/Users/stanislav/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/ui/component/ItemDetailDialog.kt)
- Redesign for full immersion: large icons, rarity glow, and detailed stat previews.

---

### Screens
Complete overhaul of Cargo/Market and addition of Settings/Debug.

#### [CargoScreen.kt](file:///C:/Users/stanislav/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/ui/screen/cargo/CargoScreen.kt)
- **Inventory**: Animated grid, rarity-colored glows, empty state with CTA.
- **Market**: Horizontal category tabs with smooth transitions, search bar, rotating deals.

#### [SettingsScreen.kt](file:///C:/Users/stanislav/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/ui/screen/settings/SettingsScreen.kt) [NEW]
- Toggles for FPS, sound, particles, and neon effects.
- Save management and reset options.

#### [DebugScreen.kt](file:///C:/Users/stanislav/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/ui/screen/debug/DebugScreen.kt) [NEW]
- Futuristic terminal UI for cheats: add coins, freeze stats, warp time.
- Real-time diagnostics overlay (recompositions, memory).

---

### Infrastructure
#### [MainActivity.kt](file:///C:/Users/stanislav/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/MainActivity.kt)
- Ensure `GlobalOverlay` handles all diagnostic types from settings.

#### [DevLabManager.kt](file:///C:/Users/stanislav/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/ui/debug/DevLabManager.kt)
- Expand to handle new cheat types (warp time, unlock all).

## Verification Plan

### Automated Tests
- Run existing unit tests: `./gradlew test`
- Add new tests for `SimulationEngine` buff/debuff logic.

### Manual Verification
- **Visual Audit**: Compare new screens against cyber-life-simulator style requirements.
- **Performance**: Monitor FPS in `GlobalOverlay` while navigating complex screens.
- **Persistence**: Verify settings and inventory survive app restarts.
- **Debug Tools**: Test each cheat in the `DebugScreen` and verify impact on gameplay.
