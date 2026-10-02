# Google Play Data Safety declaration

Use this as the source of truth when completing **Play Console → App content → Data safety**. Re-check it after every SDK or feature change.

## App behaviour represented by this release

| Play Console question | Declaration for VaaniVerse4U 1.0.0 |
| --- | --- |
| Does the app collect or share user data? | **Yes, collects. No, does not share for third-party advertising.** Firebase Authentication and Firestore receive the anonymous installation ID and learning backup. |
| Personal info | **User IDs**: anonymous Firebase UID. Collected for app functionality; transmitted to Firebase; not shared. |
| App activity | **App interactions**: selected course plus completed lessons/challenges/review words, XP, and streak. Collected for app functionality; transmitted to Firebase; not shared. |
| Data encrypted in transit? | **Yes.** Firebase uses TLS for network traffic. |
| Can users request deletion? | **Yes.** In app: Settings → Delete cloud backup. Support route: `[SUPPORT_EMAIL]`. |
| Is data required to use the app? | **No.** Core learning content and progress work offline. Cloud backup is an auxiliary function. |
| Sold or used for advertising/marketing? | **No.** |
| Other data categories | **No** for location, contacts, photos/videos, audio recordings, financial info, health/fitness, messages, browsing history, diagnostics, and device identifiers other than the Firebase user ID above. |

## SDK verification before submission

This source includes Firebase Authentication, Cloud Firestore, and Google Services. The publisher must re-check each included SDK’s current Data safety guidance in the Google Play SDK Index and ensure no new dependency adds analytics, crash reporting, ads, or identifier collection.

## Required publisher action

Replace `[SUPPORT_EMAIL]` in the public privacy policy, host it at a public HTTPS URL, then complete the Data safety form with the truthful answers above. Do not copy the table blindly if a future build changes Firebase configuration or adds any analytics SDK.
