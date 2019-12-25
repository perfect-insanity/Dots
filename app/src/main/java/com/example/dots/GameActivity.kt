package com.example.dots

import android.app.AlertDialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dots.core.Game
import com.example.dots.core.Player
import com.google.gson.Gson

class GameActivity : AppCompatActivity() {
    private lateinit var fieldView: FieldView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        setSupportActionBar(findViewById(R.id.game_toolbar))
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayShowHomeEnabled(false)
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        fieldView = findViewById(R.id.field)

        if (!intent.getBooleanExtra(Labels.RESET, false))
            getSharedPreferences(Labels.APP_PREFERENCES, Context.MODE_PRIVATE).apply {
                getString(Labels.GAME, null)?.let {
                    fieldView.game = Gson().fromJson(it, Game::class.java)
                }

                Player.FIRST.ownership = getInt(
                    Labels.FIRST_PLAYER_OWNERSHIP,
                    Player.FIRST.ownership
                )
                Player.SECOND.ownership = getInt(
                    Labels.SECOND_PLAYER_OWNERSHIP,
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
                fieldView.undoLastMove()
                true
            }
            R.id.clear -> {
                if (fieldView.game.moves.isNotEmpty())
                    AlertDialog.Builder(this).apply {
                        setTitle(getString(R.string.clear_field_sure))
                        setMessage(getString(R.string.irreplaceable))

                        setPositiveButton(getString(R.string.yes)) { dialog, id ->
                            fieldView.clear()
                        }
                        setNegativeButton(getString(R.string.no)) { dialog, id -> }

                        create()
                    }.show()
                else
                    Toast.makeText(
                        this,
                        getString(R.string.field_already_clear),
                        Toast.LENGTH_SHORT
                    ).show()

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStop() {
        super.onStop()
        getSharedPreferences(Labels.APP_PREFERENCES, Context.MODE_PRIVATE).edit().apply {
            val gameJson = Gson().toJson(fieldView.game)
            putString(Labels.GAME, gameJson)
            putInt(Labels.FIRST_PLAYER_OWNERSHIP, Player.FIRST.ownership)
            putInt(Labels.SECOND_PLAYER_OWNERSHIP, Player.SECOND.ownership)

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