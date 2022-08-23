package cn.fuckhome.xiaowine.activity

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import cn.aodlyric.xiaowine.utils.ActivityUtils
import cn.fkj233.ui.activity.MIUIActivity
import cn.fkj233.ui.dialog.MIUIDialog
import cn.fuckhome.xiaowine.BuildConfig
import cn.fuckhome.xiaowine.R
import cn.fuckhome.xiaowine.utils.ActivityOwnSP
import cn.fuckhome.xiaowine.utils.BackupUtils
import cn.fuckhome.xiaowine.utils.Utils
import com.jaredrummler.ktsh.Shell
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

class SettingsActivity : MIUIActivity() {
    private val activity = this

    init {
        initView {
            registerMain(getString(R.string.AppName), false) {
                TextS(textId = R.string.MainSwitch, key = "MainSwitch")
                TextS(textId = R.string.MemoryView, key = "MemoryView")
                TextS(textId = R.string.ZarmView, key = "ZarmView")
                TextS(textId = R.string.StorageView, key = "StorageView")
                TextS(textId = R.string.BootTime, key = "BootTime")
                TextS(textId = R.string.RunningAppTotal, key = "RunningAppTotal")
                TextS(textId = R.string.RunningServiceTotal, key = "RunningServiceTotal")
                TextS(textId = R.string.warning, key = "Warning")
                TextA(textId = R.string.LeftMargin, onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.LeftMargin)
                        setMessage(R.string.MarginTips)
                        setEditText(ActivityOwnSP.ownSPConfig.getInt("TopMargin").toString(), "0")
                        setRButton(R.string.Ok) {
                            if (getEditText().isNotEmpty()) {
                                try {
                                    val value = getEditText().toInt()
                                    if (value in (-900..900)) {
                                        ActivityOwnSP.ownSPConfig.setValue("TopMargin", value)
                                        dismiss()
                                        return@setRButton
                                    }
                                } catch (_: Throwable) {
                                }
                            }
                            ActivityUtils.showToastOnLooper(activity, getString(R.string.InputError))
                            ActivityOwnSP.ownSPConfig.setValue("TopMargin", 0)
                            dismiss()
                        }
                        setLButton(R.string.Cancel) { dismiss() }
                    }.show()
                })
                TextA(textId = R.string.TopMargin, onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.TopMargin)
                        setMessage(R.string.MarginTips)
                        setEditText(ActivityOwnSP.ownSPConfig.getInt("LeftMargin").toString(), "0")
                        setRButton(R.string.Ok) {
                            if (getEditText().isNotEmpty()) {
                                try {
                                    val value = getEditText().toInt()
                                    if (value in (-900..900)) {
                                        ActivityOwnSP.ownSPConfig.setValue("LeftMargin", value)
                                        dismiss()
                                        return@setRButton
                                    }
                                } catch (_: Throwable) {
                                }
                            }
                            ActivityUtils.showToastOnLooper(activity, getString(R.string.InputError))
                            ActivityOwnSP.ownSPConfig.setValue("LeftMargin", 0)
                            dismiss()
                        }
                        setLButton(R.string.Cancel) { dismiss() }
                    }.show()
                })
                TextS(textId = R.string.Pad, key = "Pad")
                Text()
            }
            registerMenu(getString(R.string.Menu)) {
                TextS(textId = R.string.HideDeskIcon, key = "hLauncherIcon", onClickListener = {
                    packageManager.setComponentEnabledSetting(ComponentName(activity, "${BuildConfig.APPLICATION_ID}.launcher"), if (it) {
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                    } else {
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                    }, PackageManager.DONT_KILL_APP)
                })
                TextS(textId = R.string.DebugMode, key = "Debug")
                TextA(textId = R.string.ResetModule, onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.ResetModuleDialog)
                        setMessage(R.string.ResetModuleDialogTips)
                        setLButton(R.string.Ok) {
                            ActivityOwnSP.ownSPConfig.clear()
                            ActivityUtils.showToastOnLooper(activity, activity.getString(R.string.ResetSuccess))
                            activity.finishActivity(0)
                            dismiss()
                        }
                        setRButton(R.string.Cancel) { dismiss() }
                    }.show()
                })
                TextA(textId = R.string.ReStartHome, onClickListener = {
                    Thread { Shell("su").run("am force-stop com.miui.home") }.start()
                })
                TextA(textId = R.string.Backup, onClickListener = { getSP()?.let { BackupUtils.backup(activity, it) } })
                TextA(textId = R.string.Recovery, onClickListener = { getSP()?.let { BackupUtils.recovery(activity, it) } })
                Line()
                TextSummary(textId = R.string.ModulePackName, tips = BuildConfig.APPLICATION_ID)
                TextSummary(textId = R.string.ModuleVersion, tips = "${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})-${BuildConfig.BUILD_TYPE}")
                val buildTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(BuildConfig.BUILD_TIME.toLong())
                TextSummary(textId = R.string.BuildTime, tips = buildTime)
                Text()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        ActivityOwnSP.activity = this
        if (!checkLSPosed()) isLoad = false
        super.onCreate(savedInstanceState)
    }

    private fun checkLSPosed(): Boolean {
        return try {
            Utils.getSP(this, "Fuck_Home_Config")?.let { setSP(it) }
            true
        } catch (e: Throwable) {
            MIUIDialog(activity) {
                setTitle(R.string.Tips)
                setMessage(R.string.NotSupport)
                setRButton(R.string.Restart) {
                    val intent = packageManager.getLaunchIntentForPackage(packageName)
                    intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    exitProcess(0)
                }
                setCancelable(false)
            }.show()
            false
        }
    }

}
