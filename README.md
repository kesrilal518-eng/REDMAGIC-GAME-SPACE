# REDMAGIC GAME SPACE

**Professional Android gaming utility inspired by REDMAGIC 10 Pro Game Space**

A complete, modern Jetpack Compose application that brings a Game Space–style command center to Android devices. Optimized for daily drivers such as the **Redmi Note 11T 5G** while simulating REDMAGIC-exclusive features (ICE cooling fan curves, RGB lighting profiles, charge bypass, and per-game performance presets).

> **Disclaimer:** This is an independent open-source project. It is **not** affiliated with, endorsed by, or sponsored by Nubia, REDMAGIC, or ZTE.

---

## Features

### Performance Command Center
- **Performance Modes** — Eco / Balance / Performance / Beast Mode
- Live CPU, GPU, RAM, temperature, FPS and network latency metrics
- Smooth animated progress indicators and real-time simulation engine

### ICE Cooling System
- Toggle intelligent thermal control
- Multi-level internal fan control (Off → Turbo)
- Live RPM readout and temperature-aware fan curves

### Battery & Charging
- **Charge Bypass** — Prefer charger power during long sessions (hardware-dependent)
- **Battery Protection** — Configurable charge limit (50–100%) for longevity
- Charging temperature monitor

### RGB Lighting
- Modes: Off, Static, Breathing, Rainbow, Wave, Music Sync, Custom
- Color presets + brightness control
- Profiles stored and applied per game

### Game Profiles
- Auto-discover installed games
- Per-game performance mode, fan speed, RGB, FPS overlay and latency settings
- Favorites and quick profile creation

### Overlay & Wellbeing
- FPS / latency / CPU overlay service (requires SYSTEM_ALERT_WINDOW)
- Digital wellbeing session reminders with configurable time limits

### Technical Stack
- **Language:** Kotlin 2.0
- **UI:** Jetpack Compose + Material 3
- **Architecture:** MVVM + Repository
- **Persistence:** Room (profiles & performance logs) + DataStore (settings)
- **Min SDK:** 26 · **Target SDK:** 35 · **Compile SDK:** 35
- **CI:** GitHub Actions Android build workflow

---

## Project Structure

```
REDMAGIC-GAME-SPACE/
├── .github/workflows/android.yml   # CI build
├── app/
│   ├── src/main/
│   │   ├── java/com/redmagic/gamespace/
│   │   │   ├── data/               # Room, models, repositories
│   │   │   ├── ui/                 # Compose screens, theme, components
│   │   │   ├── viewmodel/
│   │   │   ├── service/            # Overlay & performance monitor
│   │   │   └── util/
│   │   ├── res/
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── LICENSE
└── README.md
```

---

## Getting Started

### Prerequisites
- Android Studio Ladybug (2024.2+) or newer
- JDK 17
- Android SDK 35

### Clone & Open
```bash
git clone https://github.com/kesrilal518-eng/REDMAGIC-GAME-SPACE.git
cd REDMAGIC-GAME-SPACE
```

Open the folder in Android Studio and let Gradle sync.

### Build
```bash
./gradlew assembleDebug
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`

### Release
```bash
./gradlew assembleRelease
```

---

## Permissions

| Permission | Purpose |
|------------|---------|
| `POST_NOTIFICATIONS` | Performance & wellbeing notifications |
| `FOREGROUND_SERVICE` | Background performance monitor |
| `SYSTEM_ALERT_WINDOW` | FPS / latency overlay |
| `PACKAGE_USAGE_STATS` / `QUERY_ALL_PACKAGES` | Game discovery |
| `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | Reliable monitoring |

Grant overlay and usage-access permissions from system settings when prompted for full functionality.

---

## Hardware Notes

| Feature | REDMAGIC hardware | Other devices (e.g. Redmi Note 11T 5G) |
|---------|-------------------|---------------------------------------|
| Fan control | Real internal fan | Simulated RPM & thermal response |
| RGB | LED matrix / strip | Profile storage + on-screen preview |
| Charge Bypass | Hardware path | Preference stored; OS-level effect limited |
| Metrics | Can be extended with vendor APIs | High-quality simulation driven by mode + fan |

---

## GitHub Actions

The included workflow (`.github/workflows/android.yml`) runs on every push and pull request to `main`:

- Sets up JDK 17
- Caches Gradle
- Builds debug APK
- Uploads the APK as an artifact

---

## License

MIT License — see [LICENSE](LICENSE).

---

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-improvement`)
3. Commit your changes
4. Push and open a Pull Request

Ideas welcome: real vendor SDK integrations, more overlay widgets, custom themes, Tasker/MacroDroid plugins, Wear OS companion, etc.

---

**Built with ❤️ for mobile gamers.**
