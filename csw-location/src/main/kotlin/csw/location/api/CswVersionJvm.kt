package csw.location.api

import arrow.core.None
import arrow.core.Some
import csw.location.client.CswVersion
import csw.params.core.models.Prefix
import csw.location.api.models.Metadata
/*
data class CswVersionJvm(): CswVersion {
    private val logger: Logger = LocationServiceLogger.getLogger

    override fun check(metadata: Metadata, prefix: Prefix): Unit =
        validateCswVersion(metadata, prefix)


    private fun validateCswVersion(metadata: Metadata, prefix: Prefix): Unit = {
        val mayBeCswVersion = metadata.getCSWVersion()
        mayBeCswVersion when {
            Some ->
                val myCswVersion = get()
                    if (cswVersionInMetadata != myCswVersion)
                    //logger.error(
//                        s"csw-version mismatch, In $prefix's Metadata Found:$cswVersionInMetadata, my csw-version:$myCswVersion"
//                    )
            None -> logger.error(s"Could not find csw-version for $prefix")
        }
    }
    override fun get(): String = {
        Option(
            classOf<LocationService>.getPackage.getSpecificationVersion
        ).getOrElse("0.1.0-SNAPSHOT")
    }
}

 */