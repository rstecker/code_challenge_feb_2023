package com.example.basicquiz.logic

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.basicquiz.R
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json


interface QuizVM {
  fun handleAction(action: QuizViewModel.Action)
  var quiz: Quiz
  var currentQuestion: DisplayQuestion?
}

class QuizViewModel(private val application: Application) : QuizVM, AndroidViewModel(application) {
  override var quiz: Quiz by mutableStateOf(Quiz(emptyList()))
  override var currentQuestion: DisplayQuestion? by mutableStateOf(null)
  private var currentQuestionIndex = -1

  private suspend fun loadData() {
    withContext(Dispatchers.IO) {
      val rawJson = application.resources
        .openRawResource(R.raw.quiz_data)
        .bufferedReader().use { it.readText() }

      val gson = Gson()
      quiz = gson.fromJson(rawJson, Quiz::class.java)
      currentQuestionIndex = 0
      currentQuestion = quiz.questions.getOrNull(currentQuestionIndex)?.mapToFreshDisplayQuestion()
    }
  }

  override fun handleAction(action: Action) {
    viewModelScope.launch {
      when (action) {
        Action.LoadQuiz -> loadData()
        Action.AdvanceQuestion -> TODO()
        is Action.AnswerQuestion -> TODO()
      }
    }
  }

  sealed class Action {
    object LoadQuiz : Action()
    data class AnswerQuestion(val guessIndex:Int) : Action()
    object AdvanceQuestion: Action()
  }
}