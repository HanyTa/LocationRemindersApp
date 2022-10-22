package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private lateinit var database: RemindersDatabase
    private lateinit var remindersDAO: RemindersDao
    private lateinit var repository: RemindersLocalRepository


    @Before
    fun initDB() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        remindersDAO = database.reminderDao()
        repository =
            RemindersLocalRepository(
                remindersDAO,
                Dispatchers.Main
            )
    }

    @After
    fun closeDB() {
        database.close()
    }

    @Test
    fun addReminder_getReminderById() = mainCoroutineRule.runBlockingTest {
        val reminder = ReminderDTO(
            "Test Reminder Title",
            "Test Reminder Description",
            "Test ",
            24.1144,
            27.4465
        )
        repository.saveReminder(reminder)
        val savedReminder = repository.getReminder(reminder.id) as Result.Success<ReminderDTO>

        assertThat(savedReminder.data, notNullValue())
        assertThat(savedReminder.data.id, `is`(reminder.id))
        assertThat(savedReminder.data.description, `is`(reminder.description))
        assertThat(savedReminder.data.location, `is`(reminder.location))
        assertThat(savedReminder.data.latitude, `is`(reminder.latitude))
        assertThat(savedReminder.data.longitude, `is`(reminder.longitude))
    }


    @Test
    fun deleteReminders_checkEmptyReminders() = mainCoroutineRule.runBlockingTest {
        val reminder = ReminderDTO(
            "Test Reminder Title",
            "Test Reminder Description",
            "Test ",
            24.1144,
            27.4465
        )
        repository.saveReminder(reminder)
        repository.deleteAllReminders()
        val savedReminders = repository.getReminders() as Result.Success<List<ReminderDTO>>
        assertThat(savedReminders.data.isEmpty(),`is`(true))
    }


    @Test
    fun getReminder_checkNonExistingReminders() = mainCoroutineRule.runBlockingTest {
        val reminder = repository.getReminder("151") as Result.Error
        assertThat(reminder.message, notNullValue())
        assertThat(reminder.message, `is`("Reminder not found!"))
    }

}