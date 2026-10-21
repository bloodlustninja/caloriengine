# ⚡ caloriengine // Cyberpunk & Retro Terminal Nutrition System

```
   ██████╗ █████╗ ██╗      ██████╗ ██████╗ ██╗███████╗███╗   ██╗ ██████╗ ██╗███╗   ██╗███╗   ██╗
  ██╔════╝██╔══██╗██║     ██╔═══██╗██╔══██╗██║██╔════╝████╗  ██║██╔════╝ ██║████╗  ██║████╗  ██║
  ██║     ███████║██║     ██║   ██║██████╔╝██║█████╗  ██╔██╗ ██║██║  ███╗██║██╔██╗ ██║██╔██╗ ██║
  ██║     ██╔══██║██║     ██║   ██║██╔══██╗██║██╔══╝  ██║╚██╗██║██║   ██║██║██║╚██╗██║██║╚██╗██║
  ╚██████╗██║  ██║███████╗╚██████╔╝██║  ██║██║███████╗██║ ╚████║╚██████╔╝██║██║ ╚████║██║ ╚████║
   ╚═════╝╚═╝  ╚═╝╚══════╝ ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝╚═╝  ╚═══╝ ╚═════╝ ╚═╝╚═╝  ╚═══╝╚═╝  ╚═══╝
```

> **caloriengine**: A high-performance, hardware-themed Android Nutrition, Activity, and Telemetry Engine built for lifters, biohackers, and cyberpunk enthusiasts. Powered by a single-page React UI running inside a native Android WebView, Jetpack Compose Protocol Hub, and Homescreen Android App Widgets with live Biometric Synthesizer Waveforms.

---

## 📸 Key Highlights & Feature Matrix

- **🎮 3 Hardware Shell Themes**:
  - **Phosphor Green**: Classic Cyberpunk Terminal (`#00FF66`, CRT Scanlines, Pitch Black, Vector Grid)
  - **Atomic Pink Game Boy**: Translucent Lavender / Pink Shell & Cobalt Blue PCB (`#1E3A8A` board with copper traces, `#F4D3EE` pastel bezel, `#EC4899` hot pink 3D buttons)
  - **80s Macintosh Platinum**: Retro Apple Cream (`#E2DDD5`), System Blue (`#0044AA`), Atari Emerald (`#00A36C`)
- **📊 2x2 Metabolic HUD App Widget**:
  - 42dp dual overlapping sine wave synthesizer canvas (`Math.sin()`) — Static White Target Baseline Wave + Live Energy Wave scaling to 100% overlap at goal sync or flatlining at 0 KCAL.
  - Live P / C / F macro telemetry row displayed directly above quick action buttons.
- **⚡ Quick-Log Homescreen Overlay**:
  - Standalone translucent overlay activity (`QuickLogActivity`) for instant meal logging without interrupting current app tasks.
- **🍛 160+ Local Regional Food & Eatery Database (ICMR-NIN & IFCT Standard)**:
  - **Iconic Eateries & Dishes**: `PUNE CAMP BURGER` (752 KCAL | 48g P), `GOODLUCK KEEMA PAV` (525 KCAL), `KATAKIRR MISAL` (525 KCAL), `VAISHALI DOSA`, `SP BIRYANI`, `CHITALE BAKARWADI`.
  - **Alcoholic Drinks & Craft Brews**: `KINGFISHER`, `BIRA 91`, `DOOLALLY`, `EFFINGUT`, `OLD MONK`, `SULA WINE`, `TODDY`.
  - **Non-Alcoholic Drinks & Sarbats**: `GANNACHA RAS`, `NEERA`, `KOKUM SARBAT`, `MASALA MILK`, `THANDAI`, `PUNERI LASSI`.
  - **Festive Sweets & Desserts**: `AMRAKHAND`, `RABDI`, `GAJAR HALWA`, `RASMALAI`, `MODAK`, `PURAN POLI`, `KAJU ROLL`.
- **🧠 Smart Food Parser & Categorical Macro Engine**:
  - Automatically converts written number words (`SIX STEAM MOMOS` → `6 STEAM MOMOS`).
  - Unit-calibrated gram & piece parser (`50G MILKY BAR` → 245 KCAL vs `1 MILKY BAR` → 98 KCAL / 20g bar).
  - Categorical fallback engine for gym meals, rolls, frankies, protein oats, meats, dairy, and chocolates.
- **🔄 Midnight Resets & 2-Day Rolling Telemetry Queue**:
  - Automatically resets daily sheets at 12:00 AM midnight while archiving yesterday's macros into `ACT_02` without memory/storage bloat.
- **🎯 Exact Calorie Goal & Target Sync**:
  - Synchronizes `cyber_target` down to the exact single calorie into Android `SharedPreferences` for widget and app parity.
  - Persistent goal state (`CUT`, `BULK`, `MAINTAIN`) across app restarts.

---

## 🛠️ Architecture & Tech Stack

```
   ┌─────────────────────────────────────────────────────────┐
   │                    CALORIENGINE CORE                    │
   └────────────────────────────┬────────────────────────────┘
                                │
         ┌──────────────────────┴──────────────────────┐
         ▼                                             ▼
  ┌──────────────┐                             ┌──────────────┐
  │  FRONTEND    │                             │  NATIVE      │
  │  (WebView)   │                             │  (Android)   │
  └──────┬───────┘                             └──────┬───────┘
         │                                            │
         ├── React 18 + Babel Standalone              ├── Jetpack Compose (Protocol Hub)
         ├── Hardware CSS Custom Variables            ├── App Widgets (Metabolic HUD & Quick Add)
         └── Local Storage Telemetry Sync             ├── Translucent QuickLogActivity
                                                      └── JavaScript Interface Bridges
```

| Layer | Technologies Used |
| :--- | :--- |
| **Android Core** | Kotlin, Android SDK 37, Gradle 8.x |
| **UI Frameworks** | Single-Page React 18 inside WebView + Jetpack Compose |
| **Widgets** | Android AppWidgetProvider, Canvas Drawing (`Math.sin()` waveforms), RemoteViews |
| **Database & Nutrition** | ICMR-NIN & IFCT Standard local DB + OpenFoodFacts Live API |
| **Design System** | Retro Hardware CRT, Phosphor Neon, Translucent Game Boy Shell, Macintosh Platinum |

---

## 🚀 Quick Start & Installation

### Prerequisites
- Android Studio Ladybug (2024.2.1+) or latest
- Android SDK 37
- Kotlin 2.2.10
- JDK 17+

### Building from Source

1. **Clone the repository**:
   ```bash
   git clone https://github.com/bloodlustninja/caloriengine.git
   cd caloriengine
   ```

2. **Open in Android Studio**:
   - Launch Android Studio and open the `caloriengine` project folder.
   - Perform a **Gradle Sync**.

3. **Build Debug APK**:
   ```bash
   ./gradlew app:assembleDebug
   ```

4. **Run on Connected Device**:
   ```bash
   ./gradlew app:installDebug
   ```

---

## 📡 SharedPreferences & JavaScript Bridge Matrix

| Bridge Method | Description |
| :--- | :--- |
| `saveGoal(goal)` | Persists fitness objective (`CUT`, `MAINTAIN`, `BULK`) across app launches. |
| `syncTarget(target)` | Syncs exact calorie target directly to `SharedPreferences` for widget alignment. |
| `saveTheme(theme)` | Saves active hardware shell (`GREEN`, `PURPLE`, `BEIGE`). |
| `syncStorage(data)` | Synchronizes full daily meal telemetry between React local storage & Native Android widgets. |

---

## 📜 License

This project is open source under the [MIT License](LICENSE).

---

<p align="center">
  <b>Built with ⚡ for lifters, techies & cyberpunk lovers in Pune, India.</b>
</p>
