package com.hc.wanandroid


import com.hc.wanandroid.di.kotlinJson
import com.hc.wanandroid.net.NetResult
import kotlinx.serialization.decodeFromString
import org.jsoup.Jsoup
import org.junit.Test
import java.io.File
import java.time.LocalDate
import java.time.Month
import java.time.Year
import java.time.YearMonth
import java.util.*
import java.util.function.Supplier
import java.util.stream.Collector

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {


    @Test fun run() {
//        spSave(CoinInfo::class.java,"coinInfo")
//        ktJson()
    /*forEach {
            println(it.text())
            println(it.child(0).text())
            println()
        }*/
//        boss()
    }


    fun ktJson(){
//        val string = Files.readAllBytes(
//            Paths.get("C:\\Users\\JvmDev\\Desktop\\ASProject\\t\\WanAndroid\\app\\build\\tmp\\t.json")
//        ).toString(Charset.defaultCharset())

        val string = "{\"errorCode\":-1001,\"errorMsg\":\"请先登录！\"}"

        println("ktJson")
        println(kotlinJson.decodeFromString<NetResult<String>>(string))
    }

    fun 时间(){
        val range = LocalDate.of(2020, 2, 14)..LocalDate.of(2021, 8, 28)
        years(range).forEach { year ->
            println("$year")
            months(year,range).forEach{ yearMonth ->
                print("\t ${yearMonth.monthValue} :")
                println(days(yearMonth,range).joinToString(",") { "${it.dayOfMonth}" })
            }
        }
    }

    fun years(range: ClosedRange<LocalDate>):Array<Year>{
        val yearStart = Year.from(range.start)
        val yearEnd = Year.from(range.endInclusive)
        return Array(yearEnd.value - yearStart.value + 1){ Year.of(range.start.year + it) }
    }

    fun months(year: Year,range:ClosedRange<LocalDate>):Array<YearMonth>{
        val yearMonthRange = YearMonth.from(range.start)..YearMonth.from(range.endInclusive)
        return Month.values()
            .map { year.atMonth(it) }
            .filter { it in yearMonthRange }
            .toTypedArray()
    }

    fun days(month: YearMonth,range: ClosedRange<LocalDate>):List<LocalDate>{
        return Array(month.lengthOfMonth()){
            month.atDay(it+1)
        }.filter {
            it in range
        }
    }


    fun <T> dataDefVal(kClass:Class<T>){
        kClass.declaredFields.forEach {
            val v = when(it.type){
                Int::class.java->":Int = 0"
                Boolean::class.java->":Boolean = false"
                String::class.java->":String = \"\""
                List::class.java->":List<Any> = emptyList()"
                Long::class.java->":Long = 0L"
                else -> " "
            }
            println("val ${it.name}$v,")
        }

    }


    fun <T> spLoad(kClass:Class<T>){

        kClass.declaredFields.forEach {
            val v = when(it.type){
                Int::class.java->"getInt(\"${it.name}\",0)"
                Boolean::class.java->"getBoolean(\"${it.name}\",false)"
                String::class.java->"getString(\"${it.name}\",\"\")"
                Long::class.java->"getLong(\"${it.name}\",0L)"
                else -> ""
            }
            if (v.isNotEmpty())
            println("${it.name} = sp.$v,")
        }

    }

    fun <T> spSave(kClass:Class<T>,cName:String){
        kClass.declaredFields.forEach {
            val v = when(it.type){
                Int::class.java->"putInt(\"${it.name}\",${cName}.${it.name})"
                Boolean::class.java->"putBoolean(\"${it.name}\",${cName}.${it.name})"
                String::class.java->"putString(\"${it.name}\",${cName}.${it.name})"
                Long::class.java->"putLong(\"${it.name}\",${cName}.${it.name})"
                else -> ""
            }
            if (v.isNotEmpty())
                println(v)
        }

    }
}



