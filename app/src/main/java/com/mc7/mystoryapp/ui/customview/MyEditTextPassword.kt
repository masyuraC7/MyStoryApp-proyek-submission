package com.mc7.mystoryapp.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.mc7.mystoryapp.R

class MyEditTextPassword : AppCompatEditText, View.OnTouchListener {
    private lateinit var showVisibleOnImg: Drawable
    private lateinit var showVisibleOffImg: Drawable
    private lateinit var showLockImg: Drawable
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
        // Menginisialisasi gambar button
        showVisibleOnImg =
            ContextCompat.getDrawable(
                context, R.drawable.baseline_visibility_24
            ) as Drawable
        showVisibleOffImg =
            ContextCompat.getDrawable(
                context, R.drawable.baseline_visibility_off_24
            ) as Drawable
        showLockImg =
            ContextCompat.getDrawable(
                context, R.drawable.baseline_lock_24
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

        // Menambahkan aksi kepada eye button
        setOnTouchListener(this)

        addTextChangedListener(onTextChanged = {s, _, _, _ ->
            if (s.toString().length < 8) {
                background = falseBackground
                setError(context.getString(R.string.error_password_8), null)
            } else {
                background = trueBackground
                error = null
            }
        })
    }

    private fun showVisibleOnButton(){
        setButtonDrawables(endOfTheText = showVisibleOnImg)
    }
    private fun showVisibleOffButton(){
        setButtonDrawables(endOfTheText = showVisibleOffImg)
    }

    //Konfigurasi icon/img in button
    private fun setButtonDrawables(
        startOfTheText: Drawable = showLockImg,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable = showVisibleOffImg,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val visibleButtonStart: Float
            val visibleButtonEnd: Float
            var isVisibleButtonClicked = false

            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                visibleButtonEnd = (showVisibleOnImg.intrinsicWidth + paddingStart).toFloat()

                when {
                    event.x < visibleButtonEnd -> isVisibleButtonClicked = true
                }
            } else {
                visibleButtonStart =
                    (width - paddingEnd - showVisibleOnImg.intrinsicWidth).toFloat()

                when {
                    event.x > visibleButtonStart -> isVisibleButtonClicked = true
                }
            }

            //akan visible jika icon visible ditekan dan ditahan
            if (isVisibleButtonClicked) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        showVisibleOnButton()

                        transformationMethod = null

                        return true
                    }

                    MotionEvent.ACTION_UP -> {
                        showVisibleOffButton()

                        transformationMethod = PasswordTransformationMethod.getInstance()

                        return true
                    }

                    else -> return false
                }
            } else return false
        }
        return false
    }
}