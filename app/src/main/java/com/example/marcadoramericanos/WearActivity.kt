package com.example.marcadoramericanos

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable

class WearActivity : AppCompatActivity(), DataClient.OnDataChangedListener {

    private var scoreA = 0
    private var scoreB = 0
    private var gamesA = 0
    private var gamesB = 0

    private lateinit var tvScoreA: TextView
    private lateinit var tvScoreB: TextView
    private lateinit var tvGamesA: TextView
    private lateinit var tvGamesB: TextView
    private lateinit var layoutStart: LinearLayout
    private lateinit var layoutMainContent: LinearLayout
    private lateinit var btnStart: Button
    private lateinit var layoutTeamA: FrameLayout
    private lateinit var layoutTeamB: FrameLayout

    companion object {
        private const val PATH_SCORE = "/marcador_update"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wear)

        tvScoreA = findViewById(R.id.tvScoreA)
        tvScoreB = findViewById(R.id.tvScoreB)
        tvGamesA = findViewById(R.id.tvGamesA)
        tvGamesB = findViewById(R.id.tvGamesB)
        layoutStart = findViewById(R.id.layoutStart)
        layoutMainContent = findViewById(R.id.layoutWearMainContent)
        btnStart = findViewById(R.id.btnStart)
        layoutTeamA = findViewById(R.id.layoutTeamA)
        layoutTeamB = findViewById(R.id.layoutTeamB)

        btnStart.setOnClickListener {
            layoutStart.visibility = View.GONE
            layoutMainContent.visibility = View.VISIBLE
        }

        layoutTeamA.setOnClickListener {
            addPointTeamA()
        }

        layoutTeamB.setOnClickListener {
            addPointTeamB()
        }

        updateUI()
    }

    override fun onResume() {
        super.onResume()
        Wearable.getDataClient(this).addListener(this)
    }

    override fun onPause() {
        super.onPause()
        Wearable.getDataClient(this).removeListener(this)
    }

    private fun addPointTeamA() {
        scoreA += 15
        if (scoreA == 45) scoreA = 40
        if (scoreA > 40) {
            scoreA = 0
            scoreB = 0
            gamesA++
        }
        syncData()
        updateUI()
    }

    private fun addPointTeamB() {
        scoreB += 15
        if (scoreB == 45) scoreB = 40
        if (scoreB > 40) {
            scoreA = 0
            scoreB = 0
            gamesB++
        }
        syncData()
        updateUI()
    }

    private fun updateUI() {
        tvScoreA.text = scoreA.toString()
        tvScoreB.text = scoreB.toString()
        tvGamesA.text = "JUEGOS: $gamesA"
        tvGamesB.text = "JUEGOS: $gamesB"
    }

    private fun syncData() {
        val putDataReq = PutDataMapRequest.create(PATH_SCORE).run {
            dataMap.putInt("SCORE_A", scoreA)
            dataMap.putInt("SCORE_B", scoreB)
            dataMap.putInt("GAMES_A", gamesA)
            dataMap.putInt("GAMES_B", gamesB)
            dataMap.putLong("TIMESTAMP", System.currentTimeMillis())
            asPutDataRequest()
        }
        Wearable.getDataClient(this).putDataItem(putDataReq)
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        for (event in dataEvents) {
            if (event.dataItem.uri.path == PATH_SCORE) {
                val dataMapItem = DataMapItem.fromDataItem(event.dataItem)
                scoreA = dataMapItem.dataMap.getInt("SCORE_A")
                scoreB = dataMapItem.dataMap.getInt("SCORE_B")
                gamesA = dataMapItem.dataMap.getInt("GAMES_A")
                gamesB = dataMapItem.dataMap.getInt("GAMES_B")
                runOnUiThread {
                    updateUI()
                }
            }
        }
    }
}
