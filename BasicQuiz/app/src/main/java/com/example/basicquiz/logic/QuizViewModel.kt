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
}

class QuizViewModel(private val application: Application) : QuizVM, AndroidViewModel(application) {
  override var quiz: Quiz by mutableStateOf(Quiz(emptyList()))

  private suspend fun loadData() {
    withContext(Dispatchers.IO) {
      val rawJson = application.resources
        .openRawResource(R.raw.quiz_data)
        .bufferedReader().use { it.readText() }

      val gson = Gson()
      quiz = gson.fromJson(rawJson, Quiz::class.java)
    }
  }

  override fun handleAction(action: Action) {
    viewModelScope.launch {
      when (action) {
        Action.LoadQuiz -> loadData()
      }
    }
  }

  sealed class Action {
    object LoadQuiz : Action()
  }
}