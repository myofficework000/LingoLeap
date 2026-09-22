# LingoLeap — multilingual learning app

LingoLeap is a buildable Android app for short, game-like Indian-language learning loops. The prototype reference image below defines the intended journey and visual direction; the implementation uses Material 3 tokens rather than copied hard-coded screen colors.

## Stack

- Kotlin 2.3.21, Android Gradle Plugin 9.3.2, Gradle 9.6, Java 17
- API 37 (`compileSdk` / `targetSdk`), Jetpack Compose BOM 2026.08.00, Material 3 and Navigation Compose
- Hilt 2.60.1 with KSP (no kapt), ViewModel dependencies, Coroutines, Gson, and Glide Compose
- Single activity, feature-first clean architecture and unidirectional MVI contracts
- Central Material 3 light/dark color schemes: green primary actions, blue secondary selections, orange progress accents, and accessible error/surface tokens



<img width="1024" height="1536" alt="cab2c957-03ce-4e77-818a-4d1a2d548276" src="https://github.com/user-attachments/assets/444ad978-712b-46da-a297-674a12abaaca" />

```bash
./gradlew assembleDebug
```

## What is working now

- Hilt application, local JSON catalog, repository boundary, and use cases.
- Onboarding → language selection → Home navigation with intermediate onboarding routes removed from the back stack.
- Home, lessons list, lesson, quiz, practice, progress, profile, and achievements routes.
- Four-tab bottom navigation for Home, Learn, Practice, and Profile; profile effects also route to language selection, statistics/progress, and achievements.
- Lifecycle-aware `StateFlow` collection. Independent catalog reads load concurrently and disk JSON reads run on `Dispatchers.IO`.
- One source of truth for primary/secondary/error/surface colors in `presentation/theme`.
- Phase 2 foundations: DataStore session/language preferences, Room-backed learner progress, a platform-neutral pronunciation player, lesson completion persistence, achievement rules, and learning-path state derivation.
- Gamified learning path: progress-aware completed, current, and locked lesson nodes with direct navigation into an available lesson.
- Daily Challenge: a data-driven, MVI-based quiz flow with immediate feedback and session XP totals.

## Architecture

The product roadmap and the Firebase boundary are documented in [Offline-first plan](docs/OFFLINE_FIRST_PLAN.md). Learning content stays bundled and functional without a connection; Firebase is reserved for optional user backup and release operations.

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

`assets/learning_catalog.json` is the temporary backend. Catalog v2 contains English plus 14 regional-language curricula, 28 bidirectional English language pairs, 84 generated beginner lessons, 168 vocabulary entries, deterministic quiz data, and sample progress. `assets/daily_challenges.json` adds 14 language-specific daily challenges with hints and XP rewards. A single regional curriculum generates both directions, so translations do not drift between English → regional and regional → English courses. `assets/learning_catalog.schema.json` documents the authoring contract for future content additions. The flow is fully implemented:

```text
learning_catalog.json → LearningCatalogDataSource → LearningRepositoryImpl → use cases
```

The local catalog is intentionally retained as a temporary backend. Feature ViewModels expose immutable state and events; persistence, audio playback, and authenticated profiles remain the next iteration.

## Completed ownership — milestone one

| Student | Owns exactly these two screens | Primary files | Expected journey |
|---------|---|---|---|
| Himaja  | Onboarding, language picker | `feature/onboarding`, `feature/language` | Welcome → select source and target language |
| Sriteja | Home dashboard, lesson | `feature/home`, `feature/lesson` | See streak/course → complete vocabulary cards |
| Tiru    | Quiz, practice | `feature/quiz`, `feature/practice` | Answer a quiz → word-match/listening/fill-blank practice |
| Dhyan   | Progress, profile | `feature/progress`, `feature/profile` | Track XP/streak → manage learner profile |

Each student should only edit their two feature folders plus tests. Shared changes to models, routes, dependency injection, or JSON should be proposed separately to avoid merge conflicts.

## Next ownership — milestone two

| Student | Two next features | Deliverable |
|---|---|---|
| Himaja | Splash/session restore, language preferences | Persist onboarding and selected language pair with DataStore; restore the correct start route. |
| Sriteja | Lesson player, audio pronunciation | Add real word paging, progress saving, and an audio abstraction with accessibility labels. |
| Tiru | Practice modes, quiz feedback | Complete listening/fill-blank modes, answer feedback effects, and unit tests for scoring. |
| Dhyan | Achievements, gamified learning map | Implement the gallery and path-map UI backed by progress milestones. |

## Recommended delivery order

1. Add DataStore and completion/progress write use cases.
2. Replace the temporary catalog with Room caching plus a remote learning API.
3. Add audio, offline sync, error states, and accessibility/UI tests.
4. Add authenticated profile, cloud progress sync, achievements, and the gamified map.

## Git handoff

```bash
git init
git add LingoLeap
git commit -m "chore: bootstrap LingoLeap clean architecture"
```

Use small feature branches, e.g. `feature/onboarding-ui`, `feature/quiz-mvi`, and keep this starter branch as the integration baseline.
