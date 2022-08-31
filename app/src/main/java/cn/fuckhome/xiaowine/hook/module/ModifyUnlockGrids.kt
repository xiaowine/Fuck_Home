package cn.fuckhome.xiaowine.hook.module

import cn.fuckhome.xiaowine.R
import cn.fuckhome.xiaowine.hook.BaseHook
import cn.fuckhome.xiaowine.utils.LogUtils
import cn.fuckhome.xiaowine.utils.Utils
import com.github.kyuubiran.ezxhelper.init.InitFields
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookBefore

object ModifyUnlockGrids : BaseHook() {
    override fun init() {
        Utils.catchNoClass {
            LogUtils.i(InitFields.moduleRes.getString(R.string.UnlockGrids))
            findMethod("com.miui.home.launcher.DeviceConfig") { name == "getHotseatMaxCount" }.hookBefore { it.result = 15 }
        }
    }
}