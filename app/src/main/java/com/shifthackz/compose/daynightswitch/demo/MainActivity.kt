package com.shifthackz.compose.daynightswitch.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shifthackz.compose.daynightswitch.DayNightSwitch
import com.shifthackz.compose.daynightswitch.demo.ui.theme.DayNightSwitchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val darkThemeLaunch = isSystemInDarkTheme()
            var darkTheme by remember { mutableStateOf(darkThemeLaunch) }
            DayNightSwitchTheme(darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DemoScreen(
                        darkTheme = darkTheme,
                        onChanged = { darkTheme = it },
                    )
                }
            }
        }
    }
}

@Composable
fun DemoScreen(
    modifier: Modifier = Modifier,
    darkTheme: Boolean = false,
    onChanged: (Boolean) -> Unit = {},
) {
    Column(modifier) {
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DayNightSwitch(
                checked = darkTheme,
                onCheckedChange = onChanged,
            )
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = "Enable dark theme",
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DayNightSwitchTheme(false) {
        DemoScreen()
    }
}
