package cn.miuihone.xiaowine.hook

abstract class BaseHook {
    var isInit: Boolean = false
    abstract fun init()
}