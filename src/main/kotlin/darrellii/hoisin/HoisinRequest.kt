package darrellii.hoisin

internal data class HoisinRequestParamArray(
    val id: String,
    val jsonrpc: String,
    val method: String,
    val params: List<Any?>?
)

internal data class HoisinRequest(
    val id: String,
    val jsonrpc: String,
    val method: String,
    val params: Map<String, Any?>?
)