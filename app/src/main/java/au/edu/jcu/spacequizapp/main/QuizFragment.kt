package au.edu.jcu.spacequizapp.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import au.edu.jcu.spacequizapp.databinding.FragmentQuizBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuizFragment : Fragment() {
    private var quizId: Int? = null
    private lateinit var binding: FragmentQuizBinding
    private lateinit var questionDao: QuestionDao
    private var score = 0
    private var overall_score = 0
    private lateinit var scoreTextView: TextView
    private lateinit var submitButton: Button // Your submit button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        quizId = arguments?.getInt(ARG_QUIZ_ID)
        val db = AppDatabase.getDatabase(requireContext())
        questionDao = db.questionDao()

        quizId?.let { loadQuestions(it) }

        submitButton = binding.submitButton // Ensure the submit button exists in your layout XML
        submitButton.setOnClickListener {
            onSubmit()
        }

        scoreTextView = binding.scoreTextView // Ensure this is defined in your layout XML
    }

    private fun loadQuestions(quizId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val questions = questionDao.getQuestionsForQuiz(quizId)
            withContext(Dispatchers.Main) {
                displayQuestions(questions)
            }
        }
    }

    private fun displayQuestions(questions: List<Question>) {
        for (question in questions) {
            val questionView = TextView(requireContext()).apply {
                text = question.questionText
            }

            val optionButtons = listOf(
                Button(requireContext()).apply { text = question.correctAnswer },
                Button(requireContext()).apply { text = question.optionTwo },
                Button(requireContext()).apply { text = question.optionThree }
            ).shuffled() // Shuffle the answers

            binding.questionContainer.apply {
                addView(questionView)
                optionButtons.forEach { button ->
                    addView(button)
                    button.setOnClickListener {
                        handleAnswerClick(button, question.correctAnswer)
                    }
                }
            }
        }
    }

    private fun handleAnswerClick(button: Button, correctAnswer: String) {
        if (button.isEnabled) { // Prevent re-clicking
            if (button.text == correctAnswer) {
                score += 1
                overall_score += 1
                Toast.makeText(requireContext(), "Correct!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Try again!", Toast.LENGTH_SHORT).show()
            }
            button.isEnabled = false
            updateScoreDisplay()
        }
    }

    private fun updateScoreDisplay() {
        scoreTextView.text = "Score: $score"
    }

    private fun onSubmit() {

        // Create and show the popup with the updated score
        AlertDialog.Builder(requireContext())
            .setTitle("Quiz Completed!")
            .setMessage("Your overall score is: $overall_score")
            .setPositiveButton("OK") { _, _ ->
                // Pass the updated score to QuizActivity
                val quizIntent = Intent(requireContext(), QuizActivity::class.java)
                quizIntent.putExtra("overallScore", overall_score)
                startActivity(quizIntent)
            }
            .setCancelable(false)  // Prevent dialog from being dismissed with back button
            .show()
    }


    companion object {
        private const val ARG_QUIZ_ID = "quiz_id"

        fun newInstance(quizId: Int) = QuizFragment().apply {
            arguments = Bundle().apply { putInt(ARG_QUIZ_ID, quizId) }
        }
    }
}
