package cn.fuckhome.xiaowine.hook.module.modify

import cn.fuckhome.xiaowine.hook.BaseHook
import cn.fuckhome.xiaowine.utils.Utils
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookBefore


object UnlockNavType : BaseHook() {
    override fun init() {
        Utils.catchNoClass {
            findMethod("com.miui.home.launcher.DeviceConfig") { name == "isShowSystemNavTypePreferenceInMiuiSettings" }.hookBefore {
                it.result = true
            }
        }
    }
}