# Local Android signing setup

The app bundle is only Play-uploadable after it is signed with the project's upload key. Keep the key and its credentials private; neither belongs in Git.

## One-time key creation

Create a strong upload key in Android Studio (**Build > Generate Signed Bundle / APK**) and store the `.jks` file somewhere backed up and private. Enable Play App Signing when creating the Play Console app.

## Local configuration

Add these values to the already ignored `local.properties` file in the project root:

```properties
RELEASE_STORE_FILE=/absolute/path/to/vaaniverse4u-upload.jks
RELEASE_STORE_PASSWORD=your-keystore-password
RELEASE_KEY_ALIAS=vaaniverse4u_upload
RELEASE_KEY_PASSWORD=your-key-password
```

When all four fields are present, the `release` build type automatically uses that signing configuration. If any field is absent, a release bundle may still be assembled for verification but must **not** be uploaded to Google Play.

## Verify

```bash
./gradlew clean bundleRelease
jarsigner -verify -verbose -certs app/build/outputs/bundle/release/app-release.aab
```

Archive the final `.aab` and `app/build/outputs/mapping/release/mapping.txt` together after each published build.
