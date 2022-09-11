package cn.fuckhome.xiaowine.config


import android.content.SharedPreferences
import android.view.Gravity
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



    fun setColor(str: String) {
        config.put("Color", str)
    }

    fun getColor(): String {
        return config.optString("Color", "")
    }
    fun setBgColor(str: String) {
        config.put("BgColor", str)
    }

    fun getBgColor(): String {
        return config.optString("BgColor", "#00000000")
    }
    fun setGravity(i: Int) {
        config.put("Gravity", i)
    }
    fun getGravity(): Int {
        return config.optInt("Gravity", Gravity.START)
    }
    fun setUnit(b: Boolean) {
        config.put("Unit", b)
    }
    fun getUnit(): Boolean {
        return config.optBoolean("Unit", true)
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
