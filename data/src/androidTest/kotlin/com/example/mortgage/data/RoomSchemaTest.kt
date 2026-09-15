package com.example.mortgage.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mortgage.data.local.room.MortgageDatabase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertNotNull

@RunWith(AndroidJUnit4::class)
class RoomSchemaTest {
    private lateinit var database: MortgageDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, MortgageDatabase::class.java).build()
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun createsDatabaseAndDao() {
        assertNotNull(database.mortgageDao())
    }
}
