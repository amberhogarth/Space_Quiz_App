package au.edu.jcu.spacequizapp.needed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import au.edu.jcu.spacequizapp.R

class QuizAdapter(
    private var quizzes: List<Quiz>,
    private val onQuizSelected: (Int) -> Unit
) : RecyclerView.Adapter<QuizAdapter.QuizViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_quiz_button, parent, false)
        return QuizViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        val quiz = quizzes[position]
        holder.bind(quiz)
    }

    override fun getItemCount(): Int = quizzes.size

    fun updateQuizzes(newQuizzes: List<Quiz>) {
        quizzes = newQuizzes
        notifyDataSetChanged()
    }

    inner class QuizViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val quizButton: Button = itemView.findViewById(R.id.quiz_button)

        fun bind(quiz: Quiz) {
            quizButton.text = quiz.title
            quizButton.setOnClickListener {
                onQuizSelected(quiz.quizId)
            }
        }
    }
}
