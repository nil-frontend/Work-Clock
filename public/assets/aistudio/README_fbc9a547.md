# WorkClock — Development Documentation Pack

**Document set:** 1.0  
**Prepared:** 12 November 2026  
**Product status:** Pre-development / implementation handoff  
**Working product name:** WorkClock (name and trademark availability not yet cleared)

## 1. What this product is

WorkClock is a native Android digital clock for phones and tablets. It displays the device's local system time in a large, readable, low-clutter interface and can keep the display awake while the clock screen is in the foreground. The clock engine and user preferences run locally. The free version may load one fixed, non-intrusive bottom banner advertisement through Google AdMob when an internet connection and the required advertising-consent state are available.

**Product promise:** See the time clearly. Keep the display awake while the app is open. No account, cloud clock service, or server is needed.

**Important accuracy statement:** WorkClock displays the device's system time. It is not an official time source, synchronisation service, railway-certified clock, or safety-certified instrument.

## 2. Product decisions currently assumed

| Topic | Baseline decision |
|---|---|
| Audience | General-purpose utility; not specifically designed or marketed for children unless the owner later chooses otherwise |
| Devices | Android phones and tablets; responsive single app |
| Platform | Native Android |
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | Single-activity, single-app-module, MVVM/state-holder approach |
| Minimum Android version | API 24 baseline, because the current Google Mobile Ads SDK release noted on 12 Nov 2026 requires API 24; re-check before release |
| Compile / target SDK | API 36 baseline; re-check Play policy immediately before submission |
| Screen awake | `FLAG_KEEP_SCREEN_ON` while the clock is active in the visible Activity; no background service or partial wake lock |
| Time source | Android system clock and current device time zone |
| Local preferences | Preferences DataStore |
| Advertising | One anchored adaptive banner at the bottom of the clock screen only; no interstitial, app-open, rewarded, notification, floating, or full-screen ads |
| Ads and offline mode | Clock works offline. Ad requests are best-effort and must never block clock rendering |
| Ads consent | Google User Messaging Platform (UMP) where required; provide a privacy-options entry point when required |
| Monetisation | Free with banner ads first; possible one-time Pro unlock to remove ads and add customisation in a later release |
| Backend | None for v1 |
| Login / account | None |
| Analytics | No separate analytics SDK in v1; use Play Console / Android vitals and user feedback |
| Release channel | Google Play first |

## 3. Files in this pack

| File | Purpose |
|---|---|
| [`PRD.md`](PRD.md) | Product goals, scope, personas, requirements, priorities, success metrics and acceptance criteria |
| [`TRD.md`](TRD.md) | Technical requirements, stack, SDKs, platform configuration, implementation details and build standards |
| [`Design.md`](Design.md) | Visual system, colour, typography, themes, spacing and component styling |
| [`UI-UX.md`](UI-UX.md) | Screen specifications, interactions, responsive behaviour, ads placement and accessibility |
| [`User-Flow.md`](User-Flow.md) | User journeys and Mermaid flow diagrams |
| [`Architecture.md`](Architecture.md) | Runtime architecture, component boundaries, state/data flow, lifecycle and failure handling |
| [`Legals.md`](Legals.md) | Privacy, ads, Google Play declarations, India DPDP considerations, disclaimers and draft policy text |
| [`QA-Test-Plan.md`](QA-Test-Plan.md) | Functional, device, lifecycle, ad, privacy, accessibility and endurance test plan |
| [`Release-Checklist.md`](Release-Checklist.md) | Play Console setup, store listing, signing, testing and production release checklist |
| [`Open-Decisions.md`](Open-Decisions.md) | Decisions the owner must still make before implementation or publication |
| [`Launch-Plan.md`](Launch-Plan.md) | ASO, launch content, first-user acquisition and post-launch measurement |

## 4. Suggested repository structure

Start with one Android app module. Do not create a multi-module architecture for this small product until there is a real need.

```text
workclock/
├── README.md
├── docs/
│   ├── PRD.md
│   ├── TRD.md
│   ├── Design.md
│   ├── UI-UX.md
│   ├── User-Flow.md
│   ├── Architecture.md
│   ├── Legals.md
│   ├── QA-Test-Plan.md
│   ├── Release-Checklist.md
│   ├── Open-Decisions.md
│   └── Launch-Plan.md
├── gradle/
│   └── libs.versions.toml
├── app/
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   └── java/<package>/workclock/
│       │       ├── MainActivity.kt
│       │       ├── WorkClockApp.kt
│       │       ├── clock/
│       │       ├── settings/
│       │       ├── ads/
│       │       ├── ui/
│       │       └── util/
│       ├── test/
│       └── androidTest/
└── .gitignore
```

## 5. Recommended implementation order

1. Confirm the unresolved owner decisions in `Open-Decisions.md`.
2. Create the native Android project and make the clock render immediately.
3. Implement the clock ticker, time/date formatting, and screen-awake behaviour.
4. Add local settings and responsive layouts.
5. Add accessibility, display-care settings and lifecycle handling.
6. Integrate UMP and AdMob after the clock works without either SDK.
7. Complete QA and privacy / Play declarations.
8. Run internal testing, then closed testing if the developer account requires it.
9. Publish a free ad-supported v1.0.
10. Consider a Play Billing Pro unlock only after actual user demand is observed.

## 6. Critical implementation principles

- The clock is the product; the ad is secondary.
- Do not wait for a network request, AdMob initialization, consent update, or ad load before drawing the clock.
- Ads must occupy a separate, stable bottom slot and never overlap the time, date, settings controls, system gestures, or accessibility controls.
- If an ad cannot load, the clock continues normally. Do not retry in a tight loop.
- Keep the screen awake only while the user has the clock screen active and the preference is enabled.
- Never use a background service or partial wake lock just to keep the display on.
- Do not claim that the device's time is guaranteed accurate. The app displays whatever time Android reports.
- Do not market the app as railway-certified or as a replacement for prescribed watches or official time sources.
- Treat the AdMob SDK as a third-party data processor/recipient and complete disclosures from the exact SDK version and configuration shipped.
- Do not target children by default. The intended audience and Play Console age groups must be chosen accurately before publication.

## 7. Source-of-truth policy

This document pack captures requirements and baseline decisions, not frozen SDK versions. Before each release, the developer must re-check the official Android, AdMob, UMP, Google Play and applicable legal sources listed below. SDK versions change; use current stable releases and a version catalog rather than blindly copying an old version number.

### Official references checked on 12 November 2026

- Android keep-screen-on: https://developer.android.com/develop/background-work/background-tasks/awake/screen-on
- Compose adaptive layouts: https://developer.android.com/develop/ui/compose/layouts/adaptive
- Compose BOM: https://developer.android.com/develop/ui/compose/bom
- Android DataStore: https://developer.android.com/topic/libraries/architecture/datastore
- AdMob Android banner ads: https://developers.google.com/admob/android/banner
- AdMob UMP consent SDK: https://developers.google.com/admob/android/privacy
- AdMob Play Data Safety disclosure: https://developers.google.com/admob/android/privacy/play-data-disclosure
- Google Play target API requirements: https://support.google.com/googleplay/android-developer/answer/11926878
- Google Play app testing requirements: https://support.google.com/googleplay/android-developer/answer/14151465
- Google Play User Data policy: https://support.google.com/googleplay/android-developer/answer/10144311
- Google Play Families policy: https://support.google.com/googleplay/android-developer/answer/9893335
- India DPDP Act, 2023: https://www.meity.gov.in/static/uploads/2024/06/2bf1f0e9f04e6fb4f8fef35e82c42aa5.pdf
- India DPDP Rules, 2025: https://www.meity.gov.in/static/uploads/2025/11/53450e6e5dc0bfa85ebd78686cadad39.pdf
- India DPDP commencement notification: https://www.meity.gov.in/static/uploads/2025/11/c56ceae6c383460ca69577428d36828b.pdf

---

**Owner action:** Replace all placeholders in the legal and release documents with the actual legal publisher name, support email, privacy contact, app package ID, public privacy-policy URL, and AdMob identifiers before submission.
