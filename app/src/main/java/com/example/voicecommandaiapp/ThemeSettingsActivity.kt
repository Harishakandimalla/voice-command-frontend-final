package com.example.voicecommandaiapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView

class ThemeSettingsActivity : AppCompatActivity() {

    private lateinit var lightModeCard: MaterialCardView
    private lateinit var darkModeCard: MaterialCardView
    private lateinit var autoModeCard: MaterialCardView
    private lateinit var lightModeCheck: ImageView
    private lateinit var darkModeCheck: ImageView
    private lateinit var autoModeCheck: ImageView
    private lateinit var colorSwatchesContainer: LinearLayout
    private lateinit var themeManager: ThemeManager

    private val colors = listOf(
        "Purple" to R.color.accent_purple,
        "Blue" to R.color.accent_blue,
        "Green" to R.color.accent_green,
        "Pink" to R.color.accent_pink,
        "Orange" to R.color.accent_orange
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        themeManager = ThemeManager(this)
        themeManager.applyTheme()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theme_settings)

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        lightModeCard = findViewById(R.id.card_light_mode)
        darkModeCard = findViewById(R.id.card_dark_mode)
        autoModeCard = findViewById(R.id.card_auto_mode)
        lightModeCheck = findViewById(R.id.check_light_mode)
        darkModeCheck = findViewById(R.id.check_dark_mode)
        autoModeCheck = findViewById(R.id.check_auto_mode)
        colorSwatchesContainer = findViewById(R.id.color_swatches_container)

        setupThemeSelection()
        setupAccentColorSelection()
    }

    private fun setupThemeSelection() {
        updateThemeChecks(themeManager.getThemeMode())

        lightModeCard.setOnClickListener {
            themeManager.setThemeMode(AppCompatDelegate.MODE_NIGHT_NO)
            recreateActivity()
        }

        darkModeCard.setOnClickListener {
            themeManager.setThemeMode(AppCompatDelegate.MODE_NIGHT_YES)
            recreateActivity()
        }

        autoModeCard.setOnClickListener {
            themeManager.setThemeMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            recreateActivity()
        }
    }

    private fun setupAccentColorSelection() {
        val selectedColor = themeManager.getAccentColor()
        val inflater = LayoutInflater.from(this)

        for ((colorName, colorRes) in colors) {
            val swatch = inflater.inflate(R.layout.item_color_swatch, colorSwatchesContainer, false)
            val colorView = swatch.findViewById<View>(R.id.color_view)
            val checkMark = swatch.findViewById<ImageView>(R.id.check_mark)

            colorView.background.setTint(ContextCompat.getColor(this, colorRes))

            if (colorName == selectedColor) {
                checkMark.visibility = View.VISIBLE
            } else {
                checkMark.visibility = View.GONE
            }

            swatch.setOnClickListener {
                themeManager.setAccentColor(colorName)
                recreateActivity()
            }
            colorSwatchesContainer.addView(swatch)
        }
    }

    private fun updateThemeChecks(mode: Int) {
        lightModeCheck.visibility = if (mode == AppCompatDelegate.MODE_NIGHT_NO) View.VISIBLE else View.GONE
        darkModeCheck.visibility = if (mode == AppCompatDelegate.MODE_NIGHT_YES) View.VISIBLE else View.GONE
        autoModeCheck.visibility = if (mode == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) View.VISIBLE else View.GONE
    }

    private fun recreateActivity() {
        // A simple way to apply the theme without a full app restart.
        val intent = Intent(this, ThemeSettingsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        finish()
        startActivity(intent)
    }
}
