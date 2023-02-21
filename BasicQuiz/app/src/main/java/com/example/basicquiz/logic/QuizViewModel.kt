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
  var quizFeedback: QuizScore?
}

class QuizViewModel(private val application: Application) : QuizVM, AndroidViewModel(application) {
  override var quiz: Quiz by mutableStateOf(Quiz(emptyList()))
  override var currentQuestion: DisplayQuestion? by mutableStateOf(null)
  override var questionFeedback: QuizQuestionFeedback? by mutableStateOf(null)
  override var quizFeedback: QuizScore? by mutableStateOf(null)
  private var currentQuestionIndex = -1
  private var correctCount = 0


  private suspend fun loadData() {
    withContext(Dispatchers.IO) {
      val rawJson = application.resources
        .openRawResource(R.raw.quiz_data)
        .bufferedReader()
        .use { it.readText() }

      val gson = Gson()
      quiz = gson.fromJson(rawJson, Quiz::class.java)
      startNextQuestion()
    }
  }

  private fun resetState() {
    correctCount = 0
    currentQuestionIndex = -1
    // TODO : probably shuffle the question order?
  }

  private fun startNextQuestion() {
    ++currentQuestionIndex
    questionFeedback = null
    quizFeedback = null
    val nextQuestion = quiz.questions.getOrNull(currentQuestionIndex)
    if (nextQuestion == null) {
      val total = quiz.questions.size
      quizFeedback = QuizScore(correctCount, total)
    } else {
      currentQuestion = nextQuestion.mapToFreshDisplayQuestion()
    }
  }

  private fun gradeQuestion() {
    val choice = currentQuestion?.options?.firstOrNull { it.isSelected }
    questionFeedback = when {
      choice == null -> throw java.lang.IllegalStateException("How did we get here?")
      choice.isCorrect -> {
        ++correctCount
        QuizQuestionFeedback(true, "Correct!")
      }
      else -> {
        val correct = currentQuestion?.options?.firstOrNull { it.isCorrect }
        // TOOD : wouldn't it be nicer if this was fancier?
        QuizQuestionFeedback(false, "Sorry, the correct answer was [${correct?.text}]")
      }
    }
  }

  override fun handleAction(action: Action) {
    viewModelScope.launch {
      when (action) {
        Action.LoadQuiz -> loadData()
        Action.AdvanceQuestion -> startNextQuestion()
        is Action.AnswerQuestion -> gradeQuestion()
        Action.StartOver -> {
          resetState()
          startNextQuestion()
        }
        is Action.UpdateSelection -> currentQuestion =
          currentQuestion?.updateSelection(action.guessIndex)
      }
    }
  }


  /**
   * Basic flow of actions:
   * - [LoadQuiz] to start, only need be called once. Starts the first question
   * - [UpdateSelection] can be called multiple times as the user waffles over choice
   * - [AnswerQuestion] to be called once per question-- will update/set [QuizVM.questionFeedback]
   * - [AdvanceQuestion] will bump to the next question or trigger [QuizVM.quizFeedback] if the end is reached
   * - [StartOver] resets state and begins quiz at beginning
   */
  sealed class Action {
    object LoadQuiz : Action()
    object AnswerQuestion : Action()
    data class UpdateSelection(val guessIndex: Int) : Action()
    object AdvanceQuestion : Action()
    object StartOver : Action()
  }
}