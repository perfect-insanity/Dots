package com.example.dots

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.dots.core.Player
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.apply {
            title = getString(R.string.menu_header)
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        Config.distance = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_MM, Config.distanceMM, resources.displayMetrics
        )

        findViewById<ConstraintLayout>(R.id.container).apply {
            onGlobalLayout {
                onGlobalLayout(measuredWidth, measuredHeight)
            }
        }
    }

    private fun onGlobalLayout(
        measuredWidth: Int,
        measuredHeight: Int
    ) {
        Config.widthPixels = measuredWidth
        Config.heightPixels = measuredHeight

        var saveExists: Boolean
        getSharedPreferences(Labels.APP_PREFERENCES, Context.MODE_PRIVATE).apply {
            saveExists = contains(Labels.GAME)

            val historyJson = getString(Labels.HISTORY, null)
            val type = object : TypeToken<ArrayList<History.GameResult>>(){}.type
            History.entries = if (historyJson != null)
                Gson().fromJson(historyJson, type)
            else
                ArrayList()

            getInt(Labels.WIDTH_DOTS, -1).let {
                if (it != -1)
                    Config.widthDots = it
            }
            getInt(Labels.HEIGHT_DOTS, -1).let {
                if (it != -1)
                    Config.heightDots = it
            }

            getString(Labels.FIRST_PLAYER_NICK, Player.FIRST.nick)?.let {
                Player.FIRST.nick = it
            }
            getString(Labels.SECOND_PLAYER_NICK, Player.SECOND.nick)?.let {
                Player.SECOND.nick = it
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

        findViewById<Button>(R.id.continue_button).setOnClickListener {
            if (saveExists)
                startActivity(Intent(
                    this@MainActivity,
                    GameActivity::class.java
                ))
            else
                Toast.makeText(
                    this,
                    getString(R.string.save_not_found),
                    Toast.LENGTH_SHORT
                ).show()
        }
        findViewById<Button>(R.id.new_game_button).setOnClickListener {
            if (saveExists)
                AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.new_game_sure))
                    setMessage(getString(R.string.save_will_be_removed))

                    setPositiveButton(getString(R.string.yes)) { dialog, id ->
                        startNewGame(widthDots, heightDots)
                    }
                    setNegativeButton(getString(R.string.no)) { dialog, id -> }

                    create()
                }.show()
            else
                startNewGame(widthDots, heightDots)
        }
    }

    private fun setNick(player: Player, nick: String) {
        player.nick = if (nick != "")
            nick
        else when (player) {
            Player.FIRST -> getString(R.string.player_first_default)
            Player.SECOND -> getString(R.string.player_second_default)
            else -> null
        }
    }

    private fun startNewGame(widthDots: Int, heightDots: Int) {
        findViewById<EditText>(R.id.editText_player_first).apply {
            setNick(Player.FIRST, text.toString())
        }
        findViewById<EditText>(R.id.editText_player_second).apply {
            setNick(Player.SECOND, text.toString())
        }

        Config.widthDots = widthDots
        Config.heightDots = heightDots

        startActivity(Intent(
            this@MainActivity,
            GameActivity::class.java
        ).putExtra(Labels.RESET, true))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_tools_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.history -> {
                startActivity(Intent(this, HistoryActivity::class.java))
                true
            }
            R.id.desc -> {
                startActivity(Intent(this, DescActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStop() {
        super.onStop()

        getSharedPreferences(Labels.APP_PREFERENCES, Context.MODE_PRIVATE)
            .edit()
            .apply {
                putString(Labels.HISTORY, Gson().toJson(History.entries))
                putInt(Labels.WIDTH_DOTS, Config.widthDots!!)
                putInt(Labels.HEIGHT_DOTS, Config.heightDots!!)
                putString(Labels.FIRST_PLAYER_NICK, Player.FIRST.nick)
                putString(Labels.SECOND_PLAYER_NICK, Player.SECOND.nick)

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

    private fun View.onGlobalLayout(action: () -> Unit) {
        viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    action()
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        )
    }

}
