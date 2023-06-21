package entities;

import java.io.Serializable;

public class StatisticsReport implements Serializable 
{
	private static final long serialVersionUID = 2L;
    private int conductExamId;
    private int numOfStudents;
    private int grade0_54_9;
    private int grade55_64;
    private int grade65_69;
    private int grade70_74;
    private int grade75_79;
    private int grade80_84;
    private int grade85_89;
    private int grade90_94;
    private int grade95_100;
    private String conductExamName;
    private String usernameExamAuthor;
    private String usernameExamConductor;
    private float median;
    private float average;
    private int maxGrade;
    private int minGrade;

    public StatisticsReport(int conductExamId, int numOfStudents, int grade0_54_9, int grade55_64, int grade65_69, int grade70_74,int grade75_79 ,int grade80_84, int grade85_89, int grade90_94, int grade95_100, String conductExamName, String usernameExamAuthor, String usernameExamConductor, float median, float average, int maxGrade, int minGrade) 
    {
        this.conductExamId = conductExamId;
        this.numOfStudents = numOfStudents;
        this.grade0_54_9 = grade0_54_9;
        this.grade55_64 = grade55_64;
        this.grade65_69 = grade65_69;
        this.grade70_74 = grade70_74;
        this.grade75_79 = grade75_79;
        this.grade80_84 = grade80_84;
        this.grade85_89 = grade85_89;
        this.grade90_94 = grade90_94;
        this.grade95_100 = grade95_100;
        this.conductExamName = conductExamName;
        this.usernameExamAuthor = usernameExamAuthor;
        this.usernameExamConductor = usernameExamConductor;
        this.median = median;
        this.average = average;
        this.maxGrade = maxGrade;
        this.minGrade = minGrade;
    }

    // Add getters for all the fields

    public int getConductExamId() 
    {
        return conductExamId;
    }

    public int getNumOfStudents() 
    {
        return numOfStudents;
    }

    public int getGrade0_54_9() 
    {
        return grade0_54_9;
    }

    public int getGrade55_64() 
    {
        return grade55_64;
    }

    public int getGrade65_69() 
    {
        return grade65_69;
    }

    public int getGrade70_74() 
    {
        return grade70_74;
    }
    
    public int getGrade75_79() 
    {
        return grade75_79;
    }

    public int getGrade80_84() 
    {
        return grade80_84;
    }

    public int getGrade85_89() 
    {
        return grade85_89;
    }

    public int getGrade90_94() 
    {
        return grade90_94;
    }

    public int getGrade95_100() 
    {
        return grade95_100;
    }

    public String getConductExamName() 
    {
        return conductExamName;
    }

    public String getUsernameExamAuthor() 
    {
        return usernameExamAuthor;
    }

    public String getUsernameExamConductor() 
    {
        return usernameExamConductor;
    }

    public float getMedian() 
    {
        return median;
    }

    public float getAverage() 
    {
        return average;
    }

    public int getMaxGrade() 
    {
        return maxGrade;
    }

    public int getMinGrade() 
    {
        return minGrade;
    }
}

