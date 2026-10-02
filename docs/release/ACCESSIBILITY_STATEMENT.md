# VaaniVerse4U accessibility statement

VaaniVerse4U aims to support learners who use TalkBack, larger text, and high-contrast displays.

## Included in version 1.0.0

- Material 3 controls provide at least 48 dp touch targets for standard buttons, navigation, and icon buttons.
- Screen titles and settings sections are marked as headings for screen-reader navigation.
- Buttons, navigation items, interactive cards, and meaningful images expose descriptive labels.
- Decorative icons are excluded from screen-reader focus to avoid redundant announcements.
- Progress indicators expose their numeric progress through Compose semantics.
- The app respects the system font scale, and Settings provides Default, Large, and Extra large in-app text sizes.
- A high-contrast color scheme increases contrast for primary, secondary, tertiary, surface, and outline tokens.
- The baseline theme uses WCAG AA-compliant text/action pairs: green/white 5.45:1, blue/white 5.17:1, amber/white 5.02:1, and primary-container/on-primary-container 11.1:1 or higher.

## Validation before release

Run TalkBack through onboarding, Home, lessons, quiz answers, Practice, Settings, and destructive dialogs on a physical Android device. Also test at Android Settings font size 200%, display size Large, light mode, dark mode, and high-contrast mode. Log and fix any clipped text, unlabeled action, duplicate announcement, or keyboard-focus trap before upload.

If you need an accessibility accommodation or encounter a barrier, contact `[SUPPORT_EMAIL]`.
