package darrellii.hoisin

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Response
import org.json.JSONObject

class Call internal constructor(
        private val gson: Gson,
        private val doPost: () -> Response
) {
    suspend fun <RESULT : Any, ERROR : Any> call(
            onFail: suspend (code: Int, rawJSON: String) -> Unit = { _, _ -> Unit },
            onError: suspend (Error<ERROR>) -> Unit = {},
            onResult: suspend (RESULT) -> Unit
    ): Unit = withContext(Dispatchers.IO) {
        with(doPost()) {
            val rawJson = body?.string() ?: "{}"

            val keySet = try {
                JSONObject(rawJson).keySet()
            } catch (e: Exception) {
                emptySet<String>()
            }
            when {
                keySet.contains("result") -> onResult(gson.deserializeResult(rawJson))
                keySet.contains("error") -> onError(gson.deserializeError(rawJson))
                else -> onFail(code, rawJson)
            }
        }
    }

    private fun <RESULT : Any> Gson.deserializeResult(rawJson: String) =
            fromJson<HoisinResponse.Success<RESULT>>(
                    rawJson,
                    HoisinResponse.Success::class.java
            ).result

    private fun <ERROR : Any> Gson.deserializeError(rawJson: String) =
            fromJson<HoisinResponse.Fail<ERROR>>(
                    rawJson,
                    HoisinResponse.Fail::class.java
            ).error
}