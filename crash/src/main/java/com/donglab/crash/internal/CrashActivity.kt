package com.donglab.crash.internal

import android.content.Intent
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.core.view.updatePadding
import com.donglab.crash.R
import com.donglab.crash.databinding.ActivityCrashBinding
import com.donglab.crash.databinding.ItemCrashCodeBinding
import com.donglab.crash.databinding.ItemCrashCodeBlockBinding
import com.donglab.crash.databinding.ItemCrashErrorBinding
import com.donglab.crash.databinding.ItemCrashExceptionBinding
import com.donglab.crash.databinding.ItemCrashNormalBinding
import com.donglab.crash.databinding.ItemCrashSectionTitleBinding
import com.donglab.crash.internal.extensions.dpToPx
import com.donglab.crash.publicapi.provider.model.CrashInfoItem
import com.donglab.crash.publicapi.provider.model.CrashInfoSection
import com.donglab.crash.publicapi.provider.model.ItemType
import com.donglab.crash.publicapi.provider.model.SectionType
import com.google.android.material.divider.MaterialDivider
import com.google.android.material.R as M3R

internal class CrashActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrashBinding
    private lateinit var crashInfo: CrashInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrashBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        setupSystemBarsPadding()

        crashInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_CRASH_INFO, CrashInfo::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_CRASH_INFO)
        } ?: run {
            finish()
            return
        }

        setupToolbar()
        setupViews()
        setupButtons()
    }

    private fun setupSystemBarsPadding() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                top = systemBars.top,
                bottom = systemBars.bottom
            )
            windowInsets
        }
    }

    private fun setupToolbar() {
        // Title is set in the XML
    }

    private fun setupViews() {
        crashInfo.sections.forEachIndexed { index, section ->
            addSection(section)

            if (index < crashInfo.sections.size - 1) {
                addSectionDivider()
            }
        }
    }

    private fun addSection(section: CrashInfoSection) {
        val titleBinding = ItemCrashSectionTitleBinding.inflate(layoutInflater)
        titleBinding.tvSectionTitle.text = section.title
        binding.llContent.addView(titleBinding.root)

        section.items.forEachIndexed { index, item ->
            val itemView = createItemView(item, section.type)
            binding.llContent.addView(itemView)

            if (index < section.items.size - 1 && item.type != ItemType.CODE) {
                addItemDivider()
            }
        }
    }

    private fun createItemView(item: CrashInfoItem, sectionType: SectionType): View {
        return when (item.type) {
            ItemType.CODE -> {
                val codeBlockBinding = ItemCrashCodeBlockBinding.inflate(layoutInflater)
                codeBlockBinding.tvCodeBlock.text = item.value
                codeBlockBinding.root
            }
            ItemType.ERROR -> {
                val errorBinding = ItemCrashErrorBinding.inflate(layoutInflater)
                errorBinding.tvError.text = if (item.label.isNotEmpty()) {
                    "${item.label}: ${item.value}"
                } else {
                    item.value
                }
                errorBinding.root
            }
            ItemType.NORMAL -> {
                when (sectionType) {
                    SectionType.NORMAL -> {
                        val normalBinding = ItemCrashNormalBinding.inflate(layoutInflater)
                        normalBinding.tvLabel.text = item.label
                        normalBinding.tvValue.text = item.value
                        normalBinding.root
                    }
                    SectionType.CODE -> {
                        val codeBinding = ItemCrashCodeBinding.inflate(layoutInflater)
                        codeBinding.tvCode.text = "${item.label}: ${item.value}"
                        codeBinding.root
                    }
                    SectionType.EXCEPTION -> {
                        val exceptionBinding = ItemCrashExceptionBinding.inflate(layoutInflater)
                        exceptionBinding.tvException.text = if (item.label.isNotEmpty()) {
                            "${item.label}: ${item.value}"
                        } else {
                            item.value
                        }
                        exceptionBinding.root
                    }
                }
            }
        }
    }

    private fun addItemDivider() {
        val divider = MaterialDivider(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                1.dpToPx()
            ).apply {
                marginStart = 16.dpToPx()
                marginEnd = 16.dpToPx()
            }
            // Use theme attribute for color
            setDividerColorResource(M3R.color.material_on_surface_stroke)
        }
        binding.llContent.addView(divider)
    }

    private fun addSectionDivider() {
        val divider = MaterialDivider(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                8.dpToPx()
            )
            // Use a slightly visible color from theme, or a custom one
            setDividerColorResource(M3R.color.material_dynamic_neutral95)
        }
        binding.llContent.addView(divider)
    }

    private fun setupButtons() = with(binding) {
        btnShare.text = getString(R.string.crash_share_button)
        btnClose.text = getString(R.string.crash_close_button)

        btnShare.setOnClickListener {
            shareCrashInfo()
        }

        btnClose.setOnClickListener {
            closeApp()
        }
    }

    private fun shareCrashInfo() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Crash Report")
            putExtra(Intent.EXTRA_TEXT, crashInfo.getFormattedText())
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.crash_share_title)))
    }

    private fun closeApp() {
        finishAffinity()
        Process.killProcess(Process.myPid())
        System.exit(10)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.llContent.removeAllViews()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Block back button
    }

    companion object {
        const val EXTRA_CRASH_INFO = "extra_crash_info"
    }
}