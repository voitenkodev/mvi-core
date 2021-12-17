![Support Platform](https://img.shields.io/static/v1?label=platforms&message=windows%20|%20macos%20|%20linux%20|%20android%20|%20ios%20&color=blue&?style=flat)
![Maven Central](https://img.shields.io/static/v1?label=Maven%20Central&message=1.0.4&color=blue&?style=flat)

# **Mvi-core**

A helper to create MVI architecture pattern in project:

- **Fast**: Nothing odd.
- **Modern**: Mvi-core uses kotlin Flow and coroutines.
- **Easy to use**: Mvi-core API leverages Kotlin's language features for simplicity and minimal boilerplate.
- **Lightweight**: Nothing odd. Just a few Classes wrappers for whole architecture. No-one odd dependencies.

# Download
Mvi-core is available on `mavenCentral()`.

```kotlin
implementation("io.github.voitenkodev:mvi-core:1.0.4")
```

# Quick Start

To Create architecture using this library - let's investigate what we have:

## Feature<Async, Sync, Side, State>

Component which corrects current `State` of screen (or part of screen) regarding new Wish (`Async`, or `Sync`, or `Side`)
- [Example of Feature](documentation/Feature.md)

### Wish:

Every screen events of application we can split to:
- `Async`: Any Request in Networking or Local Storage
- `Sync` Any changes in UI wish is working synchronize.
- `Side` Any singe event from screen (f.ex. show Toast, navigation, etc.)

- `SyncReducer`: Synchronous parser for `Sync` wish's to new `State`.
- `AsyncReducer`: Asynchronous parser for `Async` wish's to new `Sync` wish.
- `AffectConventions`: Calling any `Wish`, trigger an another wish's according to the some condition.

### State:

Data class is like a projection of UI screen (or part of screen) in current moment.

 - Variable `state` works wish a pattern observer, using Kotlin Flow.

### Side: 

Chanel for listening side wish's, using Kotlin Channel.

![feature-example](https://user-images.githubusercontent.com/93656470/145410478-32b1f69d-78c6-4b22-bb33-df783d3f9b7e.jpg)

## MviCore<Root>
 
 Component for combining `Feature`s and mapping it to State of whole screen.
 - `FeatureTag`: Identficator for `Feature`.
 - `Root`: State of whole screen.
 
 - [Example of MviCore](documentation/MviCore.md)
 
![Mvi-Core-Processor](https://user-images.githubusercontent.com/93656470/146517399-6ec9ad6a-648f-49a4-971c-3f6362de3593.jpg)

