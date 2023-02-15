package cn.fuckhome.xiaowine.hook.module.modify

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import cn.fuckhome.xiaowine.R
import cn.fuckhome.xiaowine.hook.BaseHook
import cn.fuckhome.xiaowine.utils.callMethod
import cn.fuckhome.xiaowine.utils.callStaticMethod
import cn.fuckhome.xiaowine.utils.findClass
import cn.fuckhome.xiaowine.utils.getStaticObjectField
import cn.fuckhome.xiaowine.utils.hookAfterAllMethods
import cn.fuckhome.xiaowine.utils.hookBeforeMethod
import cn.fuckhome.xiaowine.utils.setStaticObjectField
import com.github.kyuubiran.ezxhelper.init.InitFields.appContext
import com.github.kyuubiran.ezxhelper.init.InitFields.moduleRes
import de.robv.android.xposed.XposedHelpers

@SuppressLint("StaticFieldLeak", "DiscouragedApi")
object ShortcutSmallWindow : BaseHook() {
    override fun init() {
        val mViewDarkModeHelper = ("com.miui.home.launcher.util.ViewDarkModeHelper").findClass()
        val mSystemShortcutMenu = ("com.miui.home.launcher.shortcuts.SystemShortcutMenu").findClass()
        val mSystemShortcutMenuItem = ("com.miui.home.launcher.shortcuts.SystemShortcutMenuItem").findClass()
        val mAppShortcutMenu = ("com.miui.home.launcher.shortcuts.AppShortcutMenu").findClass()
        val mShortcutMenuItem = ("com.miui.home.launcher.shortcuts.ShortcutMenuItem").findClass()
        val mAppDetailsShortcutMenuItem = ("com.miui.home.launcher.shortcuts.SystemShortcutMenuItem\$AppDetailsShortcutMenuItem").findClass()
        val mActivityUtilsCompat = ("com.miui.launcher.utils.ActivityUtilsCompat").findClass()
        mViewDarkModeHelper.hookAfterAllMethods("onConfigurationChanged") {
            mSystemShortcutMenuItem.callStaticMethod("createAllSystemShortcutMenuItems")
        }
        mShortcutMenuItem.hookAfterAllMethods("getShortTitle") {
            val appName = appContext.getString(appContext.resources.getIdentifier("system_shortcuts_more_operation", "string", "com.miui.home"))
            if (it.result == appName) {
                it.result = moduleRes.getString(R.string.AppInfo)
            }
        }
        mAppDetailsShortcutMenuItem.hookBeforeMethod("lambda\$getOnClickListener$0", mAppDetailsShortcutMenuItem, View::class.java) {
            val obj = it.args[0]
            val view: View = it.args[1] as View
            val mShortTitle = obj.callMethod("getShortTitle") as CharSequence
            if (mShortTitle == moduleRes.getString(R.string.SmallWindow)) {
                it.result = null
                val intent = Intent()
                val mComponentName = obj.callMethod("getComponentName") as ComponentName
                intent.action = "android.intent.action.MAIN"
                intent.addCategory("android.intent.category.LAUNCHER")
                intent.component = mComponentName
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val callStaticMethod = mActivityUtilsCompat.callStaticMethod("makeFreeformActivityOptions", view.context, mComponentName.packageName)
                if (callStaticMethod != null) {
                    view.context.startActivity(intent, callStaticMethod.callMethod("toBundle") as Bundle)
                }
            }
        }
        mSystemShortcutMenu.hookAfterAllMethods("getMaxShortcutItemCount") {
            it.result = 5
        }
        mAppShortcutMenu.hookAfterAllMethods("getMaxShortcutItemCount") {
            it.result = 5
        }
        mSystemShortcutMenuItem.hookAfterAllMethods("createAllSystemShortcutMenuItems") {
            @Suppress("UNCHECKED_CAST")
            val mAllSystemShortcutMenuItems = mSystemShortcutMenuItem.getStaticObjectField("sAllSystemShortcutMenuItems") as Collection<Any>
            val mSmallWindowInstance = XposedHelpers.newInstance(mAppDetailsShortcutMenuItem)
            mSmallWindowInstance.callMethod("setShortTitle", moduleRes.getString(R.string.SmallWindow))
            val isDarkMode =
                appContext.applicationContext.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
            @Suppress("DEPRECATION")
            mSmallWindowInstance.callMethod(
                "setIconDrawable",
                if (isDarkMode) moduleRes.getDrawable(R.drawable.ic_small_window_dark) else moduleRes.getDrawable(R.drawable.ic_small_window_light)
            )
            val sAllSystemShortcutMenuItems = ArrayList<Any>()
            sAllSystemShortcutMenuItems.add(mSmallWindowInstance)
            sAllSystemShortcutMenuItems.addAll(mAllSystemShortcutMenuItems)
            mSystemShortcutMenuItem.setStaticObjectField("sAllSystemShortcutMenuItems", sAllSystemShortcutMenuItems)
        }
    }
}
