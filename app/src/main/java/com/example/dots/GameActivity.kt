package com.example.dots

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.dots.core.Game
import com.example.dots.core.Player
import com.google.gson.Gson

class GameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        setSupportActionBar(findViewById(R.id.game_toolbar))
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayShowHomeEnabled(false)
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        if (!intent.getBooleanExtra("RESET", false))
            getSharedPreferences(Labels.APP_PREFERENCES.name, Context.MODE_PRIVATE).apply {
                val gameJson = getString(Labels.GAME.name, null)
                if (gameJson != null)
                    findViewById<FieldView>(R.id.field).game =
                        Gson().fromJson(gameJson, Game::class.java)

                Player.FIRST.ownership = getInt(
                    Labels.FIRST_PLAYER_OWNERSHIP.name,
                    Player.FIRST.ownership
                )
                Player.SECOND.ownership = getInt(
                    Labels.SECOND_PLAYER_OWNERSHIP.name,
                    Player.SECOND.ownership
                )
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.game_tools_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.undo_last_move -> {
                findViewById<FieldView>(R.id.field).undoLastMove()
                true
            }
            R.id.clear -> {
                findViewById<FieldView>(R.id.field).clear()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStop() {
        super.onStop()
        getSharedPreferences(Labels.APP_PREFERENCES.name, Context.MODE_PRIVATE).edit().apply {
            val gameJson = Gson().toJson(findViewById<FieldView>(R.id.field).game)
            putString(Labels.GAME.name, gameJson)
            putInt(Labels.FIRST_PLAYER_OWNERSHIP.name, Player.FIRST.ownership)
            putInt(Labels.SECOND_PLAYER_OWNERSHIP.name, Player.SECOND.ownership)

            apply()
        }
    }

    fun setScore(player: Player, first: Int, second: Int) {
        findViewById<CircleView>(R.id.player_circle).player = player
        findViewById<TextView>(R.id.player_name).text = player.nick
        findViewById<TextView>(R.id.first).text = "$first"
        findViewById<TextView>(R.id.second).text = "$second"
    }
}