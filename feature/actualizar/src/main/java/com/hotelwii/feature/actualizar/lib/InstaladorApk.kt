package com.hotelwii.feature.actualizar.lib

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File

/**
 * 📲 InstaladorApk — Invocador nativo del PackageInstaller de Android mediante FileProvider seguro
 */
object InstaladorApk {
    fun instalar(context: Context, apkFile: File): Result<Unit> {
        return try {
            val apkUri = getUriForFile(context, apkFile)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }

            val packageManager = context.packageManager
            val installerApps = listOf(
                "com.google.android.packageinstaller",
                "com.android.packageinstaller"
            )

            for (pkg in installerApps) {
                try {
                    packageManager.getPackageInfo(pkg, 0)
                    intent.setPackage(pkg)
                    break
                } catch (e: PackageManager.NameNotFoundException) {
                    // Continuar al siguiente instalador compatible
                }
            }

            context.startActivity(intent)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getUriForFile(context: Context, file: File): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } else {
            Uri.fromFile(file)
        }
    }
}
