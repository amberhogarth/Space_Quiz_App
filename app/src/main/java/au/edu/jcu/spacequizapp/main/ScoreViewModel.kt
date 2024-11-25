package au.edu.jcu.spacequizapp.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScoreViewModel : ViewModel() {
    private val _overallScore = MutableLiveData<Int>(0)
    val overallScore: LiveData<Int> get() = _overallScore

    fun addScore(points: Int) {
        _overallScore.value = (_overallScore.value ?: 0) + points
    }
}