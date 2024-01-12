package app.revanced.patches.bilibili.misc.integrations.patch

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.bilibili.misc.integrations.fingerprints.BaseMainFrameFragmentFingerprint
import app.revanced.patches.bilibili.misc.integrations.fingerprints.NotificationManagerCompatFingerprint
import app.revanced.patches.bilibili.utils.cloneMutable
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction35c
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

@Patch(
    name = "Config",
    description = "集成获取Config方法",
    compatiblePackages = [CompatiblePackage(name = "tv.danmaku.bili"), CompatiblePackage(name = "tv.danmaku.bilibilihd")]
)
object ConfigPatch : BytecodePatch(setOf(NotificationManagerCompatFingerprint, BaseMainFrameFragmentFingerprint)) {
    override fun execute(context: BytecodeContext) {
        val method = NotificationManagerCompatFingerprint.result?.method
            ?: throw NotificationManagerCompatFingerprint.exception
        val abMethodRef = method.implementation!!.instructions.firstNotNullOf {
            if (it.opcode == Opcode.INVOKE_STATIC && (it as Instruction35c).registerCount == 0) {
                it.reference as MethodReference
            } else null
        }
        val getMethodRef = method.implementation!!.instructions.firstNotNullOf {
            if (it.opcode == Opcode.INVOKE_INTERFACE && (it as Instruction35c).registerCount == 3) {
                it.reference as MethodReference
            } else null
        }
        val utilsMethods = context.findClass("Lapp/revanced/bilibili/utils/Utils;")!!.mutableClass.methods
        utilsMethods.first { it.name == "getAb" }.also { utilsMethods.remove(it) }
            .cloneMutable(registerCount = 3, clearImplementation = true).apply {
                addInstructions(
                    """
                    invoke-static {}, $abMethodRef
                    move-result-object v0
                    invoke-interface {v0, p0, p1}, $getMethodRef
                    move-result v0
                    return v0
                """.trimIndent()
                )
            }.also { utilsMethods.add(it) }
        val method2 = BaseMainFrameFragmentFingerprint.result?.method
            ?: throw BaseMainFrameFragmentFingerprint.exception
        val configMethodRef = method2.implementation!!.instructions.firstNotNullOf {
            if (it.opcode == Opcode.INVOKE_STATIC && (it as Instruction35c).registerCount == 0) {
                it.reference as MethodReference
            } else null
        }
        val configGetMethodRef = method2.implementation!!.instructions.firstNotNullOf {
            if (it.opcode == Opcode.INVOKE_INTERFACE && (it as Instruction35c).registerCount == 3) {
                it.reference as MethodReference
            } else null
        }
        utilsMethods.first { it.name == "getConfig" }.also { utilsMethods.remove(it) }
            .cloneMutable(registerCount = 3, clearImplementation = true).apply {
                addInstructions(
                    """
                    invoke-static {}, $configMethodRef
                    move-result-object v0
                    invoke-interface {v0, p0, p1}, $configGetMethodRef
                    move-result-object v0
                    check-cast v0, Ljava/lang/String;
                    return-object v0
                """.trimIndent()
                )
            }.also { utilsMethods.add(it) }
    }
}
