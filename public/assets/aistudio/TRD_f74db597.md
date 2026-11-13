# WorkClock — Technical Requirements Document (TRD)

**Version:** 1.0  
**Date:** 12 November 2026  
**Status:** Proposed implementation baseline

## 1. Technical objective

Build a native Android app that renders a local clock immediately, keeps the screen awake only while the clock Activity is in the foreground and the feature is enabled, stores settings locally, adapts to phones/tablets, and optionally shows one anchored adaptive AdMob banner after consent requirements are satisfied.

No WorkClock backend, user database, login, cloud API, external time API, or remote configuration is required for v1.

## 2. Platform baseline

| Setting | Baseline | Notes |
|---|---|---|
| Language | Kotlin | Primary implementation language |
| UI | Jetpack Compose + Material 3 | Use Compose BOM to align Compose libraries |
| minSdk | 24 | Current Google Mobile Ads SDK release notes on 12 Nov 2026 report SDK 25.5.0 and min Android API 24. If SDK version or minSdk changes, re-check compatibility. |
| compileSdk | 36 | Android 16 / API 36 baseline |
| targetSdk | 36 | Google Play new-app and update target requirement from 31 Aug 2026 is API 36 or higher. Re-check before submission. |
| Build output | Android App Bundle (.aab) | Google Play production distribution |
| Architecture | Single Activity / one app module | Do not over-engineer |
| Local storage | Preferences DataStore | Small preferences only |
| Time | `java.time` / Android system clock | No external clock API |

**Release gate:** Target API level and ad SDK minimum API must be checked again immediately before a public submission. Do not assume this document can stay current indefinitely.

## 3. Dependencies

Use Gradle Version Catalog (`gradle/libs.versions.toml`) to centralise versions. Pin stable versions at the time the project is created; avoid dynamic versions such as `+`.

| Dependency | Gradle coordinate | Usage |
|---|---|---|
| AndroidX Compose BOM | `androidx.compose:compose-bom` | Align Compose artifact versions |
| Compose UI / Foundation / Material 3 | `androidx.compose.ui:ui`, `androidx.compose.foundation:foundation`, `androidx.compose.material3:material3` | UI |
| Activity Compose | `androidx.activity:activity-compose` | Compose Activity integration |
| Lifecycle Runtime Compose | `androidx.lifecycle:lifecycle-runtime-compose` | Lifecycle-aware collection |
| Lifecycle ViewModel Compose | `androidx.lifecycle:lifecycle-viewmodel-compose` | ViewModel integration |
| DataStore Preferences | `androidx.datastore:datastore-preferences` | Local app preferences |
| Google Mobile Ads SDK | `com.google.android.gms:play-services-ads` | Banner ads |
| Google UMP SDK | `com.google.android.ump:user-messaging-platform` | Consent/privacy messaging |
| Kotlin Coroutines | `org.jetbrains.kotlinx:kotlinx-coroutines-android` | Clock ticker and asynchronous work; use compatible versions |
| AndroidX Core | `androidx.core:core-ktx` | Android compatibility helpers |
| AndroidX Test / Compose UI Test | AndroidX test artifacts | Instrumentation/UI testing |
| JUnit | JUnit | Unit tests |

Do not add Firebase, Supabase, Retrofit, OkHttp, Room, Hilt, Koin, Navigation Compose, WorkManager, location SDKs, or analytics SDKs unless a concrete requirement justifies them.

### Version notes

- Google Mobile Ads SDK release notes showed version 25.5.0 dated 17 September 2026 and a minimum Android API level of 24 when this document was prepared. Confirm the latest stable release at implementation time.
- The Compose BOM is recommended by Android to align Compose libraries. Select the latest stable BOM in Android Studio/official documentation at project setup; do not blindly reuse a sample version from a guide.
- Keep GMA and UMP on supported, current versions and review SDK release notes before updates.

## 4. Manifest and permissions

Expected manifest items:

- `android.permission.INTERNET` — required for AdMob/UMP network activity. The clock must not depend on it.
- `android.permission.ACCESS_NETWORK_STATE` — only if implementation uses network status; optional if SDK handles it without your app querying it.
- `com.google.android.gms.ads.APPLICATION_ID` metadata — required for AdMob, using the WorkClock AdMob application ID.
- Optional app icon, label, theme and supported screen configurations.

Do not request:

- Wake lock permission for screen-awake behaviour (not required for `FLAG_KEEP_SCREEN_ON`).
- Location, contacts, camera, microphone, call, SMS, phone state or storage permissions.
- Notification permission (no notifications feature).
- Exact alarm permission.
- Foreground service permissions.

Ad SDK dependencies may merge their own manifest entries. Inspect the merged manifest and document any unexpected permissions before release.

## 5. Clock time source and update strategy

### 5.1 Source of time

Use Android's device clock and current default time zone via `java.time`, e.g. `ZonedDateTime.now()` or an injectable `Clock.systemDefaultZone()`.

No WorkClock time server or third-party NTP call. This means the app reflects the device's time configuration and inherits any device clock error.

### 5.2 Tick behaviour

- Seconds enabled: update the visible time at second boundaries.
- Seconds disabled: update at minute boundaries, not every second.
- Recalculate from the system clock each tick; do not increment an internal counter as the source of truth.
- Align delay to the next boundary to reduce visual drift.
- Refresh immediately on Activity resume.
- Refresh on time/date/time-zone change broadcasts while appropriate, or rely on the immediate next system-time read (prefer explicit handling for deterministic behaviour).
- Avoid an unbounded busy loop or a high-frequency animation.

Pseudo-implementation:

```kotlin
while (currentCoroutineContext().isActive) {
    val now = timeProvider.now()
    state.value = now

    val delayMs = if (showSeconds) {
        millisUntilNextSecond(now)
    } else {
        millisUntilNextMinute(now)
    }
    delay(delayMs.coerceAtLeast(1L))
}
```

The `timeProvider` must be injectable so unit tests can use a fixed/test clock. Use lifecycle-aware collection and cancel the ticker when the clock screen is not visible.

### 5.3 Locale and format

- Use system locale for date formatting by default.
- Provide `System`, `12-hour`, and `24-hour` format choices.
- Use tabular numerals so digits do not shift horizontally as values change.
- Respect device time zone; do not hard-code Asia/Kolkata.

## 6. Screen-awake behaviour

Use `WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON` while the clock Activity is visible and the setting is enabled. A Compose implementation may use `Modifier.keepScreenOn()` where the Compose UI version supports it; otherwise use the Activity window flag. Pick one source of truth; do not set both without a reason.

Requirements:

- Enabled by default for the clock experience, with an obvious settings toggle.
- Apply when the clock screen is active.
- Clear/stop applying when disabled or when no longer required.
- The OS handles normal screen timeout when the app is backgrounded.
- Physical power-button lock/screen-off must continue working.
- No background service, alarm, partial wake lock or overlay permission.

## 7. Brightness and system bars

- Apply app-only brightness via the Activity window (`screenBrightness` override). Use `-1f` to return to system brightness.
- Do not modify global system brightness unless a future design specifically requests it and the permission/UX implications are reviewed.
- Restore system default when user selects System or when leaving the app's brightness mode.
- Immersive/full-screen mode is optional. System navigation gestures and the power button must remain available.
- Respect display cutouts and insets.

## 8. Local preferences model

Use Preferences DataStore. Suggested schema:

```kotlin
data class ClockSettings(
    val timeFormat: TimeFormat = TimeFormat.SYSTEM,
    val showSeconds: Boolean = true,
    val showDate: Boolean = true,
    val keepScreenAwake: Boolean = true,
    val themeId: String = "midnight",
    val brightnessMode: BrightnessMode = BrightnessMode.SYSTEM,
    val brightnessLevel: Float = 0.6f,
    val orientationMode: OrientationMode = OrientationMode.SYSTEM,
    val immersiveMode: Boolean = false,
    val oledCareEnabled: Boolean = false
)
```

Validate brightness as 0.05..1.0 when custom mode is selected. Persist only settings the user changes. Version the settings keys and handle missing/invalid values with defaults.

Do not persist current time; always read it from Android.

## 9. Advertising technical requirements

### 9.1 SDK and format

- Use Google Mobile Ads SDK for Android.
- Use one anchored adaptive banner (standard, not large adaptive) at the bottom of the main clock screen.
- Calculate banner size from the current available width in dp and reload/recreate safely when layout width changes.
- Do not use interstitial, app-open, rewarded, rewarded-interstitial, native feed, notification, overlay or floating ads.
- Do not make the banner a control; label it as an advertisement where appropriate and preserve normal ad disclosure.

### 9.2 Initialization and consent order

1. Render the local clock immediately.
2. Start UMP consent-information update on app launch.
3. Present required consent UI if the SDK says it is required.
4. Request ads only when UMP indicates an eligible ad request can be made.
5. If consent or network update fails, follow UMP/AdMob documented fallback guidance and local legal policy; do not bypass consent.
6. Expose the privacy-options entry point when UMP says it is required.
7. Do not block the clock UI waiting for any of these steps.

The consent form itself may be shown to users in regions where required. It is not an advertisement, but it is a user-facing privacy interaction and must not be disguised.

### 9.3 Banner lifecycle

- Create/reuse an AdView per visible clock screen; avoid creating a new one every recomposition.
- Load on main thread as required by SDK.
- Pause/resume/destroy according to Activity lifecycle and SDK guidance.
- Remove the view and clear references when disposed.
- Do not trigger manual repeated reloads in a loop.
- If AdMob auto-refresh is configured in AdMob UI, understand it only refreshes while the banner is visible. Use conservative settings and review current policy.
- Use official test ad unit ID during development: `ca-app-pub-3940256099942544/9214589741`.
- Replace with the actual production ad unit ID before release.
- Never click live ads during testing.

### 9.4 Banner layout fallback

The layout reserves a bottom ad region in Free mode to reduce visual jumps. If no ad loads, the clock remains fully usable. Decide in UI testing whether the failed slot remains blank or collapses after a bounded timeout; document the chosen behaviour in `UI-UX.md` and ensure it does not create layout jumps or obscure the clock. In short-height landscape, suppress the banner.

## 10. UMP requirements

- Add UMP dependency.
- Call `requestConsentInfoUpdate()` on every app launch according to current official guidance.
- Call `loadAndShowConsentFormIfRequired()` when required.
- Check `canRequestAds()` before requesting ads.
- Provide `showPrivacyOptionsForm()` from a visible privacy/ads settings entry point if required by the consent status.
- Configure debug test devices and geography for EEA/UK/Switzerland testing as supported by the current SDK.
- Test accept, reject/limited, privacy-option change, no network, timeout and repeat-launch cases.

## 11. Pro purchase (future only)

Not in v1.0. If implemented in v1.1:

- Google Play Billing one-time non-consumable product.
- Entitlement removes banner and unlocks optional appearance features.
- Query current owned purchases on app start and when returning to foreground.
- Acknowledge purchases as required by Billing Library.
- Restore purchases without a WorkClock login.
- Do not build an external payment link for digital unlocks in the Play-distributed app.
- Consider backend token verification only if fraud/scale justifies operating a server; otherwise document the limitations of client-side entitlement validation.

## 12. App lifecycle

- `onCreate`: create Compose content; clock view should render immediately.
- `onStart` / `onResume`: read current settings, refresh time, apply keep-awake flag if enabled, resume any active ad view according to SDK guidance.
- `onPause` / `onStop`: pause or release ad resources as appropriate; stop unnecessary tick updates; do not keep a background wake lock.
- Configuration change: retain settings and ViewModel state; recompute layout and banner width.
- Process death: recreate from DataStore and current system time.

## 13. Error handling

| Failure | Required behaviour |
|---|---|
| No internet | Clock and settings work; no endless ad retry |
| Ad no-fill | Clock remains visible; reserved slot follows design fallback |
| Ad SDK initialization failure | Clock remains visible; log only non-sensitive diagnostics if any |
| UMP update failure | Follow documented SDK fallback; never bypass a required consent gate |
| DataStore read failure | Use safe defaults; avoid crash; log for debugging |
| Invalid saved setting | Clamp or reset to safe default |
| Time-zone change | Show new local time and date promptly |
| Device time correction | Re-read system clock; do not continue stale elapsed counter |
| Rotation/resizing | Recompose without duplicate AdView instances or clock reset |

## 14. Testing requirements

- Unit tests: time formatting, next-tick calculation, settings validation and default values.
- Compose UI tests: screen layout, settings interaction, accessibility labels, ad-slot bounds.
- Instrumented tests: keep-awake flag application/removal, Activity lifecycle, rotation, process recreation, DataStore.
- Manual device tests: phone, tablet, AMOLED, LCD, OEM battery management and long-duration sessions.
- Ad tests: official test ad unit only, consent geographies, no-fill/offline, Pro entitlement (future).

## 15. Build and release configuration

- Keep release signing credentials outside source control.
- Use Play App Signing and secure upload key backup.
- Build release AAB with minification only after verifying SDK keep rules and smoke testing.
- Enable R8 only after inspecting mapping and crash reporting plan.
- Use separate debug and release ad IDs/configuration.
- Never ship test ad ID as production monetisation configuration.
- Use CI only if it saves time; it is not required for v1.

## 16. Dependency update policy

At each release:

1. Review Android Gradle Plugin, Kotlin, Compose BOM, Lifecycle, DataStore, GMA and UMP release notes.
2. Check minSdk compatibility and Android target policy.
3. Update one major SDK at a time where possible.
4. Run all QA tests, especially ad/consent and lifecycle tests.
5. Review merged manifest and Data Safety disclosures again.

---

### Official technical references

- Keep screen on: https://developer.android.com/develop/background-work/background-tasks/awake/screen-on
- Compose BOM: https://developer.android.com/develop/ui/compose/bom
- Compose state: https://developer.android.com/develop/ui/compose/state
- Adaptive layouts: https://developer.android.com/develop/ui/compose/layouts/adaptive
- DataStore: https://developer.android.com/topic/libraries/architecture/datastore
- AdMob banner: https://developers.google.com/admob/android/banner
- AdMob UMP: https://developers.google.com/admob/android/privacy
- AdMob data disclosures: https://developers.google.com/admob/android/privacy/play-data-disclosure
- GMA release notes: https://developers.google.com/admob/android/rel-notes
- GMA migration/minimum API: https://developers.google.com/admob/android/migration
- Play target API: https://support.google.com/googleplay/android-developer/answer/11926878
