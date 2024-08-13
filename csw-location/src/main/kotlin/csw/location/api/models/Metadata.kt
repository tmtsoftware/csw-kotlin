package csw.location.api.models

import arrow.core.Option
import arrow.core.toOption
import csw.params.core.models.Prefix

/**
 * metadata represents any additional information (metadata) associated with location
 * For example, "agentId": "ESW.agent1" this can be metadata information for sequence component location
 *
 * @param values represents additional information associated with location
 */
data class Metadata(val values: Map<String, String>) {

    // Used from java API
    //def this(value: java.util.Map[String, String]) = this(value.asScala.toMap)

    //def jValue: util.Map[String, String] = value.asJava

    fun add(key: String, value: String): Metadata = copy(this.values + mapOf(key to value))

    fun withCSWVersion(version: String): Metadata = add(CswVersionKey, version)

    fun withPid(pid: Long): Metadata = add(PidKey, pid.toString())
    fun withAgentPrefix(agentPrefix: Prefix): Metadata = add(AgentPrefixKey, agentPrefix.toString())
    fun withSequenceComponentPrefix(sequenceComponentPrefix: Prefix): Metadata =
        add(SequenceComponentPrefixKey, sequenceComponentPrefix.toString())

    fun getCSWVersion(): Option<String> = get(CswVersionKey)
    fun get(key: String): Option<String> = values.get(key).toOption()
    fun getPid(): Option<Long> = get(PidKey).map { it.toLong() }
    fun getAgentPrefix(): Option<Prefix> = get(AgentPrefixKey).map { Prefix(it) }
    fun getSequenceComponentPrefix(): Option<Prefix> = get(SequenceComponentPrefixKey).map { Prefix(it) }

    //def jGet(key: String): Optional[String]           = get(key).toJava
    //def jGetPid: Optional[Long]                       = getPid.toJava
    //def jGetAgentPrefix: Optional[Prefix]             = getAgentPrefix.toJava
    //def jGetSequenceComponentPrefix: Optional[Prefix] = getSequenceComponentPrefix.toJava


    companion object {
        private const val PidKey = "PID"
        private const val AgentPrefixKey = "agentPrefix"
        private const val SequenceComponentPrefixKey = "sequenceComponentPrefix"
        private const val CswVersionKey = "csw-version"

        val empty: Metadata = Metadata(emptyMap())

        operator fun invoke(): Metadata = Metadata(emptyMap())
    }
}
