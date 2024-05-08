package com.example.firefallrush

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class StartGame : AppCompatActivity(), GameTask {
    lateinit var rootLayout : LinearLayout
    lateinit var startBtn : Button
    lateinit var mGameView :GameView
    lateinit var score : TextView
    lateinit var hScore : TextView
    private lateinit var sharedPreferences: SharedPreferences
    private var highScore: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_game)
        sharedPreferences = getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
        startBtn = findViewById(R.id.startBtn)
        rootLayout = findViewById(R.id.rootLayout)
        score = findViewById(R.id.score)
        hScore = findViewById(R.id.hScore)
        mGameView = GameView(this, this)

        highScore = sharedPreferences.getInt("high_score", 0)
        updateHighScore(highScore)

        startBtn.setOnClickListener {
            mGameView.setBackgroundResource(R.drawable.back)
            rootLayout.addView(mGameView)
            startBtn.visibility = View.GONE
            score.visibility = View.GONE
            hScore.visibility = View.GONE
        }
    }

    override fun closeGame(mScore: Int) {
        if (mScore > highScore) {
            highScore = mScore
            sharedPreferences.edit().putInt("high_score", highScore).apply()
            updateHighScore(highScore) // Update the high score display
        }

        score.text = "Score : $mScore"
        mGameView.resetGame(highScore)
        rootLayout.removeView(mGameView)
        startBtn.visibility = View.VISIBLE
        score.visibility = View.VISIBLE
        hScore.visibility = View.VISIBLE
    }

    fun updateHighScore(newHighScore: Int) {
        highScore = newHighScore
        if (highScore > 0) {
            hScore.text = "High Score: $highScore"
        } else {
            hScore.text = "High Score: N/A"
        }
    }
}