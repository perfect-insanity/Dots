package com.example.dots

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.main_toolbar))
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayShowHomeEnabled(false)
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        findViewById<ConstraintLayout>(R.id.container).apply {
            viewTreeObserver.addOnGlobalLayoutListener(
                object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        viewTreeObserver.removeOnGlobalLayoutListener(this)
                        Config.widthPixels = measuredWidth
                        Config.heightPixels = measuredHeight

                        getSharedPreferences(Labels.APP_PREFERENCES.name, Context.MODE_PRIVATE).apply {
                            getInt(Labels.WIDTH_DOTS.name, -1).let {
                                if (it != -1)
                                    Config.widthDots = it
                            }
                            getInt(Labels.HEIGHT_DOTS.name, -1).let {
                                if (it != -1)
                                    Config.heightDots = it
                            }
                        }

                        var widthDots = Config.widthDots!!
                        var heightDots = Config.heightDots!!

                        val widthDotsView =
                            findViewById<TextView>(R.id.textView_width).apply {
                                text = getString(R.string.width_text, widthDots)
                            }
                        val heightDotsView =
                            findViewById<TextView>(R.id.textView_height).apply {
                                text = getString(R.string.height_text, heightDots)
                            }

                        findViewById<SeekBar>(R.id.slider_width).apply {
                            max = Config.getDotsCount(Config.widthPixels!!)
                            progress = widthDots
                            setOnProgressChanged {
                                widthDotsView.text = getString(R.string.width_text, it)
                                widthDots = it
                            }
                        }
                        findViewById<SeekBar>(R.id.slider_height).apply {
                            max = Config.getDotsCount(Config.heightPixels!!)
                            progress = heightDots
                            setOnProgressChanged {
                                heightDotsView.text = getString(R.string.height_text, it)
                                heightDots = it
                            }
                        }

                        findViewById<Button>(R.id.desc_button).setOnClickListener {
                            startActivity(Intent(
                                this@MainActivity,
                                DescActivity::class.java
                            ))
                        }
                        findViewById<Button>(R.id.continue_button).setOnClickListener {
                            startActivity(Intent(
                                this@MainActivity,
                                GameActivity::class.java
                            ))
                        }
                        findViewById<Button>(R.id.new_game_button).setOnClickListener {
                            Config.widthDots = widthDots
                            Config.heightDots = heightDots
                            startActivity(Intent(
                                this@MainActivity,
                                GameActivity::class.java
                            ).putExtra("RESET", true))
                        }
                    }
                }
            )
        }
    }

    override fun onStop() {
        super.onStop()

        getSharedPreferences(Labels.APP_PREFERENCES.name, Context.MODE_PRIVATE)
            .edit()
            .apply {
                putInt(Labels.WIDTH_DOTS.name, Config.widthDots!!)
                putInt(Labels.HEIGHT_DOTS.name, Config.heightDots!!)
                apply()
            }
    }

    private fun SeekBar.setOnProgressChanged(action: (Int) -> Unit) {
        setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar?) { }

                override fun onStopTrackingTouch(seekBar: SeekBar?) { }

                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) = action(progress)

            }
        )
    }
}
