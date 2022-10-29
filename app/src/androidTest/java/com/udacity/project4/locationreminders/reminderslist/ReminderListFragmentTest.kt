package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest : AutoCloseKoinTest() {

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application


    @get:Rule
    val instantExecutor = InstantTaskExecutorRule()

    /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
     * at this step we will initialize Koin related code to be able to use it in out testing.
     */
    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = getApplicationContext()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            viewModel {
                SaveReminderViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(appContext) }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //Get our real repository
        repository = get()

        //clear the data to start fresh
        runBlocking {
            repository.deleteAllReminders()
        }
    }


    /**
     * floating action button test on click to navigate to save new reminder fragment
     */
    @Test
    fun buttonClick_navigateToSaveReminderFragment() {
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
        onView(withId(R.id.addReminderFAB)).perform(click())
        verify(navController).navigate(ReminderListFragmentDirections.toSaveReminder())
    }

    /**
     * display items in reminder list fragment test
     */
    @Test
    fun reminderList_displayItems(): Unit = runBlocking {
        val reminder = ReminderDTO(
            "Test Reminder Title",
            "Test Reminder Description",
            "Test Reminder Location",
            0.0,
            0.0
        )

        repository.saveReminder(reminder)

        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        onView(withText(reminder.title)).check(ViewAssertions.matches(isDisplayed()))
        onView(withText(reminder.description)).check(ViewAssertions.matches(isDisplayed()))
        onView(withText(reminder.location)).check(ViewAssertions.matches(isDisplayed()))
        Thread.sleep(1500)
    }


    /**
     * display no data when delete all reminders test
     */
    @Test
    fun noDataText_noDataDisplayed() {
        val reminder = ReminderDTO(
            "Test Reminder Title",
            "Test Reminder Description",
            "Test Reminder Location",
            0.0,
            0.0
        )
        runBlocking {
            repository.saveReminder(reminder)

            repository.deleteAllReminders()
        }

        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        onView(withId(R.id.noDataTextView)).check(ViewAssertions.matches(isDisplayed()))
        Thread.sleep(1500)
    }

}