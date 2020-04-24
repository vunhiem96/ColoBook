package com.volio.alarmoclock.eventbus

import com.squareup.otto.Bus

object EventBus {
    private var sBus: Bus? = null
    val bus: Bus?
        get() {
            if (sBus == null) sBus = Bus()
            return sBus
        }
}