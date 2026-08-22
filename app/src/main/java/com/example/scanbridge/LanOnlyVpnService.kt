package com.example.scanbridge

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.Build

/**
 * «حالت داروخانه» (فقط شبکه محلی):
 * یک VPN محلی بدون هیچ فورواردری می‌سازد. همه‌ی بازه‌های عمومی IPv4/IPv6 به تونل هدایت
 * می‌شوند و چون هیچ چیزی آن‌ها را فوروارد نمی‌کند، عملاً بلعیده می‌شوند = اینترنت کل گوشی
 * (مرورگر، تلگرام و همه‌ی برنامه‌ها) قطع می‌شود. بازه‌های خصوصی شبکه (LAN مانند
 * 192.168.x.x و 10.x.x.x و 172.16.x.x) عمداً claim نمی‌شوند و از اینترفیس واقعی وای‌فای
 * عبور می‌کنند؛ بنابراین ارتباط با سیستمِ ScanBridge دست‌نخورده باقی می‌ماند.
 * بدون روت کار می‌کند و با خاموش‌شدن سرویس، اینترنت فوراً برمی‌گردد.
 */
class LanOnlyVpnService : VpnService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopVpn()
            return START_NOT_STICKY
        }
        startForeground(NOTIFICATION_ID, buildNotification())
        if (!establish()) {
            stopVpn()
        }
        return START_STICKY
    }

    private fun establish(): Boolean {
        val builder = Builder()
            .setSession("ScanBridge — Pharmacy Mode")
            .setMtu(MTU)
            // آدرس‌های ساختگی داخل تونل — به هیچ مقصد واقعی مسیریابی نمی‌شوند
            .addAddress("10.231.231.231", 32)
            .addAddress("fd9f:bcf:12e4::1", 128)

        // بازه‌های «عمومی» IPv4: هر چیزی که اینترنت واقعی باشد به تونل می‌رود و می‌میرد.
        // بازه‌های خصوصی (0/8، 10/8، 100.64/10، 127/8، 169.254/16، 172.16/12، 192.168/16،
        // 198.18/15، 224/4، 240/4) عمداً در این لیست نیستند تا شبکه‌ی محلی زنده بماند.
        for ((addr, prefix) in PUBLIC_IPV4_ROUTES) {
            builder.addRoute(addr, prefix)
        }

        // IPv6 جهانی هم بلعیده شود تا اینترنت از مسیر IPv6 دور زده نشود
        builder.addRoute("2000::", 3)

        return builder.establish() != null
    }

    private fun stopVpn() {
        if (Build.VERSION.SDK_INT >= 24) stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun buildNotification(): Notification {
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "حالت داروخانه",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "اینترنت قطع و فقط شبکه محلی فعال است"
            }
            nm.createNotificationChannel(channel)
        }

        val pi = PendingIntent.getActivity(
            this,
            0,
            packageManager.getLaunchIntentForPackage(packageName),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = if (Build.VERSION.SDK_INT >= 26) {
            Notification.Builder(this, CHANNEL_ID)
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this)
        }

        return builder
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setContentTitle("حالت داروخانه فعال است")
            .setContentText("اینترنت قطع شد — فقط شبکه محلی (ScanBridge) در دسترس است")
            .setContentIntent(pi)
            .setOngoing(true)
            .build()
    }

    override fun onRevoke() {
        // کاربر از تنظیماتِ سریع اندروید، VPN را خاموش کرد
        setRunning(this, false)
        super.onRevoke()
    }

    override fun onDestroy() {
        setRunning(this, false)
        super.onDestroy()
    }

    companion object {
        private const val ACTION_STOP = "com.example.scanbridge.LAN_ONLY_STOP"
        private const val CHANNEL_ID = "lan_only_vpn"
        private const val NOTIFICATION_ID = 9021
        private const val MTU = 1500
        private const val PREFS = "ScanBridgePrefs"
        private const val KEY_RUNNING = "lan_only_running"

        // پوشش کامل «همه‌ی IPv4 به‌جز بازه‌های خصوصی»
        private val PUBLIC_IPV4_ROUTES = listOf(
            "0.0.0.0" to 5, "8.0.0.0" to 7, "11.0.0.0" to 8, "12.0.0.0" to 6,
            "16.0.0.0" to 4, "32.0.0.0" to 3, "64.0.0.0" to 2, "128.0.0.0" to 3,
            "160.0.0.0" to 5, "168.0.0.0" to 6, "172.0.0.0" to 12, "172.32.0.0" to 11,
            "172.64.0.0" to 10, "172.128.0.0" to 9, "173.0.0.0" to 8, "174.0.0.0" to 7,
            "176.0.0.0" to 4, "192.0.0.0" to 9, "192.128.0.0" to 11, "192.160.0.0" to 13,
            "192.169.0.0" to 16, "192.170.0.0" to 15, "192.172.0.0" to 14, "192.176.0.0" to 12,
            "192.192.0.0" to 10, "192.224.0.0" to 11, "193.0.0.0" to 8, "194.0.0.0" to 7,
            "196.0.0.0" to 6, "200.0.0.0" to 5, "208.0.0.0" to 4
        )

        fun isRunning(context: Context): Boolean =
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getBoolean(KEY_RUNNING, false)

        private fun setRunning(context: Context, running: Boolean) {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putBoolean(KEY_RUNNING, running).apply()
        }

        fun start(context: Context) {
            val intent = Intent(context, LanOnlyVpnService::class.java)
            if (Build.VERSION.SDK_INT >= 26) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
            setRunning(context, true)
        }

        fun stop(context: Context) {
            val intent = Intent(context, LanOnlyVpnService::class.java).setAction(ACTION_STOP)
            try {
                context.startService(intent)
            } catch (e: Exception) {
                // سرویس در حال اجرا نیست — اشکالی ندارد
            }
            setRunning(context, false)
        }
    }
}
