<div align="center">

# ✋ HandBoard

**One-handed keyboard for everyone**

An accessibility-focused Android keyboard designed for people who use their phone with one hand, one finger, or have limited mobility.

[![Build](https://github.com/omersusin/Handboard/actions/workflows/build.yml/badge.svg)](https://github.com/omersusin/Handboard/actions/workflows/build.yml)
[![API 24+](https://img.shields.io/badge/API-24%2B-brightgreen.svg)](https://developer.android.com/about/versions/nougat)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-purple.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-UI-blue.svg)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

[Features](#features) • [Download](#download) • [Layouts](#layouts) • [Screenshots](#screenshots) • [Setup](#setup) • [Build](#build) • [Contributing](#contributing)

</div>

---

## 🎯 Who Is This For?

HandBoard is built for people who:

- 👆 Use their phone with **one hand**
- 👍 Type with only their **thumb**
- 🤚 Can only use their **right** or **left** hand
- ☝️ Type with a **single finger**
- ♿ Have **limited mobility** or a **physical disability**
- 🧏 Need an **accessible** keyboard experience

Standard keyboards assume two-handed use. HandBoard doesn't.

---

## ✨ Features

### Keyboard
- 🎹 **4 keyboard layouts** — QWERTY, Right Hand, Left Hand, Thumb
- 🔤 **Shift & Caps Lock** with visual feedback
- ⌫ **Hold-to-repeat backspace** — hold down to delete continuously
- 🔢 **Symbol & number layer** — switch with one tap
- 🌐 **Quick layout switching** — cycle through layouts from the toolbar

### Emoji & Clipboard
- 😊 **3600+ emojis** — powered by Google's emoji2-emojipicker
- 🎨 **Skin tone variants** — long press for options
- 📋 **Clipboard history** — remembers copied text
- 🖼️ **Image paste support** — paste screenshots and images (Android 7.1+)

### Word Prediction
- 💬 **Smart suggestions** — Trie + Bigram engine
- 📚 **400+ word dictionary** — English (US) built-in
- 🧠 **Learns from you** — gets smarter as you type
- 🔢 **Adjustable suggestion count** — show 1 to 5 suggestions

### Customization
- 📏 **Keyboard height** — scale from 0.7x to 1.5x
- 📐 **Keyboard width** — 50% to 100% of screen
- ◀️ **Position** — left, center, or right aligned
- 📱 **Bottom padding** — fix navigation bar overlap
- 📳 **Haptic feedback** — toggle vibration on/off
- 🎨 **Material You** — dynamic colors on Android 12+, purple fallback on older versions

### Accessibility
- ♿ Designed from the ground up for one-handed use
- 🎯 Large touch targets
- 🔍 Clear visual feedback on key press
- 🌙 Dark theme for reduced eye strain

---

## 📥 Download

### From GitHub Releases
1. Go to the [Releases](https://github.com/omersusin/Handboard/releases) page
2. Download the latest `HandBoard-v*.apk`
3. Install on your Android device (allow unknown sources if prompted)

### From GitHub Actions
1. Go to [Actions](https://github.com/omersusin/Handboard/actions)
2. Click the latest successful build
3. Download the `HandBoard-debug` artifact

> **Minimum:** Android 7.0 (API 24)
> **Recommended:** Android 12+ for Material You theming

---

## 🎹 Layouts

| Layout | Best For | Description |
|---|---|---|
| **QWERTY** | General use | Standard layout, 4 rows |
| **Right Hand** | Right-handed users | 5-column compact, controls on right |
| **Left Hand** | Left-handed users | 5-column compact, controls on left |
| **Thumb** | Single thumb typing | 5-column, extra rows, large keys |

All layouts include:
- Full alphabet (a–z)
- Symbol/number layer
- Shift, Backspace, Enter, Space
- Quick access to Emoji and Clipboard

---

## 🛠️ Setup

After installing the APK:

1. **Open HandBoard** app
2. Tap **"Open Settings"** → Enable HandBoard in system keyboard list
3. Go back → Tap **"Select Keyboard"** → Choose HandBoard
4. Open any app with a text field → HandBoard appears!

### Customize
- Open HandBoard app → Tap **"⚙ Keyboard Settings"**
- Adjust layout, size, position, predictions, haptic feedback

---

## 🏗️ Build

### Prerequisites
- JDK 17
- Android SDK (compileSdk 35)
- Gradle 8.7+

### Build locally
```bash
git clone https://github.com/omersusin/Handboard.git
cd Handboard
./gradlew assembleDebug
HandBoard/
├── app/src/main/
│   ├── java/handboard/app/
│   │   ├── core/theme/       → Material You theme, colors, typography
│   │   ├── ime/              → InputMethodService, Compose IME helper
│   │   ├── layout/           → Layout engine, key models, 4 layouts
│   │   │   └── ui/           → KeyView, KeyboardView, toolbar, icons
│   │   ├── prediction/       → Trie + Bigram word predictor
│   │   ├── emoji/            → Google emoji2 picker integration
│   │   ├── clipboard/        → Clipboard history with image support
│   │   └── settings/         → DataStore preferences, settings UI
│   ├── assets/
│   │   └── en_us.txt         → English dictionary (400+ words)
│   └── res/
│       ├── values/strings.xml
│       └── xml/method.xml    → IME configuration
├── .github/workflows/
│   ├── build.yml             → CI build on every push
│   └── release.yml           → Release APK on version tag
└── build.gradle.kts
cat > LICENSE << 'EOF'
MIT License

Copyright (c) 2025 Ömer

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
