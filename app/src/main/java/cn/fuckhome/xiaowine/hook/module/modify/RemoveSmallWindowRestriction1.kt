package cn.fuckhome.xiaowine.hook.module.modify

import android.content.Context
import cn.fuckhome.xiaowine.R
import cn.fuckhome.xiaowine.hook.BaseHook
import cn.fuckhome.xiaowine.utils.LogUtils
import com.github.kyuubiran.ezxhelper.init.InitFields
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import com.github.kyuubiran.ezxhelper.utils.hookReturnConstant

object RemoveSmallWindowRestriction1 : BaseHook() {
    override fun init() {
        LogUtils.i(InitFields.moduleRes.getString(R.string.RemoveSmallWindowRestriction))
        findMethod("com.android.server.wm.Task") { name == "isResizeable" }.hookReturnConstant(true)
        findMethod("android.util.MiuiMultiWindowAdapter") { name == "getFreeformBlackList" }.hookAfter { it.result = (it.result as MutableList<*>).apply { clear() } }
        findMethod("android.util.MiuiMultiWindowAdapter") { name == "getFreeformBlackListFromCloud" && parameterTypes[0] == Context::class.java }.hookAfter { it.result = (it.result as MutableList<*>).apply { clear() } }
        findMethod("android.util.MiuiMultiWindowUtils") { name == "supportFreeform" }.hookReturnConstant(true)

    }

}
