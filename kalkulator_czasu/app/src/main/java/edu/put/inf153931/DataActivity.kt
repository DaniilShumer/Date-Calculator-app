package edu.put.inf153931

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import java.util.Calendar
import java.util.Locale

class DataActivity : AppCompatActivity() {
    private var Year1 = 0
    private var Month1 = 0
    private var Day1 = 0
    private var Year2 = 0
    private var Month2 = 0
    private var Day2 = 0
    private var answerDatePicker: DatePicker? = null
    private var daysBetweenDates: EditText? = null
    private var workingDays: TextView? = null
    private var calendar: Calendar = Calendar.getInstance()

    private var inputMethodManager: InputMethodManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Locale.setDefault(Locale.UK)
        setContentView(R.layout.activity_data)
        inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        val addDaysButton = findViewById<Button>(R.id.add_days_button)
        val subDaysButton = findViewById<Button>(R.id.sub_days_button)
        addDaysButton.setOnClickListener {
            addDays(daysBetweenDates?.editableText.toString())
        }
        subDaysButton.setOnClickListener {
            subDays(daysBetweenDates?.editableText.toString())
        }

        daysBetweenDates = findViewById(R.id.days_between_dates)
        daysBetweenDates?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE)
                clearFocus(daysBetweenDates!!)

            false
        }

        workingDays = findViewById(R.id.working_days)

        val mainDatePicker = findViewById<DatePicker>(R.id.main_data_picker)
        answerDatePicker = findViewById(R.id.answer_data_picker)

        Year1 = mainDatePicker.year
        Month1 = mainDatePicker.month + 1
        Day1 = mainDatePicker.dayOfMonth
        Year2 = answerDatePicker!!.year
        Month2 = answerDatePicker!!.month + 1
        Day2 = answerDatePicker!!.dayOfMonth

        getCountDays(Year1, Month1, Day1, Year2, Month2, Day2)

        mainDatePicker.setOnDateChangedListener { _, year, month, day ->
            Year1 = year
            Month1 = month + 1
            Day1 = day
            getCountDays(Year1, Month1, Day1, Year2, Month2, Day2)
        }

        answerDatePicker!!.setOnDateChangedListener { _, year, month, day ->
            Year2 = year
            Month2 = month + 1
            Day2 = day
            getCountDays(Year1, Month1, Day1, Year2, Month2, Day2)
        }
    }

    private fun getCountDays(Year1: Int, Month1: Int, Day1: Int, _Year2: Int, _Month2: Int, _Day2: Int): Int
    {
        var TotalDays = 0
        var Weekends = 0
        var Holidays = 0
        var EasterDays = 0
        var dayWeek: Int
        var EasterDay1: Pair<Int, Int>
        var EasterDay2: Pair<Int, Int>
        var MonthLength: Int
        var exitFlag = false
        var selectedYear = Year1
        var selectedMonth = Month1
        var selectedDay = Day1
        var Year2 = _Year2
        var Month2 = _Month2
        var Day2 = _Day2

        if (selectedYear > Year2)
            TotalDays = -1
        else if (selectedYear == Year2){
            if (selectedMonth > Month2)
                TotalDays = -1
            else if (selectedMonth == Month2) {
                if (selectedDay > Day2)
                    TotalDays = -1
            }
        }

        if (TotalDays == -1) {
            selectedYear = Year2.also { Year2 = selectedYear }
            selectedMonth = Month2.also { Month2 = selectedMonth }
            selectedDay = Day2.also { Day2 = selectedDay }
            Log.w("timestamp", "SWAP")
        }

        TotalDays = 0
        calendar.set(selectedYear, selectedMonth - 1, selectedDay)
        dayWeek = calendar.get(Calendar.DAY_OF_WEEK)

        do {
            EasterDay1 = isEasterDate1(selectedYear)
            EasterDay2 = isEasterDate2(EasterDay1)
            do {
                MonthLength = getMonthLength(selectedMonth, selectedYear)
                do {
                    if (selectedYear == Year2 && selectedMonth == Month2 && selectedDay == Day2)
                    {
                        exitFlag = true
                        break
                    }
                    if (dayWeek == 1 || dayWeek == 7)
                        Weekends++
                    else {
                        if (selectedMonth == 1 || selectedMonth == 5 || selectedMonth == 8 ||
                            selectedMonth == 11 || selectedMonth == 12)
                            Holidays += isHolidays(selectedDay, selectedMonth)

                        if (selectedMonth in 3..5 && dayWeek == 2 && EasterDay1.second == selectedMonth &&
                            EasterDay1.first == selectedDay)
                            EasterDays++

                        if (selectedMonth in 4..7 && dayWeek == 5 && EasterDay2.second == selectedMonth &&
                            EasterDay2.first == selectedDay)
                            EasterDays++
                    }
                    if (dayWeek++ >= 7)
                        dayWeek = 1
                    TotalDays++
                } while (selectedDay++ < MonthLength)
                selectedDay = 1
                if (selectedMonth++ >= 12)
                    break
            } while (!exitFlag)
            selectedMonth = 1
            selectedYear++
        } while (!exitFlag)

        Log.i("timestamp", "Weekends: $Weekends")
        Log.i("timestamp", "Easter: $EasterDays")
        daysBetweenDates?.setText(TotalDays.toString())
        workingDays?.text = "Working days between dates: ${TotalDays - Weekends - Holidays - EasterDays}"
        return TotalDays
    }

    private fun addDays(count: String)
    {
        var countDays: Int = count.toInt()
        var selectedYear = Year1
        var selectedMonth = Month1
        var selectedDay = Day1
        var MonthLength: Int
        var exitFlag = false
        clearFocus(daysBetweenDates!!)

        do {
            do {
                MonthLength = getMonthLength(selectedMonth, selectedYear)
                do {
                    if (countDays-- == 0)
                    {
                        answerDatePicker!!.updateDate(selectedYear, selectedMonth - 1, selectedDay)
                        exitFlag = true
                        break
                    }
                } while (selectedDay++ < MonthLength)
                selectedDay = 1
                if (selectedMonth++ >= 12)
                    break
            } while (!exitFlag)
            selectedMonth = 1
            selectedYear++
        } while (!exitFlag)
    }

    private fun subDays(count: String)
    {
        var countDays: Int = count.toInt()
        var selectedYear = Year1
        var selectedMonth = Month1
        var selectedDay = Day1
        var exitFlag = false
        clearFocus(daysBetweenDates!!)

        do {
            do {
                do {
                    if (countDays-- == 0)
                    {
                        answerDatePicker!!.updateDate(selectedYear, selectedMonth - 1, selectedDay)
                        exitFlag = true
                        break
                    }
                } while (selectedDay-- > 1)
                if (selectedMonth-- <= 1)
                    break
                selectedDay = getMonthLength(selectedMonth, selectedYear)
            } while (!exitFlag)
            selectedMonth = 12
            selectedDay = getMonthLength(selectedMonth, --selectedYear)
        } while (!exitFlag)
    }

    private fun getMonthLength(month: Int, year: Int): Int
    {
        val ans: Int
        val bigMonth = setOf(1, 3, 5, 7, 8, 10, 12)
        val smallMonth = setOf(4, 6, 9, 11)

        if (month in bigMonth)
            ans = 31
        else if (month in smallMonth)
            ans = 30
        else
            if ((year % 100 == 0) && (year % 1000 != 0))
                if (year % 400 == 0)
                    ans = 29
                else
                    ans = 28
            else if (year % 4 == 0)
                ans = 29
            else
                ans = 28

        return ans
    }

    private fun isHolidays(day: Int, month: Int): Int
    {
        val holidays = listOf(
            Pair(1, 1),
            Pair(6, 1),
            Pair(1, 5),
            Pair(3, 5),
            Pair(15, 8),
            Pair(1, 11),
            Pair(11, 11),
            Pair(25, 12),
            Pair(26, 12)
        )

        for (holiday in holidays)
            if (holiday.second == month && holiday.first == day)
                return 1

        return 0
    }

    private fun isEasterDate1(year: Int): Pair<Int, Int>
    {
        val a: Int = year % 19
        val b: Int = year / 100
        val c: Int = year % 100
        val d: Int = b / 4
        val e: Int = b % 4
        val f: Int = (b + 8) / 25
        val g: Int = (b - f + 1) / 3
        val h: Int = (19 * a + b - d - g + 15) % 30
        val i: Int = c / 4
        val k: Int = c % 4
        val l: Int = (32 + 2 * e + 2 * i - h - k) % 7
        val m: Int = (a + 11 * h + 22 * l) / 451
        val p: Int = (h + l - 7 * m + 114)
        var day = p % 31 + 1
        var month = p / 31

        if ((month == 3 && day == 31) || (month == 4 && day == 30)) {
            month++
            day = 1
        } else
            day++
        return Pair(day, month)
    }

    private fun isEasterDate2(EasterDate1: Pair<Int, Int>): Pair<Int, Int>
    {
        var days = 59 + EasterDate1.first
        var month = EasterDate1.second

        while (days > 0)
            days -= getMonthLength(month++, 2000)

        return Pair(days + getMonthLength(--month, 200), month)
    }



    private fun clearFocus(view: View)
    {
        inputMethodManager!!.hideSoftInputFromWindow(view.windowToken, 0)
        view.clearFocus()
    }
}


