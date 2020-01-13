package darrellii.hoisin

internal sealed class HoisinResponse {
    data class Success<SUCCESS : Any>(val id: String, val result: SUCCESS) : HoisinResponse()
    data class Fail<FAIL : Any>(val id: String, val error: Error<FAIL>) : HoisinResponse()
}

data class Error<DATA : Any> internal constructor(
    val code: Int,
    val message: String,
    val data: DATA?
)