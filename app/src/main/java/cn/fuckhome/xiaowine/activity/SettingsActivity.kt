package cn.fuckhome.xiaowine.activity

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import cn.aodlyric.xiaowine.utils.ActivityUtils
import cn.fkj233.ui.activity.MIUIActivity
import cn.fkj233.ui.dialog.MIUIDialog
import cn.fuckhome.xiaowine.BuildConfig
import cn.fuckhome.xiaowine.R
import cn.fuckhome.xiaowine.utils.ActivityOwnSP.ownSPConfig as config
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
                Line()
                TitleText(textId=R.string.AdvancedFeatures)
                TextS(textId = R.string.Pad, key = "Pad")
                Line()
                TitleText(textId=R.string.Customize)
                TextA(textId = R.string.Color, onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.Color)
                        setMessage(R.string.LyricColorTips)
                        setEditText(config.getColor(), "")
                        setRButton(R.string.Ok) {
                            if (getEditText().isNotEmpty()) {
                                try {
                                    Color.parseColor(getEditText())
                                    config.setColor(getEditText())
                                    dismiss()
                                    return@setRButton
                                } catch (_: Throwable) {
                                }
                            }
                            ActivityUtils.showToastOnLooper(activity, getString(R.string.LyricColorError))
                            config.setColor("")
                            dismiss()
                        }
                        setLButton(R.string.Cancel) { dismiss() }
                    }.show()
                })
                TextA(textId = R.string.BackgroundColor, onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.BackgroundColor)
                        setMessage(R.string.LyricColorTips)
                        setEditText(config.getBgColor(), "#00000000")
                        setRButton(R.string.Ok) {
                            if (getEditText().isNotEmpty()) {
                                try {
                                    Color.parseColor(getEditText())
                                    config.setBgColor(getEditText())
                                    dismiss()
                                    return@setRButton
                                } catch (_: Throwable) {
                                }
                            }
                            ActivityUtils.showToastOnLooper(activity, getString(R.string.LyricColorError))
                            config.setBgColor("#00000000")
                            dismiss()
                        }
                        setLButton(R.string.Cancel) { dismiss() }
                    }.show()
                })
                Line()
                TextA(textId = R.string.LeftMargin0, onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.LeftMargin0)
                        setMessage(R.string.MarginTips)
                        setEditText(ActivityOwnSP.ownSPConfig.getInt("LeftMargin0").toString(), "0")
                        setRButton(R.string.Ok) {
                            if (getEditText().isNotEmpty()) {
                                try {
                                    val value = getEditText().toInt()
                                    if (value in (0..100)) {
                                        ActivityOwnSP.ownSPConfig.setValue("LeftMargin0", value)
                                        dismiss()
                                        return@setRButton
                                    }
                                } catch (_: Throwable) {
                                }
                            }
                            ActivityUtils.showToastOnLooper(activity, getString(R.string.InputError))
                            ActivityOwnSP.ownSPConfig.setValue("LeftMargin0", 0)
                            dismiss()
                        }
                        setLButton(R.string.Cancel) { dismiss() }
                    }.show()
                })
                TextA(textId = R.string.TopMargin0, onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.TopMargin0)
                        setMessage(R.string.MarginTips)
                        setEditText(ActivityOwnSP.ownSPConfig.getInt("TopMargin0").toString(), "0")
                        setRButton(R.string.Ok) {
                            if (getEditText().isNotEmpty()) {
                                try {
                                    val value = getEditText().toInt()
                                    if (value in (0..100)) {
                                        ActivityOwnSP.ownSPConfig.setValue("TopMargin0", value)
                                        dismiss()
                                        return@setRButton
                                    }
                                } catch (_: Throwable) {
                                }
                            }
                            ActivityUtils.showToastOnLooper(activity, getString(R.string.InputError))
                            ActivityOwnSP.ownSPConfig.setValue("TopMargin0", 0)
                            dismiss()
                        }
                        setLButton(R.string.Cancel) { dismiss() }
                    }.show()
                })
                TextA(textId = R.string.LeftMargin1, onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.TopMargin1)
                        setMessage(R.string.MarginTips)
                        setEditText(ActivityOwnSP.ownSPConfig.getInt("LeftMargin1").toString(), "0")
                        setRButton(R.string.Ok) {
                            if (getEditText().isNotEmpty()) {
                                try {
                                    val value = getEditText().toInt()
                                    if (value in (-900..900)) {
                                        ActivityOwnSP.ownSPConfig.setValue("LeftMargin1", value)
                                        dismiss()
                                        return@setRButton
                                    }
                                } catch (_: Throwable) {
                                }
                            }
                            ActivityUtils.showToastOnLooper(activity, getString(R.string.InputError))
                            ActivityOwnSP.ownSPConfig.setValue("LeftMargin1", 0)
                            dismiss()
                        }
                        setLButton(R.string.Cancel) { dismiss() }
                    }.show()
                })
                TextA(textId = R.string.TopMargin1, onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.LeftMargin1)
                        setMessage(R.string.MarginTips)
                        setEditText(ActivityOwnSP.ownSPConfig.getInt("TopMargin1").toString(), "0")
                        setRButton(R.string.Ok) {
                            if (getEditText().isNotEmpty()) {
                                try {
                                    val value = getEditText().toInt()
                                    if (value in (-900..900)) {
                                        ActivityOwnSP.ownSPConfig.setValue("TopMargin1", value)
                                        dismiss()
                                        return@setRButton
                                    }
                                } catch (_: Throwable) {
                                }
                            }
                            ActivityUtils.showToastOnLooper(activity, getString(R.string.InputError))
                            ActivityOwnSP.ownSPConfig.setValue("TopMargin1", 0)
                            dismiss()
                        }
                        setLButton(R.string.Cancel) { dismiss() }
                    }.show()
                })
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

        if (isLoad && BuildConfig.DEBUG) {
            ActivityOwnSP.ownSPConfig.setValue("MemoryView", true)
            ActivityOwnSP.ownSPConfig.setValue("ZarmView", true)
            ActivityOwnSP.ownSPConfig.setValue("MainSwitch", true)
            ActivityOwnSP.ownSPConfig.setValue("Debug", true)
        }

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
