package com.openclassrooms.rebonnte

import android.net.Uri
import com.openclassrooms.rebonnte.model.Medicine
import com.openclassrooms.rebonnte.repository.MedicineRepository
import com.openclassrooms.rebonnte.viewmodel.MedicineViewModel
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MedicineViewModelTest {

    private lateinit var viewModel: MedicineViewModel
    private val repository: MedicineRepository = mockk(relaxed = true) // Mock du Repository

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MedicineViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadMedicinesUpdateTest() = runTest {
        //Arrange
        val mockMedicines = listOf(
            Medicine("Paracetamol", 10, "A1", emptyList(), ""),
            Medicine("Ibuprofen", 333, "B2", emptyList(), "")
        )
        coEvery { repository.getAllMedicines() } returns mockMedicines

        //Act
        viewModel.loadMedicines()
        advanceUntilIdle()

        //Assert
        assertEquals(mockMedicines, viewModel.medicines.first())
    }

    @Test
    fun `addMedicine should add a new medicine to state`() = runTest {
        val newMedicine = Medicine("Aspirine", 20, "C3", emptyList(), "")

        viewModel.addMedicine(newMedicine)
        advanceUntilIdle()

        coVerify { repository.addMedicine(newMedicine) }
        assertTrue(viewModel.medicines.first().contains(newMedicine))
    }

    @Test
    fun `deleteMedicine should remove medicine from state`() = runTest {
        val medicineId = "1234"

        viewModel.deleteMedicine(medicineId)
        advanceUntilIdle()

        coVerify { repository.deleteMedicine(medicineId) }
    }

    @Test
    fun `filterByName should return correct filtered medicines`() = runTest {
        val medicines = listOf(
            Medicine("Doliprane", 10, "A1", emptyList(), ""),
            Medicine("Spasfon", 5, "B2", emptyList(), ""),
            Medicine("Dafalgan", 7, "C3", emptyList(), "")
        )

        coEvery { repository.getMedicinesFilteredByName("Doli") } returns listOf(medicines[0])

        viewModel.filterByName("Doli")
        advanceUntilIdle()

        assertEquals(1, viewModel.medicines.first().size)
        assertEquals("Doliprane", viewModel.medicines.first()[0].name)
    }

    @Test
    fun `sortByName should return medicines sorted alphabetically`() = runTest {
        val medicines = listOf(
            Medicine("Ibuprofen", 5, "B2", emptyList(), ""),
            Medicine("Paracetamol", 10, "A1", emptyList(), ""),
            Medicine("Aspirine", 8, "C3", emptyList(), "")
        )

        coEvery { repository.getMedicinesSortedByName() } returns medicines.sortedBy { it.name }

        viewModel.sortByName()
        advanceUntilIdle()

        assertEquals("Aspirine", viewModel.medicines.first()[0].name)
        assertEquals("Ibuprofen", viewModel.medicines.first()[1].name)
        assertEquals("Paracetamol", viewModel.medicines.first()[2].name)
    }

    @Test
    fun `sortByStock should sort medicines by stock amount`() = runTest {
        val medicines = listOf(
            Medicine("Ibuprofen", 5, "B2", emptyList(), ""),
            Medicine("Paracetamol", 10, "A1", emptyList(), "")
        )

        viewModel._medicines.value = medicines

        viewModel.sortByStock()
        advanceUntilIdle()

        assertEquals(5, viewModel.medicines.first()[0].stock)
        assertEquals(10, viewModel.medicines.first()[1].stock)
    }

    @Test
    fun `incrementStock should increase stock count`() = runBlocking {
        val medicine = Medicine("Paracetamol", 10, "A1", emptyList(), "")

        viewModel._medicines.value = listOf(medicine)

        viewModel.incrementStock(medicine, "user@example.com")

        assertEquals(10, viewModel.medicines.first()[0].stock)
    }

    @Test
    fun `decrementStock should decrease stock count`() = runTest {
        val medicine = Medicine("Paracetamol", 10, "A1", emptyList(), "")
        viewModel._medicines.value = listOf(medicine)

        viewModel.decrementStock(medicine, "user@example.com")
        advanceUntilIdle()

        assertEquals(9, viewModel.medicines.first()[0].stock)
    }

    @Test
    fun `uploadImage should call repository and update medicine`() = runTest {
        val imageUri = mockk<Uri>()
        val medicine = Medicine("Paracetamol", 10, "A1", emptyList(), "")

        coEvery { repository.uploadImageToFirestore(imageUri, any()) } returns "http://image-url.com"

        viewModel.updateMedicineImage(imageUri, medicine) { uploadedUrl ->
            assertEquals("http://image-url.com", uploadedUrl)
        }
        advanceUntilIdle()

        coVerify { repository.uploadImageToFirestore(imageUri, any()) }
    }
}