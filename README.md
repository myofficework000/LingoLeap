# LingoLeap — multilingual learning app starter

LingoLeap is a buildable Android starter for an Indian-language learning app inspired by short, game-like learning loops. It deliberately supplies architecture, local data, navigation, MVI contracts, and empty Compose entry points—not finished UI or ViewModel logic—so four students can work independently without fighting over the same files.

## Stack

- Kotlin 2.3.21, Android Gradle Plugin 9.3.2, Gradle 9.6, Java 17
- API 37 (`compileSdk` / `targetSdk`), Jetpack Compose BOM 2026.08.00, Material 3 and Navigation Compose
- Hilt 2.60.1 with KSP (no kapt), ViewModel dependencies, Coroutines, Gson, and Glide Compose
- Single activity, feature-first clean architecture and unidirectional MVI contracts



<img width="1024" height="1536" alt="cab2c957-03ce-4e77-818a-4d1a2d548276" src="https://github.com/user-attachments/assets/444ad978-712b-46da-a297-674a12abaaca" />

```bash
./gradlew assembleDebug
```

## Architecture

```text
app/src/main/java/com/lingoleap
├── core/mvi                 # UiState, UiEvent and UiEffect markers
├── data
│   ├── local                # JSON asset reader
│   └── repository           # LearningRepository implementation
├── di                       # Hilt bindings
├── domain
│   ├── model                # Pure course, language, lesson, quiz models
│   ├── repository           # Repository boundary
│   └── usecase              # Read use cases
└── presentation
    ├── feature              # One folder per feature; screen + MVI contract
    ├── navigation           # Stable route definitions
    └── theme
```

`assets/learning_catalog.json` is the temporary backend. It already contains 15 Indian languages and bidirectional English pair examples, course/lesson/vocabulary/quiz/progress sample data. The flow is fully implemented:

```text
learning_catalog.json → LearningCatalogDataSource → LearningRepositoryImpl → use cases
```

No ViewModel reducer, UI rendering, or remote backend is implemented on purpose. Each screen accepts immutable `State` and `onEvent`, and its feature file declares the event types and sub-composable signatures. Students can implement independently while preserving the contract.

## ownership — first milestone

| Student | Owns exactly these two screens | Primary files | Expected journey |
|---------|---|---|---|
| Himaja  | Onboarding, language picker | `feature/onboarding`, `feature/language` | Welcome → select source and target language |
| Sriteja | Home dashboard, lesson | `feature/home`, `feature/lesson` | See streak/course → complete vocabulary cards |
| Tiru    | Quiz, practice | `feature/quiz`, `feature/practice` | Answer a quiz → word-match/listening/fill-blank practice |
| Dhyan   | Progress, profile | `feature/progress`, `feature/profile` | Track XP/streak → manage learner profile |

Each student should only edit their two feature folders plus tests. Shared changes to models, routes, dependency injection, or JSON should be proposed separately to avoid merge conflicts.

## Delivery order

1. Implement each feature's `@HiltViewModel`: load its use case, reduce events into state, and expose one-off navigation/message effects.
2. Implement the supplied screen and sub-composable signatures using Material 3.
3. Wire real navigation only after Student A/B agree on language/course identifiers.
4. Add completion writes to a local persistence layer (Room/DataStore) after the mock-data milestone.
5. Add stretch screens: splash, lesson list, achievement gallery, and gamified path map.

## Git handoff

```bash
git init
git add LingoLeap
git commit -m "chore: bootstrap LingoLeap clean architecture"
```

Use small feature branches, e.g. `feature/onboarding-ui`, `feature/quiz-mvi`, and keep this starter branch as the integration baseline.
