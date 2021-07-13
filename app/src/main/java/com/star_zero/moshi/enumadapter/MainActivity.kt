package com.star_zero.moshi.enumadapter

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi

@JsonClass(generateAdapter = true)
data class Sample(
    @Json(name = "id")
    val id: Int,
    @Json(name = "name")
    val name: String,
    @Json(name = "type")
    val type: Type,
    @Json(name = "kind")
    val kind: Kind,
)

@JsonClass(generateAdapter = false)
enum class Type {
    @Json(name = "a")
    A,

    @Json(name = "b")
    B,

    @MoshiEnumFallback
    @Json(name = "unknown")
    UNKNOWN
}

@JsonClass(generateAdapter = false)
enum class Kind {
    @MoshiEnumFallback
    @Json(name = "foo")
    Foo,

    @Json(name = "bar")
    Bar,
}

class MainActivity : AppCompatActivity() {

    private val moshi = Moshi.Builder()
        .add(MoshiEnumAdapterFactory())
        .build()

    private val moshiAdapter = moshi.adapter(Sample::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.button_to_json).setOnClickListener {
            toJson()
        }

        findViewById<Button>(R.id.button_from_json).setOnClickListener {
            fromJson()
        }
    }

    private fun toJson() {
        val sample = Sample(1, "sample", Type.A, Kind.Bar)
        val json = moshiAdapter.toJson(sample)
        Log.d(TAG, "json = $json")
    }

    private fun fromJson() {
        val json1 = "{\"id\":1,\"name\":\"sample\",\"type\":\"a\",\"kind\":\"bar\"}"
        val sample1 = moshiAdapter.fromJson(json1)

        // unknown value
        val json2 = "{\"id\":1,\"name\":\"sample\",\"type\":\"c\",\"kind\":\"piyo\"}"
        val sample2 = moshiAdapter.fromJson(json2)

        Log.d(TAG, "sample1 = $sample1")
        Log.d(TAG, "sample2 = $sample2")
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}