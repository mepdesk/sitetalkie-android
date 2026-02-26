# SiteTalkie Android — Cursor/Claude Code Spine

PASTE THIS AT THE START OF EVERY NEW CURSOR OR CLAUDE CODE CHAT.

## What is this project?
SiteTalkie Android is forked from BitChat Android (permissionlesstech/bitchat-android). It is an offline BLE mesh messaging app for construction sites. It maintains 100% BLE protocol compatibility with SiteTalkie iOS (forked from BitChat iOS).

## NEVER MODIFY — BLE Protocol Layer
These files/packages contain the BLE mesh networking, encryption, and binary protocol code. They MUST stay identical to upstream BitChat Android to maintain cross-platform compatibility with iOS.

**Never modify any file in these packages/directories:**
- `app/src/main/java/com/bitchat/android/ble/` — BLE service, scanning, advertising, connection management
- `app/src/main/java/com/bitchat/android/protocol/` — binary packet encoding/decoding, message types, fragmentation
- `app/src/main/java/com/bitchat/android/crypto/` — Noise Protocol, X25519 key exchange, AES-256-GCM encryption
- `app/src/main/java/com/bitchat/android/mesh/` — mesh routing, multi-hop relay, peer discovery
- Any file containing `SERVICE_UUID`, `CHARACTERISTIC_UUID`, or BLE GATT operations

**Specifically never change:**
- BLE Service UUID: `F47B5E2D-4A9E-4C5A-9B3F-8E1D2C3A4B5C`
- BLE Characteristic UUID: `A1B2C3D4-E5F6-4A5B-8C9D-0E1F2A3B4C5D`
- Binary packet header format (14 bytes V1, 16 bytes V2)
- Message type constants (0x01 announce, 0x02 message, 0x03 leave, etc.)
- Fragmentation logic (469 byte fragments, 13 byte fragment headers)
- Compression logic (raw deflate, 100 byte threshold)
- Noise Protocol handshake
- Any TLV encoding/decoding

## NEVER RENAME
- Do NOT rename Kotlin packages from `com.bitchat.android` — internal names stay as bitchat
- Do NOT rename class files — keep original BitChat class names
- Do NOT rename the application ID in build.gradle unless specifically told to
- Internal references stay as "bitchat" for upstream merge compatibility

## SAFE TO MODIFY
- User-facing strings in `strings.xml` and hardcoded UI strings in .kt files (change "BitChat" → "SiteTalkie")
- Theme colours in theme files (amber #E8960C, dark background #0E1012)
- Layout files and Composable UI components (styling only)
- Navigation structure (tabs, screens)
- New feature files (create new .kt files for SiteTalkie-specific features)
- App icon and drawable resources
- Splash/loading screen

## Key Protocol Compatibility Notes
- Default channel MUST be `#site` (not `#mesh`) to match iOS SiteTalkie
- Android uses production UUID `...4B5C` always. iOS debug builds use `...4B5A` — test with iOS Release builds
- iOS sends extra TLV tags 0x05 (trade), 0x06-0x08 (location) that Android currently ignores — this is fine, both sides skip unknown TLVs gracefully
- iOS has an isRSR flag (0x10) that Android doesn't handle — no breakage, just suboptimal routing

## Project Structure
- Language: Kotlin
- UI: Jetpack Compose + Material Design 3
- Min SDK: API 26 (Android 8.0)
- Build: Gradle (build.gradle.kts)
- BLE: Android Bluetooth LE API

## Design System (SiteTalkie)
- Primary accent: #E8960C (amber/construction)
- Background: #0E1012
- Card/surface: #1A1C20
- Primary text: #F0F0F0
- Secondary text: #8A8E96
- Dark theme only
- Tab order: Chat | Nearby | Site | People | Settings
