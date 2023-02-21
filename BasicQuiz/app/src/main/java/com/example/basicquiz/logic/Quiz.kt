package com.example.basicquiz.logic

import kotlinx.serialization.*

@Serializable
data class Quiz(val questions: List<Question>)

@Serializable
data class Question(
  val question: String,
  val correct_answer: String,
  val answers: Map<String, String>
) {
  fun mapToFreshDisplayQuestion(): DisplayQuestion {
    return DisplayQuestion(
      question = this.question,
      options = answers.entries.map {
        DisplayQuestionOption(it.value, isSelected = false, isCorrect = it.key == correct_answer)
      }
    )
  }
}

data class DisplayQuestion(
  val question: String,
  val options: List<DisplayQuestionOption>
)

data class DisplayQuestionOption(
  val text: String,
  val isSelected: Boolean,
  val isCorrect: Boolean
)