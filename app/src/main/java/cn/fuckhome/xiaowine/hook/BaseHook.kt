package cn.fuckhome.xiaowine.hook

abstract class BaseHook {
    var isInit: Boolean = false
    abstract fun init()
}