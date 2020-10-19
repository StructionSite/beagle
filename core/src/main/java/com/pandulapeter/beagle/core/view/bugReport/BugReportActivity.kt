package com.pandulapeter.beagle.core.view.bugReport

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.pandulapeter.beagle.BeagleCore
import com.pandulapeter.beagle.common.configuration.Text
import com.pandulapeter.beagle.core.R
import com.pandulapeter.beagle.core.list.delegates.DeviceInfoDelegate
import com.pandulapeter.beagle.core.util.LogEntry
import com.pandulapeter.beagle.core.util.NetworkLogEntry
import com.pandulapeter.beagle.core.util.extension.append
import com.pandulapeter.beagle.core.util.extension.text
import com.pandulapeter.beagle.core.util.extension.visible
import com.pandulapeter.beagle.core.view.bugReport.list.BugReportAdapter
import com.pandulapeter.beagle.core.view.gallery.MediaPreviewDialogFragment
import com.pandulapeter.beagle.utils.BundleArgumentDelegate
import com.pandulapeter.beagle.utils.consume
import com.pandulapeter.beagle.utils.extensions.colorResource
import com.pandulapeter.beagle.utils.extensions.dimension
import com.pandulapeter.beagle.utils.extensions.tintedDrawable

internal class BugReportActivity : AppCompatActivity() {

    @Suppress("UNCHECKED_CAST")
    private val viewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T = intent.getBundleExtra(ARGUMENTS)!!.let { arguments ->
                BugReportViewModel(
                    context = applicationContext,
                    shouldShowGallerySection = arguments.shouldShowGallerySection,
                    shouldShowNetworkLogsSection = arguments.shouldShowNetworkLogsSection,
                    logLabelSectionsToShow = arguments.logLabelSectionsToShow,
                    shouldShowMetadataSection = arguments.shouldShowMetadataSection,
                    buildInformation = arguments.buildInformation,
                    deviceInformation = generateDeviceInformation(),
                    descriptionTemplate = arguments.descriptionTemplate?.let(::text) ?: ""
                ) as T
            }
        }).get(BugReportViewModel::class.java)
    }
    private lateinit var sendButton: MenuItem
    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.beagle_recycler_view) }
    private val appBarLayout by lazy { findViewById<AppBarLayout>(R.id.beagle_app_bar) }
    private val onGlobalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener { recyclerView.run { post { appBarLayout.setLifted(computeVerticalScrollOffset() != 0) } } }

    override fun onCreate(savedInstanceState: Bundle?) {
        BeagleCore.implementation.appearance.themeResourceId?.let { setTheme(it) }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.beagle_activity_bug_report)
        supportActionBar?.hide()
        findViewById<Toolbar>(R.id.beagle_toolbar).apply {
            val textColor = colorResource(android.R.attr.textColorPrimary)
            setNavigationOnClickListener { supportFinishAfterTransition() }
            navigationIcon = tintedDrawable(R.drawable.beagle_ic_close, textColor)
            title = text(BeagleCore.implementation.appearance.bugReportTexts.title)
            sendButton = menu.findItem(R.id.beagle_send).also {
                it.title = text(BeagleCore.implementation.appearance.bugReportTexts.sendButtonHint)
                it.icon = tintedDrawable(R.drawable.beagle_ic_send, textColor)
            }
            setOnMenuItemClickListener(::onMenuItemClicked)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            val contentPadding by lazy { dimension(R.dimen.beagle_content_padding) }
            val bottomNavigationOverlay = findViewById<View>(R.id.beagle_bottom_navigation_overlay)
            bottomNavigationOverlay.setBackgroundColor(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) window.navigationBarColor else Color.BLACK)
            window.decorView.run {
                setOnApplyWindowInsetsListener { _, insets ->
                    onApplyWindowInsets(insets).also {
                        recyclerView.setPadding(
                            0,
                            0,
                            0,
                            it.systemWindowInsetBottom + contentPadding
                        )
                        bottomNavigationOverlay.run { layoutParams = layoutParams.apply { height = it.systemWindowInsetBottom } }
                    }
                }
                requestApplyInsets()
            }
        }
        val bugReportAdapter = BugReportAdapter(
            onMediaFileSelected = ::showMediaPreviewDialog,
            onMediaFileLongTapped = viewModel::onMediaFileLongTapped,
            onNetworkLogSelected = { id -> BeagleCore.implementation.getNetworkLogEntries().firstOrNull { it.id == id }?.let(::showNetworkLogDetailDialog) },
            onNetworkLogLongTapped = viewModel::onNetworkLogLongTapped,
            onShowMoreNetworkLogsTapped = viewModel::onShowMoreNetworkLogsTapped,
            onLogSelected = { id, label -> BeagleCore.implementation.getLogEntries(label).firstOrNull { it.id == id }?.let(::showLogDetailDialog) },
            onLogLongTapped = viewModel::onLogLongTapped,
            onShowMoreLogsTapped = viewModel::onShowMoreLogsTapped,
            onDescriptionChanged = viewModel::onDescriptionChanged,
            onMetadataItemClicked = ::showMetadataDetailDialog,
            onMetadataItemSelectionChanged = viewModel::onMetadataItemSelectionChanged
        )
        recyclerView.run {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = bugReportAdapter
        }
        viewModel.items.observe(this, bugReportAdapter::submitList)
        findViewById<ProgressBar>(R.id.beagle_progress_bar).let { progressBar ->
            viewModel.shouldShowLoadingIndicator.observe(this) { progressBar.visible = it }
        }
        viewModel.isSendButtonEnabled.observe(this) {
            sendButton.isEnabled = it
            sendButton.icon.alpha = if (it) 255 else 63
        }
    }

    override fun onResume() {
        super.onResume()
        recyclerView.viewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener)
    }

    override fun onPause() {
        super.onPause()
        recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalLayoutListener)
    }

    fun refresh() = viewModel.refresh()

    private fun generateDeviceInformation(): CharSequence {
        var text: CharSequence = ""
        //TODO: Should be configurable
        DeviceInfoDelegate.getDeviceInfo(
            shouldShowManufacturer = true,
            shouldShowModel = true,
            shouldShowResolutionsPx = true,
            shouldShowResolutionsDp = true,
            shouldShowDensity = true,
            shouldShowAndroidVersion = true
        ).also { sections ->
            val lastIndex = sections.lastIndex
            sections.forEachIndexed { index, (keyText, value) ->
                val key = text(keyText)
                text = text.append(SpannableString("$key: $value".let { if (index == lastIndex) it else "$it\n" }).apply {
                    setSpan(StyleSpan(Typeface.BOLD), 0, key.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                })
            }
        }
        return text
    }

    private fun onMenuItemClicked(menuItem: MenuItem) = when (menuItem.itemId) {
        R.id.beagle_send -> consume(viewModel::onSendButtonPressed)
        else -> false
    }

    private fun showMediaPreviewDialog(fileName: String) = MediaPreviewDialogFragment.show(supportFragmentManager, fileName)

    private fun showNetworkLogDetailDialog(entry: NetworkLogEntry) = BeagleCore.implementation.showNetworkEventDialog(
        isOutgoing = entry.isOutgoing,
        url = entry.url,
        payload = entry.payload,
        headers = entry.headers,
        duration = entry.duration,
        timestamp = entry.timestamp,
        id = entry.id
    )

    private fun showLogDetailDialog(entry: LogEntry) = BeagleCore.implementation.showDialog(
        content = entry.getFormattedContents(BeagleCore.implementation.appearance.logTimestampFormatter),
        timestamp = entry.timestamp,
        id = entry.id
    )

    private fun showMetadataDetailDialog(type: BugReportViewModel.MetadataType) = BeagleCore.implementation.showDialog(
        content = when (type) {
            BugReportViewModel.MetadataType.BUILD_INFORMATION -> viewModel.buildInformation
            BugReportViewModel.MetadataType.DEVICE_INFORMATION -> viewModel.deviceInformation
        },
        shouldShowShareButton = false
    )

    companion object {
        private const val ARGUMENTS = "arguments"
        private var Bundle.shouldShowGallerySection by BundleArgumentDelegate.Boolean("shouldShowGallerySection")
        private var Bundle.shouldShowNetworkLogsSection by BundleArgumentDelegate.Boolean("shouldShowNetworkLogsSection")
        private var Bundle.logLabelSectionsToShow by BundleArgumentDelegate.StringList("logLabelSectionsToShow")
        private var Bundle.shouldShowMetadataSection by BundleArgumentDelegate.Boolean("shouldShowMetadataSection")
        private var Bundle.buildInformation by BundleArgumentDelegate.CharSequence("buildInformation")
        private var Bundle.descriptionTemplate by BundleArgumentDelegate.Parcelable<Text>("descriptionTemplate")

        fun newIntent(
            context: Context,
            shouldShowGallerySection: Boolean,
            shouldShowNetworkLogsSection: Boolean,
            logLabelSectionsToShow: List<String?>,
            shouldShowMetadataSection: Boolean,
            buildInformation: CharSequence,
            descriptionTemplate: Text
        ) = Intent(context, BugReportActivity::class.java).putExtra(ARGUMENTS, Bundle().also {
            it.shouldShowGallerySection = shouldShowGallerySection
            it.shouldShowNetworkLogsSection = shouldShowNetworkLogsSection
            it.logLabelSectionsToShow = logLabelSectionsToShow
            it.shouldShowMetadataSection = shouldShowMetadataSection
            it.buildInformation = buildInformation
            it.descriptionTemplate = descriptionTemplate
        })
    }
}