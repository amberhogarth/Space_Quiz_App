package au.edu.jcu.spacequizapp.needed

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Quiz::class, Question::class], version = 5, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun quizDao(): QuizDao
    abstract fun questionDao(): QuestionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "quiz_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database.quizDao(), database.questionDao())
                    }
                }
            }
        }

        suspend fun populateDatabase(quizDao: QuizDao, questionDao: QuestionDao) {
            // Sample data for each celestial body
            val quizzes = listOf(
                Quiz(title = "Sun Quiz"),
                Quiz(title = "Moon Quiz"),
                Quiz(title = "Mercury Quiz"),
                Quiz(title = "Venus Quiz"),
                Quiz(title = "Earth Quiz"),
                Quiz(title = "Mars Quiz"),
                Quiz(title = "Jupiter Quiz"),
                Quiz(title = "Saturn Quiz"),
                Quiz(title = "Uranus Quiz"),
                Quiz(title = "Neptune Quiz")
            )

            quizzes.forEach { quiz ->
                val quizId = quizDao.insert(quiz).toInt()

                val questions = when (quiz.title) {
                    "Sun Quiz" -> listOf(
                        Question(quizId = quizId, questionText = "What color is the Sun?", correctAnswer = "Yellow", optionTwo = "Blue", optionThree = "Green"),
                        Question(quizId = quizId, questionText = "What is the Sun made of?", correctAnswer = "Hydrogen and Helium", optionTwo = "Rock", optionThree = "Ice"),
                        Question(quizId = quizId, questionText = "How old is the Sun?", correctAnswer = "4.6 billion years", optionTwo = "1 million years", optionThree = "10 billion years"),
                        Question(quizId = quizId, questionText = "What type of star is the Sun?", correctAnswer = "G-type", optionTwo = "A-type", optionThree = "M-type"),
                        Question(quizId = quizId, questionText = "How many planets orbit the Sun?", correctAnswer = "8", optionTwo = "5", optionThree = "12"),
                        Question(quizId = quizId, questionText = "What is the surface temperature of the Sun?", correctAnswer = "5,500°C", optionTwo = "100°C", optionThree = "10,000°C"),
                        Question(quizId = quizId, questionText = "Does the Sun have any solid surface?", correctAnswer = "No", optionTwo = "Yes", optionThree = "Sometimes"),
                        Question(quizId = quizId, questionText = "What is the Sun’s core primarily made of?", correctAnswer = "Hydrogen", optionTwo = "Iron", optionThree = "Oxygen"),
                        Question(quizId = quizId, questionText = "What is solar wind?", correctAnswer = "Stream of particles", optionTwo = "Sound wave", optionThree = "Ocean current"),
                        Question(quizId = quizId, questionText = "Which planet is closest to the Sun?", correctAnswer = "Mercury", optionTwo = "Venus", optionThree = "Earth")
                    )
                    "Moon Quiz" -> listOf(
                        Question(quizId = quizId, questionText = "How long does it take the Moon to orbit Earth?", correctAnswer = "27.3 days", optionTwo = "30 days", optionThree = "365 days"),
                        Question(quizId = quizId, questionText = "What causes the phases of the Moon?", correctAnswer = "Its position relative to Earth and Sun", optionTwo = "Its own light", optionThree = "Its distance from Earth"),
                        Question(quizId = quizId, questionText = "Does the Moon have an atmosphere?", correctAnswer = "Very thin one", optionTwo = "Thick atmosphere", optionThree = "No atmosphere"),
                        Question(quizId = quizId, questionText = "Who was the first person to walk on the Moon?", correctAnswer = "Neil Armstrong", optionTwo = "Yuri Gagarin", optionThree = "Buzz Aldrin"),
                        Question(quizId = quizId, questionText = "How far is the Moon from Earth?", correctAnswer = "384,400 km", optionTwo = "100,000 km", optionThree = "1 million km"),
                        Question(quizId = quizId, questionText = "What is the Moon mostly made of?", correctAnswer = "Rock", optionTwo = "Water", optionThree = "Gas"),
                        Question(quizId = quizId, questionText = "What is a lunar eclipse?", correctAnswer = "Earth’s shadow on the Moon", optionTwo = "Moon’s shadow on Earth", optionThree = "Sun’s shadow on Moon"),
                        Question(quizId = quizId, questionText = "How many moons does Earth have?", correctAnswer = "1", optionTwo = "2", optionThree = "5"),
                        Question(quizId = quizId, questionText = "Does the Moon have water?", correctAnswer = "Yes, in ice form", optionTwo = "Only liquid water", optionThree = "No water"),
                        Question(quizId = quizId, questionText = "What is the dark side of the Moon?", correctAnswer = "The side we never see", optionTwo = "A special area", optionThree = "A brighter area")
                    )
                    "Mercury Quiz" -> listOf(
                        Question(quizId = quizId, questionText = "How close is Mercury to the Sun?", correctAnswer = "Closest planet", optionTwo = "Third closest", optionThree = "Second closest"),
                        Question(quizId = quizId, questionText = "What is Mercury's surface like?", correctAnswer = "Rocky and cratered", optionTwo = "Gaseous", optionThree = "Icy"),
                        Question(quizId = quizId, questionText = "Does Mercury have any moons?", correctAnswer = "No", optionTwo = "1", optionThree = "2"),
                        Question(quizId = quizId, questionText = "How long is a day on Mercury?", correctAnswer = "59 Earth days", optionTwo = "1 Earth day", optionThree = "365 Earth days"),
                        Question(quizId = quizId, questionText = "What is Mercury's atmosphere like?", correctAnswer = "Very thin", optionTwo = "Thick", optionThree = "None"),
                        Question(quizId = quizId, questionText = "How many times does Mercury orbit the Sun in a year?", correctAnswer = "About 4", optionTwo = "Once", optionThree = "Twice"),
                        Question(quizId = quizId, questionText = "Is Mercury hotter than Venus?", correctAnswer = "No", optionTwo = "Yes", optionThree = "Same temperature"),
                        Question(quizId = quizId, questionText = "What is Mercury named after?", correctAnswer = "A Roman god", optionTwo = "A Greek hero", optionThree = "A scientist"),
                        Question(quizId = quizId, questionText = "Can we see Mercury easily from Earth?", correctAnswer = "No, it’s close to the Sun", optionTwo = "Yes, it’s very bright", optionThree = "Only in winter"),
                        Question(quizId = quizId, questionText = "Which spacecraft visited Mercury?", correctAnswer = "Messenger", optionTwo = "Voyager", optionThree = "Pioneer")
                    )
                    "Venus Quiz" -> listOf(
                        Question(quizId = quizId, questionText = "Which planet is closest to Earth?", correctAnswer = "Venus", optionTwo = "Mars", optionThree = "Mercury"),
                        Question(quizId = quizId, questionText = "What is Venus’s atmosphere mainly composed of?", correctAnswer = "Carbon Dioxide", optionTwo = "Oxygen", optionThree = "Nitrogen"),
                        Question(quizId = quizId, questionText = "Why is Venus called Earth’s sister planet?", correctAnswer = "Similar in size and structure", optionTwo = "It has life", optionThree = "It has water"),
                        Question(quizId = quizId, questionText = "Which direction does Venus rotate?", correctAnswer = "Opposite to most planets", optionTwo = "Same as Earth", optionThree = "Doesn’t rotate"),
                        Question(quizId = quizId, questionText = "How hot is the surface of Venus?", correctAnswer = "Around 465°C", optionTwo = "100°C", optionThree = "900°C"),
                        Question(quizId = quizId, questionText = "Does Venus have any moons?", correctAnswer = "No", optionTwo = "1", optionThree = "2"),
                        Question(quizId = quizId, questionText = "How long is a day on Venus?", correctAnswer = "243 Earth days", optionTwo = "1 Earth day", optionThree = "365 Earth days"),
                        Question(quizId = quizId, questionText = "What causes Venus's yellowish appearance?", correctAnswer = "Sulfuric acid clouds", optionTwo = "Dust storms", optionThree = "Yellow rocks"),
                        Question(quizId = quizId, questionText = "Who is Venus named after?", correctAnswer = "Roman goddess of love", optionTwo = "Greek hero", optionThree = "Famous scientist"),
                        Question(quizId = quizId, questionText = "Is Venus the hottest planet in our solar system?", correctAnswer = "Yes", optionTwo = "No", optionThree = "Only sometimes")
                    )

                    "Earth Quiz" -> listOf(
                            Question(quizId = quizId, questionText = "What percentage of Earth’s surface is covered by water?", correctAnswer = "71%", optionTwo = "50%", optionThree = "85%"),
                            Question(quizId = quizId, questionText = "How old is Earth?", correctAnswer = "4.5 billion years", optionTwo = "2 million years", optionThree = "10 billion years"),
                            Question(quizId = quizId, questionText = "What layer of Earth do we live on?", correctAnswer = "Crust", optionTwo = "Mantle", optionThree = "Core"),
                            Question(quizId = quizId, questionText = "What is Earth’s atmosphere mainly made of?", correctAnswer = "Nitrogen", optionTwo = "Oxygen", optionThree = "Carbon Dioxide"),
                            Question(quizId = quizId, questionText = "How many continents are on Earth?", correctAnswer = "7", optionTwo = "5", optionThree = "10"),
                            Question(quizId = quizId, questionText = "What is the largest ocean on Earth?", correctAnswer = "Pacific Ocean", optionTwo = "Atlantic Ocean", optionThree = "Indian Ocean"),
                            Question(quizId = quizId, questionText = "Does Earth have a magnetic field?", correctAnswer = "Yes", optionTwo = "No", optionThree = "Only at the poles"),
                            Question(quizId = quizId, questionText = "What causes day and night on Earth?", correctAnswer = "Earth’s rotation", optionTwo = "Earth’s orbit", optionThree = "Moon’s phases"),
                            Question(quizId = quizId, questionText = "How long does it take Earth to orbit the Sun?", correctAnswer = "365.25 days", optionTwo = "30 days", optionThree = "1000 days"),
                            Question(quizId = quizId, questionText = "What is Earth’s closest natural neighbor in space?", correctAnswer = "The Moon", optionTwo = "Mars", optionThree = "Venus")
                        )

                    "Mars Quiz" -> listOf(
                            Question(quizId = quizId, questionText = "What is Mars often called?", correctAnswer = "The Red Planet", optionTwo = "Blue Planet", optionThree = "Green Planet"),
                            Question(quizId = quizId, questionText = "What gas makes Mars appear red?", correctAnswer = "Iron oxide", optionTwo = "Carbon Dioxide", optionThree = "Sulfur"),
                            Question(quizId = quizId, questionText = "Does Mars have any moons?", correctAnswer = "Yes, 2", optionTwo = "No", optionThree = "5"),
                            Question(quizId = quizId, questionText = "What is the largest volcano on Mars?", correctAnswer = "Olympus Mons", optionTwo = "Mauna Kea", optionThree = "Vesuvius"),
                            Question(quizId = quizId, questionText = "How long is a day on Mars?", correctAnswer = "24.6 hours", optionTwo = "12 hours", optionThree = "48 hours"),
                            Question(quizId = quizId, questionText = "What is the average surface temperature on Mars?", correctAnswer = "-63°C", optionTwo = "0°C", optionThree = "100°C"),
                            Question(quizId = quizId, questionText = "Does Mars have seasons like Earth?", correctAnswer = "Yes", optionTwo = "No", optionThree = "Only in summer"),
                            Question(quizId = quizId, questionText = "What rover landed on Mars in 2021?", correctAnswer = "Perseverance", optionTwo = "Opportunity", optionThree = "Curiosity"),
                            Question(quizId = quizId, questionText = "Who is Mars named after?", correctAnswer = "Roman god of war", optionTwo = "Greek goddess of love", optionThree = "A scientist"),
                            Question(quizId = quizId, questionText = "Can liquid water be found on Mars?", correctAnswer = "No, only frozen", optionTwo = "Yes, in rivers", optionThree = "Yes, in lakes")
                        )
                    "Jupiter Quiz" -> listOf(
                        Question(quizId = quizId, questionText = "What is Jupiter mainly made of?", correctAnswer = "Hydrogen and Helium", optionTwo = "Iron", optionThree = "Methane"),
                        Question(quizId = quizId, questionText = "What is the most prominent feature of Jupiter?", correctAnswer = "The Great Red Spot", optionTwo = "Its rings", optionThree = "Its mountains"),
                        Question(quizId = quizId, questionText = "How many moons does Jupiter have?", correctAnswer = "79", optionTwo = "10", optionThree = "30"),
                        Question(quizId = quizId, questionText = "How long is a day on Jupiter?", correctAnswer = "About 10 hours", optionTwo = "24 hours", optionThree = "1 week"),
                        Question(quizId = quizId, questionText = "Who is Jupiter named after?", correctAnswer = "King of the Roman gods", optionTwo = "A Greek hero", optionThree = "An astronomer"),
                        Question(quizId = quizId, questionText = "Is Jupiter the largest planet in our solar system?", correctAnswer = "Yes", optionTwo = "No", optionThree = "Second largest"),
                        Question(quizId = quizId, questionText = "What is Jupiter’s magnetic field like?", correctAnswer = "Very strong", optionTwo = "Weak", optionThree = "Same as Earth"),
                        Question(quizId = quizId, questionText = "What is Europa?", correctAnswer = "One of Jupiter's moons", optionTwo = "A mountain on Jupiter", optionThree = "A star near Jupiter"),
                        Question(quizId = quizId, questionText = "What spacecraft visited Jupiter in 2016?", correctAnswer = "Juno", optionTwo = "Voyager", optionThree = "Cassini"),
                        Question(quizId = quizId, questionText = "Does Jupiter have a solid surface?", correctAnswer = "No", optionTwo = "Yes", optionThree = "Only partially")
                    )

                    "Saturn Quiz" -> listOf(
                            Question(quizId = quizId, questionText = "What is Saturn most famous for?", correctAnswer = "Its rings", optionTwo = "Its moons", optionThree = "Its size"),
                            Question(quizId = quizId, questionText = "What is Saturn mainly composed of?", correctAnswer = "Hydrogen and Helium", optionTwo = "Oxygen", optionThree = "Carbon dioxide"),
                            Question(quizId = quizId, questionText = "How long is a day on Saturn?", correctAnswer = "About 10.7 hours", optionTwo = "24 hours", optionThree = "1 month"),
                            Question(quizId = quizId, questionText = "How many moons does Saturn have?", correctAnswer = "83", optionTwo = "12", optionThree = "1"),
                            Question(quizId = quizId, questionText = "What is Titan?", correctAnswer = "A moon of Saturn", optionTwo = "A ring of Saturn", optionThree = "A planet near Saturn"),
                            Question(quizId = quizId, questionText = "Is Saturn the second largest planet in our solar system?", correctAnswer = "Yes", optionTwo = "No", optionThree = "It is the third largest"),
                            Question(quizId = quizId, questionText = "What are Saturn’s rings made of?", correctAnswer = "Ice and rock particles", optionTwo = "Iron", optionThree = "Methane gas"),
                            Question(quizId = quizId, questionText = "What spacecraft studied Saturn up close?", correctAnswer = "Cassini", optionTwo = "Voyager", optionThree = "Juno"),
                            Question(quizId = quizId, questionText = "Does Saturn have a solid core?", correctAnswer = "Yes", optionTwo = "No", optionThree = "Only gas"),
                            Question(quizId = quizId, questionText = "Who is Saturn named after?", correctAnswer = "Roman god of agriculture", optionTwo = "A famous scientist", optionThree = "An ancient king")
                        )

                    "Uranus Quiz" -> listOf(
                            Question(quizId = quizId, questionText = "What is unusual about Uranus's rotation?", correctAnswer = "It rotates on its side", optionTwo = "It doesn’t rotate", optionThree = "It rotates backwards"),
                            Question(quizId = quizId, questionText = "What is Uranus mainly made of?", correctAnswer = "Hydrogen, Helium, and Methane", optionTwo = "Nitrogen", optionThree = "Oxygen"),
                            Question(quizId = quizId, questionText = "What color does Uranus appear?", correctAnswer = "Blue-green", optionTwo = "Red", optionThree = "Yellow"),
                            Question(quizId = quizId, questionText = "How many moons does Uranus have?", correctAnswer = "27", optionTwo = "5", optionThree = "50"),
                            Question(quizId = quizId, questionText = "Who discovered Uranus?", correctAnswer = "William Herschel", optionTwo = "Galileo", optionThree = "Copernicus"),
                            Question(quizId = quizId, questionText = "Does Uranus have rings?", correctAnswer = "Yes", optionTwo = "No", optionThree = "Only during certain seasons"),
                            Question(quizId = quizId, questionText = "What spacecraft visited Uranus?", correctAnswer = "Voyager 2", optionTwo = "Cassini", optionThree = "Pioneer"),
                            Question(quizId = quizId, questionText = "How long does it take Uranus to orbit the Sun?", correctAnswer = "84 years", optionTwo = "1 year", optionThree = "10 years"),
                            Question(quizId = quizId, questionText = "What is the average temperature on Uranus?", correctAnswer = "-224°C", optionTwo = "-100°C", optionThree = "0°C"),
                            Question(quizId = quizId, questionText = "Who is Uranus named after?", correctAnswer = "Greek god of the sky", optionTwo = "Roman god of war", optionThree = "A famous scientist")
                        )

                    "Neptune Quiz" -> listOf(
                            Question(quizId = quizId, questionText = "What color does Neptune appear?", correctAnswer = "Blue", optionTwo = "Red", optionThree = "Green"),
                            Question(quizId = quizId, questionText = "What is Neptune mainly made of?", correctAnswer = "Hydrogen, Helium, and Methane", optionTwo = "Nitrogen", optionThree = "Oxygen"),
                            Question(quizId = quizId, questionText = "How long is a year on Neptune?", correctAnswer = "165 Earth years", optionTwo = "1 Earth year", optionThree = "10 Earth years"),
                            Question(quizId = quizId, questionText = "How many moons does Neptune have?", correctAnswer = "14", optionTwo = "3", optionThree = "20"),
                            Question(quizId = quizId, questionText = "What is Neptune's largest moon?", correctAnswer = "Triton", optionTwo = "Europa", optionThree = "Titan"),
                            Question(quizId = quizId, questionText = "What spacecraft visited Neptune?", correctAnswer = "Voyager 2", optionTwo = "Cassini", optionThree = "New Horizons"),
                            Question(quizId = quizId, questionText = "Does Neptune have rings?", correctAnswer = "Yes", optionTwo = "No", optionThree = "Only in winter"),
                            Question(quizId = quizId, questionText = "What causes Neptune’s blue color?", correctAnswer = "Methane in its atmosphere", optionTwo = "Oceans", optionThree = "Sulfur clouds"),
                            Question(quizId = quizId, questionText = "What is the Great Dark Spot on Neptune?", correctAnswer = "A storm", optionTwo = "A mountain", optionThree = "A moon"),
                            Question(quizId = quizId, questionText = "Who is Neptune named after?", correctAnswer = "Roman god of the sea", optionTwo = "Greek hero", optionThree = "A scientist")
                        )
                    else -> emptyList()
                }

                questionDao.insertAll(questions)
            }
        }

    }
}

