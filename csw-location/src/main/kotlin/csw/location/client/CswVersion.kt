package csw.location.client

import csw.params.core.models.Prefix

interface CswVersion {
    fun check(metadata: Metadata, prefix: Prefix): Unit
    fun get(): String

    companion object {

        fun noOp(): CswVersion = object: CswVersion {
            override fun check(metadata: Metadata, prefix: Prefix) {}
            override fun get(): String = "no-version"
        }
    }
}