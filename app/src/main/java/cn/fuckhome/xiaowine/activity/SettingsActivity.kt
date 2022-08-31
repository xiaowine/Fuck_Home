package cn.fuckhome.xiaowine.activity

import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import cn.aodlyric.xiaowine.utils.ActivityUtils
import cn.fkj233.ui.activity.MIUIActivity
import cn.fkj233.ui.activity.view.SpinnerV
import cn.fkj233.ui.activity.view.TextV
import cn.fkj233.ui.dialog.MIUIDialog
import cn.fuckhome.xiaowine.BuildConfig
import cn.fuckhome.xiaowine.R
import cn.fuckhome.xiaowine.utils.ActivityOwnSP.ownSPConfig as config
import cn.fuckhome.xiaowine.utils.ActivityOwnSP
import cn.fuckhome.xiaowine.utils.BackupUtils
import cn.fuckhome.xiaowine.utils.FileUtils
import cn.fuckhome.xiaowine.utils.Utils
import cn.fuckhome.xiaowine.utils.Utils.isNotNull
import com.jaredrummler.ktsh.Shell
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

class SettingsActivity : MIUIActivity() {
    private val activity = this
    private val openFontFile = 1511

    init {
        initView {
            registerMain(getString(R.string.AppName), false) {
                TextS(textId = R.string.MainSwitch, key = "MainSwitch")
                Line()
                TextA(textId = R.string.AddInformation, onClickListener = { showFragment("AddInformation") })
                TextA(textId = R.string.AddInformationStyle, onClickListener = { showFragment("AddInformationStyle") })
                Line()
                TextA(textId = R.string.Unrestricted, onClickListener = { showFragment("Unrestricted") })
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
                TextA(textId = R.string.Backup, onClickListener = { BackupUtils.backup(activity, ActivityOwnSP.ownSP) })
                TextA(textId = R.string.Recovery, onClickListener = { BackupUtils.backup(activity, ActivityOwnSP.ownSP) })
                Line()
                TextSummary(textId = R.string.ModulePackName, tips = BuildConfig.APPLICATION_ID)
                TextSummary(textId = R.string.ModuleVersion, tips = "${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})-${BuildConfig.BUILD_TYPE}")
                val buildTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(BuildConfig.BUILD_TIME.toLong())
                TextSummary(textId = R.string.BuildTime, tips = buildTime)
                Text()
            }

            register("AddInformation", getString(R.string.AddInformation), false) {

                TextS(textId = R.string.Memory, key = "MemoryView")
                TextS(textId = R.string.Zarm, key = "ZarmView")
                TextS(textId = R.string.Storage, key = "StorageView")
                TextS(textId = R.string.BootTime, key = "BootTime")
                TextS(textId = R.string.RunningAppTotal, key = "RunningAppTotal")
                TextS(textId = R.string.RunningServiceTotal, key = "RunningServiceTotal")
                TextS(textId = R.string.warning, key = "Warning")

            }
            register("AddInformationStyle", getString(R.string.AddInformationStyle), false) {
                TextS(textId = R.string.optimizeAnimation, key = "optimizeAnimation")
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
                TextA(textId = R.string.CustomFont, onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.CustomFont)
                        setRButton(R.string.ChooseFont) {
                            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                            intent.addCategory(Intent.CATEGORY_OPENABLE)
                            intent.type = "*/*"
                            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                            startActivityForResult(intent, openFontFile)
                            dismiss()
                        }
                        setLButton(R.string.Reset) {
                            application.sendBroadcast(Intent().apply {
                                action = "MIUIHOME_Server"
                                putExtra("Type", "delete_font")
                            })
                            dismiss()
                        }
                    }.show()
                })
                val dict: HashMap<Int, String> = hashMapOf()
                dict[Gravity.CENTER] = getString(R.string.CENTER)
                dict[Gravity.START] = getString(R.string.START)
                dict[Gravity.END] = getString(R.string.END)
                TextWithSpinner(TextV(textId = R.string.Gravity), SpinnerV(dict[Gravity.START]!!) {
                    dict.forEach { (key, value) -> add(value) { config.setGravity(key) } }
                })
                TextA(textId = R.string.LeftMargin0, onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.LeftMargin0)
                        setMessage(R.string.MarginTips)
                        setEditText(ActivityOwnSP.ownSPConfig.getInt("LeftMargin0").toString(), "0")
                        setRButton(R.string.Ok) {
                            if (getEditText().isNotEmpty()) {
                                try {
                                    val value = getEditText().toInt()
                                    if (value in (-100..100)) {
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
                                    if (value in (-100..100)) {
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
                        setTitle(R.string.LeftMargin1)
                        setMessage(R.string.MarginTips)
                        setEditText(ActivityOwnSP.ownSPConfig.getInt("LeftMargin1").toString(), "0")
                        setRButton(R.string.Ok) {
                            if (getEditText().isNotEmpty()) {
                                try {
                                    val value = getEditText().toInt()
                                    if (value in (-100..100)) {
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
                        setTitle(R.string.TopMargin1)
                        setMessage(R.string.MarginTips)
                        setEditText(ActivityOwnSP.ownSPConfig.getInt("TopMargin1").toString(), "0")
                        setRButton(R.string.Ok) {
                            if (getEditText().isNotEmpty()) {
                                try {
                                    val value = getEditText().toInt()
                                    if (value in (-100..100)) {
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
            }
            register("Unrestricted", getString(R.string.Unrestricted), false) {
                TextS(textId = R.string.Pad, key = "Pad")
                TextS(textId = R.string.Shortcuts, key = "Shortcuts")
                TextS(textId = R.string.UnlockGrids, key = "UnlockGrids")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data.isNotNull() && resultCode == RESULT_OK) {
            when (requestCode) {
                BackupUtils.CREATE_DOCUMENT_CODE -> {
                    BackupUtils.handleCreateDocument(activity, data!!.data)
                }

                BackupUtils.OPEN_DOCUMENT_CODE -> {
                    BackupUtils.handleReadDocument(activity, data!!.data)
                }

                openFontFile -> {
                    data!!.data?.let {
                        activity.sendBroadcast(Intent().apply {
                            action = "MIUIHOME_Server"
                            putExtra("Type", "copy_font")
                            putExtra("Font_Path", FileUtils(activity).getFilePathByUri(it))
                        })
                    }
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        ActivityOwnSP.activity = this
        if (!checkLSPosed()) isLoad = false
        super.onCreate(savedInstanceState)
        if (isLoad) {
            registerReceiver(AppReceiver(), IntentFilter().apply { addAction("MIUIHOME_App_Server") })
            if (BuildConfig.DEBUG) {
                ActivityOwnSP.ownSPConfig.setValue("MemoryView", true)
                ActivityOwnSP.ownSPConfig.setValue("ZarmView", true)
                ActivityOwnSP.ownSPConfig.setValue("MainSwitch", true)
                ActivityOwnSP.ownSPConfig.setValue("Debug", true)
            }
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

    inner class AppReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                Handler(Looper.getMainLooper()).post {
                    when (intent.getStringExtra("app_Type")) {
                        "CopyFont" -> {
                            val message: String = if (intent.getBooleanExtra("CopyFont", false)) {
                                getString(R.string.CustomFontSuccess)
                            } else {
                                getString(R.string.CustomFontFail) + "\n" + intent.getStringExtra("font_error")
                            }
                            MIUIDialog(activity) {
                                setTitle(getString(R.string.CustomFont))
                                setMessage(message)
                                setRButton(getString(R.string.Ok)) { dismiss() }
                            }.show()
                        }

                        "DeleteFont" -> {
                            val message: String = if (intent.getBooleanExtra("DeleteFont", false)) {
                                getString(R.string.DeleteFontSuccess)
                            } else {
                                getString(R.string.DeleteFontFail)
                            }
                            MIUIDialog(activity) {
                                setTitle(getString(R.string.DeleteFont))
                                setMessage(message)
                                setRButton(getString(R.string.Ok)) { dismiss() }
                            }.show()
                        }
                    }
                }
            } catch (_: Throwable) {
            }
        }
    }
}
