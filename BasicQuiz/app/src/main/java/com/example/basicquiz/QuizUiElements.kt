package com.example.basicquiz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.basicquiz.logic.DisplayQuestion
import com.example.basicquiz.logic.DisplayQuestionOption
import com.example.basicquiz.logic.QuizQuestionFeedback
import com.example.basicquiz.ui.theme.BasicQuizTheme

@Composable
fun QuizRound(question: DisplayQuestion, submitGuess: (Int) -> Unit) {
  var selectedOption by remember { mutableStateOf(-1) }

  Column(
    modifier = Modifier
      .verticalScroll(rememberScrollState())
      .fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(question.question, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
    Row(modifier = Modifier.fillMaxWidth()) {
      QuizOption(question.options[0], selectedOption == 0) { selectedOption = 0 }
      QuizOption(question.options[1], selectedOption == 1) { selectedOption = 1 }
    }
    Row {
      QuizOption(question.options[2], selectedOption == 2) { selectedOption = 2 }
      QuizOption(question.options[3], selectedOption == 3) { selectedOption = 3 }
    }
    Button(onClick = { submitGuess(selectedOption) }) {
      Text(stringResource(id = R.string.submit_btn))
    }
  }
}

@Composable
fun RowScope.QuizOption(
  option: DisplayQuestionOption, isSelected: Boolean, updateSelection: () -> Unit) {
  val bgColor = if (isSelected) Color.Blue else Color.White
  Button(
    onClick = updateSelection,
    modifier = Modifier
      .background(bgColor)
      .weight(1f, true)
  ) {
    Text(text = option.text)
  }
}


@Composable
fun QuizAnswerFeedback(feedback: QuizQuestionFeedback, dismiss: () -> Unit) {
  AlertDialog(
    onDismissRequest = { dismiss() },
    title = {
      Text(stringResource(
        id = if (feedback.wasCorrect) R.string.feedback_title_correct else R.string.feedback_title_incorrect))
    },
    text = { Text(feedback.feedback) },
    confirmButton = {
      Button(onClick = { dismiss() }) {
        Text(stringResource(id = R.string.feedback_btn_next))
      }
    }
  )
}

@Preview(showBackground = true)
@Composable
fun QuizPreview() {
  BasicQuizTheme {
    QuizRound(DisplayQuestion(
      question = "Here's some really long text to test the size of the content. Sharks sharks. More sharks. Things and words go here.",
      options = listOf(
        DisplayQuestionOption("choice 1", false, false),
        DisplayQuestionOption("Can't make the class as final class", false, false),
        DisplayQuestionOption("a", false, false),
        DisplayQuestionOption(
          "some kinda' long answer text that will be rather annoying to deal with", false, false)
      )
    )
    ) {}
  }
}