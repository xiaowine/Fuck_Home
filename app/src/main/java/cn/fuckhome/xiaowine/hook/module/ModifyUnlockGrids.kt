package cn.fuckhome.xiaowine.hook.module

import cn.fuckhome.xiaowine.hook.BaseHook
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookBefore

object ModifyUnlockGrids : BaseHook() {
    override fun init() {
        findMethod("com.miui.home.launcher.DeviceConfig") { name == "getHotseatMaxCount" }.hookBefore { it.result = 99 }
    }
}