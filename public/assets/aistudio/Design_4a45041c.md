# WorkClock — Visual Design System

**Version:** 1.0  
**Date:** 12 November 2026  
**Design direction:** Quiet, readable, functional, minimal

## 1. Design principles

1. **Time is the hero.** The time must be the largest and most visually important element.
2. **Readable at a glance.** High contrast, stable digit widths, uncluttered spacing.
3. **No visual noise.** No gradients behind digits, unnecessary illustration, feed, navigation rail or busy animation.
4. **Ads are separate.** One clearly bounded bottom banner region; never overlay the clock.
5. **Device-adaptive.** Phone portrait, phone landscape, tablet portrait and tablet landscape all receive deliberate layouts.
6. **Comfortable for long sessions.** Optional dim setting, restrained brightness and minimal animation.
7. **Accessible.** Respect font/display scaling where practical; do not make controls depend only on colour or gesture.

## 2. Brand direction

**Working name:** WorkClock  
**Short product statement:** See the time. Nothing else.  
**Personality:** Calm, dependable, precise-looking, neutral, unobtrusive.

The name is provisional. Check Play Store name conflicts, domain availability and trademark risk before publishing.

## 3. Base theme — Midnight

| Token | Value | Usage |
|---|---|---|
| `wc-bg` | `#06080D` | Main background |
| `wc-surface` | `#10141C` | Settings cards / control sheet |
| `wc-surface-2` | `#1A202B` | Secondary panels |
| `wc-text-primary` | `#F5F7FA` | Main time |
| `wc-text-secondary` | `#B6C0CE` | Date and labels |
| `wc-text-muted` | `#788596` | Supporting metadata |
| `wc-accent` | `#79B8FF` | Selected state / focus |
| `wc-divider` | `#29313D` | Dividers |
| `wc-ad-bg` | `#0D1118` | Reserved banner region background |
| `wc-focus` | `#A6D0FF` | Focus outline |
| `wc-danger` | `#E57373` | Error only |

Text and controls must meet WCAG AA contrast targets where applicable. Do not place muted text over a low-contrast background merely to make the screen look minimal.

## 4. Optional themes

Provide a small, curated set. Each theme must preserve readable contrast.

| Theme | Background | Time | Secondary text | Intent |
|---|---|---|---|---|
| Midnight | `#06080D` | `#F5F7FA` | `#B6C0CE` | Default dark |
| OLED Black | `#000000` | `#F5F5F5` | `#A8A8A8` | Darkest display |
| Forest | `#07120D` | `#D9FBE8` | `#9AB9A8` | Green accent feeling |
| Amber | `#171006` | `#FFE0A3` | `#C8AD7B` | Warm display |
| Navy | `#07111F` | `#D8E8FF` | `#9BB4D1` | Cool display |
| Paper (optional) | `#F4F1E9` | `#17202A` | `#536070` | Light mode |

In v1, expose no more than four or five themes. Do not create a theme editor yet.

## 5. Typography

- Default family: Android sans-serif / Roboto system family.
- Main time: bold or semibold; avoid thin font weights at distance.
- Use tabular numerals so the clock does not horizontally jitter when digits change.
- Keep the colon visually consistent. A blinking colon is not necessary and should be off by default.
- Date: medium weight, uppercase only if it remains readable in all supported locales.
- Secondary labels: small but not below accessible practical sizes.

### Suggested scale (adapt to available space)

| Element | Phone portrait | Tablet portrait | Landscape |
|---|---:|---:|---:|
| Main time | 64–88sp | 88–120sp | 48–88sp depending on height |
| 12-hour AM/PM label | 14–18sp | 18–20sp | 12–16sp |
| Date | 14–18sp | 18–22sp | 12–18sp |
| Settings section title | 18–20sp | 20–24sp | 18–20sp |
| Settings row label | 14–16sp | 16–18sp | 14–16sp |
| Helper text | 12–14sp | 14sp | 12–14sp |

These are starting design ranges, not hard-coded universal sizes. Use available width/height and font scale to calculate actual size. Avoid clipping when system font scaling is increased.

## 6. Layout and spacing tokens

Use a 4dp base spacing grid.

| Token | Value |
|---|---:|
| `space-1` | 4dp |
| `space-2` | 8dp |
| `space-3` | 12dp |
| `space-4` | 16dp |
| `space-5` | 20dp |
| `space-6` | 24dp |
| `space-8` | 32dp |
| `space-10` | 40dp |
| `space-12` | 48dp |

- Main screen safe content padding: 16–24dp depending on width.
- Minimum interactive target: 48dp where practical.
- Settings cards: 12–16dp internal padding.
- Corner radius: 12dp cards, 16dp sheets, 8dp small controls.
- No persistent bottom navigation bar; the clock is one primary screen.

## 7. Main clock composition

### Portrait

- Main clock horizontally centred.
- Use a flexible vertical layout so time remains visually central.
- Date below the time, with moderate spacing.
- Small control affordance near bottom of clock area or revealed on tap.
- Banner slot anchored to bottom, outside the clock content.

### Landscape

- Main clock horizontally and vertically balanced.
- Reduce secondary spacing before reducing time readability.
- Hide banner if available height becomes too small or ad region would shrink the clock below minimum readable size.
- Controls can be arranged in a horizontal row.

### Tablet

- Avoid stretching the clock to the full width. Use a centred max-width clock container.
- Main digits can be larger, but maintain balance and whitespace.
- Banner can be width-adaptive and full available width within safe screen bounds.

## 8. Component styling

### Main time

- No card or border around the main digits.
- Main time uses high-contrast foreground.
- Prefer a single line. If width cannot fit, reduce font size responsively; do not split digits across lines.
- 12-hour format may show AM/PM as a smaller aligned suffix.

### Date

- Secondary emphasis.
- If the locale date string is long, allow a smaller size or locale-aware wrapping rather than truncating essential date information.

### Control affordance

- Icon buttons use clear labels/content descriptions.
- Selected settings use accent plus text/icon state, not colour alone.
- Controls should not look like advertisements.

### Ad region

- Reserved in-layout bottom region; not an overlay or floating layer.
- Label the region as an ad only where appropriate/required by the ad format and network policy; do not imitate clock controls.
- Keep at least 8–12dp separation from the nearest app control.
- Do not draw a button border around the entire ad region.
- Never place decorative arrows or tap cues near the ad.

## 9. Motion

- No animation on the time digits other than their natural text update.
- Controls can fade in/out over approximately 120–180ms.
- No blinking colon by default.
- No pulsing, bouncing, marquee or auto-scrolling text.
- Do not animate the banner container. Ad creative movement is controlled by the ad network; review content settings and accept that all creatives cannot be guaranteed static.
- Respect system animation scale / reduced-motion preference where possible.

## 10. OLED and long-session care

Optional settings may:

- Offer a very dark/black theme.
- Let the user lower the app window brightness.
- Apply tiny, infrequent pixel-position shifts to the main content if technically safe and not visually distracting.
- Hide controls after inactivity.

Do not imply that these methods eliminate burn-in. Static high-brightness content can contribute to image retention on some displays. The screen-awake feature can increase battery use and heat, especially at high brightness. Tell users to use a stand, charging arrangement and brightness appropriate to the device.

## 11. Iconography

- Use Material Symbols / Material icons or a small consistent icon set.
- Prefer simple line icons: settings, brightness, palette, expand/full-screen, privacy, info.
- Do not use emoji as functional icons.
- Every icon-only control requires an accessibility label and adequate hit area.

## 12. App icon direction

Suggested concept: a minimal digital clock face or two bold digits on a dark square. Avoid railway signals, train silhouettes, bus icons or professional transport badges because v1 is a general-purpose clock.

The icon must remain legible at small sizes and should be tested on light/dark launcher backgrounds. Final icon artwork is not included in this documentation pack.

## 13. Accessibility design requirements

- Test TalkBack reading order.
- Main time should have an intelligible description such as “Current time, 17:42:08” but should not announce every second continuously to TalkBack users.
- Controls must have text labels or content descriptions.
- Support font/display scaling and avoid clipped settings labels.
- Ensure contrast for each theme.
- Do not convey selected/unselected state with colour alone.
- Settings must be reachable without relying on a long press or a precise gesture.
- Consider a visible settings button in addition to tap-to-toggle controls.

## 14. Design QA

Before implementation is accepted, review screenshots and live builds on:

- 5–6 inch phone portrait.
- Large phone portrait.
- Phone landscape with short height.
- 8–10 inch tablet portrait.
- Tablet landscape.
- System font scaling at default and enlarged settings.
- AMOLED and LCD screens.
- Display cutout / gesture navigation configurations.

---

**Design decision:** Keep v1 visually restrained. One primary clock screen and one focused settings experience are enough.
