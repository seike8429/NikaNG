package com.spiderproxyfinder.app.fmt

import com.spiderproxyfinder.app.AppConfig
import com.spiderproxyfinder.app.dto.EConfigType
import com.spiderproxyfinder.app.dto.ProfileItem
import com.spiderproxyfinder.app.dto.V2rayConfig.OutboundBean
import com.spiderproxyfinder.app.extension.idnHost
import com.spiderproxyfinder.app.handler.MmkvManager
import com.spiderproxyfinder.app.util.JsonUtil
import com.spiderproxyfinder.app.util.Utils
import java.net.URI

object VlessFmt : FmtBase() {

    fun parse(str: String): ProfileItem? {
        var allowInsecure = MmkvManager.decodeSettingsBool(AppConfig.PREF_ALLOW_INSECURE, false)
        val config = ProfileItem.create(EConfigType.VLESS)

        val uri = URI(Utils.fixIllegalUrl(str))
        if (uri.rawQuery.isNullOrEmpty()) return null
        val queryParam = getQueryParam(uri)

        config.remarks = Utils.urlDecode(uri.fragment.orEmpty())
        config.server = uri.idnHost
        config.serverPort = uri.port.toString()
        config.password = uri.userInfo
        config.method = queryParam["encryption"] ?: "none"

        getItemFormQuery(config, queryParam, allowInsecure)

        return config
    }

    fun toUri(config: ProfileItem): String {
        val dicQuery = getQueryDic(config)
        dicQuery["encryption"] = config.method ?: "none"

        return toUri(config, config.password, dicQuery)
    }


    fun toOutbound(profileItem: ProfileItem): OutboundBean? {
        val outboundBean = OutboundBean.create(EConfigType.VLESS)

        outboundBean?.settings?.vnext?.first()?.let { vnext ->
            vnext.address = profileItem.server.orEmpty()
            vnext.port = profileItem.serverPort.orEmpty().toInt()
            vnext.users[0].id = profileItem.password.orEmpty()
            vnext.users[0].encryption = profileItem.method
            vnext.users[0].flow = profileItem.flow
        }

        val sni = outboundBean?.streamSettings?.populateTransportSettings(
            profileItem.network.orEmpty(),
            profileItem.headerType,
            profileItem.host,
            profileItem.path,
            profileItem.seed,
            profileItem.quicSecurity,
            profileItem.quicKey,
            profileItem.mode,
            profileItem.serviceName,
            profileItem.authority,
        )
        outboundBean?.streamSettings?.xhttpSettings?.mode = profileItem.xhttpMode
        outboundBean?.streamSettings?.xhttpSettings?.extra = JsonUtil.parseString(profileItem.xhttpExtra)

        outboundBean?.streamSettings?.populateTlsSettings(
            profileItem.security.orEmpty(),
            profileItem.insecure == true,
            if (profileItem.sni.isNullOrEmpty()) sni else profileItem.sni,
            profileItem.fingerPrint,
            profileItem.alpn,
            profileItem.publicKey,
            profileItem.shortId,
            profileItem.spiderX,
        )

        return outboundBean
    }


}