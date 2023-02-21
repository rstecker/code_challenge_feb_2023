package com.example.basicquiz.logic
import kotlinx.serialization.*

@Serializable
data class Quiz(val questions:List<Question>)

@Serializable
data class Question(val question: String, val correct_answer: String, val answers:Map<String, String>)
