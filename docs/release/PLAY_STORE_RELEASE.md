# VaaniVerse4U 1.0.0 release runbook

## What is ready in the repository

- Package: `com.code4galaxy.vaaniverse4u`
- Version: `1.0.0` (`versionCode` 1)
- Target SDK: 37
- Release build: R8 code shrinking and Android resource shrinking enabled
- Local-only Firebase configuration: `app/google-services.json` is intentionally ignored by Git
- Vector-only in-app and launcher artwork; no raster app assets remain under `app/src/main/res`
- Store listing copy, policies, Data safety declaration, and artwork source in this folder and `release-assets/`

## Build a signed Android App Bundle

1. Create a keystore and keep it outside Git. Back it up securely.
2. Add the keystore path and credentials to ignored `local.properties` or configure them through Android Studio’s signing UI. Never commit a keystore, passwords, `local.properties`, or `google-services.json`.
3. In Android Studio, select the `release` variant and use **Build → Generate Signed Bundle / APK → Android App Bundle**.
4. Run the full release checks before signing/uploading:

   ```bash
   ./gradlew clean testDebugUnitTest lintDebug bundleRelease
   ```

5. Install and test the signed release build on a physical device. Preserve the generated `mapping.txt` beside the AAB so Firebase/Play crash stacks can be de-obfuscated later.

## Create upload assets

Google Play requires PNG/JPEG for graphic uploads, not SVG. Render `release-assets/` source files as follows:

- App icon: 512 × 512 PNG, no transparency around the icon.
- Feature graphic: 1024 × 500 PNG/JPEG, no misleading device frames or small unreadable text.
- Phone screenshots: capture **real, signed-release** app screens in portrait, at least two screenshots. Do not upload mockups as product screenshots.

The included SVG screenshot artwork is a marketing layout reference only. Capture the equivalent Home and Lesson flows from an emulator/physical device after release signing. Keep the screen content and UI truthful to the uploaded AAB.

## Play Console sequence

1. Create the app with default language English (India), app type App, category Education, and free/paid choice. A free app cannot later be converted to paid.
2. Paste [PLAY_STORE_LISTING.md](PLAY_STORE_LISTING.md), upload the rendered icon/feature graphic/real screenshots, and add the public privacy-policy URL.
3. Complete [PLAY_CONSOLE_DECLARATIONS.md](PLAY_CONSOLE_DECLARATIONS.md) and [DATA_SAFETY_DECLARATION.md](DATA_SAFETY_DECLARATION.md) truthfully.
4. Upload the signed `.aab` to Internal testing first. Add test accounts and run the onboarding, learning, practice, cloud deletion, reset, dark theme, large text, and TalkBack checklist.
5. Resolve every Play Console warning/error, create production release notes from [PLAY_STORE_LISTING.md](PLAY_STORE_LISTING.md), then roll out gradually.

## Release blockers that require publisher input

- Replace `[SUPPORT_EMAIL]` in every policy and listing document.
- Host the privacy policy at a public HTTPS URL.
- Create and protect the production signing key / enable Play App Signing.
- Capture genuine signed-release screenshots; no emulator or physical Android device is connected to this workspace.
- Review policy text with a qualified legal professional for the publisher’s jurisdiction.
