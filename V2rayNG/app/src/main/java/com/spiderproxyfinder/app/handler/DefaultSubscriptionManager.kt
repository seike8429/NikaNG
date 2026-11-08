package com.spiderproxyfinder.app.handler

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.multiprocess.RemoteWorkManager
import com.spiderproxyfinder.app.AppConfig
import com.spiderproxyfinder.app.dto.SubscriptionItem
import com.spiderproxyfinder.app.service.SubscriptionUpdater
import java.util.concurrent.TimeUnit

/**
 * Spider Proxy Finder v2 — first-run seeding of the free-node feeds.
 * The 4 feeds are added as auto-updating subscriptions; the 3 GitHub
 * feeds are Reality-only (filtered at import, see AngConfigManager),
 * CyberShield carries all protocols. Auto-update runs every 3 hours
 * (WorkManager) and each update is followed by a real Xray URL test
 * that prunes dead configs (see SpiderTestPrune).
 */
object DefaultSubscriptionManager {
    private const val PREF_DONE = "spider_default_subs_done"
    const val UPDATE_INTERVAL_MINUTES = 180L

    val FEEDS = listOf(
        "Spider Pawdroid" to "https://raw.githubusercontent.com/Pawdroid/Free-servers/main/sub",
        "Spider getpower668" to "https://raw.githubusercontent.com/getpower668/vpn-nodes/main/free-nodes-subscription.b64",
        "Spider nodesradar" to "https://radarhub.top/v2ray.txt",
        "Spider CyberShield" to "https://gist.githubusercontent.com/cybershieldvpn/d7981da88de440c4ef8ee9271b32eb6f/raw/cybershieldvpn"
    )

    fun ensureDefaultSubscriptions(context: Context) {
        if (MmkvManager.decodeSettingsBool(PREF_DONE)) return
        if (MmkvManager.decodeSubscriptions().isNotEmpty()) {
            MmkvManager.encodeSettings(PREF_DONE, true)
            return
        }
        FEEDS.forEach { (remarks, url) ->
            val subItem = SubscriptionItem().apply {
                this.remarks = remarks
                this.url = url
                enabled = true
                autoUpdate = true
            }
            MmkvManager.encodeSubscription("", subItem)
        }
        MmkvManager.encodeSettings(AppConfig.SUBSCRIPTION_AUTO_UPDATE, true)
        MmkvManager.encodeSettings(
            AppConfig.SUBSCRIPTION_AUTO_UPDATE_INTERVAL,
            UPDATE_INTERVAL_MINUTES.toString()
        )
        val rw = RemoteWorkManager.getInstance(context.applicationContext)
        rw.enqueueUniquePeriodicWork(
            AppConfig.SUBSCRIPTION_UPDATE_TASK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            PeriodicWorkRequest.Builder(
                SubscriptionUpdater.UpdateTask::class.java,
                UPDATE_INTERVAL_MINUTES, TimeUnit.MINUTES
            ).build()
        )
        MmkvManager.encodeSettings(PREF_DONE, true)
        // First fetch + real test + prune in background, so the list is
        // live (and dead-free) on first open instead of after 3 hours.
        Thread {
            try {
                AngConfigManager.updateConfigViaSubAll()
                SpiderTestPrune.pruneAllSeeded(context.applicationContext)
            } catch (_: Exception) {
            }
        }.start()
    }
}
