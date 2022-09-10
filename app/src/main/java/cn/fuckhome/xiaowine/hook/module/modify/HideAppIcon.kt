package cn.fuckhome.xiaowine.hook.module.modify

import cn.fuckhome.xiaowine.hook.BaseHook
import cn.fuckhome.xiaowine.utils.Utils
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookBefore

object HideAppIcon:BaseHook() {
    override fun init() {
        Utils.catchNoClass {
            findMethod("com.miui.home.launcher.ItemIcon") { name == "setIconImageView" }.hookBefore {
                it.result = null
            }
        }
    }
}