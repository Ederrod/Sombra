package com.example.sombra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sombra.ui.theme.SombraTheme
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private lateinit var announcer: ComboAnnouncer
    private lateinit var roundManager: RoundManager
    private lateinit var bellSoundManager: SoundManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        announcer = ComboAnnouncer(this)
        bellSoundManager = SoundManager(this)

        enableEdgeToEdge()
        setContent {
            SombraTheme {
                var remainingTime by remember { mutableLongStateOf(0L) }

                roundManager = RoundManager(
                    onTimeTick = {
                        remainingTime = it
                    },
                    onShadowBoxSessionStarted = {
                        // Notify UI that a round has started
                        //TODO("Change UI layout to Round page")
                    },
                    onShadowBoxSessionEnded = {
                        //TODO("Change UI layout back")
                    },
                    onRoundStarted = {
                        bellSoundManager.playBoxingBell()
                    },
                    onRoundEnded = {
                        bellSoundManager.playBoxingBell()
                    },
                    onNewGeneratedCombo = {
                        announcer.speak(it)
                    }
                )

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainPage(
                        innerPadding = innerPadding,
                        onStartClicked = { settings -> startShadowBoxing(settings) },
                        remainingTime = remainingTime
                    )
                }
            }
        }
    }

    private fun startShadowBoxing(settings: Settings) {
        roundManager.startRounds()
    }

    override fun onDestroy() {
        announcer.shutdown()
        bellSoundManager.release()
        super.onDestroy()
    }
}

@Composable
fun MainPage(
    innerPadding: PaddingValues,
    onStartClicked: (settings: Settings) -> Unit,
    remainingTime: Long) {
    val skillLevelOptions = listOf("Amateur", "Pro")
    var selectedSkillLevel by remember { mutableStateOf(skillLevelOptions[0]) }

    val roundLength = remember { mutableIntStateOf(3) } // Default to 3 minutes
    val restTime = remember { mutableIntStateOf(1) } // Default to 1 minute
    
    var showClockUI by remember { mutableStateOf(false) }

    Surface(color = Color.White) {
        Column(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (showClockUI) {
                ClockUI(remainingTime = remainingTime)
            }
            else {
                Text(text = "Skill Level:")
                RadioGroup(options = skillLevelOptions, selectedOption = selectedSkillLevel.toString(), onOptionSelected = { selectedSkillLevel = it })

                Text(text = "Round Length: ${roundLength.intValue} minutes")
                Slider(value = roundLength.intValue.toFloat(), onValueChange = { roundLength.intValue = it.toInt() }, valueRange = 1f..10f, steps = 1)

                Text(text = "Rest Time: ${restTime.intValue} minutes")
                Slider(value = restTime.intValue.toFloat(), onValueChange = { restTime.intValue = it.toInt() }, valueRange = 1f..5f, steps = 1)

                Button(onClick = {
                    showClockUI = true
                    onStartClicked.invoke(Settings(selectedSkillLevel.toString(), roundLength.intValue, restTime.intValue))
                }) {
                    Text(text = "Start")
                }
            }
        }
    }
}

data class Settings(val skillLevel: String, val roundLength: Int, val restTime: Int)

@Composable
fun RadioGroup(options: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit) {
    Column {
        options.forEach { option ->
            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = option == selectedOption,
                    onClick = { onOptionSelected(option) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = option)
            }
        }
    }
}

@Preview(showSystemUi =  true)
@Composable
fun ClockUI(remainingTime: Long) {
    val minutes = TimeUnit.SECONDS.toMinutes(remainingTime)
    val seconds = TimeUnit.SECONDS.toSeconds(remainingTime) % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = "Round X",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 2.dp)

        )
        Text(
            modifier = Modifier.padding(bottom = 16.dp),
            text = "out of Y",
            style = MaterialTheme.typography.titleMedium
        )

    Surface(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        color = Color(0xFF333333),
        shape = RoundedCornerShape(8.dp),
//        elevation = 4.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = formattedTime,
                style = MaterialTheme.typography.titleLarge

                )
            }
        }
    }
}

