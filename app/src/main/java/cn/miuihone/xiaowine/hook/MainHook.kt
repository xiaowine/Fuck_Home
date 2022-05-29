package cn.miuihone.xiaowine.hook

import android.app.Application
import android.content.Context
import cn.miuihone.xiaowine.utils.hookBeforeMethod
import cn.miuihone.xiaowine.hook.app.MiuiHome
import cn.miuihone.xiaowine.utils.LogUtils
import com.github.kyuubiran.ezxhelper.init.EzXHelperInit
import com.github.kyuubiran.ezxhelper.utils.Log.logexIfThrow
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage

private const val PACKAGE_MIUI_HOME = "com.miui.home"


class MainHook : IXposedHookLoadPackage, IXposedHookZygoteInit /* Optional */ {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        when (lpparam.packageName) {
            PACKAGE_MIUI_HOME -> Application::class.java.hookBeforeMethod("attach", Context::class.java) {
                EzXHelperInit.apply {
                    initHandleLoadPackage(lpparam)
                    initAppContext(it.args[0] as Context)
                    initHooks(MiuiHome)
                }
            }
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
                LogUtils.i("Inited hook: ${it.javaClass.simpleName}")
            }.logexIfThrow("Failed init hook: ${it.javaClass.simpleName}")
        }
    }
}
