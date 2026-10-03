# Play Console app-content declarations

This document records the declarations supported by the current source code. The Play Console account owner must attest to them truthfully at submission time.

## App access

The app has no visible sign-in wall, paid wall, or restricted area. Select **All functionality is available without special access**. Guest learning works without credentials; Email/Password and Google sign-in are optional cloud-backup features, so tester credentials are not required.

## Ads

Select **No, my app does not contain ads**. This release contains no advertising SDK, advertising ID integration, or ad placements.

## Content rating

Complete the Education/Language Learning questionnaire truthfully. The current app has no user-generated content, chat, violence, sexual content, gambling, controlled substances, news, or location-based social features. Do not select an age group until the publisher confirms the intended audience. The current privacy policy states the app is not directed to children under 13.

## Target audience and Families

Recommended initial declaration: **not designed for children** and **not enrolled in the Families program**, unless the publisher intentionally designs, reviews, and supports the app for children. This is a publisher/business decision; update the privacy policy and product design if a child audience is selected.

## Data safety

Use [DATA_SAFETY_DECLARATION.md](DATA_SAFETY_DECLARATION.md). The cloud backup stores Firebase user IDs and learning progress; optional account creation additionally processes a name and email address, so “we collect no data” would be inaccurate.

## Permissions and device access

The current manifest requests no dangerous runtime permissions. Text-to-speech uses installed device voices and does not record microphone audio. The app does not access contacts, storage media, location, camera, or notifications.

## News and government declarations

Not applicable to the current source. No news aggregation or government-affiliated functionality is included.
