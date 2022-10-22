package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {


    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var database: RemindersDatabase

    @Before
    fun initializeDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun saveReminderTest() = runBlockingTest {
        val reminder = ReminderDTO(
            "University",
            "don't miss the sessions for today",
            "Cairo",
            22.12155,
            24.223564
        )

        database.reminderDao().saveReminder(reminder)
        val storedReminder = database.reminderDao().getReminderById(reminder.id)
        assertThat(storedReminder?.id, `is`(reminder.id))
        assertThat(storedReminder?.title, `is`(reminder.title))
        assertThat(storedReminder?.description, `is`(reminder.description))
        assertThat(storedReminder?.location, `is`(reminder.location))
        assertThat(storedReminder?.latitude, `is`(reminder.latitude))
        assertThat(storedReminder?.longitude, `is`(reminder.longitude))
    }

    @Test
    fun clearRemindersTest() = runBlockingTest {
        val reminder = ReminderDTO(
            "University",
            "don't miss the sessions for today",
            "Cairo",
            22.12155,
            24.223564
        )

        database.reminderDao().saveReminder(reminder)
        database.reminderDao().deleteAllReminders()
        val reminders = database.reminderDao().getReminders()
        assertThat(reminders.isEmpty(), `is`(true))
    }

    @Test
    fun nullableReminderTest() = runBlockingTest {
        val reminder = database.reminderDao().getReminderById("5")
        assertThat(reminder, nullValue())
    }


}