package com.example.voicecommandaiapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SoundOptionsAdapter(
    private val sounds: List<String>,
    private var selectedSound: String,
    private val onSoundSelected: (String) -> Unit
) : RecyclerView.Adapter<SoundOptionsAdapter.SoundViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoundViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sound_option, parent, false)
        return SoundViewHolder(view)
    }

    override fun onBindViewHolder(holder: SoundViewHolder, position: Int) {
        val sound = sounds[position]
        holder.bind(sound, sound == selectedSound) {
            selectedSound = sound
            onSoundSelected(sound)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() = sounds.size

    class SoundViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val soundName: TextView = itemView.findViewById(R.id.sound_name)
        private val radioButton: RadioButton = itemView.findViewById(R.id.sound_radio_button)

        fun bind(sound: String, isSelected: Boolean, onClick: () -> Unit) {
            soundName.text = sound
            radioButton.isChecked = isSelected
            itemView.setOnClickListener { onClick() }
        }
    }
}
