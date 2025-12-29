package com.cookingtimer.app

import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class TimerAdapter(
    private val onTimerClick: (CookingTimer) -> Unit,
    private val onTimerLongClick: (CookingTimer) -> Unit
) : RecyclerView.Adapter<TimerAdapter.TimerViewHolder>() {

    private val timers = mutableListOf<CookingTimer>()

    fun updateTimers(newTimers: List<CookingTimer>) {
        timers.clear()
        timers.addAll(newTimers)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_timer, parent, false)
        return TimerViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimerViewHolder, position: Int) {
        holder.bind(timers[position])
    }

    override fun getItemCount() = timers.size

    inner class TimerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameText: TextView = itemView.findViewById(R.id.timerName)
        private val timeText: TextView = itemView.findViewById(R.id.timerTime)
        private val notesText: TextView = itemView.findViewById(R.id.timerNotes)
        private val statusText: TextView = itemView.findViewById(R.id.timerStatus)
        private val imageView: ImageView = itemView.findViewById(R.id.timerImage)

        fun bind(timer: CookingTimer) {
            nameText.text = timer.name
            timeText.text = formatTime(timer.remainingMillis)
            notesText.text = timer.notes
            notesText.visibility = if (timer.notes.isNotEmpty()) View.VISIBLE else View.GONE

            when {
                timer.isRunning -> {
                    statusText.text = "Running"
                    statusText.setTextColor(Color.parseColor("#4CAF50"))
                    statusText.visibility = View.VISIBLE
                }
                timer.isPaused -> {
                    statusText.text = "Paused"
                    statusText.setTextColor(Color.parseColor("#FF9800"))
                    statusText.visibility = View.VISIBLE
                }
                timer.remainingMillis == 0L -> {
                    statusText.text = "Completed"
                    statusText.setTextColor(Color.parseColor("#F44336"))
                    statusText.visibility = View.VISIBLE
                }
                else -> {
                    statusText.visibility = View.GONE
                }
            }

            timer.imagePath?.let { path ->
                val file = File(path)
                if (file.exists()) {
                    val bitmap = BitmapFactory.decodeFile(path)
                    imageView.setImageBitmap(bitmap)
                    imageView.visibility = View.VISIBLE
                } else {
                    imageView.visibility = View.GONE
                }
            } ?: run {
                imageView.visibility = View.GONE
            }

            itemView.setOnClickListener {
                onTimerClick(timer)
            }

            itemView.setOnLongClickListener {
                onTimerLongClick(timer)
                true
            }
        }

        private fun formatTime(millis: Long): String {
            val totalSeconds = millis / 1000
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60
            return String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }
    }
}
