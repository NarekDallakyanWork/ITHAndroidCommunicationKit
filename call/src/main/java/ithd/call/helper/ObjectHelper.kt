package ithd.call.helper

import org.webrtc.SurfaceViewRenderer

class ObjectHelper private constructor() {
    companion object {
        /**
         * Verifies if the object is not null and returns it or throws a NullPointerException
         * with the given message.
         * @param <T> the value type
         * @param object the object to verify
         * @param message the message to use with the NullPointerException
         * @return the object itself
         * @throws NullPointerException if object is null
        </T> */
        fun <T> requireNonNull(`object`: T?, message: String?): T {
            if (`object` == null) {
                throw NullPointerException(message)
            }
            return `object`
        }

        /**
         * Compares two potentially null objects with each other using Object.equals.
         * @param o1 the first object
         * @param o2 the second object
         * @return the comparison result
         */
        fun equals(o1: Any?, o2: Any?): Boolean {
            return o1 == o2
        }

        /**
         * Returns the hashCode of a non-null object or zero for a null object.
         * @param o the object to get the hashCode for.
         * @return the hashCode
         */
        fun hashCode(o: Any?): Int {
            return o?.hashCode() ?: 0
        }

        /**
         * Compares two integer values similar to Integer.compare.
         * @param v1 the first value
         * @param v2 the second value
         * @return the comparison result
         */
        fun compare(v1: Int, v2: Int): Int {
            return Integer.compare(v1, v2)
        }

        /**
         * Compares two long values similar to Long.compare.
         * @param v1 the first value
         * @param v2 the second value
         * @return the comparison result
         */
        fun compare(v1: Long, v2: Long): Int {
            return java.lang.Long.compare(v1, v2)
        }

        /**
         * Validate that the given value is positive or report an IllegalArgumentException with
         * the parameter name.
         * @param value the value to validate
         * @param paramName the parameter name of the value
         * @return value
         * @throws IllegalArgumentException if bufferSize &lt;= 0
         */
        fun verifyPositive(value: Int, paramName: String): Int {
            require(value > 0) { "$paramName > 0 required but it was $value" }
            return value
        }

        /**
         * Validate that the given value is positive or report an IllegalArgumentException with
         * the parameter name.
         * @param value the value to validate
         * @param paramName the parameter name of the value
         * @return value
         * @throws IllegalArgumentException if bufferSize &lt;= 0
         */
        fun verifyPositive(value: Long, paramName: String): Long {
            require(value > 0L) { "$paramName > 0 required but it was $value" }
            return value
        }
    }

    /** Utility class.  */
    init {
        throw IllegalStateException("No instances!")
    }
}