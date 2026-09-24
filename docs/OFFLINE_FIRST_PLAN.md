# LingoLeap offline-first delivery plan

## Product boundary

Learning must work in airplane mode after installation. Courses, vocabulary, lesson prompts, quizzes, practice exercises, daily challenges, achievement definitions, translations, and audio metadata are versioned app assets. No learning session depends on Firebase or a network request.

## Local source of truth

| Concern | Local implementation | Current asset or store |
| --- | --- | --- |
| Curriculum, language pairs, lessons, words | JSON asset → repository | `learning_catalog.json` |
| Daily challenge prompts | JSON asset → repository | `daily_challenges.json` |
| Achievement rules and copy | JSON asset → repository | `achievements.json` |
| JSON authoring contract | JSON Schema | `learning_catalog.schema.json` |
| Selected languages and onboarding | DataStore | `lingoleap_preferences` |
| XP, completed lessons, completed daily challenges | Room | `lingoleap.db` |

Each content asset must have a schema version, stable IDs, and a validation test before it is added to a release. Content is immutable in the installed app; progress is always local and mutable.

## Firebase: optional, small, and user-owned

Firebase is not part of the learning loop. Add it only after the offline experience has complete navigation and test coverage.

1. **Authentication:** Anonymous sign-in first; optionally link Google sign-in. The app remains usable without an account.
2. **Cloud backup:** Firestore stores one compact document per signed-in user containing preferences, current course, XP, completions, and a `lastUpdatedAt` value. It never stores the catalog, quiz payloads, or media.
3. **Reliability:** Crashlytics and Analytics are optional release-only tools. Remote Config is limited to a content-pack minimum version and feature flags.
4. **Sync:** A WorkManager job uploads local changes when connected. On conflict, preserve the higher XP and union completion IDs. The local database remains the source of truth while offline.

This footprint stays within a low-cost/free usage profile because reads and writes are limited to explicit account sync rather than every lesson interaction.

## Delivery sequence

### Milestone A — complete local learning loop

- Switch the active Room course when the language pair changes.
- Persist lesson, quiz, practice, and daily-challenge outcomes locally.
- Make quizzes deterministic from JSON so retrying a lesson remains reproducible.
- Show Room-backed progress, achievements, and locked/unlocked path nodes.

### Milestone B — richer offline curriculum

- Expand every language to themed units: greetings, family, food, travel, directions, work, and conversations.
- Add listening, word-match, fill-in-the-blank, translation, and recap definitions as JSON content types.
- Bundle compact audio files only for the first lessons; use platform text-to-speech as the no-download fallback.

### Milestone C — offline quality

- Add repository, ViewModel, JSON validation, and Compose navigation tests.
- Add migration tests for Room progress and a manual airplane-mode test script.
- Add accessibility labels, scalable text checks, and dark-theme verification.

### Milestone D — optional account backup

- Add Firebase credentials outside source control, then anonymous auth and explicit “Back up my progress” consent.
- Implement WorkManager sync and a conflict-resolution test suite.
- Keep all content packs local; only user state syncs.
