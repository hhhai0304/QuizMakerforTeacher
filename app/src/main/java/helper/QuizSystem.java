package helper;

import java.util.ArrayList;
import java.util.List;

import item.CauhoiItem;

public class QuizSystem
{
    private int numRounds;
    private int right;
    private int wrong;
    private int round;
    private int time;
    private String UsernameSV, UsernameGV, MaDe;

    private List<CauhoiItem> questions = new ArrayList<CauhoiItem>();

    public int getRight()
    {
        return right;
    }

    public void setRight(int right)
    {
        this.right = right;
    }

    public int getWrong()
    {
        return wrong;
    }

    public void setWrong(int wrong)
    {
        this.wrong = wrong;
    }

    public int getRound()
    {
        return round;
    }

    public void setRound(int round)
    {
        this.round = round;
    }

    public void setQuestions(List<CauhoiItem> questions)
    {
        this.questions = questions;
    }

    public void addQuestions(CauhoiItem q)
    {
        this.questions.add(q);
    }

    public List<CauhoiItem> getQuestions()
    {
        return questions;
    }

    public CauhoiItem getNextQuestion()
    {
        CauhoiItem next = questions.get(this.getRound());
        this.setRound(this.getRound() + 1);
        return next;
    }

    public void incrementRightAnswers()
    {
        right ++;
    }

    public void incrementWrongAnswers()
    {
        wrong ++;
    }

    public void setNumRounds(int numRounds)
    {
        this.numRounds = numRounds;
    }

    public int getNumRounds()
    {
        return numRounds;
    }

    public boolean isGameOver()
    {
        return (getRound() >= getNumRounds());
    }

    public String getUsernameSV()
    {
        return UsernameSV;
    }

    public void setUsernameSV(String UsernameSV)
    {
        this.UsernameSV = UsernameSV;
    }

    public String getUsernameGV()
    {
        return UsernameGV;
    }

    public void setUsernameGV(String UsernameGV)
    {
        this.UsernameGV = UsernameGV;
    }

    public String getMaDe()
    {
        return MaDe;
    }

    public void setMaDe(String MaDe)
    {
        this.MaDe = MaDe;
    }

    public int getTime()
    {
        return time;
    }

    public void setTime(int time)
    {
        this.time = time;
    }
}