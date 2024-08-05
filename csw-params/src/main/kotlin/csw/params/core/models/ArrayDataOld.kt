package csw.params.core.models

/**
 * A top level key for a parameter set representing an array like collection.
 *
 * @param data input array
 */
data class ArrayDataOld<T>(val data: Array<T>) {

    /**
     * An Array of values this parameter holds
     */
    fun values(): Array<T> = data

    /**
     * A Java helper that returns an Array of values this parameter holds
     */
    //fun jValues: java.util.List<T> = data.asJava


    /**
     * A comma separated string representation of all values this ArrayData holds
     */
    override fun toString(): String = data.joinToString("(", ",", ")")

    companion object {

        /**
         * Create an ArrayData from one or more values
         *
         * @param values an Array of one or more values
         * @tparam T the type of values
         * @return an instance of ArrayData
         */
        fun <T> fromArray(values: Array<T>): ArrayDataOld<T> = ArrayDataOld(values)

        /**
         * Create an ArrayData from one or more values
         *
         * @param rest one or more values
         * @tparam T the type of values
         * @return an instance of ArrayData
         */
        fun <T> fromArrays(vararg rest: T): ArrayDataOld<out T> {
            //implicit val ct: ClassTag[T] = ClassTag[T](first.getClass)
            return ArrayDataOld(rest)
        }
    }
}
