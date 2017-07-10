package baanhem.quizmakerforteacher;

import android.app.Application;

import helper.QuizSystem;

public class QuizApplication extends Application
{
    private QuizSystem currentQuiz;

    public void setCurrentGame(QuizSystem currentGame)
    {
        this.currentQuiz = currentGame;
    }

    public QuizSystem getCurrentGame()
    {
        return currentQuiz;
    }
}
