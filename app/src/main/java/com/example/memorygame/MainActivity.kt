package com.example.memorygame

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.memorygame.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val sequence = mutableListOf<Int>()
    private var userIndex = 0
    private var round = 1
    private val scope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButtons()
        startGame()
    }

    private fun setupButtons() {
        listOf(binding.btn0, binding.btn1, binding.btn2, binding.btn3)
            .forEachIndexed { idx, view ->
                view.setOnClickListener { checkUserInput(idx) }
            }
    }

    private fun startGame() {
        sequence.clear()
        repeat(4) { addStep() }  // start at 4
        binding.tvRound.text = getString(R.string.round_prefix, round)
        playSequence()
    }

    private fun addStep() {
        sequence.add(Random.nextInt(4))
    }

    private fun playSequence() {
        userIndex = 0
        scope.launch {
            for (step in sequence) {
                highlight(step)
                delay(600)
            }
        }
    }

    private suspend fun highlight(index: Int) {
        val view = listOf(binding.btn0, binding.btn1, binding.btn2, binding.btn3)[index]
        view.setBackgroundColor(Color.WHITE)
        delay(400)
        view.setBackgroundColor(contextColor(index))
    }

    private fun contextColor(idx: Int) = Color.parseColor(
        listOf("#FF3D00", "#00C853", "#2962FF", "#FFD600")[idx]
    )

    private fun checkUserInput(idx: Int) {
        if (sequence[userIndex] == idx) {
            userIndex++
            if (userIndex == sequence.size) {
                round++
                binding.tvRound.text = getString(R.string.round_prefix, round)
                addStep()
                playSequence()
            }
        } else {
            showGameOverDialog()
        }
    }

    private fun showGameOverDialog() {
        AlertDialog.Builder(this)
            .setTitle("Oops!")
            .setMessage("Wrong square. Try again or restart the game.")
            .setPositiveButton("Try Again") { _, _ ->
                userIndex = 0
                playSequence()
            }
            .setNegativeButton("Restart") { _, _ ->
                round = 1
                startGame()
            }
            .setCancelable(false)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}