# RetrofitUtils

## 下载

### 步骤1. 

添加以下配置到你项目根目录的`build.gradle`

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

### 步骤2. 

在你项目module中的`build.gradle`中添加依赖

```
dependencies {
    implementation 'com.github.zeropercenthappy:RetrofitUtils:1.1.1'
}
```

## 使用

### RetrofitBuilder

```kotlin
val retrofit = RetrofitBuilder()
    .baseUrl(url)
    // 可选: 如果你想禁用cookies管理器(框架将自动处理cookies，如果你设置为false，记得自己处理)
    .handleCookie(false)
    // 可选: 如果你有额外的请求参数要添加
    .addParam(key, value)
    // 可选: 如果你有额外的header要添加
    .addHeader(key, value)
    // 可选: 如果你有额外的拦截器要添加
    .addInterceptor(yourInterceptor)
    // 可选: 如果你要自定义connect超时时间（默认10秒）
    .connectTimeout(10_000)
    // 可选: 如果你要自定义read超时时间（默认10秒）
    .readTimeout(10_000)
    // 可选: 如果你要自定义write超时时间（默认10秒）
    .writeTimeout(10_000)
    // 可选: 如果你有Converter要添加，比如GsonConverter
    .addConverterFactory(GsonConverterFactory.create())
    // 可选: 如果你有CallAdapter要添加
    .addCallAdapterFactory(yourCallAdapter)
    .build(context)
val api =retrofit.create(Api::class.java)
```

### RequestBodyBuilder

创建`text/plain` body

```kotlin
RequestBodyBuilder.createText(content)
```

创建`multilpart body part`: ( 框架会自动处理文件的mimetype，只需要传入带有接口规定的key和你要传的文file的map即可)

```kotlin
RequestBodyBuilder.createMultipartBodyPartList(fileMap)
```

创建`application/json` body

```
RequestBodyBuilder.createJson(json)
```

## 其它

### StringConverter

提供了`StringConverter` ，可以用于当希望结果直接是String，便于开发者自行处理时：

```kotlin
addConverterFactory(StringConverterFactory())
```

定义接口，返回类型为Call\<String\>

```kotlin
@FormUrlEncoded
@POST(Url.POST)
fun query(@Field("name") name: String): Call<String>
```

### CoroutineCallAdapter

提供了`CoroutineConverter` ，支持在协程内进行请求，整个请求只需几行代码：

```kotlin
addCallAdapterFactory(CoroutineCallAdapterFactory())
```

定义接口，返回类型为`CoroutineCall<T>` 

```kotlin
@FormUrlEncoded
@POST(Url.POST)
fun query(@Field("name") name: String): CoroutineCall<QueryBean>
```

然后在协程中进行请求

```kotlin
try {
    val coroutineCall = api.query(name)
    val queryBean = coroutineCall.request()
    // 请求成功，做你自己的后续逻辑处理
} catch (e: Exception) {
    // 请求失败或取消
    e.printStackTrace()
}
```

顺带一提，从2.6.0的Retrofit开始，Retrofit已经天生支持协程内进行请求了，只需要在定义接口时加上`suspend`，返回类型直接是最终类型

```kotlin
@FormUrlEncoded
@POST(Url.POST)
suspend fun query(@Field("name") name: String): QueryBean
```

然后在协程中进行请求

```kotlin
try {
    val queryBean = api.query(name)
    // 请求成功，做你自己的后续逻辑处理
} catch (e: Exception) {
    // 请求失败或取消
    e.printStackTrace()
}
```

如果你感兴趣的话，可以自行查找资料了解更多用法 :)

### Log Interceptor

[OkHttpInterceptor](https://github.com/zeropercenthappy/OkHttpLogInterceptor)