package cn.fuckhome.xiaowine.hook

import android.app.Application
import android.content.Context
import cn.fuckhome.xiaowine.utils.hookBeforeMethod
import cn.fuckhome.xiaowine.hook.module.Main
import cn.fuckhome.xiaowine.utils.LogUtils
import cn.fuckhome.xiaowine.utils.Utils.XConfig
import com.github.kyuubiran.ezxhelper.init.EzXHelperInit
import com.github.kyuubiran.ezxhelper.utils.Log.logexIfThrow
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage

private const val PACKAGE_MIUI_HOME = "com.miui.home"


class MainHook : IXposedHookLoadPackage, IXposedHookZygoteInit /* Optional */ {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (!XConfig.getBoolean("MainSwitch")) {
            LogUtils.i("总开关未打开")
            return
        }
        when (lpparam.packageName) {
            PACKAGE_MIUI_HOME -> Application::class.java.hookBeforeMethod("attach", Context::class.java) {
                EzXHelperInit.apply {
                    initHandleLoadPackage(lpparam)
                    initAppContext(it.args[0] as Context)
                    initHooks(Main)
                }
            }

            "com.android.settings" -> {}
            else -> return
        }

    }

    // Optional
    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        EzXHelperInit.initZygote(startupParam)
    }

    private fun initHooks(vararg hook: BaseHook) {
        hook.forEach {
            runCatching {
                if (it.isInit) return@forEach
                it.init()
                it.isInit = true
                LogUtils.i("Inited hook: ${it.javaClass.simpleName}")
            }.logexIfThrow("Failed init hook: ${it.javaClass.simpleName}")
        }
    }
}
