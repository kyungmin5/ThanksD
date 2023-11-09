package com.example.thanksd.httpconnection

import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

class HttpURLConn {
    // Post
    fun POST(mUrl: String, params: String): String? {
        try {
            // String으로 받아온 URL을 URL 객체로 생성
            val url = URL(mUrl)

            // HttpURLConnection을 열어줌
            val conn = url.openConnection() as HttpURLConnection

            // Timeout 설정
            conn.readTimeout = 10000
            conn.connectTimeout = 15000
            // 요청 방식 설정 (POST)
            conn.requestMethod = "POST"
            conn.doInput = true
            conn.doOutput = true

            // Accept-Charset 설정 (UTF-8)
            conn.setRequestProperty("Accept-Charset", "UTF-8")
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")

            // POST로 넘겨줄 파라미터를 OutputStream으로 전송
            conn.outputStream.use { os ->
                val outputInBytes = params.toByteArray(StandardCharsets.UTF_8)
                os.write(outputInBytes)
            }

            // 결과값 받아오기
            val response = StringBuilder()
            val resCode = conn.responseCode
            if(resCode == HttpURLConnection.HTTP_OK) {
                conn.inputStream.use { `is` ->
                    BufferedReader(InputStreamReader(`is`)).use { br ->
                        var line: String?
                        while (br.readLine().also { line = it } != null) {
                            response.append(line)
                            response.append('\r')
                        }
                    }
                }
                Log.d("HTTPResponse",response.toString().trim { it <= ' '})
                return response.toString().trim { it <= ' ' }
            }
            // 결과 처리

        } catch (e: Exception) {
            e.printStackTrace()
            // 오류 발생 시 로그 출력 후 null 반환
            Log.d("ERROR", "Exception Error")
        }
        return null
    }

    // GET
    fun GET(mUrl: String): String? {
        try {
            val url = URL(mUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.apply {
                requestMethod = "GET"
                connectTimeout = 15000
                readTimeout = 10000
                doInput = true
                doOutput = true
            }
            // 결과값 받아오기 수정 필요함
            val resCode = conn.responseCode
            val response = StringBuilder()

            if(resCode == HttpURLConnection.HTTP_OK) {
                conn.inputStream.use { `is` ->
                    BufferedReader(InputStreamReader(`is`)).use { br ->
                        var line: String?
                        while (br.readLine().also { line = it } != null) {
                            response.append(line)
                            response.append('\r')
                        }
                    }
                }
                Log.d("HTTPResponse",response.toString().trim { it <= ' '})
                return response.toString().trim { it <= ' ' }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("ERROR", "Check URL")
        }
        return null
    }

}