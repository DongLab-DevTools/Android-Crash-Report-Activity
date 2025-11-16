package com.donglab.crash.internal

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
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

internal class CrashActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrashBinding
    private lateinit var crashInfo: CrashInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrashBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        setupStatusBarAppearance()
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

    private fun setupStatusBarAppearance() {
        // Status bar 아이콘을 어둡게 설정 (light status bar)
        WindowCompat.getInsetsController(window, binding.root)?.apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
    }

    private fun setupSystemBarsPadding() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            // 루트 레이아웃에 status bar와 navigation bar 높이만큼 padding 추가
            view.updatePadding(
                top = systemBars.top,
                bottom = systemBars.bottom
            )

            windowInsets
        }
    }

    private fun setupToolbar() {
        binding.tvTitle.text = getString(R.string.crash_app_bar_title)
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
        // 섹션 타이틀
        val titleBinding = ItemCrashSectionTitleBinding.inflate(layoutInflater)
        titleBinding.tvSectionTitle.text = section.title
        binding.llContent.addView(titleBinding.root)

        // 섹션 아이템들
        section.items.forEachIndexed { index, item ->
            val itemView = createItemView(item, section.type)
            binding.llContent.addView(itemView)

            // 아이템 사이에 구분선 (CODE 타입 아이템은 제외)
            if (index < section.items.size - 1 && item.type != ItemType.CODE) {
                addItemDivider()
            }
        }
    }

    private fun createItemView(item: CrashInfoItem, sectionType: SectionType): View {
        return when (item.type) {
            ItemType.CODE -> {
                // 코드 블록 레이아웃
                val codeBlockBinding = ItemCrashCodeBlockBinding.inflate(layoutInflater)
                codeBlockBinding.tvCodeBlock.text = item.value
                codeBlockBinding.root
            }

            ItemType.ERROR -> {
                // 에러 레이아웃
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
                        // 일반 레이아웃: 라벨-값 수평 배치
                        val normalBinding = ItemCrashNormalBinding.inflate(layoutInflater)
                        normalBinding.tvLabel.text = item.label
                        normalBinding.tvValue.text = item.value
                        normalBinding.root
                    }

                    SectionType.CODE -> {
                        // 코드 섹션 레이아웃: monospace
                        val codeBinding = ItemCrashCodeBinding.inflate(layoutInflater)
                        codeBinding.tvCode.text = "${item.label}: ${item.value}"
                        codeBinding.root
                    }

                    SectionType.EXCEPTION -> {
                        // 예외 섹션 레이아웃
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
            dividerColor = ContextCompat.getColor(context, R.color.c_ececec)
        }
        binding.llContent.addView(divider)
    }

    private fun addSectionDivider() {
        val divider = MaterialDivider(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                8.dpToPx()
            )
            dividerColor = ContextCompat.getColor(context, R.color.c_f5f5f5)
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
        // 메모리 누수 방지: View 바인딩 정리
        // binding.llContent의 모든 자식 뷰 제거
        binding.llContent.removeAllViews()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // 뒤로가기 막기 - 반드시 확인 버튼을 누르게 함
    }

    companion object {
        const val EXTRA_CRASH_INFO = "extra_crash_info"
    }
}
