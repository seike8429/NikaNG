package com.spiderproxyfinder.app.ui

import android.os.Bundle
import com.spiderproxyfinder.app.R
import com.spiderproxyfinder.app.service.V2RayServiceManager
import com.spiderproxyfinder.app.util.Utils

class ScSwitchActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        moveTaskToBack(true)

        setContentView(R.layout.activity_none)

        if (V2RayServiceManager.v2rayPoint.isRunning) {
            Utils.stopVService(this)
        } else {
            Utils.startVServiceFromToggle(this)
        }
        finish()
    }
}
