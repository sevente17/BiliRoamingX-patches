package app.revanced.patches.bilibili.misc.other.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object OgvSearchResultFingerprint : MethodFingerprint(
    strings = listOf("pgc.bangumi-search.0.0.pv"),
    customFingerprint = { _, classDef ->
        classDef.type == "Lcom/bilibili/search/ogv/OgvSearchResultFragment;"
    }
)
