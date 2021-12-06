**Mvi-core**

An image loading library for Android backed by Kotlin Coroutines. Coil is:

- **Fast**: Nothing odd.
- **Modern**: Mvi-core uses kotlin Flow and coroutines.
- **Easy to use**: Mvi-core API leverages Kotlin's language features for simplicity and minimal boilerplate.
- **Lightweight**: Nothing odd. Just a few Classes wrappers for whole architecture. No-one odd dependencies.

## Download
Mvi-core is available on `mavenCentral()`.

```kotlin
implementation("io.github.voitenkodev:mvi-core:1.0.2")
```

## Quick Start
To Create architecture using this library - lets investigate what we have:

# Wish
Every screen of application we can split to:
- **Async**: Any Request in Networking or Local Storage
- **Sync**: Any changes in UI wish is working synchronize.
- **Side**: Any singe event from screen (f.ex. show Toast, navigation, etc.)


