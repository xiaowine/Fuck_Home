package cn.fuckhome.xiaowine.hook.module

import cn.fuckhome.xiaowine.hook.BaseHook
import cn.fuckhome.xiaowine.utils.callMethod
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter

object ModifyShortcutItemCount : BaseHook() {
    override fun init() {
        findMethod("com.miui.home.launcher.shortcuts.AppShortcutMenu") { name == "getMaxCountInCurrentOrientation" }.hookAfter { it.result = 999 }
        findMethod("com.miui.home.launcher.shortcuts.AppShortcutMenu") { name == "getMaxShortcutItemCount" }.hookAfter { it.result = 999 }
        findMethod("com.miui.home.launcher.shortcuts.AppShortcutMenu") { name == "getMaxVisualHeight" }.hookAfter { it.result = it.thisObject.callMethod("getItemHeight") }
    }
}