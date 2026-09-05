# Healthy Bite Quest – Learning Healthy Eating Through Fun Gaming

University Android prototype (Java) that teaches children aged 8–15 to sort foods into **Healthy** and **Less Healthy** categories using drag-and-drop.

## Project description

Healthy Bite Quest is a small offline educational game. A child sees one food at a time, drags it onto the matching box, earns 10 points for a correct choice, and builds simple badges. Progress is stored on the device so points remain after the app is closed.

This is a focused assessment prototype, not a full commercial product.

## Selected user story

**As a child, I want to drag food items into the correct healthy or unhealthy category so that I can learn about healthy food choices while earning points and rewards.**

Persona: Sanju Bhujel, 10-year-old primary school student.

## Features implemented

- First-launch player name (saved locally; can be changed from Home)
- Home screen with welcome, total points, badge count, Play Game, and My Progress
- Simple instructions screen
- 10-question game using foods from local JSON
- Real Android drag-and-drop onto Healthy / Less Healthy drop zones
- +10 points for a correct drop; “Try Again!” plus a short explanation if incorrect
- Success and incorrect sounds
- Result screen (score, correct/incorrect, accuracy, badges)
- Progress screen reading all stored stats
- Simple badges: Food Explorer (50+), Healthy Hero (80+), Nutrition Champion (100)
- Async JSON loading with a loading indicator
- JUnit unit tests and Android instrumented tests

## Technology used

- Java 11
- Native Android (XML layouts, Activities)
- AndroidX AppCompat + Material Components
- SharedPreferences for local storage
- `assets/foods.json` for game content
- `CompletableFuture` for background JSON loading
- Android drag-and-drop APIs (`startDragAndDrop` / `OnDragListener`)
- JUnit 4, Espresso, AndroidX Test
- No Kotlin, no Firebase, no remote database, no backend

Minimum SDK: **24**. Target / compile SDK: **35**.

## Local Storage approach

Class: `LocalStorageManager` wrapping **SharedPreferences**.

Stored values:

- Player name
- Total points (updated immediately when a correct answer is scored)
- Highest score
- Games completed
- Correct answers
- Total questions attempted
- Unlocked badges

Example persistence check: 100 points, then +30 in a game → **130** still shows after closing and reopening the app.

No NoSQL or SQL database is used.

## Async approach

When the game starts, `GameActivity` shows a loading indicator and calls `JsonLoader.loadFromAssetsAsync()`. That method uses `CompletableFuture.supplyAsync()` so JSON is read off the main thread. Results (or errors) are posted back to the UI thread. The UI does not freeze while assets load. If loading fails, the player sees “Unable to load game data. Please try again.”

## Touch / drag-and-drop implementation

The food card starts an Android drag on **touch down** (`MotionEvent.ACTION_DOWN` + `View.startDragAndDrop`). The two category boxes implement `OnDragListener` and handle `ACTION_DROP`. This is real drag-and-drop, not a button click. Dropping on the matching category awards points; dropping on the wrong category shows feedback and lets the child try again or continue.

## Testing approach

| Test | What it covers | Where |
| --- | --- | --- |
| 1 Correct classification | Right category → +10 | `GameManagerTest` |
| 2 Incorrect classification | Wrong category → 0 points + explanation | `GameManagerTest` |
| 3 Score calculation | 8 correct = 80 points | `GameManagerTest` |
| 4 Progress persistence | Save points, new storage instance still reads 130 | `LocalStoragePersistenceTest` (instrumented) |
| 5 Game completion | 10 questions → game finished | `GameManagerTest` + `ResultScreenTest` |
| 6 Badge calculation | 50 / 80 / 100 unlock rules | `BadgeCalculatorTest` |
| 7 Local JSON loading | Parse sample foods JSON | `JsonLoaderTest` |
| 8 UI | Home welcome/points/buttons; Progress reads storage | `HomeScreenTest` (Espresso) |

Unit tests run on the JVM. Instrumented tests need an emulator or device.

```
./gradlew test
./gradlew connectedAndroidTest
```

On Windows: `gradlew.bat test`

## How to run the project in Android Studio

1. Open Android Studio.
2. Choose **Open** and select this folder (`BiteQuest`).
3. Wait for Gradle sync (internet is needed the first time to download Gradle and Android libraries).
4. If asked, use the bundled JDK or JDK 17+.
5. Create/start an Android emulator (API 24+) or plug in a device with USB debugging.
6. Click **Run** (green triangle) on the `app` configuration.
7. First launch: enter a name (for example `Sanju`) and play.

The app works fully **offline** after the first Gradle download. No internet permission is used.

### Command line

```
gradlew.bat assembleDebug
gradlew.bat test
```

## Why NoSQL, geolocation, microphone, and camera were not used

The assessment is built around **one user story**: dragging food into healthy / less-healthy boxes.

- **NoSQL / remote databases:** A single-player offline prototype only needs bundled JSON for questions and SharedPreferences for progress. A database would add complexity without helping this story.
- **Geolocation:** Food classification does not depend on location.
- **Microphone / camera:** The child interacts by touching and dragging on-screen food cards. Capture hardware is unrelated and would distract from a simple, reliable prototype.

Relevant assessment topics that **are** implemented: layouts/UI, async (`CompletableFuture`), performance (local assets, no network, load-once JSON), local storage, touch/drag events, simple multimedia, and testing.

## Project structure (main code)

```
app/src/main/java/com/healthybitquest/app/
  MainActivity.java, HomeActivity.java, NameActivity.java
  InstructionsActivity.java, GameActivity.java
  ResultActivity.java, ProgressActivity.java
  model/FoodItem.java, model/PlayerProgress.java
  storage/LocalStorageManager.java
  game/GameManager.java, game/BadgeCalculator.java
  utils/JsonLoader.java, utils/SoundPlayer.java
app/src/main/assets/foods.json
```

## Sounds

Short original WAV files are included in `app/src/main/res/raw/` (`success.wav`, `incorrect.wav`). They are not copied from copyrighted media.
