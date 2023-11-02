package com.mc7.mystoryapp

import android.support.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.mc7.mystoryapp.ui.view.WelcomeActivity
import com.mc7.mystoryapp.ui.view.login.LoginActivity
import com.mc7.mystoryapp.ui.view.main.MainActivity
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginActivityTest {

    private val dummyEmail = "testivy@dico.com"
    private val dummyPassword = "testivy23"

    @get:Rule
    val activity = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }
    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun loginLogout_Success(){

        //login
        onView(withId(R.id.edt_email_login)).perform(typeText(dummyEmail), closeSoftKeyboard())
        onView(withId(R.id.edt_pass_login)).perform(typeText(dummyPassword), closeSoftKeyboard())

        Intents.init()
        onView(withId(R.id.btn_login)).check(matches(isDisplayed())).perform(click())

        onView(withText("Berhasil")).inRoot(isDialog()).check(matches(isDisplayed()))
        onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click())
        intended(IntentMatchers.hasComponent(MainActivity::class.java.name))

        // logout
        onView(withId(R.id.list_menu)).check(matches(isDisplayed())).perform(click())
        onView(withText(R.string.logout)).check(matches(isDisplayed()))
            .perform(click())
        onView(withText(R.string.confirm_logout)).inRoot(isDialog()).check(matches(isDisplayed()))
        onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click())
        intended(IntentMatchers.hasComponent(WelcomeActivity::class.java.name))
    }

}