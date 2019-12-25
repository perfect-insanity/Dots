package com.example.dots

import android.app.AlertDialog
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dots.core.Player

class HistoryActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView

    object Adapter : RecyclerView.Adapter<Adapter.ViewHolder>() {
        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflater = LayoutInflater.from(parent.context)

            return ViewHolder(inflater.inflate(R.layout.history_item, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.itemView.apply {
                val entry = History.entries[position]
                findViewById<TextView>(R.id.time).apply {
                    text = entry.time
                }
                findViewById<TextView>(R.id.players).apply {
                    val firstPlayerSpannable = SpannableString(entry.firstPlayer).apply {
                        setSpan(
                            ForegroundColorSpan(Player.FIRST.color),
                            0, entry.firstPlayer.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                    val secondPlayerSpannable = SpannableString(entry.secondPlayer).apply {
                        setSpan(
                            ForegroundColorSpan(Player.SECOND.color),
                            0, entry.secondPlayer.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                    setText(
                        TextUtils.concat(
                            firstPlayerSpannable,
                            context.getString(R.string.players_sep),
                            secondPlayerSpannable
                        ),
                        TextView.BufferType.SPANNABLE
                    )
                }
                findViewById<TextView>(R.id.score).apply {
                    text = entry.score
                }
            }
        }

        override fun getItemCount() = History.entries.size
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        supportActionBar?.apply {
            title = getString(R.string.history)
        }

        recyclerView = findViewById<RecyclerView>(R.id.recycler_view).apply {
            adapter = Adapter
            layoutManager = LinearLayoutManager(this@HistoryActivity)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.history_tools_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.clear_history -> {
                if (History.entries.isNotEmpty())
                    AlertDialog.Builder(this).apply {
                        setTitle(getString(R.string.clear_history_sure))
                        setMessage(getString(R.string.irreplaceable))

                        setPositiveButton(getString(R.string.yes)) { dialog, id ->
                            History.entries.clear()
                            recyclerView.adapter!!.notifyDataSetChanged()
                        }
                        setNegativeButton(getString(R.string.no)) { dialog, id -> }

                        create()
                    }.show()
                else
                    Toast.makeText(
                        this,
                        getString(R.string.history_already_clear),
                        Toast.LENGTH_SHORT
                    ).show()

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}