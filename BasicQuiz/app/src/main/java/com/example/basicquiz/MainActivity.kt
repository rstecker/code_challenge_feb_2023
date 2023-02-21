package com.example.basicquiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          Greeting("Android")
          QuizBasics(quiz = vm.quiz)
          vm.currentQuestion?.let { q ->
            QuizRound(q) {
              vm.handleAction(
                QuizViewModel.Action.AnswerQuestion(it))
            }
          }
        }
      }
    }
  }
}

@Composable
fun Greeting(name: String) {
  Text(text = "Hello $name!")
}

@Composable
fun QuizBasics(quiz: Quiz) {
  Text(text = "Some quiz stuff: $quiz")
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
  BasicQuizTheme {
    Greeting("Android")
  }
}