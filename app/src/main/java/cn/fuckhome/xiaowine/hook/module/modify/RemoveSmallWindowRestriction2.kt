package cn.fuckhome.xiaowine.hook.module.modify

import cn.fuckhome.xiaowine.R
import cn.fuckhome.xiaowine.hook.BaseHook
import cn.fuckhome.xiaowine.utils.LogUtils
import com.github.kyuubiran.ezxhelper.init.InitFields
import com.github.kyuubiran.ezxhelper.utils.findAllMethods
import com.github.kyuubiran.ezxhelper.utils.hookReturnConstant

object RemoveSmallWindowRestriction2 : BaseHook() {
    override fun init() {
        LogUtils.i(InitFields.moduleRes.getString(R.string.RemoveSmallWindowRestriction))
        findAllMethods("com.miui.home.launcher.RecentsAndFSGestureUtils") { name == "canTaskEnterSmallWindow" }.hookReturnConstant(true)
        findAllMethods("com.miui.home.launcher.RecentsAndFSGestureUtils") { name == "canTaskEnterMiniSmallWindow" }.hookReturnConstant(true)
    }

}
