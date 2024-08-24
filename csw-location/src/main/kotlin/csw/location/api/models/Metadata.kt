package csw.location.api.models

import arrow.core.Option
import arrow.core.toOption
import csw.params.core.models.Prefix
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
/**
 * metadata represents any additional information (metadata) associated with location
 * For example, "agentId": "ESW.agent1" this can be metadata information for sequence component location
 *
 * @param values represents additional information associated with location
 */
@Serializable(with = MetadataSerializer::class)
data class Metadata(val value: Map<String, String>) {

    fun add(key: String, value: String): Metadata = copy(this.value + mapOf(key to value))

    fun withCSWVersion(version: String): Metadata = add(CswVersionKey, version)

    fun withPid(pid: Long): Metadata = add(PidKey, pid.toString())
    fun withAgentPrefix(agentPrefix: Prefix): Metadata = add(AgentPrefixKey, agentPrefix.toString())
    fun withSequenceComponentPrefix(sequenceComponentPrefix: Prefix): Metadata =
        add(SequenceComponentPrefixKey, sequenceComponentPrefix.toString())

    fun getCSWVersion(): Option<String> = get(CswVersionKey)
    fun get(key: String): Option<String> = value.get(key).toOption()
    fun getPid(): Option<Long> = get(PidKey).map { it.toLong() }
    fun getAgentPrefix(): Option<Prefix> = get(AgentPrefixKey).map { Prefix(it) }
    fun getSequenceComponentPrefix(): Option<Prefix> = get(SequenceComponentPrefixKey).map { Prefix(it) }

    companion object {
        private const val PidKey = "PID"
        private const val AgentPrefixKey = "agentPrefix"
        private const val SequenceComponentPrefixKey = "sequenceComponentPrefix"
        private const val CswVersionKey = "csw-version"

        val empty: Metadata = Metadata(emptyMap())

        operator fun invoke(): Metadata = Metadata(emptyMap())
    }
}

object MetadataSerializer : KSerializer<Metadata> {
    private val mapSerializer = MapSerializer(String.serializer(), String.serializer())
    override val descriptor: SerialDescriptor = mapSerializer.descriptor

    override fun serialize(encoder: Encoder, value: Metadata) {
        mapSerializer.serialize(encoder, value.value)
    }

    override fun deserialize(decoder: Decoder): Metadata {
        val map = mapSerializer.deserialize(decoder)
        return Metadata(map)
    }
}


