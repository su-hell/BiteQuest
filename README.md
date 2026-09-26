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



### AI Prompt 1: Architecture & Package Decomposition
> **My Prompt:**  
> *"I am developing an offline Android educational game in Java 11 called 'Healthy Bite Quest'. Children learn nutrition by dragging food cards into 'Healthy' and 'Less Healthy' drop zones.  
> Constraints: Offline only, no Firebase, no SQLite/Room. 10 questions per round, 10 points per correct answer, with unlockable badges.  
> How should I organize the packages and classes so that domain logic (scoring, questions, badges) remains decoupled from Android Activities for easy unit testing?"*

* **AI Response Summary:**  
  The AI recommended separating the project into distinct packages:
  1. `model/`: Plain Java POJOs (`FoodItem`, `PlayerProgress`) holding immutable state.
  2. `game/`: Pure Java classes (`GameManager`, `BadgeCalculator`) containing all gameplay rules, state transitions, and scoring formulas with zero dependencies on `android.*` packages.
  3. `storage/`: `LocalStorageManager` wrapping Android `SharedPreferences` to isolate persistence.
  4. `utils/`: Asynchronous JSON parser and sound player helpers.
  5. UI Activities: Serving purely as controllers binding views to the domain models.
* **Outcome:**  
  Adopted the proposed package architecture directly, which allowed running all core game logic tests in pure JVM unit tests without needing Robolectric or device emulators.

---

### AI Prompt 2: Data Modeling and JSON Validation
> **My Prompt:**  
> *"Write a `FoodItem` class representing items loaded from `assets/foods.json`. Each item has `id`, `name`, `category`, `image`, and `explanation`.  
> How can I make sure the model handles null fields or unexpected values gracefully?"*

* **AI Response Summary:**  
  The AI provided an immutable `FoodItem` class with private final fields initialized via constructor. It suggested defaulting null strings to empty strings (`""`) to prevent potential `NullPointerExceptions` when calling `.equalsIgnoreCase()` or displaying text in views.
* **My Own Refinement / Fix:**  
  * While the AI provided basic null safety, it did not validate domain correctness. I added an explicit `isValid()` method that verifies that `id > 0`, `name` and `explanation` are not blank after trimming, and `category` strictly matches either `"healthy"` or `"less_healthy"`. This guarantees that corrupted or malformed entries in `foods.json` are filtered out before starting a game round.

---

### AI Prompt 3: Round Flow & Seeded Testability in GameManager
> **My Prompt:**  
> *"Write a `GameManager` class for managing a single 10-question round. It should take a list of all foods, randomly select 10 items, calculate score (+10 per correct answer), and track the current question index.  
> How do I write this so I can verify random question selection in unit tests?"*

* **AI Response Summary:**  
  The AI supplied a `GameManager` that filters valid foods, shuffles the list using `Collections.shuffle()`, and takes a sublist of 10 items. It provided state management methods: `getCurrentFood()`, `checkAnswer(String category)`, `moveToNextQuestion()`, and `isFinished()`.
* **My Own Refinements / Fixes:**  
  * **Testability fix:** The AI's initial `startNewGame(List<FoodItem>)` used an internal unseeded `new Random()`, which produced non-deterministic results in unit tests. I overloaded the method with `startNewGame(List<FoodItem> allFoods, Random random)` so tests could pass a deterministic `new Random(1)` for predictable assertions.
  * **Double-scoring prevention:** During manual testing, I noticed that repeatedly dropping the card on the correct zone before clicking 'Continue' added 10 points on each drop. I added a `currentQuestionResolved` boolean flag to ensure points are awarded exactly once per question.

---

### AI Prompt 4: Real Android Drag-and-Drop Implementation
> **My Prompt:**  
> *"In `GameActivity`, I need real touch drag-and-drop. When the user touches the food card, it should start dragging with a visible shadow. When hovered over the Healthy or Less Healthy boxes, the boxes should highlight. When dropped, it should detect the target category.  
> How do I implement `View.startDragAndDrop` and `View.OnDragListener` in Java?"*

* **AI Response Summary:**  
  The AI provided code demonstrating Android's native Drag-and-Drop framework:
  * Calling `view.startDragAndDrop(clipData, new View.DragShadowBuilder(view), view, 0)` inside an `OnTouchListener` on `MotionEvent.ACTION_DOWN`.
  * Setting an `OnDragListener` on the drop targets that listens for `DragEvent.ACTION_DRAG_ENTERED` (setting `alpha` to `0.7f`), `ACTION_DRAG_EXITED` (restoring `alpha` to `1.0f`), and `ACTION_DROP` (retrieving target view ID to determine category).
* **My Own Refinements / Fixes:**  
  * **Touch conflict fix:** Because the layout was inside a scrollable container, initiating a drag frequently triggered parent vertical scrolling instead of the drag shadow. I resolved this by adding `view.getParent().requestDisallowInterceptTouchEvent(true);` on `ACTION_DOWN`.
  * **Alpha reset bug:** If the user dragged a card into a drop box and released it outside of any valid drop target, the box remained permanently semi-transparent. I handled `ACTION_DRAG_ENDED` in addition to `ACTION_DRAG_EXITED` to ensure the view's alpha was always restored to `1.0f`.

---

### AI Prompt 5: Asynchronous Asset Loading
> **My Prompt:**  
> *"My assignment requires loading `assets/foods.json` asynchronously so the UI thread doesn't freeze.  
> How should I implement `CompletableFuture` in Java 11 on Android to read the JSON asset on a background thread and safely return the list of items to the main UI thread?"*

* **AI Response Summary:**  
  The AI explained that `CompletableFuture.supplyAsync()` delegates file reading and JSON parsing (`org.json.JSONObject`) to the background `ForkJoinPool.commonPool()`. It provided:
  ```java
  public CompletableFuture<List<FoodItem>> loadFromAssetsAsync(Context context) {
      Context appContext = context.getApplicationContext();
      return CompletableFuture.supplyAsync(() -> {
          try {
              return loadFromAssets(appContext);
          } catch (Exception e) {
              throw new CompletionException(e);
          }
      });
  }
  ```
  It also explained how `GameActivity` handles the result with `.thenAccept()` and dispatches UI updates via `runOnUiThread()`, while catching errors with `.exceptionally()`.
* **Outcome:**  
  Implemented directly as advised. Added `panelLoading`, `panelGame`, and `panelError` view containers in `activity_game.xml` to present clean loading, active game, and error/retry states.

---

### AI Prompt 6: SharedPreferences Persistence Strategy
> **My Prompt:**  
> *"I need to save player progress locally using Android SharedPreferences.  
> Values to store: player name, total points, highest score, games completed, correct answers, total questions, and unlocked badges.  
> Example requirement: If a player has 100 points and earns 30 in a game, they must have 130 points even if the app closes and reopens.  
> How should `LocalStorageManager` be structured?"*

* **AI Response Summary:**  
  The AI suggested creating a helper class wrapping `SharedPreferences` initialized with `Context.MODE_PRIVATE`. It suggested getter/setter methods for integer stats and storing completed game results in a single batch editor transaction (`editor.apply()`).
* **My Own Refinements / Fixes:**  
  * **Immediate persistence:** The AI's initial solution only saved points at the end of a round. If a student played 7 questions and the app was killed or closed, those earned points were lost. I added a dedicated `addPoints(int points)` method that commits points immediately upon every correct drop during gameplay.
  * **Badge serialization:** To avoid well-known bugs where `SharedPreferences.getStringSet()` fails to persist in-place set mutations, I implemented custom serialization joining badge names with a pipe delimiter (`joinBadges` with `|` and `split("\\|")`).

---

### AI Prompt 7: Sound Feedback with SoundPool
> **My Prompt:**  
> *"I want to play short audio cues for correct (`success.wav`) and incorrect (`incorrect.wav`) answers.  
> Should I use `MediaPlayer` or `SoundPool`, and how do I set up a helper class in Java?"*

* **AI Response Summary:**  
  The AI explained that `MediaPlayer` has noticeable initialization latency and higher memory overhead, making it unsuitable for rapid gameplay interactions. It recommended `SoundPool` configured with `AudioAttributes.USAGE_GAME` and `CONTENT_TYPE_SONIFICATION` for low-latency playback. It provided the setup:
  ```java
  AudioAttributes attributes = new AudioAttributes.Builder()
          .setUsage(AudioAttributes.USAGE_GAME)
          .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
          .build();
  soundPool = new SoundPool.Builder()
          .setMaxStreams(2)
          .setAudioAttributes(attributes)
          .build();
  ```
* **Outcome:**  
  Integrated `SoundPlayer` following this design. Verified that sounds load from `res/raw/` and that the application safely continues if audio fails on older emulator configurations.

---

### AI Prompt 8: Score Milestone Badge Calculator
> **My Prompt:**  
> *"I need simple badge unlocking rules based on the game score:  
> - 50+ points: 'Food Explorer'  
> - 80+ points: 'Healthy Hero'  
> - 100 points: 'Nutrition Champion'  
> Can you write a standalone helper class that returns all earned badges and an encouraging summary message?"*

* **AI Response Summary:**  
  The AI wrote `BadgeCalculator` with two static methods:
  * `badgesForScore(int score)`: Returns a `List<String>` containing all badges achieved up to the player's score.
  * `summaryForScore(int score)`: Returns age-appropriate encouraging strings (e.g., *"Perfect! You are a Nutrition Champion!"* or *"Keep practising — you can unlock a badge next time!"*).
* **Outcome:**  
  Adopted the implementation cleanly into `com.healthybitquest.app.game.BadgeCalculator` and connected its strings to `ResultActivity` and `ProgressActivity`.

---

### AI Prompt 9: Unit Testing & Espresso UI Testing
> **My Prompt:**  
> *"I need to write unit tests for `GameManager` using JUnit 4 and UI instrumented tests using Espresso.  
> What test cases will prove that scoring, question limits, and home screen navigation work correctly?"*

* **AI Response Summary:**  
  The AI proposed:
  * JVM tests in `GameManagerTest`: Testing +10 points for correct category, 0 points for incorrect category, score calculation formula (`calculateScore(8) == 80`), and round completion after 10 questions.
  * Espresso test in `HomeScreenTest`: Using `ActivityScenario.launch(HomeActivity.class)` to verify that `textWelcome` displays the player's name and clicking `buttonProgress` navigates to `ProgressActivity`.
* **My Own Refinement / Fix:**  
  * **Test Isolation:** When running instrumented tests on an emulator, the tests frequently passed individually but failed when run together because data persisted in `SharedPreferences` from previous runs. I added a `@Before` setup method in `HomeScreenTest.java` and `LocalStoragePersistenceTest.java` that clears preferences (`.clear().commit()`) before each test execution.
