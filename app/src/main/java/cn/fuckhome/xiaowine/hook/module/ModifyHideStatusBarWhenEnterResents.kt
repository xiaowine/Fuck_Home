package cn.fuckhome.xiaowine.hook.module

import cn.fuckhome.xiaowine.R
import cn.fuckhome.xiaowine.hook.BaseHook
import cn.fuckhome.xiaowine.utils.LogUtils
import cn.fuckhome.xiaowine.utils.Utils.XConfig
import com.github.kyuubiran.ezxhelper.init.InitFields.moduleRes
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookBefore


object ModifyHideStatusBarWhenEnterResents : BaseHook() {

    override fun init() {
        LogUtils.i(moduleRes.getString(R.string.HideStatusBar))
        if (XConfig.getBoolean("HideStatusBar")) {
            findMethod("com.miui.home.launcher.common.DeviceLevelUtils") { name == "isHideStatusBarWhenEnterRecents" }.hookBefore { it.result = true }
            findMethod("com.miui.home.launcher.DeviceConfig") { name == "keepStatusBarShowingForBetterPerformance" }.hookBefore { it.result = false }

        } else {
            findMethod("com.miui.home.launcher.common.DeviceLevelUtils") { name == "keepStatusBarShowingForBetterPerformance" }.hookBefore { it.result = false }
        }
    }
}