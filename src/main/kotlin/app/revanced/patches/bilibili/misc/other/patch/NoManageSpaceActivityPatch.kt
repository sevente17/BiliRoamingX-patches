package app.revanced.patches.bilibili.misc.other.patch

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.bilibili.utils.get

@Patch(
    name = "Remove managerSpaceActivity",
    description = "移除管理存储空间入口",
    compatiblePackages = [
        CompatiblePackage(name = "tv.danmaku.bili"),
        CompatiblePackage(name = "tv.danmaku.bilibilihd"),
        CompatiblePackage(name = "com.bilibili.app.in")
    ]
)
object NoManageSpaceActivityPatch : ResourcePatch() {
    override fun execute(context: ResourceContext) {
        context.xmlEditor["AndroidManifest.xml"].use {
            it.file["application"].removeAttribute("android:manageSpaceActivity")
        }
    }
}
