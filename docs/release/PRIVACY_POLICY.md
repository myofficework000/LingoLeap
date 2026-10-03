# VaaniVerse4U Privacy Policy

**Effective date:** 2 October 2026
**Publisher:** Abhishek Pathak (Code4Galaxy)
**Contact:** code4galaxy@gmail.com

VaaniVerse4U is an offline-first language-learning application. This policy explains how the app handles information when you use it.

## Information processed

VaaniVerse4U stores the following learning information locally on your device:

- selected language pair and daily learning goal;
- completed lessons, quiz review words, XP, streak, and completed daily challenges; and
- accessibility preferences such as in-app text scale and high-contrast mode.

You can learn as a guest without supplying personal information. If you choose to create an account or sign in, the app processes your display name and email address through Firebase Authentication. Google sign-in may also provide the name and email associated with your selected Google account. The app does not collect phone numbers, contacts, photos, microphone recordings, precise location, or payment information. Lesson content is bundled in the app and text-to-speech pronunciation uses the Android text-to-speech service installed on your device.

## Optional cloud backup

VaaniVerse4U starts with an anonymous Firebase Authentication identifier and uses Cloud Firestore to back up the learning information listed above. You may optionally link that guest identity to an Email/Password or Google account. For a linked account, Firebase Authentication processes the account credential and the app stores your display name, email address, sign-in provider, account identifier, selected course ID, lesson/challenge/review IDs, XP, streak, and server update timestamp in the cloud backup.

Firebase processes this information on Google infrastructure to provide the backup service. The app uses the backup only to preserve learner progress; it does not sell information, use it for advertising, or share it with third-party advertisers.

## Retention and deletion

Local learning data remains on the device until you reset it in **Settings → Reset local progress** or uninstall the app. **Settings → Delete cloud backup** deletes the cloud learning data and profile document; for guests, it also removes the anonymous Firebase identity. If you have a linked Email/Password or Google account, contact code4galaxy@gmail.com to request deletion of the remaining Firebase Authentication account. Deleting cloud data does not delete local progress.

## Security

Firebase traffic is encrypted in transit. Firestore rules restrict the backup document tree to the authenticated Firebase user, whether that user is a guest or has linked an account. No method of transmission or storage is completely secure, but VaaniVerse4U uses reasonable safeguards for the limited information it processes.

## Children

VaaniVerse4U is not directed to children under 13 and does not knowingly collect personal information from children. If you believe a child has provided personal information, contact code4galaxy@gmail.com.

## Changes and contact

We may update this policy when the app’s data practices change. The effective date above will be updated with the revision. For privacy questions, contact code4galaxy@gmail.com.

---

Before Google Play submission, publish this exact policy at a public, non-editable HTTPS URL (for example a page on the publisher website or a public GitHub Pages site), then use that URL in Play Console.
