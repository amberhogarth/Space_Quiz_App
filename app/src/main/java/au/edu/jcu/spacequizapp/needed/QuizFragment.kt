package au.edu.jcu.spacequizapp.needed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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

        binding.closeQuizButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
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
                        if (button.text == question.correctAnswer) {
                            Toast.makeText(requireContext(), "Correct!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "Try again!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val ARG_QUIZ_ID = "quiz_id"

        fun newInstance(quizId: Int) = QuizFragment().apply {
            arguments = Bundle().apply { putInt(ARG_QUIZ_ID, quizId) }
        }
    }
}
