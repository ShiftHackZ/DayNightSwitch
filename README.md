# DayNightSwitch

Implementation of Jetpack Compose material day/night switch. 

## Usage

Add DayNightSwitch and its' mutable state to your composable:

```kotlin
var darkTheme by remember { mutableStateOf(false) }
DayNightSwitch(
    checked = darkTheme,
    onCheckedChange = { darkTheme = it },
)
```

## Demo

<img src="https://raw.githubusercontent.com/ShiftHackZ/DayNightSwitch/main/docs/demo.gif" width="400" />
