# LingoLeap

LingoLeap is an offline-first Android app for learning Indian languages through short, game-like lessons. It supports English ↔ regional-language learning with a complete local learner flow—no account or network connection required.

<img width="1024" height="1536" alt="LingoLeap prototype and supported app flow" src="https://github.com/user-attachments/assets/444ad978-712b-46da-a297-674a12abaaca" />

## What works

- Onboarding, language-pair selection, daily learning-goal setup, and session restoration.
- English plus 14 Indian regional languages, with 28 English ↔ regional language pairs.
- Course overview, structured lesson path, locked/completed lesson progression, and lesson recap.
- Vocabulary cards with device text-to-speech pronunciation fallback.
- Deterministic lesson quizzes, word matching, listening practice, and fill-in-the-blank exercises.
- Course-aware Practice tab, daily challenges, XP, streaks, progress statistics, achievements, and a gamified learning path.
- Missed quiz words are saved in a local review deck and can be marked as mastered.
- Local settings let a learner reset progress without changing the chosen language.
- Profile Help & Support provides offline guidance; Restart setup returns safely to onboarding without a Firebase account.
- Material 3 light/dark themes with centralised primary, secondary, progress, surface, and error colour tokens.

## Learner journey

```text
Splash → Onboarding → Choose language pair → Set daily goal → Home
Home → Course overview → Lesson → Recap → Quiz → Practice → Home
Home/Profile → Progress, achievements, daily challenge, review deck, settings
```

All learner progress is stored on the device, so the core journey works in airplane mode.

## Offline content

The bundled JSON content provides 14 regional-language curricula, 84 beginner lessons, 168 vocabulary entries, generated quiz data, 14 daily challenges, and achievement definitions.

```text
learning_catalog.json → LearningCatalogDataSource → LearningRepository → use cases → ViewModels → Compose UI
```

Content files:

- `app/src/main/assets/learning_catalog.json` — languages, language pairs, lessons, words, and starter progress.
- `app/src/main/assets/daily_challenges.json` — daily challenges, hints, and XP rewards.
- `app/src/main/assets/achievements.json` — achievement rules and thresholds.
- `app/src/main/assets/learning_catalog.schema.json` — content authoring contract.

## Architecture

```text
app/src/main/java/com/lingoleap
├── core/mvi                 UiState, UiEvent, UiEffect contracts
├── data                     JSON sources, DataStore, Room, repositories
├── di                       Hilt modules
├── domain                   models, repository interfaces, use cases
└── presentation             Compose features, navigation, Material theme
```

The app uses a single-activity Compose setup, Hilt dependency injection, immutable `StateFlow` UI state, repository/use-case boundaries, DataStore for preferences, and Room for offline learner progress and review words.

## Tech stack

- Kotlin 2.3.21, Java 17, Gradle 9.6, Android Gradle Plugin 9.3.2
- `compileSdk` / `targetSdk` 37
- Jetpack Compose, Material 3, Navigation Compose, Lifecycle ViewModels
- Hilt 2.60.1 with KSP
- Kotlin Coroutines and Flow
- Room, DataStore Preferences, Gson, Glide Compose

## Build and verify

```bash
./gradlew testDebugUnitTest assembleDebug
```

The app has been verified with the command above. JSON assets can be checked with:

```bash
jq empty app/src/main/assets/learning_catalog.json
jq empty app/src/main/assets/daily_challenges.json
jq empty app/src/main/assets/achievements.json
```

## Firebase boundary

Firebase is deliberately not included yet. The app is fully usable offline; Firebase can later provide optional sign-in, backup/sync of learner progress, remote configuration, analytics, and crash reporting. Curriculum content remains local JSON so learning continues without an internet connection.

For the full local-first strategy, see [docs/OFFLINE_FIRST_PLAN.md](docs/OFFLINE_FIRST_PLAN.md).
