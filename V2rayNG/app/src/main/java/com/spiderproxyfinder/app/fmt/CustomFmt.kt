package com.spiderproxyfinder.app.fmt

import com.spiderproxyfinder.app.dto.EConfigType
import com.spiderproxyfinder.app.dto.ProfileItem
import com.spiderproxyfinder.app.dto.V2rayConfig
import com.spiderproxyfinder.app.util.JsonUtil

object CustomFmt : FmtBase() {
    fun parse(str: String): ProfileItem? {
        val config = ProfileItem.create(EConfigType.CUSTOM)

        val fullConfig = JsonUtil.fromJson(str, V2rayConfig::class.java)
        val outbound = fullConfig.getProxyOutbound()

        config.remarks = fullConfig?.remarks ?: System.currentTimeMillis().toString()
        config.server = outbound?.getServerAddress()
        config.serverPort = outbound?.getServerPort().toString()

        return config
    }
}