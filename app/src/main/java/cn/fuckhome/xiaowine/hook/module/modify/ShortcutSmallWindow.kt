package cn.fuckhome.xiaowine.hook.module.modify

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import cn.fuckhome.xiaowine.R
import cn.fuckhome.xiaowine.hook.BaseHook
import cn.fuckhome.xiaowine.utils.*
import com.github.kyuubiran.ezxhelper.init.InitFields.appContext
import com.github.kyuubiran.ezxhelper.init.InitFields.moduleRes
import com.github.kyuubiran.ezxhelper.utils.findAllMethods
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import com.github.kyuubiran.ezxhelper.utils.hookBefore

@SuppressLint("StaticFieldLeak", "DiscouragedApi")
object ShortcutSmallWindow : BaseHook() {
    override fun init() {
        val mSystemShortcutMenuItem = ("com.miui.home.launcher.shortcuts.SystemShortcutMenuItem").findClass()
        val mAppDetailsShortcutMenuItem = ("com.miui.home.launcher.shortcuts.SystemShortcutMenuItem\$AppDetailsShortcutMenuItem").findClass()
        findAllMethods("com.miui.home.launcher.util.ViewDarkModeHelper") { name == "onConfigurationChanged" }.hookAfter {
            mSystemShortcutMenuItem.callStaticMethod("createAllSystemShortcutMenuItems")
        }
        findAllMethods(mAppDetailsShortcutMenuItem) { name == "lambda\$getOnClickListener$0" }.hookBefore {
            val obj = it.args[0]
            val mShortTitle = obj.callMethod("getShortTitle") as CharSequence
            if (mShortTitle == moduleRes.getString(R.string.SmallWindow)) {
                it.result = null
                val intent = Intent()
                val mComponentName = obj.callMethod("getComponentName") as ComponentName
                intent.action = "android.intent.action.MAIN"
                intent.addCategory("android.intent.category.DEFAULT")
                intent.component = mComponentName
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val callStaticMethod = ("com.miui.launcher.utils.ActivityUtilsCompat").findClass().callStaticMethod("makeFreeformActivityOptions", appContext, mComponentName.packageName)
                if (callStaticMethod != null) {
                    appContext.startActivity(intent, callStaticMethod.callMethod("toBundle") as Bundle)
                }
            }
        }
        findAllMethods("com.miui.home.launcher.shortcuts.SystemShortcutMenu") { name == "getMaxShortcutItemCount" }.hookAfter {
            it.result = 5
        }
        findAllMethods(mSystemShortcutMenuItem) { name == "createAllSystemShortcutMenuItems" }.hookAfter {
            val mAllSystemShortcutMenuItems = mSystemShortcutMenuItem.getStaticObjectField("sAllSystemShortcutMenuItems") as Collection<Any>
            val mSmallWindowInstance = mAppDetailsShortcutMenuItem.newInstance()
            mSmallWindowInstance.callMethod("setShortTitle", moduleRes.getString(R.string.SmallWindow))
            mSmallWindowInstance.callMethod("setIconDrawable", appContext.getDrawable(appContext.resources.getIdentifier("ic_task_small_window", "drawable", "com.miui.home")))
            val sAllSystemShortcutMenuItems = ArrayList<Any>()
            sAllSystemShortcutMenuItems.add(mSmallWindowInstance)
            sAllSystemShortcutMenuItems.addAll(mAllSystemShortcutMenuItems)
            mSystemShortcutMenuItem.setStaticObjectField("sAllSystemShortcutMenuItems", sAllSystemShortcutMenuItems)
        }
    }
}
