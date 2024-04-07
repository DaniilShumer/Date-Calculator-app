package edu.put.inf153931

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class HMSActivity : AppCompatActivity(), TextView.OnEditorActionListener,
    View.OnFocusChangeListener, View.OnClickListener {

    private var H1operand: EditText? = null
    private var M1operand: EditText? = null
    private var S1operand: EditText? = null
    private var H2operand: EditText? = null
    private var M2operand: EditText? = null
    private var S2operand: EditText? = null
    private var clearTimeButton: Button? = null
    private var addTimeButton: Button? = null
    private var subTimeButton: Button? = null
    private var inputMethodManager: InputMethodManager? = null
    private lateinit var toast: Toast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hms)

        toast = Toast.makeText(applicationContext, "Negative result", Toast.LENGTH_LONG)
        inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        H1operand = findViewById(R.id.H1_operand)
        M1operand = findViewById(R.id.M1_operand)
        S1operand = findViewById(R.id.S1_operand)
        H2operand = findViewById(R.id.H2_operand)
        M2operand = findViewById(R.id.M2_operand)
        S2operand = findViewById(R.id.S2_operand)
        addTimeButton = findViewById(R.id.add_time)
        subTimeButton = findViewById(R.id.sub_time)
        clearTimeButton = findViewById(R.id.clear_time)

        addTimeButton?.setOnClickListener(this)
        subTimeButton?.setOnClickListener(this)
        clearTimeButton?.setOnClickListener(this)

        H1operand?.setOnEditorActionListener(this)
        H1operand?.onFocusChangeListener = this

        M1operand?.setOnEditorActionListener(this)
        M1operand?.onFocusChangeListener = this

        S1operand?.setOnEditorActionListener(this)
        S1operand?.onFocusChangeListener = this

        H2operand?.setOnEditorActionListener(this)
        H2operand?.onFocusChangeListener = this

        M2operand?.setOnEditorActionListener(this)
        M2operand?.onFocusChangeListener = this

        S2operand?.setOnEditorActionListener(this)
        S2operand?.onFocusChangeListener = this
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            v?.clearFocus()
            inputMethodManager!!.hideSoftInputFromWindow(v?.windowToken, 0)
        }

        return false
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (hasFocus)
            (v as EditText).editableText.clear()
        else{
            if ((v as EditText).editableText.isEmpty())
                (v as EditText).setText("0")
        }
    }

    override fun onClick(v: View?) {
        var h1: Int = H1operand?.editableText.toString().toInt()
        var m1: Int = M1operand?.editableText.toString().toInt()
        var s1: Int = S1operand?.editableText.toString().toInt()
        var h2: Int = H2operand?.editableText.toString().toInt()
        var m2: Int = M2operand?.editableText.toString().toInt()
        var s2: Int = S2operand?.editableText.toString().toInt()
        var S1Time: Int = h1 * 3600 + m1 * 60 + s1
        var S2Time: Int = h2 * 3600 + m2 * 60 + s2

        if (v?.id == R.id.sub_time)
            if (S2Time > S1Time) {
                if (toast.view!!.windowVisibility != View.VISIBLE)
                    toast.show()

                return
            }
            else
                S2Time = -S2Time

        H1operand?.setText("0")
        M1operand?.setText("0")
        S1operand?.setText("0")
        H2operand?.setText("0")
        M2operand?.setText("0")
        S2operand?.setText("0")

        if (v?.id != R.id.clear_time){
            var tmp = S1Time + S2Time
            H1operand?.setText((tmp / 3600).toString())
            tmp %= 3600
            M1operand?.setText((tmp / 60).toString())
            S1operand?.setText((tmp % 60).toString())
        }
    }
}