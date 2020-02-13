package darrellii.hoisin

class HoisinException(message: String) : Exception(message) {
    companion object {
        val INTERFACE_REQUIRED_EXCEPTION = HoisinException("Only Interfaces are supported")
        fun RETURNTYPE_NOT_SUPPORTED_EXCEPTION(clazz: Class<*>) = HoisinException(
                "Return type (${clazz.simpleName}) is not supported. " +
                        "Try using darrellii.hoisin.Call"
        )
    }
}