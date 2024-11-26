package au.edu.jcu.spacequizapp.main

import android.content.Context
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
    private var score = 0 // Local score for this quiz
    private lateinit var scoreTextView: TextView
    private lateinit var submitButton: Button

    private var quizCompletionListener: QuizCompletionListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        quizId = arguments?.getInt(ARG_QUIZ_ID)
        val db = AppDatabase.getDatabase(requireContext())
        questionDao = db.questionDao()

        quizId?.let { loadQuestions(it) }

        submitButton = binding.submitButton
        submitButton.setOnClickListener {
            onSubmit()
        }

        scoreTextView = binding.scoreTextView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is QuizCompletionListener) {
            quizCompletionListener = context
        } else {
            throw RuntimeException("$context must implement QuizCompletionListener")
        }
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
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(requireContext())
            val quiz = db.quizDao().getQuizById(quizId ?: 0)

            withContext(Dispatchers.Main) {
                val isCompleted = quiz.completed

                // Clear the question container to avoid duplication
                binding.questionContainer.removeAllViews()

                for (question in questions) {
                    val questionView = TextView(requireContext()).apply {
                        text = question.questionText
                    }

                    val optionButtons = listOf(
                        Button(requireContext()).apply { text = question.correctAnswer },
                        Button(requireContext()).apply { text = question.optionTwo },
                        Button(requireContext()).apply { text = question.optionThree }
                    ).shuffled()

                    binding.questionContainer.apply {
                        addView(questionView)
                        optionButtons.forEach { button ->
                            addView(button)
                            button.isEnabled = !isCompleted // Disable buttons for completed quizzes
                            if (!isCompleted) {
                                button.setOnClickListener {
                                    handleAnswerClick(button, question.correctAnswer)
                                }
                            }
                        }
                    }
                }

                // Disable the Submit button if the quiz is completed
                submitButton.isEnabled = !isCompleted

                // Show a toast message if the quiz is view-only
                if (isCompleted) {
                    Toast.makeText(requireContext(), "Quiz is view-only. You scored 10/10!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    private fun handleAnswerClick(button: Button, correctAnswer: String) {
        if (button.isEnabled) {
            if (button.text == correctAnswer) {
                score += 1
                Toast.makeText(requireContext(), "Correct!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Try again!", Toast.LENGTH_SHORT).show()
            }
            button.isEnabled = false
            updateScoreDisplay()
        }
    }

    private fun updateScoreDisplay() {
        "Score: $score".also { scoreTextView.text = it }
    }

    private fun onSubmit() {
        quizCompletionListener?.onQuizCompleted(score)

        AlertDialog.Builder(requireContext())
            .setTitle("Quiz Completed!")
            .setMessage("Your score is: $score")
            .setPositiveButton("OK") { _, _ ->
                if (score == 10) {
                    quizId?.let { id ->
                        CoroutineScope(Dispatchers.IO).launch {
                            val db = AppDatabase.getDatabase(requireContext())
                            db.quizDao().updateQuizCompletionStatus(id, true)
                        }
                    }
                }
                activity?.let {
                    (it as QuizActivity).binding.quizSpinner.setSelection(0)
                }
            }
            .setCancelable(false)
            .show()
    }


    companion object {
        private const val ARG_QUIZ_ID = "quiz_id"

        fun newInstance(quizId: Int) = QuizFragment().apply {
            arguments = Bundle().apply { putInt(ARG_QUIZ_ID, quizId) }
        }
    }

    interface QuizCompletionListener {
        fun onQuizCompleted(score: Int)
    }
}

