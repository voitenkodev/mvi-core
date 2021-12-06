# **Mvi-core**

A helper to create MVI architecture pattern in project:

- **Fast**: Nothing odd.
- **Modern**: Mvi-core uses kotlin Flow and coroutines.
- **Easy to use**: Mvi-core API leverages Kotlin's language features for simplicity and minimal boilerplate.
- **Lightweight**: Nothing odd. Just a few Classes wrappers for whole architecture. No-one odd dependencies.

# Download
Mvi-core is available on `mavenCentral()`.

```kotlin
implementation("io.github.voitenkodev:mvi-core:1.0.2")
```

# Quick Start
To Create architecture using this library - let's investigate what we have:
## Feature<Async, Sync, Side, State>
Component which corrects current `State` of screen (or part of screen) regarding new Wish (`Async`, or `Sync`, or `Side`)

### Wish:

Every screen events of application we can split to:
- `Async`: Any Request in Networking or Local Storage
- `Sync` Any changes in UI wish is working synchronize.
- `Side` Any singe event from screen (f.ex. show Toast, navigation, etc.)

### State:

Data class is like a projection of UI screen (or part of screen) in current moment.

## Example of Feature
