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


interface QuizVM {
  fun handleAction(action: QuizViewModel.Action)
  var quiz: Quiz
  var currentQuestion: DisplayQuestion?
  var questionFeedback: QuizQuestionFeedback?
}

class QuizViewModel(private val application: Application) : QuizVM, AndroidViewModel(application) {
  override var quiz: Quiz by mutableStateOf(Quiz(emptyList()))
  override var currentQuestion: DisplayQuestion? by mutableStateOf(null)
  override var questionFeedback: QuizQuestionFeedback? by mutableStateOf(null)
  private var currentQuestionIndex = -1

  private suspend fun loadData() {
    withContext(Dispatchers.IO) {
      val rawJson = application.resources
        .openRawResource(R.raw.quiz_data)
        .bufferedReader().use { it.readText() }

      val gson = Gson()
      quiz = gson.fromJson(rawJson, Quiz::class.java)
      currentQuestionIndex = -1
      startNextQuestion()
    }
  }
  private fun startNextQuestion() {
    // TODO : audit for end of quiz
    ++currentQuestionIndex
    questionFeedback = null
    currentQuestion = quiz.questions.getOrNull(currentQuestionIndex)?.mapToFreshDisplayQuestion()
  }

  private fun gradeQuestion(userAnswer:Int) {
    val choice = currentQuestion?.options?.getOrNull(userAnswer)
    when{
      choice == null -> throw java.lang.IllegalStateException("How did we get here?")
      choice.isCorrect -> questionFeedback = QuizQuestionFeedback(true, "Correct!")
      else -> {
        val correct = currentQuestion?.options?.firstOrNull { it.isCorrect }
        questionFeedback = QuizQuestionFeedback(false, "Sorry, the correct answer was [${correct?.text}]")
      }
    }
  }

  override fun handleAction(action: Action) {
    viewModelScope.launch {
      when (action) {
        Action.LoadQuiz -> loadData()
        Action.AdvanceQuestion -> startNextQuestion()
        is Action.AnswerQuestion -> gradeQuestion(action.guessIndex)
      }
    }
  }

  sealed class Action {
    object LoadQuiz : Action()
    data class AnswerQuestion(val guessIndex: Int) : Action()
    object AdvanceQuestion : Action()
  }
}