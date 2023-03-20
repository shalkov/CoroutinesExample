package ru.shalkoff.coroutineexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import kotlinx.coroutines.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val titleTv = findViewById<TextView>(R.id.title_tv)

        titleTv.setOnClickListener {
            simple5()
        }
    }

    suspend fun doWork(name: String): String {
        //delay это специальная суспенд функция, которая приостанавливает выполнение корутины
        delay(1000)
        return "Done $name"
    }

    /**
     * Запускаем 100 корутин асинхронно.
     * По-факту, практически моментально(после задержки в 1 секунду) выполнятся все 100 раз функция
     */
    private fun simple1() {
        runBlocking {
            repeat(100) { number ->
                launch {
                    val result = doWork(number.toString())
                    Log.d("COR_TEST", result)
                }
            }
        }
    }

    /**
     * Запускаем 100 корутин синхронно.
     * Из-за того, что не использует lunch, suspend функции выполняются последовательно, друг за другом (не параллельно)
     * (При этом блокируется интерфейс)
     */
    private fun simple2() {
        runBlocking {
            repeat(100) { number ->
                val result = doWork(number.toString())
                Log.d("COR_TEST", result)
            }
        }
    }

    /**
     * Запускаем 100 корутин асинхронно, с помощью async
     */
    private fun simple3() {
        runBlocking {
            val coroutines: List<Deferred<String>> = List(100) { number ->
                async {
                    doWork(number.toString())
                }
            }
            coroutines.forEach {
                val result: String = it.await()
                Log.d("COR_TEST", result)
            }
        }
    }

    /**
     * Запускаем 100 корутин синхронно, с помощью async
     */
    private fun simple4() {
        runBlocking {
            val coroutines: List<Deferred<String>> = List(100) { number ->
                async(start = CoroutineStart.LAZY) {
                    doWork(number.toString())
                }
            }
            coroutines.forEach {
                val result: String = it.await()
                Log.d("COR_TEST", result)
            }
        }
    }

    /**
     * Запускаем 100 корутин асинхронно, с помощью lunch.
     * При этом выполняются все ЧЁТНЫЕ, а НЕ ЧЁТНЫЕ отменяются.
     */
    private fun simple5() {
        runBlocking {
            val coroutines: List<Job> = List(100) { number ->
               launch {
                   val result = doWork(number.toString())
                   Log.d("COR_TEST", result)
               }
            }
            coroutines.forEachIndexed { index, job ->
                if (index % 2 == 0) {
                    job.cancelAndJoin()
                } else {
                    job.start()
                }
            }
        }
    }
}