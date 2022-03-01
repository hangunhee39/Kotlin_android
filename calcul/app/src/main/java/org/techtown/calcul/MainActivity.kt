package org.techtown.calcul

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.room.Room
import org.techtown.calcul.model.History
import org.w3c.dom.Text
import java.lang.NumberFormatException

class MainActivity : AppCompatActivity() {

    private val expressonTextView: TextView by lazy {
        findViewById<TextView>(R.id.expressionTextView)
    }
    private val resultTextView: TextView by lazy {
        findViewById<TextView>(R.id.resultTextView)
    }
    private val historyLayout: View by lazy {   //constraint 안하고 view로 한 이유 : visialbe 기능이 있어서
        findViewById<View>(R.id.historyLayout)
    }
    private val historyLinearLayout: LinearLayout by lazy {
        findViewById<LinearLayout>(R.id.historyLinearLayout)
    }
    lateinit var db: AppDatabase

    private var isOperator = false
    private var hasOperator = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "historyDB").build()
    }

    fun buttonClicked(v: View) {
        when (v.id) {
            R.id.button0 -> numberButtonClicked("0")
            R.id.button1 -> numberButtonClicked("1")
            R.id.button2 -> numberButtonClicked("2")
            R.id.button3 -> numberButtonClicked("3")
            R.id.button4 -> numberButtonClicked("4")
            R.id.button5 -> numberButtonClicked("5")
            R.id.button6 -> numberButtonClicked("6")
            R.id.button7 -> numberButtonClicked("7")
            R.id.button8 -> numberButtonClicked("8")
            R.id.button9 -> numberButtonClicked("9")
            R.id.buttonPlus -> operatorButtonClicked("+")
            R.id.buttonMulti -> operatorButtonClicked("X")
            R.id.buttonDivider -> operatorButtonClicked("/")
            R.id.buttonModulo -> operatorButtonClicked("%")
            R.id.buttonMinus -> operatorButtonClicked("-")
        }
    }

    private fun numberButtonClicked(number: String) {
        if (isOperator) {       //연산자가 있는 경우 띄어쓰기로 split할 부분 나누기 (연산자)
            expressonTextView.append(" ")
        }
        isOperator = false

        val expresstionText = expressonTextView.text.split(" ")

        if (expresstionText.isNotEmpty() && expresstionText.last().length >= 15) {
            Toast.makeText(this, "15자리 까지만 사용할 수 있습니다.", Toast.LENGTH_SHORT).show()
            return
        } else if (expresstionText.last().isEmpty() && number == "0") {
            Toast.makeText(this, "0은 제일 앞에 올 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        expressonTextView.append(number)
        //바로바로 결과값 나오게
        resultTextView.text = calculateExpression()
    }

    private fun operatorButtonClicked(oparator: String) {

        //처음 부터 연산자를 넣은 경우 실행x
        if (expressonTextView.text.isEmpty()) {
            return
        }
        when {
            isOperator -> {              //연산자를 연속으로 누룬 경우
                val text = expressonTextView.text.toString()
                expressonTextView.text = text.dropLast(1) + oparator
            }
            hasOperator -> {             //(숫자 + 연산자 + 숫자 )상테에서 또 연산자를 누른 경우
                Toast.makeText(this, "연산자는 한번만 사용할 수 있습니다.", Toast.LENGTH_SHORT).show()
                return
            }
            else -> {
                expressonTextView.append(" $oparator")  //띄어쓰기 하고 연산자 넣기(첫번째 숫자 split 부분 생성)
            }
        }
        //연산자는 초록색으로 표시하기
        val ssb = SpannableStringBuilder(expressonTextView.text)
        ssb.setSpan(
            ForegroundColorSpan(getColor(R.color.green)),
            expressonTextView.text.length - 1,
            expressonTextView.text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        expressonTextView.text = ssb

        isOperator = true
        hasOperator = true
    }

    fun resultButtonClicked(v: View) {
        val expresstionTexts = expressonTextView.text.split(" ")

        //아무것도 안한경우 또는 숫자 한개만 입력한경우
        if (expressonTextView.text.isEmpty() || expresstionTexts.size == 1) {
            return
        }
        //수식까지만 입력한 경우
        if (expresstionTexts.size != 3 && isOperator) {
            Toast.makeText(this, "아직 안성되지 않은 수식입니다.", Toast.LENGTH_SHORT).show()
            return
        }
        //숫자가 아닌경우
        if (expresstionTexts[0].isNumber().not() || expresstionTexts[2].isNumber().not()) {
            Toast.makeText(this, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val expressText = expressonTextView.text.toString()
        val resultText = calculateExpression()

        //todo 디비에 넣어주는 부문
        Thread(Runnable {
            db.historyDao().insertHistory(History(null, expressText, resultText))
        }).start()

        //결과값을 expresson으로 나오게
        resultTextView.text = ""
        expressonTextView.text = resultText

        isOperator = false
        hasOperator = false
    }

    private fun calculateExpression(): String {
        val expresstionTexts = expressonTextView.text.split(" ")

        if (hasOperator.not() || expresstionTexts.size != 3) {
            return ""                     //연산자가 없거나 (숫자 +연산자 +숫자)를 입력안한경우
        } else if (expresstionTexts[0].isNumber().not() || expresstionTexts[2].isNumber().not()) {
            return ""                      //숫자가 아닌경우
        }

        val exp1 = expresstionTexts[0].toBigInteger()
        val exp2 = expresstionTexts[2].toBigInteger()
        val op = expresstionTexts[1]

        return when (op) {
            "+" -> (exp1 + exp2).toString()
            "-" -> (exp1 - exp2).toString()
            "/" -> (exp1 / exp2).toString()
            "%" -> (exp1 % exp2).toString()
            "X" -> (exp1 * exp2).toString()
            else -> ""
        }
    }

    fun clearButtonClicked(v: View) {
        expressonTextView.text = ""
        resultTextView.text = ""

        isOperator = false
        hasOperator = false

    }

    fun historyButtonClicked(v: View) {
        historyLayout.isVisible = true
        historyLinearLayout.removeAllViews()

        Thread(Runnable {
            db.historyDao().getAll().reversed().forEach{
                //그 히스토리를 보여줘야 함으로 매인쓰레드로 열어야 함 runOnUiThread
                runOnUiThread {
                    //history_row.xml 파일 연결 하고 DB에서 데이터 역순으로 가져옴
                    val historyView =LayoutInflater.from(this).inflate(R.layout.history_row,null,false)
                    historyView.findViewById<TextView>(R.id.expressionTextView).text = it.expression
                    historyView.findViewById<TextView>(R.id.resultTextView).text = "= ${it.result}"

                    //뷰 붙이기
                    historyLinearLayout.addView(historyView)
                }
            }
        }).start()

    }

    fun closeHistoryButtonClicked(v: View) {
        historyLayout.isVisible = false
    }

    fun historyClearButtonClicked(v: View) {
        historyLinearLayout.removeAllViews()

        Thread(Runnable {
            db.historyDao().deleteAll()
        }).start()
    }


}

//숫자인지 확인하는 isNumber함수 (확장함수)
fun String.isNumber(): Boolean {
    return try {
        this.toBigInteger()
        true
    } catch (e: NumberFormatException) {
        false
    }
}