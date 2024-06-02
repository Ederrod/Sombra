package com.example.sombra

import android.os.CountDownTimer
import kotlin.random.Random

class RoundManager(
    private val onTimeTick: (timeRemainingInSec: Long) -> Unit,
    private val onShadowBoxSessionStarted: () -> Unit,
    private val onShadowBoxSessionEnded: () -> Unit,
    private val onRoundStarted: () -> Unit,
    private val onRoundEnded: () -> Unit,
    private val onNewGeneratedCombo: (combo: String) -> Unit) {
    companion object {
        const val TOTAL_ROUNDS: Int = 5

        // Round time in milliseconds
        const val ROUND_LENGTH_MS: Long = 180000 // Three minutes
        const val BETWEEN_ROUND_LENGTH_MS: Long = 60000 // One minute

        val translation: Map<Int, String> = mapOf(
            1 to "Jab",
            2 to "Cross",
            3 to "Left Hook"
        )

        val amateurCombos = arrayOf(
            arrayOf(1, 1),
            arrayOf(1, 2),
            arrayOf(1, 1, 2),
            arrayOf(1, 2, 3),
            arrayOf(1, 1, 2, 3),
        )

        val proCombos = arrayOf(
            arrayOf(1, 1),
            arrayOf(1, 2),
            arrayOf(1, 1, 2),
            arrayOf(1, 2, 3),
            arrayOf(1, 1, 2, 3)
            // TODO: Add more combos :)
        )
    }

    private var roundStarted: Boolean = false
    private var currentRound: Int = 1

    fun startRounds() {
        if (!roundStarted) {
            roundStarted = true
            onShadowBoxSessionStarted.invoke() // Notify that a round has started
            runRound()
        }
    }

    private fun runRound() {
        val roundDurationMs = ROUND_LENGTH_MS
        val restDurationMs = BETWEEN_ROUND_LENGTH_MS

        var timeSinceLastComboMs = 0L
        var comboDelayMs = Random.nextLong(1000, 5000)

        val roundTimer = object : CountDownTimer(roundDurationMs, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000

                onTimeTick.invoke(secondsRemaining)

                // Play sound if 10 seconds remaining
                if (secondsRemaining == 10L) {
                    //playSound("10 seconds remaining") // Implement playSound method
                }

                // Check if time since last combo announcement is close to the combo delay
                if (timeSinceLastComboMs >= comboDelayMs) {
                    // If close to the combo delay, announce the next combo
                    val combo = amateurCombos.random()
                    val comboText = getComboText(combo)

                    onNewGeneratedCombo.invoke(comboText)

                    // Reset time since last combo and randomize the combo delay again
                    timeSinceLastComboMs = 0
                    comboDelayMs = Random.nextLong(1000, 5000)
                }

                // Update time since last combo announcement
                timeSinceLastComboMs += 1000
            }

            override fun onFinish() {
                onRoundEnded.invoke()
                ++currentRound
                if (currentRound <= TOTAL_ROUNDS) {
                    // Start rest timer
                    startRestTimer()
                } else {
                    // All rounds finished
                    roundStarted = false
                    onShadowBoxSessionEnded.invoke()
                }
            }
        }

        onRoundStarted.invoke() // Notify that a round has started
        roundTimer.start()
    }

    private fun startRestTimer() {
        val restTimer = object : CountDownTimer(BETWEEN_ROUND_LENGTH_MS, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Do nothing while resting between rounds
            }

            override fun onFinish() {
                // Start the next round
                runRound()
            }
        }

        restTimer.start()
    }

    private fun getComboText(combo: Array<Int>) : String {
        // Implement combo announcement logic here
        return combo.joinToString(" ") { translation[it] ?: "Unknown" }
    }
}
