# WorkClock — User Flows

**Version:** 1.0  
**Date:** 12 November 2026

## 1. Primary flow — first launch

```mermaid
flowchart TD
    A[User installs WorkClock] --> B[User opens app]
    B --> C[Render local clock immediately]
    C --> D[Load local settings]
    D --> E[Apply time format, theme and keep-awake]
    C --> F[Start UMP consent information update]
    F --> G{Consent form required?}
    G -- Yes --> H[Show required privacy choice]
    H --> I{Can request ads?}
    G -- No --> I
    I -- Yes --> J[Request one anchored bottom banner]
    I -- No --> K[Do not request ad]
    J --> L{Ad loaded?}
    L -- Yes --> M[Display banner in dedicated slot]
    L -- No --> N[Keep clock working; quiet empty slot]
    K --> O[Clock remains fully usable]
    M --> O
    N --> O
```

The clock is rendered before consent/ad networking. A legally required consent interaction may still be presented, but an advertisement must never block clock initialization.

## 2. Returning user flow

```mermaid
flowchart TD
    A[User opens WorkClock] --> B[Read saved preferences]
    B --> C[Read current Android system time]
    C --> D[Render current clock]
    D --> E[Apply keep-awake if enabled]
    D --> F[Refresh UMP consent status]
    F --> G{Ads allowed and user is Free?}
    G -- Yes --> H[Load/reuse eligible banner]
    G -- No --> I[No ad request]
```

## 3. Clock controls flow

```mermaid
flowchart TD
    A[Clock visible] --> B[User taps clock/control affordance]
    B --> C[Quick Controls appear]
    C --> D{Choose action}
    D --> E[Toggle seconds/date]
    D --> F[Change 12h/24h format]
    D --> G[Toggle keep-awake]
    D --> H[Open theme selector]
    D --> I[Open full Settings]
    E --> J[Apply immediately and persist locally]
    F --> J
    G --> J
    H --> J
    I --> K[Settings screen]
    J --> L[Return to clock]
    K --> L
```

## 4. Screen-awake flow

```mermaid
flowchart TD
    A[Clock Activity becomes visible] --> B{Keep screen awake enabled?}
    B -- Yes --> C[Apply FLAG_KEEP_SCREEN_ON]
    B -- No --> D[Use normal Android timeout]
    C --> E{User action}
    E -- Press power --> F[Android turns display off / locks]
    E -- Home or app switch --> G[Activity backgrounded; normal timeout may resume]
    E -- Disable setting --> H[Clear keep-awake flag]
    E -- Close app --> I[No WorkClock background wake lock]
    G --> J[User returns]
    J --> K[Refresh system time]
    K --> B
```

## 5. Offline and ad-failure flow

```mermaid
flowchart TD
    A[Clock screen shown] --> B[Network unavailable or ad request fails]
    B --> C[Do not show an ad]
    C --> D[Keep local clock and settings active]
    D --> E[Network may return later]
    E --> F[Retry only according to controlled SDK/lifecycle policy]
    F --> G[Do not retry in a tight loop]
```

## 6. Privacy choices flow

```mermaid
flowchart TD
    A[User opens Privacy & Ads settings] --> B{UMP privacy options required?}
    B -- Yes --> C[Show UMP privacy options form]
    B -- No --> D[Show privacy policy and plain-language explanation]
    C --> E[UMP updates privacy choice]
    E --> F[Next ad request follows current consent state]
    D --> G[User can open policy/support]
```

## 7. App background and return flow

```mermaid
flowchart TD
    A[Clock active] --> B[User presses Home / switches app]
    B --> C[WorkClock is backgrounded]
    C --> D[Android manages screen timeout normally]
    D --> E[User returns to WorkClock]
    E --> F[Re-read device time, date and zone]
    F --> G[Re-apply keep-awake if enabled]
    G --> H[Resume or recreate banner based on AdMob lifecycle]
```

## 8. Future Pro purchase flow (not v1.0)

```mermaid
flowchart TD
    A[User opens Upgrade from Settings] --> B[Show one-time Pro benefits and price]
    B --> C[Google Play Billing purchase sheet]
    C --> D{Purchase successful?}
    D -- No --> E[Keep Free edition]
    D -- Yes --> F[Verify/query owned purchase and acknowledge]
    F --> G[Set Pro entitlement]
    G --> H[Remove banner and unlock Pro themes]
    H --> I[Restore entitlement on future launch from Play purchase query]
```

## 9. Flow exceptions

- **No network:** do not make the clock wait; no ad appears.
- **UMP cannot update:** do not bypass a consent requirement. Follow current UMP/AdMob documented failure handling and keep the clock visible.
- **Ad SDK crashes or fails:** isolate the ad surface; primary clock must remain usable.
- **User declines personalised ads:** use the eligible ad-serving mode returned by UMP/AdMob, or do not request ads if no eligible mode exists.
- **User taps power button:** WorkClock must not try to wake the screen back up.
- **Device time changes while open:** re-read system time; never keep displaying a stale incremented counter.
- **Rotation / resizing:** preserve settings and recompute dimensions; avoid duplicate banner instances.
