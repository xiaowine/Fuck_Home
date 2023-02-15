package cn.fuckhome.xiaowine.hook.module.modify

import cn.fuckhome.xiaowine.R
import cn.fuckhome.xiaowine.hook.BaseHook
import cn.fuckhome.xiaowine.utils.LogUtils
import com.github.kyuubiran.ezxhelper.init.InitFields
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookReturnConstant

object RemoveSmallWindowRestriction3 : BaseHook() {
    override fun init() {
        LogUtils.i(InitFields.moduleRes.getString(R.string.RemoveSmallWindowRestriction))
        findMethod("com.android.systemui.statusbar.notification.NotificationSettingsManager") { name == "canSlide" }.hookReturnConstant(true)
    }

}
