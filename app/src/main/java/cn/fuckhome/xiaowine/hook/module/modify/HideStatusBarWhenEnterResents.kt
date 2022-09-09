package cn.fuckhome.xiaowine.hook.module.modify

import cn.fuckhome.xiaowine.R
import cn.fuckhome.xiaowine.hook.BaseHook
import cn.fuckhome.xiaowine.utils.LogUtils
import cn.fuckhome.xiaowine.utils.Utils
import cn.fuckhome.xiaowine.utils.Utils.XConfig
import com.github.kyuubiran.ezxhelper.init.InitFields.moduleRes
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookBefore


object HideStatusBarWhenEnterResents : BaseHook() {

    override fun init() {
        Utils.catchNoClass {
            if (XConfig.getBoolean("HideStatusBar")) LogUtils.i(moduleRes.getString(R.string.HideStatusBar))
            findMethod("com.miui.home.launcher.common.DeviceLevelUtils") { name == "isHideStatusBarWhenEnterRecents" }.hookBefore { it.result = XConfig.getBoolean("HideStatusBar") }
            findMethod("com.miui.home.launcher.DeviceConfig") { name == "keepStatusBarShowingForBetterPerformance" }.hookBefore { it.result = !XConfig.getBoolean("HideStatusBar") }

        }
    }
}