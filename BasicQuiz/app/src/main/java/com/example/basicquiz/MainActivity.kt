package com.example.basicquiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import com.example.basicquiz.logic.Quiz
import com.example.basicquiz.logic.QuizViewModel
import com.example.basicquiz.ui.theme.BasicQuizTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val vm = ViewModelProvider(this)[QuizViewModel::class.java]
    vm.handleAction(QuizViewModel.Action.LoadQuiz)
    setContent {
      BasicQuizTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            QuizTitle()
            vm.currentQuestion?.let { q ->
              QuizRound(q,
                updateSelection = { vm.handleAction(QuizViewModel.Action.UpdateSelection(it)) },
                submitGuess = { vm.handleAction(QuizViewModel.Action.AnswerQuestion) }
              )
            }
            vm.questionFeedback?.let { feedback ->
              QuizAnswerFeedback(feedback) { vm.handleAction(QuizViewModel.Action.AdvanceQuestion) }
            }
            vm.quizFeedback?.let { score ->
              QuizScoreFeedback(score) { vm.handleAction(QuizViewModel.Action.StartOver) }
            }
          }
        }
      }
    }
  }
}

@Composable
fun QuizTitle() {
  Text(stringResource(id = R.string.quiz_title))
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
  BasicQuizTheme {
    QuizTitle()
  }
}