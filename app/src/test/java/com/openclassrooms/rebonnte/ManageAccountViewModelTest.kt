package com.openclassrooms.rebonnte

import com.openclassrooms.rebonnte.repository.AuthRepository
import com.openclassrooms.rebonnte.viewmodel.ManageAccountViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ManageAccountViewModelSimpleTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: ManageAccountViewModel
    private val authRepository: AuthRepository = mockk()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { authRepository.isUserAuthenticated() } returns true
        viewModel = ManageAccountViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun signOutChangingIsAuthenticatedToFalse() = runTest(testDispatcher) {
        //Arrange
        coEvery { authRepository.signOut() } just runs

        //Act
        viewModel.signOut()
        testScheduler.advanceUntilIdle()
        //Assert
        coVerify { authRepository.signOut() }
        assertFalse(viewModel.isAuthenticated.first())
    }

    @Test
    fun getAccountNameTest() {
        //Arrange
        every { authRepository.getUserName() } returns "Bobby"
        //Act
        val name = viewModel.getAccountName()
        //Assert
        assertEquals("Bobby", name)
    }

    @Test
    fun deleteAccountTest() = runTest(testDispatcher) {
        //Arrange
        coEvery { authRepository.deleteUser() } returns true
        //Act
        viewModel.deleteUserAccount()
        testScheduler.advanceUntilIdle()
        //Assert
        assertTrue(viewModel.deleteAccountState.first()!!)
        assertFalse(viewModel.isAuthenticated.first())
    }

}