package com.openclassrooms.rebonnte.viewmodel

import android.net.Uri
import com.openclassrooms.rebonnte.model.Aisle
import com.openclassrooms.rebonnte.repository.AisleRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AisleViewModelTest {

    //test dispatcher is used to control coroutine executions
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: AisleViewModel

    //mocked repo used to tests the viewmodel
    private val repository: AisleRepository = mockk(relaxed = true)

    @Before
    fun setup() {
        //replace main dispatcher by testDispatcher to execute tasks on main thread
        Dispatchers.setMain(testDispatcher)

        //mock getAllAisles to avoid using Firebase
        coEvery { repository.getAllAisles() } returns emptyList()
        viewModel = AisleViewModel(repository)
        runTest { advanceUntilIdle() }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadAislesWithRepoDataTest() = runTest {
        //Arrange : Repository will return a list of Aisle
        val fakeListFromRepo = listOf(
            Aisle(id = "1", name = "Aisle 1"),
            Aisle(id = "2", name = "Aisle 2")
        )

        coEvery { repository.getAllAisles() } returns fakeListFromRepo

        //ACT
        viewModel.addAisle("Test Aisle")
        //Waiting for the end of the taks
        advanceUntilIdle()

        //ASSERT
        assertEquals(fakeListFromRepo, viewModel.aisles.first())
    }

    @Test
    fun filterByNameTest() = runTest {
        //ARRange
        val filteredAisles = listOf(
            Aisle(id = "1", name = "Test Aisle")
        )
        coEvery { repository.getAislesFilteredByName("Test") } returns filteredAisles


        //ACT
        viewModel.filterByName("Test")
        advanceUntilIdle()


        //ASSERT
        assertEquals(filteredAisles, viewModel.aisles.first())
    }


    @Test
    fun addingAisleTest() = runTest {
        //Arrange
        coEvery { repository.aisleExists("Test Aisle") } returns false
        coEvery { repository.addAisle(any()) } just Runs

        val updatedAisles = listOf(Aisle(id = "1", name = "Test Aisle"))
        coEvery { repository.getAllAisles() } returns updatedAisles

        //Act
        viewModel.addAisle("Test Aisle")
        advanceUntilIdle()

        //ASSERT
        val result = viewModel.aisleAddedStatus.first()
        assertNotNull(result)
        assertTrue(result?.isSuccess == true)
        assertEquals(updatedAisles, viewModel.aisles.first())
    }

    @Test
    fun uploadImageTest() = runTest {
        //Arrange
        val imageUri = mockk<Uri>()
        val aisle = Aisle(id = "1", name = "Aisle 1", mapUrl = "")
        val imageUrlTest = "http://image-url.com"

        coEvery { repository.uploadImageToFirestore(imageUri, aisle.id) } returns imageUrlTest
        coEvery { repository.updateAisle(any()) } just Runs
        coEvery { repository.getAllAisles() } returns listOf(aisle.copy(mapUrl = imageUrlTest))

        //Act
        var callbackUrl: String? = null
        viewModel.uploadImage(imageUri, aisle) { url ->
            callbackUrl = url
        }
        advanceUntilIdle()

        //Assert
        assertEquals(imageUrlTest, callbackUrl)
        coVerify { repository.uploadImageToFirestore(imageUri, aisle.id) }
        coVerify { repository.updateAisle(aisle.copy(mapUrl = imageUrlTest)) }
        assertEquals(listOf(aisle.copy(mapUrl = imageUrlTest)), viewModel.aisles.first())
    }
}