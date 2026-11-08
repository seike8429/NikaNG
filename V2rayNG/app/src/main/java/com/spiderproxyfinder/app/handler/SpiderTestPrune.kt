package com.spiderproxyfinder.app.handler

import android.content.Context
import android.util.Log
import com.spiderproxyfinder.app.dto.EConfigType
import com.spiderproxyfinder.app.util.PluginUtil
import com.spiderproxyfinder.app.util.SpeedtestUtil
import com.spiderproxyfinder.app.util.Utils
import go.Seq
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import libv2ray.Libv2ray
import java.util.concurrent.Executors

/**
 * Spider Proxy Finder v2 — real Xray URL test + auto-remove.
 * Unlike the v1 TLS-handshake check (proven useless: its top nodes all
 * timed out in Happ), this runs the real Xray core per config
 * (Libv2ray.measureOutboundDelay, same engine as the in-app "real ping"
 * test) and DELETES configs that fail. Runs after every subscription
 * update (WorkManager) and once after first-run seeding.
 */
object SpiderTestPrune {
    private const val TAG = "SpiderTestPrune"
    private const val THREADS = 4

    private var libv2rayReady = false

    private fun ensureLibv2ray(context: Context): Boolean {
        if (libv2rayReady) return true
        return try {
            Seq.setContext(context)
            Libv2ray.initV2Env(
                Utils.userAssetPath(context),
                Utils.getDeviceIdForXUDPBaseKey()
            )
            libv2rayReady = true
            true
        } catch (e: Exception) {
            Log.d(TAG, "initV2Env failed: $e")
            false
        }
    }

    private fun testOne(context: Context, guid: String): Long {
        return try {
            val config = MmkvManager.decodeServerConfig(guid) ?: return -1L
            if (config.configType == EConfigType.HYSTERIA2) {
                PluginUtil.realPingHy2(context, config)
            } else {
                val res = V2rayConfigManager.getV2rayConfig(context, guid)
                if (!res.status) -1L
                else SpeedtestUtil.realPing(Utils.removeKeepAliveAndRouting(res.content))
            }
        } catch (e: Exception) {
            Log.d(TAG, "test failed for $guid: $e")
            -1L
        }
    }

    /** Test every config of one subscription; delete the failures. */
    fun pruneDead(context: Context, subId: String) {
        if (subId.isBlank()) return
        val guids = MmkvManager.decodeServerList().filter { guid ->
            MmkvManager.decodeServerConfig(guid)?.subscriptionId == subId
        }
        if (guids.isEmpty()) return
        if (!ensureLibv2ray(context.applicationContext)) return
        val ctx = context.applicationContext
        val dispatcher = Executors.newFixedThreadPool(THREADS).asCoroutineDispatcher()
        try {
            runBlocking {
                guids.map { guid ->
                    async(dispatcher) { guid to testOne(ctx, guid) }
                }.awaitAll().forEach { (guid, delay) ->
                    if (delay < 0) {
                        Log.d(TAG, "pruning dead config $guid (sub $subId)")
                        MmkvManager.removeServer(guid)
                    } else {
                        MmkvManager.encodeServerTestDelayMillis(guid, delay)
                    }
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "pruneDead failed: $e")
        } finally {
            dispatcher.close()
        }
    }

    /** Prune all seeded Spider subscriptions (first-run path). */
    fun pruneAllSeeded(context: Context) {
        MmkvManager.decodeSubscriptions().forEach { (subId, sub) ->
            if (DefaultSubscriptionManager.FEEDS.any { it.second == sub.url }) {
                pruneDead(context, subId)
            }
        }
    }
}
