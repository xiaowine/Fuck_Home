package cn.fuckhome.xiaowine.hook.module.modify

import cn.fuckhome.xiaowine.R
import cn.fuckhome.xiaowine.hook.BaseHook
import cn.fuckhome.xiaowine.utils.LogUtils
import cn.fuckhome.xiaowine.utils.Utils
import com.github.kyuubiran.ezxhelper.init.InitFields.moduleRes
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookBefore

object UnlockPad : BaseHook() {
    override fun init() {
        Utils.catchNoClass {
            LogUtils.i(moduleRes.getString(R.string.Pad))
            findMethod("com.miui.home.launcher.common.Utilities") { name == "isPadDevice" }.hookBefore {
                it.result = true
            }
        }
    }
}