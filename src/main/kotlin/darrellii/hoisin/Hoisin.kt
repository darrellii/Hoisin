package darrellii.hoisin

import com.google.gson.Gson
import darrellii.hoisin.HoisinException.Companion.INTERFACE_REQUIRED_EXCEPTION
import darrellii.hoisin.HoisinException.Companion.RETURNTYPE_NOT_SUPPORTED_EXCEPTION
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.reflect.Method
import java.lang.reflect.Parameter
import java.lang.reflect.Proxy

class Hoisin(
        private val baseUrl: String,
        var paramsAreArrays: Boolean = false,
        private val client: OkHttpClient = OkHttpClient()
) {

    private val gson: Gson = Gson()
    private var callId = 0

    fun <T : Any> get(clazz: Class<T>, paramsAreArrays: Boolean = false): T {
        this.paramsAreArrays = paramsAreArrays
        return this[clazz]
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T : Any> get(clazz: Class<T>): T {
        if (!clazz.isInterface) throw INTERFACE_REQUIRED_EXCEPTION

        return Proxy.newProxyInstance(
                clazz.classLoader,
                arrayOf(clazz)
        ) { proxy, method, args: Array<Any?>? ->

            if (method.returnType != Call::class.java)
                throw RETURNTYPE_NOT_SUPPORTED_EXCEPTION(method.returnType)

            Call(gson) {
                // This is on IO Thread.
                val jsonRaw: String = gson.toJson(
                        if (paramsAreArrays) {
                            HoisinRequestParamArray(
                                    jsonrpc = "2.0",
                                    id = "${callId++}",
                                    method = method.hoisinName,
                                    params = args?.toList()
                            )
                        } else {
                            HoisinRequest(
                                    jsonrpc = "2.0",
                                    id = "${callId++}",
                                    method = method.hoisinName,
                                    params = method.parameters mapTo args
                            )
                        }
                )

                client hoisinCall (jsonRaw postTo baseUrl + clazz.hoisinName)
            }
        } as T
    }
}

private val <T> Class<T>.hoisinName: String
    get() = (annotations.filterIsInstance<Clazz>().firstOrNull()?.name ?: simpleName)
            .toLowerCase().trim()

private val Parameter.hoisinName: String
    get() = annotations.filterIsInstance<Param>().firstOrNull()?.name
            ?.takeIf { it.isNotEmpty() && it.isNotBlank() }
            ?: name
private val Method.hoisinName: String
    get() = annotations.filterIsInstance<Function>().firstOrNull()?.name
            ?.takeIf { it.isNotEmpty() && it.isNotBlank() }
            ?: name

private infix fun String.postTo(url: String) = Request.Builder()
        .url(url)
        .post(toRequestBody("application/json; charset=utf-8".toMediaType()))
        .build()

private infix fun OkHttpClient.hoisinCall(request: Request) = newCall(request).execute()

private infix fun Array<Parameter>.mapTo(args: Array<Any?>?) =
        args?.mapIndexed { i, arg -> this[i].hoisinName to arg }?.toMap()