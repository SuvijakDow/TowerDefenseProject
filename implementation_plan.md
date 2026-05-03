# Tower Defense Project Roadmap

This implementation plan structures your vision into logical development phases. It covers everything from core game mechanics to UI implementation and visual polish, ensuring all requested features (and some essential TD mechanics you might have missed) are addressed.

---

## 🛑 Missing & Suggested Features
1. **Victory Screen:** You mentioned "Game Over" when HP hits 0, but there must be a "Victory" condition when the player survives the 3-minute wave cycle.
2. **Upgrade & Sell:** Players may want to sell misplaced towers or upgrade them for higher damage. (The `Upgradable` interface is already in the codebase).
3. **Game Speed & Pause:** A 3-minute run can feel slow during easy waves. A "Pause" and "Speed Up (x2)" button are standard Quality of Life features.
4. **Audio System:** BGM and SFX (shooting, enemy hit, coin pickup) drastically improve game feel.

---

## 📋 Implementation Plan

### Phase 1: Core Gameplay & Wave System (The Foundation)
We will focus on getting a full, playable game loop from start to finish.

- [ ] **Economy & Base Health System:**
  - Initialize starting money so players can buy at least 1-2 starting towers.
  - Set specific castle damage values for different enemies (e.g., Slime = 1 DMG, Boss = 5 DMG).
  - Set specific reward money values upon enemy death based on enemy type.
- [ ] **Predefined Wave System:**
  - Create a `WaveManager` class to strictly control the pacing. It will spawn predefined enemy types at specific intervals throughout the 3-minute game length.
- [ ] **Tower Mechanics & Projectile Polish:**
  - Fix projectile spawn origins: offset the spawn Y-coordinate so projectiles shoot from the "head/weapon" of the tower, not the bottom base.
  - **Projectile Rotation:** Update the `Projectile` drawing logic in `GameView` or `Projectile` class to rotate the sprite (specifically Archer/Crossbow arrows) to point towards the target enemy using `Math.atan2(dy, dx)`.
  - Draw a semi-transparent **Range Indicator Circle** around the tower when hovered or during placement.

### Phase 2: User Interface & Menus (Using Provided Assets)
We will integrate your custom assets from `resources/Alphabets`, `resources/Numbers`, and `resources/Fonts`.

- [ ] **Main Menu:**
  - Create a Start Screen with "Start" and "Exit" options.
- [ ] **In-Game HUD (Head-Up Display):**
  - Top-Left: Display Base HP (using the heart icon) and current Money (using the coin icon). We will use your `resources/Fonts` or image-based numbers to display the values.
  - Bottom/Side Panel: Implement a **Tower Shop** UI where players can click to buy different towers (Archer, Cannon, Wizard), showing their respective prices.
- [ ] **End Game Screens:**
  - **Game Over:** Triggered when Base HP <= 0, featuring a "Return to Menu" button.
  - **Victory:** Triggered when the final wave is cleared, featuring a "Return to Menu" button.

### Phase 3: Game Feel & Polish (Visual Feedback)
Making the game feel dynamic, rewarding, and responsive.

- [ ] **Damage & Death Effects:**
  - **Hit Flash:** Enemies flash red briefly when taking damage.
  - **Floating Damage Numbers:** Display the exact damage dealt popping out of the enemy and floating upwards.
  - **Death Feedback:** When an enemy dies, display a particle effect (smoke/explosion) and spawn a **Floating Coin Icon** that drifts up to signify earned money.
- [ ] **Projectile Impact Polish:**
  - Add small hit effects when projectiles strike (e.g., ice shatters, poison splatters).
- [ ] **Quality of Life (Future Expansion):**
  - Implement a clickable menu on existing towers to show "Upgrade" and "Sell" buttons.
  - Add a wave progress bar or timer.

---

## ❓ Open Questions / Review
> [!IMPORTANT]
> Please review this plan and confirm:
> 1. Do you approve starting immediately with **Phase 1: Economy & Wave System**?
> 2. For the UI text, would you prefer I use the standard JavaFX `Text` with your custom font from `resources/Fonts`, or should I build a custom renderer to manually piece together the letter/number images from `resources/Alphabets` & `resources/Numbers`? (Using the Font file is significantly easier and more performant).
