package com.mc7.mystoryapp.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.mc7.mystoryapp.R

class MyEditTextEmail : AppCompatEditText {
    private lateinit var showEmailImg: Drawable
    private lateinit var trueBackground: Drawable
    private lateinit var falseBackground: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun init() {
        showEmailImg =
            ContextCompat.getDrawable(
                context, R.drawable.baseline_email_24
            ) as Drawable
        trueBackground =
            ContextCompat.getDrawable(
                context, R.drawable.custom_input
            ) as Drawable
        falseBackground =
            ContextCompat.getDrawable(
                context, R.drawable.custom_input_false
            ) as Drawable

        background = trueBackground
        setPadding(30, 45, 30, 45)

        //show icon/img btn
        setButtonDrawables()

        addTextChangedListener(onTextChanged = {s, _, _, _ ->
            if (isTextEmailAddress()){
                background = trueBackground
                error = null
            }else{
                background = falseBackground
                error = context.getString(R.string.error_email)
            }
        })
    }

    private fun isTextEmailAddress(): Boolean{
        return Patterns.EMAIL_ADDRESS.matcher(text.toString()).matches()
    }

    //Konfigurasi icon/img in button
    private fun setButtonDrawables(
        startOfTheText: Drawable = showEmailImg,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }
}