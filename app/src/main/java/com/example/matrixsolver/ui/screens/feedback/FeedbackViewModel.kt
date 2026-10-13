package com.example.matrixsolver.ui.screens.feedback

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.matrixsolver.data.FeedbackRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FeedbackUiState(
    val name: String = "",
    val email: String = "",
    val message: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val messageError: String? = null,
    val isSent: Boolean = false,
    val isLoading: Boolean = false
)

class FeedbackViewModel(application: Application) : AndroidViewModel(application) {

    private val feedbackRepository = FeedbackRepository(application)

    private val _uiState = MutableStateFlow(FeedbackUiState())
    val uiState: StateFlow<FeedbackUiState> = _uiState.asStateFlow()

    fun updateName(name: String) {
        _uiState.update { it.copy(name = name, nameError = null) }
    }

    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email, emailError = null) }
    }

    fun updateMessage(message: String) {
        _uiState.update { it.copy(message = message, messageError = null) }
    }

    fun validateAndSend() {
        val state = _uiState.value
        var hasError = false

        val nameError = if (state.name.isBlank()) "Name is required" else null
        val emailError = when {
            state.email.isBlank() -> "Email is required"
            !isValidEmail(state.email) -> "Invalid email format"
            else -> null
        }
        val messageError = when {
            state.message.isBlank() -> "Message is required"
            state.message.length < 10 -> "Message must be at least 10 characters"
            else -> null
        }

        if (nameError != null || emailError != null || messageError != null) {
            _uiState.update {
                it.copy(
                    nameError = nameError,
                    emailError = emailError,
                    messageError = messageError
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            feedbackRepository.addFeedback(
                name = state.name,
                email = state.email,
                message = state.message
            )

            _uiState.update {
                it.copy(
                    isLoading = false,
                    isSent = true
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = FeedbackUiState()
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
