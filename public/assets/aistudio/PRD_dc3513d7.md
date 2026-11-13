# WorkClock — Product Requirements Document (PRD)

**Version:** 1.0  
**Status:** Proposed baseline for v1 development  
**Date:** 12 November 2026  
**Owner:** Product owner / publisher — to be confirmed

## 1. Product summary

WorkClock is a simple full-screen digital clock application for Android phones and tablets. When opened, it immediately displays the device's current local time in a large, readable format. When the Keep Screen Awake option is enabled, the app prevents the ordinary display timeout while the clock Activity is visible. The user can still manually lock or turn off the display with the physical power button.

The clock functionality is local and works without internet access. The free edition may display one small, anchored adaptive banner advertisement at the bottom of the clock screen when consent and network conditions permit. Ads must never interrupt, cover, or obscure the clock.

**Positioning:** A clean, reliable, large digital clock for everyday use, desks, homes, study spaces and workplaces.

**Primary marketing hook:** Turn a spare Android phone or tablet into a digital desk clock.

## 2. Problem statement

People sometimes need the time visible from a distance or for extended periods. A normal phone display dims and turns off according to its screen-timeout setting. Small clock widgets and lock-screen clocks may be too small, hidden, or require repeated interaction.

WorkClock addresses this by providing one always-visible foreground clock screen with readable time, optional seconds, date, basic appearance controls and a simple way to keep the display awake.

## 3. Goals

1. Show the time immediately when the app launches.
2. Keep the display awake while the clock is foregrounded and the setting is enabled.
3. Let the user manually turn off the screen at any time.
4. Work without a WorkClock server, account, database, or internet connection for clock features.
5. Support phones and tablets in portrait and landscape.
6. Keep the interface minimal and readable at a glance.
7. Monetise the free product with one anchored bottom banner that does not disrupt clock use.
8. Keep privacy, battery use, thermal behaviour and accessibility in scope from the beginning.

## 4. Non-goals for v1.0

- Official time certification or guaranteed atomic/GPS/network time.
- Railway, aviation, transport or other safety-critical certification.
- Replacement for prescribed watches, control-room clocks, or official operating procedures.
- Alarm, stopwatch, countdown, reminders or calendar.
- Weather, GPS, location, maps or external time APIs.
- Cloud synchronisation, user accounts or user profiles.
- Multi-device management, enterprise console or remote configuration.
- Smartwatch, Wear OS, Android Auto, Android TV or iOS versions.
- Full-screen, app-open, interstitial, rewarded, notification or floating advertisements.
- Pro purchase implementation in the first release (planned only after validation).
- Third-party analytics SDKs or in-app behavioural tracking.

## 5. Target users and use cases

The product is general-purpose, but v1 marketing should be concrete rather than claiming a specialised safety use.

| Segment | Need | Main use case |
|---|---|---|
| General smartphone users | Larger time than status bar / widget | Keep time visible on a table |
| Students | Visible study timer reference (not a timer) | Desk clock while studying |
| Home users | Simple room clock | Spare phone or tablet on a stand |
| Office workers | Visible time at desk | Workstation clock |
| Shopkeepers / reception | Readable counter display | Customer-facing or staff clock |
| Tablet owners | Make use of a spare device | Landscape or wall/desk-mounted clock |

### Safety boundary

Do not promote the app for interaction while operating a vehicle, train, bus, machinery, or other safety-critical task. Do not claim that WorkClock is approved by Indian Railways or another authority. It displays device system time and can be inaccurate if the device time is inaccurate.

## 6. User stories

- As a user, I want the clock to appear immediately so that I do not have to configure it before seeing the time.
- As a user, I want large digits so that I can read the time from a distance.
- As a user, I want optional seconds so that I can see seconds when needed.
- As a user, I want 12-hour, 24-hour, or system-default time formatting.
- As a user, I want the display to stay awake while I use the clock.
- As a user, I want the physical power button to work normally.
- As a user, I want the clock to work without Wi-Fi or mobile data.
- As a user, I want portrait and landscape layouts on phones and tablets.
- As a user, I want to change brightness and theme without changing my global phone settings permanently.
- As a user, I want settings to remain after restarting the app.
- As a user, I want ads to remain in a small, separate bottom area and never cover the clock.
- As a user, I want the clock to work even when ads are unavailable or consent prevents ad requests.
- As a user, I want to access privacy and support information easily.

## 7. Functional requirements

Priority definitions: **P0** = mandatory for public v1.0; **P1** = include if time permits and QA is complete; **P2** = post-launch.

| ID | Priority | Requirement | Acceptance criteria |
|---|---|---|---|
| FR-001 | P0 | Launch directly into clock | On cold launch, time is visible without onboarding or login. |
| FR-002 | P0 | Display current time | Time reflects Android's current system clock and device time zone. |
| FR-003 | P0 | Refresh clock | Seconds mode updates once per second; minute-only mode updates at minute boundaries. |
| FR-004 | P0 | Time format | User can use system default, 12-hour or 24-hour format. |
| FR-005 | P0 | Optional seconds | A setting controls whether seconds appear. |
| FR-006 | P0 | Date display | Optional weekday and date display. |
| FR-007 | P0 | Keep awake | When enabled, normal display timeout does not turn screen off while clock Activity is visible. |
| FR-008 | P0 | Manual screen off | Physical power button can still turn off the display. |
| FR-009 | P0 | Background behaviour | When app is backgrounded, the OS can apply normal screen timeout; no background wake lock/service exists. |
| FR-010 | P0 | Resume behaviour | On returning to app, time/date update immediately and keep-awake is re-applied if enabled. |
| FR-011 | P0 | Phone/tablet responsiveness | UI adapts to available width, height, orientation and window size. |
| FR-012 | P0 | Local settings | Preferences persist using local app storage only. |
| FR-013 | P0 | Offline clock | Clock and settings remain functional with all network connectivity disabled. |
| FR-014 | P0 | Bottom banner slot | Free clock screen contains at most one separate anchored adaptive bottom banner slot. |
| FR-015 | P0 | Non-blocking advertising | Ad init, consent update and ad load must not delay clock rendering. |
| FR-016 | P0 | Ad failure fallback | No fill, network error, SDK failure or consent denial must not break the clock. |
| FR-017 | P0 | No intrusive ads | No interstitial, app-open, rewarded, notification, floating or full-screen advertising. |
| FR-018 | P0 | Consent UI | UMP flow is implemented where required; privacy choices remain accessible where required. |
| FR-019 | P0 | Clear privacy/support | Settings/About includes Privacy Policy, Ads/Privacy choices where applicable, and Support. |
| FR-020 | P0 | No account | No login, registration or server-side profile. |
| FR-021 | P1 | Theme presets | Provide a limited set of readable themes. |
| FR-022 | P1 | App brightness | Provide a per-window brightness override and restore system default when disabled/exiting. |
| FR-023 | P1 | Full-screen mode | Optional immersive display; system gestures and power button still work. |
| FR-024 | P1 | OLED care option | Optional dim/low-light and subtle pixel-shift mode; no claim that it eliminates burn-in. |
| FR-025 | P1 | Controls auto-hide | Controls fade after inactivity and reappear on a safe tap; ad area remains a separate tap target. |
| FR-026 | P2 | Custom colours/fonts | More appearance controls after demand is validated. |
| FR-027 | P2 | Ad-free Pro | One-time Google Play purchase removes banner ads. |
| FR-028 | P2 | Additional clock layouts | Optional styles after product validation. |
| FR-029 | P2 | Multiple time zones | Only if requested by a meaningful user segment. |

## 8. Advertising requirements

### 8.1 Allowed placement

- One anchored adaptive banner at the bottom of the main clock screen.
- Separate layout container, not an overlay.
- Banner never overlaps time, date, control buttons, settings affordances or system navigation.
- Banner is not placed on the settings/privacy/support screens in v1 unless a later product decision and policy review explicitly approves it.
- Portrait and tablet landscape may display the banner if adequate space remains.
- In short-height landscape or other constrained layouts, hide the banner rather than sacrifice clock readability.
- Use AdMob's configured banner refresh behaviour; do not create a custom rapid refresh loop.

### 8.2 Prohibited placements

No full-screen, interstitial, app-open, rewarded, notification, floating, pop-up, auto-click, deceptive, or accidental-click placements. No ads on the lock screen, outside the app, or as notifications.

### 8.3 Ad availability

Ads require connectivity and an eligible request. The clock is never dependent on ad delivery. If the request fails, the app shows the clock normally. A reserved bottom region may remain to avoid layout jumps; the chosen offline/failure appearance must be validated in UI tests.

### 8.4 Ad content and animation

AdMob chooses eligible creatives. The developer cannot guarantee that every banner creative is static. Configure content rating and blocking controls conservatively in AdMob. Test whether animated or video-capable banner creatives are acceptable for a long-running clock display. Do not promise that all ad creatives will be motionless.

## 9. Non-functional requirements

| Area | Requirement |
|---|---|
| Startup | Clock text should render before any ad network result. Target a perceived first display under 1 second on representative mid-range devices; measure, do not promise universally. |
| Time accuracy | Show Android system time; no independent accuracy guarantee. Refresh after clock/time-zone/date changes. |
| Battery | No background polling, no partial wake lock, no permanent service. Screen-on is an explicit foreground feature and increases display power use. |
| Thermal | Test prolonged display sessions; app must not add unnecessary animations or CPU-heavy loops. |
| Memory | One Activity; release AdView resources appropriately. |
| Reliability | No crash when offline, ad SDK unavailable, consent update fails, user rotates device, or app resumes. |
| Privacy | No account or app-owned remote profile. Disclose third-party SDK collection accurately. |
| Security | Use HTTPS/TLS via SDK; no secrets/API keys embedded other than public SDK identifiers. Never include service-account credentials. |
| Accessibility | High contrast, scalable layouts, TalkBack labels, adequate touch targets, no continuous spoken announcement every second. |
| Compatibility | Test API 24+ baseline on phones/tablets and a spread of OEM implementations. |
| Maintainability | Use version catalog, dependency review, automated tests and current stable SDKs. |

## 10. Settings defaults

Recommended initial defaults (validate in usability tests):

| Setting | Default |
|---|---|
| Time format | Follow Android system preference |
| Seconds | On |
| Date | On |
| Keep screen awake | On while clock screen is active |
| Theme | Midnight / high-contrast dark |
| Brightness | Follow system |
| Orientation | Follow device |
| Full-screen immersive mode | Off initially; user can enable |
| OLED care / dim | Off or user-configurable; do not unexpectedly dim the display |
| Ads | Enabled for free version, subject to consent and request eligibility |

## 11. Monetisation requirements

### v1.0

- Free download.
- One anchored adaptive banner on the main clock screen.
- No other ad formats.
- No paid install requirement.

### Potential v1.1

- One-time WorkClock Pro purchase through Google Play Billing.
- Remove banner ads and unlock additional themes/fonts/layouts.
- No subscription unless a genuinely recurring service is introduced (not expected for this app).
- Do not charge for core clock, seconds, screen-awake, date, or basic brightness features.

Illustrative price hypothesis: ₹99 one-time in India. This is not validated willingness-to-pay and must be tested. Set local prices through Play Console rather than hard-coding currencies.

## 12. Success metrics

Use aggregate Play Console / Android vitals and ad network reporting. Do not add a separate analytics SDK for v1.

- Crash-free user/device rate and ANR rate.
- Store listing impressions, visitors and conversion rate.
- Installs and uninstall trend.
- Ratings and review themes.
- Banner ad requests, match/fill rate, impressions and estimated earnings.
- Reported battery/heat complaints.
- Number and severity of bugs found in testing.
- Feature requests for themes, layout and ad removal.

Do not treat ad impressions alone as product success. Retention, reviews, stability and user complaints matter more.

## 13. Release acceptance criteria

v1.0 is ready only when:

1. P0 requirements pass on representative phone and tablet devices.
2. Clock appears without waiting for consent/ad requests.
3. The screen-awake behaviour works in foreground and ends naturally in background.
4. Power-button screen-off works.
5. Offline and ad-failure tests pass.
6. Banner has no overlap with the clock or controls.
7. UMP, privacy policy, Data Safety and Contains Ads declarations are completed accurately.
8. Play Console app content, content rating and target audience are correctly declared.
9. No critical or high-severity defects remain.
10. Store screenshots show the real released UI.
11. Required Play testing is complete for the developer account type.
12. The app does not claim official/certified time or safety-critical suitability.

## 14. Risks and mitigations

| Risk | Impact | Mitigation |
|---|---|---|
| Similar clock apps already exist | Low differentiation | Win on minimal UI, phone/tablet quality, reliable keep-awake, privacy and no intrusive ads. |
| Banner makes display too small | Poor experience | Dedicated bottom slot; suppress in short-height layouts. |
| Ad SDK adds data collection | Legal/policy complexity | UMP, privacy policy, Data Safety, version-specific SDK review and audience decision. |
| Child audience selected accidentally | Families/DPDP compliance burden | Decide target audience accurately before Play Console setup; do not casually select child age groups. |
| OLED image retention | Device/user complaint | Low-brightness option, subtle shifting, user warning; no guarantee. |
| Battery drain | User complaint | Disclose screen-on battery use; no background service; test realistic sessions. |
| Incorrect device time | User may rely on wrong time | Explain system-time dependency and prohibit official-time claims. |
| Ad SDK update breaks minimum OS | Compatibility change | Re-check current SDK min API before changing dependencies or releasing. |

## 15. Assumptions to confirm

See `Open-Decisions.md`. Most important: target age groups, launch countries, app name/package ID, legal publisher/contact identity, the precise banner fallback behaviour, and whether Pro ships after v1.0.
