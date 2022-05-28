package cn.miuihone.xiaowine.utils


object Utils {
     fun catchNoClass(callback: () -> Unit) {
        try {
            callback()
        } catch (e: NoSuchMethodException) {
            LogUtils.i("${e.message} 未找到class：${callback.javaClass.name}")
        }
    }
}