package com.cookingtimer.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity(), TimerManager.TimerUpdateListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: View
    private lateinit var adapter: TimerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.timersRecyclerView)
        emptyView = findViewById(R.id.emptyView)
        val fab = findViewById<FloatingActionButton>(R.id.fabAddTimer)

        adapter = TimerAdapter(
            onTimerClick = { timer ->
                val intent = Intent(this, TimerDisplayActivity::class.java)
                intent.putExtra("TIMER_ID", timer.id)
                startActivity(intent)
            },
            onTimerLongClick = { timer ->
                TimerManager.removeTimer(timer.id)
                updateUI()
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fab.setOnClickListener {
            val intent = Intent(this, AddTimerActivity::class.java)
            startActivity(intent)
        }

        TimerManager.addListener(this)
        updateUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        TimerManager.removeListener(this)
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    override fun onTimerUpdated(timer: CookingTimer) {
        runOnUiThread {
            updateUI()
        }
    }

    override fun onTimerCompleted(timer: CookingTimer) {
        runOnUiThread {
            updateUI()
        }
    }

    private fun updateUI() {
        val timers = TimerManager.getAllTimers()
        adapter.updateTimers(timers)
        
        if (timers.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
        }
    }
}
