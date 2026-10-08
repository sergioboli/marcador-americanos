package com.example.marcadoramericanos

import android.os.Bundle
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class WearActivity : AppCompatActivity() {

    private var scoreA = 0
    private var scoreB = 0
    private var gamesA = 0
    private var gamesB = 0

    private val pointsSequence = arrayOf("0", "15", "30", "40")
    private var timer: CountDownTimer? = null
    private var isMatchRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wear)

        val layoutStart = findViewById<View>(R.id.layoutStart)
        val layoutMainContent = findViewById<View>(R.id.layoutWearMainContent)
        val btnStart = findViewById<Button>(R.id.btnStart)

        val layoutTeamA = findViewById<View>(R.id.layoutTeamA)
        val layoutTeamB = findViewById<View>(R.id.layoutTeamB)

        btnStart?.setOnClickListener {
            layoutStart?.visibility = View.GONE
            layoutMainContent?.visibility = View.VISIBLE
            startMatch()
        }

        layoutTeamA?.setOnClickListener { if (isMatchRunning) addPoint(true) }
        layoutTeamB?.setOnClickListener { if (isMatchRunning) addPoint(false) }

        layoutTeamA?.setOnLongClickListener {
            if (isMatchRunning) subtractPoint(true)
            true
        }
        layoutTeamB?.setOnLongClickListener {
            if (isMatchRunning) subtractPoint(false)
            true
        }
    }

    private fun startMatch() {
        scoreA = 0
        scoreB = 0
        gamesA = 0
        gamesB = 0
        isMatchRunning = true
        updateUI()

        // Temporizador de 17 minutos (17 * 60 * 1000 ms)
        timer?.cancel()
        timer = object : CountDownTimer(17 * 60 * 1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                val tvTimer = findViewById<TextView>(R.id.tvTimer)
                tvTimer?.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                isMatchRunning = false
                val tvTimer = findViewById<TextView>(R.id.tvTimer)
                tvTimer?.text = "FIN"
                vibrateFinish()
            }
        }.start()
    }

    private fun addPoint(isTeamA: Boolean) {
        if (isTeamA) scoreA++ else scoreB++
        checkGameWinner()
        updateUI()
    }

    private fun subtractPoint(isTeamA: Boolean) {
        if (isTeamA) {
            if (scoreA > 0) scoreA--
        } else {
            if (scoreB > 0) scoreB--
        }
        updateUI()
    }

    private fun checkGameWinner() {
        // Con Punto de Oro: el primero que llegue a 4 puntos gana el juego
        if (scoreA >= 4) {
            winGame(true)
        } else if (scoreB >= 4) {
            winGame(false)
        }
    }

    private fun winGame(isTeamA: Boolean) {
        scoreA = 0
        scoreB = 0
        if (isTeamA) gamesA++ else gamesB++
    }

    private fun updateUI() {
        val tvScoreA = findViewById<TextView>(R.id.tvScoreA)
        val tvScoreB = findViewById<TextView>(R.id.tvScoreB)
        val tvGamesA = findViewById<TextView>(R.id.tvGamesA)
        val tvGamesB = findViewById<TextView>(R.id.tvGamesB)

        tvScoreA?.text = formatScore(scoreA)
        tvScoreB?.text = formatScore(scoreB)

        tvGamesA?.text = "JUEGOS: $gamesA"
        tvGamesB?.text = "JUEGOS: $gamesB"
    }

    private fun formatScore(score: Int): String {
        return pointsSequence.getOrElse(score) { "0" }
    }

    private fun vibrateFinish() {
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator?
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(1000)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}
