package com.example.basicquiz.logic

import kotlinx.serialization.*

/**
 * File to hold data class objects representing the JSON the quiz is encoded in as well as the
 * data classes used for UI purposes
 */

@Serializable
data class Quiz(val questions: List<Question>)

@Serializable
data class Question(
  val question: String,
  val correct_answer: String,
  val answers: Map<String, String>
) {
  /**
   * This method assumes there are 4 questions and that the correct answer is within them
   * Should one want to make more robust auditing, here or during the JSON parsing would be best
   */
  fun mapToFreshDisplayQuestion(): DisplayQuestion {
    return DisplayQuestion(
      question = this.question,
      options = answers.entries.map {
        DisplayQuestionOption(it.value, isSelected = false, isCorrect = it.key == correct_answer)
      }
    )
  }
}

data class QuizScore(val numberCorrect: Int, val questionCount: Int)

data class QuizQuestionFeedback(
  val wasCorrect: Boolean,
  val feedback: String
)

data class DisplayQuestion(
  val question: String,
  val options: List<DisplayQuestionOption>
) {
  fun updateSelection(index: Int): DisplayQuestion {
    return this.copy(
      options = options.mapIndexed { i, opt ->
        opt.copy(isSelected = i == index)
      }
    )
  }
}

data class DisplayQuestionOption(
  val text: String,
  val isSelected: Boolean,
  val isCorrect: Boolean
)
