package cn.fuckhome.xiaowine.config


import android.content.SharedPreferences
import cn.fuckhome.xiaowine.utils.ConfigUtils
import de.robv.android.xposed.XSharedPreferences

class Config {
    private var config: ConfigUtils

    constructor(xSharedPreferences: XSharedPreferences?) {
        config = ConfigUtils(xSharedPreferences)
    }

    constructor(sharedPreferences: SharedPreferences) {
        config = ConfigUtils(sharedPreferences)
    }

    fun update() {
        config.update()
    }



    fun getString(key: String, def: String = ""): String {
        return config.optString(key, def)
    }

    fun getBoolean(key: String, def: Boolean = false): Boolean {
        return config.optBoolean(key, def)
    }

    fun getInt(key: String, def: Int = 0): Int {
        return config.optInt(key, def)
    }


    fun setValue(key: String, value: Any) {
        config.put(key, value)
    }


    fun clear() {
        config.clearConfig()
    }
}
