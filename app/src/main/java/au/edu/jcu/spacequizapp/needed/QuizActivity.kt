package au.edu.jcu.spacequizapp.needed

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import au.edu.jcu.spacequizapp.R
import au.edu.jcu.spacequizapp.databinding.ActivityQuizBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuizActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuizBinding
    private lateinit var quizDao: QuizDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getDatabase(this)
        quizDao = db.quizDao()

        CoroutineScope(Dispatchers.IO).launch {
            val quizzes = quizDao.getAllQuizzes()
            withContext(Dispatchers.Main) {
                setupQuizButtons(quizzes)
            }
        }
    }

private fun setupQuizButtons(quizzes: List<Quiz>) {
    Log.d("QuizActivity", "Quizzes loaded: ${quizzes.size}")
    val inflater = LayoutInflater.from(this)

    for (quiz in quizzes) {
        // Inflate the custom quiz button layout
        val buttonView = inflater.inflate(R.layout.item_quiz_button, binding.quizListContainer, false)

        // Customize the button using the inflated layout
        buttonView.findViewById<Button>(R.id.quiz_button).apply {
            text = quiz.title // Set the text to the quiz title
            setOnClickListener {
                val fragment = QuizFragment.newInstance(quiz.quizId)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.quizListContainer, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        // Add the customized button to the container
        binding.quizListContainer.addView(buttonView)
    }
}
}
