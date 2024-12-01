package au.edu.jcu.spacequizapp.main

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.children
import androidx.fragment.app.Fragment
import au.edu.jcu.spacequizapp.R
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

    @SuppressLint("InflateParams")
    private fun displayQuestions(questions: List<Question>) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(requireContext())
            val quiz = db.quizDao().getQuizById(quizId ?: 0)

            withContext(Dispatchers.Main) {
                val isCompleted = quiz.completed

                // Update the quiz overlay visibility
                val overlay = binding.root.findViewById<View>(R.id.quizOverlay)
                val content = binding.root.findViewById<LinearLayout>(R.id.quizContent)

                if (isCompleted) {
                    overlay.visibility = View.VISIBLE
                    content.alpha = 0.5f // Dim the content
                } else {
                    overlay.visibility = View.GONE
                    content.alpha = 1f // Normal opacity
                }

                // Clear the question container before adding new questions
                binding.questionContainer.removeAllViews()

                for (question in questions) {
                    // Question text view
                    val questionView = TextView(requireContext()).apply {
                        id = View.generateViewId()
                        text = question.questionText
                        setTextColor(Color.WHITE)
                        textSize = 18f
                        setPadding(0, 16, 0, 16)
                        gravity = Gravity.CENTER_HORIZONTAL
                    }
                    binding.questionContainer.addView(questionView)

                    // Buttons for the options
                    val optionButtons = listOf(
                        layoutInflater.inflate(R.layout.item_quiz_button, null) as Button,
                        layoutInflater.inflate(R.layout.item_quiz_button, null) as Button,
                        layoutInflater.inflate(R.layout.item_quiz_button, null) as Button
                    ).apply {
                        this[0].text = question.correctAnswer
                        this[1].text = question.optionTwo
                        this[2].text = question.optionThree
                    }.shuffled()

                    // Set up each button's behavior
                    optionButtons.forEach { button ->
                        button.isEnabled = !isCompleted // Disable if quiz is completed
                        button.setOnClickListener {
                            handleAnswerClick(button, question.correctAnswer, optionButtons)
                        }
                        binding.questionContainer.addView(button)
                    }

                    // Spacer between questions
                    val spacerView = View(requireContext()).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            32 // Height of the spacer
                        )
                    }
                    binding.questionContainer.addView(spacerView)
                }

                if (isCompleted) {
                    Toast.makeText(
                        requireContext(),
                        "This quiz is completed and view-only.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun handleAnswerClick(button: Button, correctAnswer: String, optionButtons: List<Button>) {
        if (button.isEnabled) {
            // Check if the answer is correct
            if (button.text == correctAnswer) {
                score += 1
                button.setBackgroundColor(Color.parseColor("#4CAF50")) // Green for correct
                Toast.makeText(requireContext(), "Correct!", Toast.LENGTH_SHORT).show()
            } else {
                button.setBackgroundColor(Color.parseColor("#F44336")) // Red for incorrect
                Toast.makeText(requireContext(), "Incorrect!", Toast.LENGTH_SHORT).show()
            }

            // Disable all buttons for this question
            optionButtons.forEach { btn ->
                btn.isEnabled = false
                btn.alpha = 0.5f // Dim all buttons
            }

            // Find the question text view within the parent container
            val questionGroup = button.parent as? LinearLayout
            val questionTextView = questionGroup?.getChildAt(0) as? TextView
            questionTextView?.alpha = 0.5f // Dim the question text

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

