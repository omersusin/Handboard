<div align="center">

# ✋ HandBoard

**One-handed keyboard for everyone**

An accessibility-focused Android keyboard designed for people who type with one hand, one finger, or have limited mobility.

[![Build](https://github.com/omersusin/Handboard/actions/workflows/build.yml/badge.svg)](https://github.com/omersusin/Handboard/actions/workflows/build.yml)
[![API 24+](https://img.shields.io/badge/API-24%2B-brightgreen.svg)](https://developer.android.com/about/versions/nougat)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-purple.svg)](https://kotlinlang.org)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

</div>

---

## Who Is This For?

HandBoard is designed for people who:

- use their phone with **one hand**
- type mostly with their **thumb**
- can only use their **right or left hand**
- type with a **single finger**
- have **limited mobility**
- need a more **accessible keyboard**

---

## Features

### Keyboard Layouts

- **QWERTY** — standard 4-row layout
- **Right Hand** — 5-column compact layout with controls on the right
- **Left Hand** — 5-column compact layout with controls on the left
- **Thumb** — 5-column layout optimized for thumb typing
- **Two symbol layers** — common symbols plus extended characters
- **Optional number row** — toggle in settings

### Typing

- **Long-press accented characters** — à, é, ñ, ü, ş, ğ, ö and more
- **Spacebar cursor control** — swipe left/right to move cursor
- **Auto-capitalize** after sentences
- **Hold backspace** for continuous delete
- **Emoji-aware backspace** — prevents breaking multi-byte characters

### Text Editing Panel

- Cursor arrows — **left, right, home, end**
- **Select All / Copy / Cut / Paste**
- **Undo / Redo**

### Emoji

- **3600+ emojis** via Google emoji2-emojipicker
- **Skin tone variants**
- **Search**

### Kaomoji

- **100+ kaomoji** in categories
- **Grid layout** for quick browsing

### Inline Search

Search **emoji, kaomoji, and clipboard items** from a single panel.

Results appear instantly while typing.

### Clipboard

- **Clipboard history** (opt-in)
- **Image paste support** (Android 7.1+)
- **Clear all** with one tap

### Word Prediction

- **Trie + Bigram prediction engine**
- **Small built-in dictionary**
- **Persistent learning**
- **Auto-disabled in password fields**
- **1–5 suggestions adjustable**

### Customization

- Keyboard **height** (0.7x – 1.5x)
- Keyboard **width** (50% – 100%)
- Keyboard **alignment** (left / center / right)
- **Bottom padding** adjustment
- **Haptic feedback**
- **Key sound**
- **Material You theme (Android 12+)**
- **Light / Dark keyboard**
- **Follow system theme**

### Accessibility

- **Large keys mode**
- **High contrast option**
- **TalkBack-friendly semantics**
- **Predictions disabled on password fields**
- **Clipboard disabled by default**

---

## Download

Download the latest APK from:

https://github.com/omersusin/Handboard/releases

**Minimum Android version:** Android 7.0 (API 24)

---

## Setup

1. Install the APK
2. Open **HandBoard**
3. Tap **Open Settings**
4. Enable **HandBoard keyboard**
5. Tap **Select Keyboard**
6. Choose **HandBoard**
7. Customize in **Keyboard Settings**

---

## Build From Source

Requirements:

- JDK 17
- Android SDK
- compileSdk 35

Clone repository:

git clone https://github.com/omersusin/Handboard.git
cd Handboard

Build debug APK:

gradle assembleDebug

Output file:

app/build/outputs/apk/debug/app-debug.apk

---

## Tech Stack

Language: Kotlin  
UI: Jetpack Compose  
IME: Android InputMethodService  
Theme: Material 3 / Material You  
Emoji: emoji2-emojipicker  
Prediction: Trie + Bigram engine  
Storage: DataStore Preferences  
CI/CD: GitHub Actions  

---

## Project Structure

app/src/main/java/handboard/app/

core/theme/ → Colors, Theme, Typography  
ime/ → HandBoardService, ComposeIMEHelper  
layout/ → Keyboard layouts, key actions, state logic  
layout/ui/ → KeyboardView, KeyView, Toolbar, Panels  
prediction/ → Trie dictionary, WordPredictor, SuggestionBar  
emoji/ → EmojiView, KaomojiView  
clipboard/ → ClipboardHistory, ClipboardView  
settings/ → PreferencesManager, SettingsScreen  

---

## Known Limitations

- Dictionary is still small
- No swipe typing (intentional design)
- Image paste depends on target app support
- Undo / redo relies on key events

---

## Roadmap

- More languages
- Custom layouts
- Arc / radial layout
- GIF panel
- Sticker panel
- Voice input
- Tablet / split keyboard
- Personal dictionary UI

---

## License

MIT License

---

## Contact

Email: tekgercek73@gmail.com  
GitHub: https://github.com/omersusin  

<div align="center">

Made with ❤️ for accessibility

</div>

