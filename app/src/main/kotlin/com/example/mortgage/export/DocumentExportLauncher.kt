package com.example.mortgage.export

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

object DocumentExportLauncher {
    fun createCsvIntent(fileName: String): Intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "text/csv"
        putExtra(Intent.EXTRA_TITLE, fileName)
    }

    fun createPdfIntent(fileName: String): Intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "application/pdf"
        putExtra(Intent.EXTRA_TITLE, fileName)
    }

    fun shareUri(context: Context, file: File): Intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/octet-stream"
        putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file))
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
}
