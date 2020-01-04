# RetrofitUtils [简体中文](https://github.com/zeropercenthappy/RetrofitUtils/blob/master/README_CN.md)

## Download

### Step 1. 

Add it in your root build.gradle at the end of repositories:

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

### Step 2. 

Add the dependency

```
dependencies {
    implementation 'com.github.zeropercenthappy:RetrofitUtils:1.1.1'
}
```

## Usage

### RetrofitBuilder

```kotlin
val retrofit = RetrofitBuilder()
    .baseUrl(url)
    // option: if you want to disable cookies manager (lib will handle cookies defaualt, if you set it to false, remember to handle it yourself)
    .handleCookie(false)
    // option: if you have extra quest params to add
    .addParam(key, value)
    // option: if you have custom header to add
    .addHeader(key, value)
    // option: if you have other interceptor to add
    .addInterceptor(yourInterceptor)
    // option: if you want to define connect timeout (default is 10 sec)
    .connectTimeout(10_000)
    // option: if you want to define read timeout (default is 10 sec)
    .readTimeout(10_000)
    // option: if you want to define write timeout (default is 10 sec)
    .writeTimeout(10_000)
    // option: if you have converter to add, like GsonConverter or others
    .addConverterFactory(GsonConverterFactory.create())
    // option: if you have call adapter to add
    .addCallAdapterFactory(yourCallAdapter)
    .build(context)
val api =retrofit.create(Api::class.java)
```

### RequestBodyBuilder

Create `text/plain` body:

```kotlin
RequestBodyBuilder.createText(content)
```

Create `multilpart body part`: (It will handle file mimetype itself, just put a map with key and file)

```kotlin
RequestBodyBuilder.createMultipartBodyPartList(fileMap)
```

Create `application/json` body:

```
RequestBodyBuilder.createJson(json)
```

## Other

### StringConverter

Here offer a `StringConverter` for simple request that with response is String:

```kotlin
addConverterFactory(StringConverterFactory())
```

Then you can define a request with a String response:

```kotlin
@FormUrlEncoded
@POST(Url.POST)
fun query(@Field("name") name: String): Call<String>
```

### CoroutineCallAdapter

Here offer a `CoroutineConverter` that support request in coroutine scope which just need several lines code:

```kotlin
addCallAdapterFactory(CoroutineCallAdapterFactory())
```

Then you can define a requst with `CoroutineCall` response:

```kotlin
@FormUrlEncoded
@POST(Url.POST)
fun query(@Field("name") name: String): CoroutineCall<QueryBean>
```

Start this request in a coroutine scope:

```kotlin
try {
    val coroutineCall = api.query(name)
    val queryBean = coroutineCall.request()
    // request success, do your other logic work
} catch (e: Exception) {
    // request fail or cancel
    e.printStackTrace()
}
```

BTW, since Retrofit 2.6.0, it support coroutine natural, you can define a request with `suspend`, and response is just the result :

```kotlin
@FormUrlEncoded
@POST(Url.POST)
suspend fun query(@Field("name") name: String): QueryBean
```

Then start this quest in a coroutine scope:

```kotlin
try {
    val queryBean = api.query(name)
    // request success, do your other logic work
} catch (e: Exception) {
    // request fail or cancel
    e.printStackTrace()
}
```

You can learn more by yourself if  you interested :)

### Log Interceptor

[OkHttpInterceptor](https://github.com/zeropercenthappy/OkHttpLogInterceptor)