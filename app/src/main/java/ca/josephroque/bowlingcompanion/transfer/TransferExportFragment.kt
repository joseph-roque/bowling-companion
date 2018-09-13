package ca.josephroque.bowlingcompanion.transfer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.Android
import ca.josephroque.bowlingcompanion.utils.Analytics
import kotlinx.android.synthetic.main.dialog_transfer_export.export_status as exportStatus
import kotlinx.android.synthetic.main.dialog_transfer_export.export_next_step as exportNextStep
import kotlinx.android.synthetic.main.dialog_transfer_export.btn_cancel as cancelButton
import kotlinx.android.synthetic.main.dialog_transfer_export.btn_export as exportButton
import kotlinx.android.synthetic.main.dialog_transfer_export.progress as progressView
import kotlinx.android.synthetic.main.dialog_transfer_export.view.*
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.launch

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A fragment to export user's data.
 */
class TransferExportFragment : BaseTransferFragment() {

    companion object {
        @Suppress("unused")
        private const val TAG = "TransferExportFragment"

        fun newInstance(): TransferExportFragment {
            return TransferExportFragment()
        }
    }

    private var exportTask: Deferred<String?>? = null

    private val onClickListener = View.OnClickListener {
        when (it.id) {
            R.id.btn_export -> {
                exportUserData()
            }
            R.id.btn_cancel -> {
                exportTask?.cancel()
                exportTask = null
            }
        }
    }

    // MARK: BaseTransferFragment

    override val toolbarTitle = R.string.export
    override val isBackEnabled = exportTask == null

    // MARK: Lifecycle functions

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_transfer_export, container, false)

        view.btn_export.setOnClickListener(onClickListener)
        view.btn_cancel.setOnClickListener(onClickListener)

        return view
    }

    // MARK: Private functions

    private fun getServerConnection(): TransferServerConnection? {
        val context = this@TransferExportFragment.context ?: return null
        return TransferServerConnection.openConnection(context).apply {
            this.progressView = this@TransferExportFragment.progressView
            this.cancelButton = this@TransferExportFragment.cancelButton
        }
    }

    private fun exportFailed() {
        exportButton.visibility = View.VISIBLE
        exportStatus.visibility = View.GONE
        exportNextStep.visibility = View.GONE
    }

    private fun exportSucceeded(serverResponse: String) {
        val requestIdRegex = "requestId:(.*)".toRegex()
        val key = requestIdRegex.matchEntire(serverResponse)?.groups?.get(1)?.value
        if (key == null) {
            exportFailed()
            return
        }

        exportNextStep.visibility = View.VISIBLE
        exportStatus.apply {
            text = resources.getString(R.string.export_upload_complete, key)
            visibility = View.VISIBLE
        }
    }

    private fun exportUserData() {
        launch(Android) {
            val connection = getServerConnection() ?: return@launch
            Analytics.trackTransferExport(Analytics.Companion.EventTime.Begin)

            exportButton.visibility = View.GONE

            if (!connection.prepareConnection().await()) {
                exportFailed()
            }

            exportTask = connection.uploadUserData()
            val serverResponse = exportTask?.await()
            exportTask = null
            if (serverResponse == null) {
                exportFailed()
            } else {
                exportSucceeded(serverResponse)
            }

            Analytics.trackTransferExport(Analytics.Companion.EventTime.End)
        }
    }
}