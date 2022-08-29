package cn.fuckhome.xiaowine.hook.module


import android.annotation.SuppressLint
import android.util.Log
import cn.fuckhome.xiaowine.hook.BaseHook
import cn.fuckhome.xiaowine.utils.Utils.XConfig
import cn.fuckhome.xiaowine.utils.Utils.catchNoClass
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookBefore


@SuppressLint("StaticFieldLeak")
object Main : BaseHook() {
    @SuppressLint("SetTextI18n")
    override fun init() {
        if (XConfig.getBoolean("Pad")) {
            catchNoClass {
                findMethod("com.miui.home.launcher.common.Utilities") { name == "isPadDevice" }.hookBefore {
                    Log.i("LSPosed", "isPadDevice")
                    it.result = true
                }
            }
        }
        if (XConfig.getBoolean("Shortcuts")) {
            ModifyShortcutItemCount.init()
        }
        Info.init()
    }
}