package csw.params.core.models

/**
 * A top level key for a parameter set representing an array like collection.
 *
 * @param data input array
 */
data class ArrayData(val data: DoubleArray) {

    /**
     * An Array of values this parameter holds
     */
    fun values(): DoubleArray = data

    /**
     * A Java helper that returns an Array of values this parameter holds
     */
    //fun jValues: java.util.List<T> = data.asJava

    fun set(data: Array<Number>) {
        println("Data: $data")
    }


    /**
     * A comma separated string representation of all values this ArrayData holds
     */
    override fun toString(): String = data.joinToString("(", ",", ")")

    companion object {

        fun fromArray(data: FloatArray): ArrayData {
            val x = ArrayData(data.map { it.toDouble() }.toDoubleArray() )
            println("da: $x")
            return x
        }

        /**
         * Create an ArrayData from one or more values
         *
         * @param values an Array of one or more values
         * @tparam T the type of values
         * @return an instance of ArrayData
         */
        fun fromArray(values: Array<Number>): ArrayData= ArrayData(values.map { it.toDouble()}.toDoubleArray())

        /**
         * Create an ArrayData from one or more values
         *
         * @param rest one or more values
         * @tparam T the type of values
         * @return an instance of ArrayData
         */
        /*
        fun <T> fromArrays(vararg rest: Number): ArrayData {
            //implicit val ct: ClassTag[T] = ClassTag[T](first.getClass)
            return ArrayData(arrayOf(*rest))
        }

         */
    }
}
