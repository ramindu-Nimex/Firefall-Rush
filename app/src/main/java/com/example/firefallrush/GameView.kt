package com.example.firefallrush

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View

class GameView(var c :Context, var gameTask: GameTask):View(c) {

    private var myPaint: Paint? = null
    private var speed = 1
    private var time = 0
    private var score = 0
    private var highScore = 0
    private var manPosition = 0
    private val fireBalls = ArrayList<HashMap<String,Any>>()
    private lateinit var sharedPreferences: SharedPreferences

    var viewWidth = 0
    var viewHeight = 0
    init {
        myPaint = Paint()
        // Initialize SharedPreferences
        sharedPreferences = c.getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
        // Load the previous high score and score
        highScore = sharedPreferences.getInt("high_score", 0)
        score = sharedPreferences.getInt("score", 0)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (time == 0) {
            score = 0
        }

        viewWidth = this.measuredWidth
        viewHeight = this.measuredHeight

        if (time % 700 < 10 +speed) {
            val map = HashMap<String, Any>()
            map["lane"] = (0..2).random()
            map["startTime"] = time
            fireBalls.add(map)

            // Update score
            score++

            // Update high score if necessary
            if (score > highScore) {
                highScore = score
                // Save the updated high score to SharedPreferences
                sharedPreferences.edit().putInt("high_score", highScore).apply()
            }

            // Save the updated score to SharedPreferences
            sharedPreferences.edit().putInt("score", score).apply()
        }

        time = time + 10 + speed
        val manWidth = viewWidth / 5
        val manHeight = manWidth + 10
        myPaint!!.style = Paint.Style.FILL
        val d = resources.getDrawable(R.drawable.boy, null)

        d.setBounds(
            manPosition * viewWidth / 3 + viewWidth / 15 + 25,
            viewHeight-2 - manHeight,
            manPosition * viewWidth / 3 + viewWidth / 15 + manWidth - 25,
            viewHeight - 2
        )
        d.draw(canvas)
        myPaint!!.color = Color.GREEN
        var highScore = 0

        for (i in fireBalls.indices) {
            try {
                val carX = fireBalls[i]["lane"] as Int * viewWidth / 3 + viewWidth / 15
                var carY = time - fireBalls[i]["startTime"] as Int
                val d2 = resources.getDrawable(R.drawable.rock, null)

                d2.setBounds(
                    carX + 25, carY - manHeight , carX + manWidth - 25 , carY
                )
                d2.draw(canvas)
                if (fireBalls[i]["lane"] as Int == manPosition) {
                    if (carY > viewHeight - 2 - manHeight && carY < viewHeight - 2) {
                        gameTask.closeGame(score)
                    }
                }

                if (carY > viewHeight + manHeight) {
                    fireBalls.removeAt(i)
                    score++
                    speed = 1 + Math.abs(score / 8)
                    if (score > highScore) {
                        highScore = score
                    }
                }
            } catch (e:Exception) {
                e.printStackTrace()
            }
        }

        myPaint!!.color = Color.WHITE
        myPaint!!.textSize = 40f
        canvas.drawText("Score : $score", 80f, 80f,myPaint!!)
        canvas.drawText("Speed : $speed", 380f, 80f,myPaint!!)
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        when(event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                val x1 = event.x
                if (x1 < viewWidth / 2) {
                    if (manPosition > 0) {
                        manPosition--
                    }
                }
                if (x1 > viewWidth / 2) {
                    if (manPosition < 2) {
                        manPosition++
                    }
                }
                invalidate()
            }
            MotionEvent.ACTION_UP -> {

            }
        }

        return true
    }

    fun resetGame(newHighScore: Int) {
        score = 0
        invalidate()
        if (newHighScore > 0) {
            // Update the high score display with the new high score
            (context as? StartGame)?.updateHighScore(newHighScore)
        }
    }
}