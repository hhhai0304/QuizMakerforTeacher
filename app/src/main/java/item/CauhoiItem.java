package item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CauhoiItem
{
    public int MaCauHoi;
    public String NoiDung;
    public String DapAnDung;
    public String DapAn1;
    public String DapAn2;
    public String DapAn3;

    public CauhoiItem()
    {

    }

    public CauhoiItem(int MaCauHoi, String NoiDung, String DapAnDung, String DapAn1, String DapAn2, String DapAn3)
    {
        this.MaCauHoi = MaCauHoi;
        this.NoiDung = NoiDung;
        this.DapAnDung = DapAnDung;
        this.DapAn1 = DapAn1;
        this.DapAn2 = DapAn2;
        this.DapAn3 = DapAn3;
    }

    public String getAnswer()
    {
        return DapAnDung;
    }

    public void setAnswer(String DapAnDung)
    {
        this.DapAnDung = DapAnDung;
    }

    public List<String> getQuestionOptions()
    {
        List<String> shuffle = new ArrayList<>();
        shuffle.add(DapAnDung);
        shuffle.add(DapAn1);
        shuffle.add(DapAn2);
        shuffle.add(DapAn3);
        Collections.shuffle(shuffle);
        return shuffle;
    }

    public String getQuestion()
    {
        return NoiDung;
    }

    public void setQuestion(String NoiDung)
    {
        this.NoiDung = NoiDung;
    }
}
