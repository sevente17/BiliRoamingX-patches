package app.revanced.patches.bilibili.misc.config.patch

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.bilibili.misc.config.fingerprints.ABSourceFingerprint
import app.revanced.patches.bilibili.utils.cloneMutable

@Patch(
    name = "Config",
    description = "Config hook",
    compatiblePackages = [CompatiblePackage(name = "tv.danmaku.bili"), CompatiblePackage(name = "tv.danmaku.bilibilihd")]
)
object ConfigPatch : BytecodePatch(fingerprints = setOf(ABSourceFingerprint)) {
    override fun execute(context: BytecodeContext) {
        ABSourceFingerprint.result?.run {
            val method = mutableMethod
            method.cloneMutable(registerCount = 4, clearImplementation = true).apply {
                method.name += "_Origin"
                addInstructions(
                    """
                    invoke-virtual {p0, p1, p2}, $method
                    move-result-object v0
                    invoke-static {p1, p2, v0}, Lapp/revanced/bilibili/patches/ConfigPatch;->getAb(Ljava/lang/String;Ljava/lang/Boolean;Ljava/lang/Boolean;)Ljava/lang/Boolean;
                    move-result-object v0
                    return-object v0
                """.trimIndent()
                )
            }.also { mutableClass.methods.add(it) }
        } ?: throw ABSourceFingerprint.exception
    }
}
