import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.OkHttpClient
import okhttp3.Request
import spark.ModelAndView
import spark.Spark
import spark.template.thymeleaf.ThymeleafTemplateEngine

fun main(args: Array<String>) {
    Spark.get("/list", { req, res ->
        animeModelView()
    }, ThymeleafTemplateEngine())

    Spark.get("/get", { req, res ->
        animeModelView(req.queryParams("date"))
    }, ThymeleafTemplateEngine())
}

fun animeModelView(date :String = "2018/1"): ModelAndView {
    val client = OkHttpClient()
    val request = Request.Builder()
            .url("http://api.moemoe.tokyo/anime/v1/master/" + date)
            .build()
    val response = client.newCall(request).execute()
    val json = response.body()?.string()
    // JSONパース
    val animeInfoList: List<AnimeInfo> = jacksonObjectMapper().readValue(json ?: "")

    // Thymeleaf
    val model = HashMap<String, Any>()
    // ページタイトルを設定
    model["mainTitle"] = "アニメタイトル一覧"
    // 一覧を設定
    val animeList = mutableListOf<String>()
    animeInfoList.forEach {
        animeList.add(it.title)
    }
    model["animeList"] = animeList

    return ModelAndView( model, "index")
}
