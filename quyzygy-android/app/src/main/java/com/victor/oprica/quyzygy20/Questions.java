package com.victor.oprica.quyzygy20;

public class Questions { //to be modified

    public String mQuestions[] = {
            "Which is the first planet in the solar system?",
            "Which is the second planet in the solar system?",
            "Which is the third planet in the solar system?",
            "Which is the fourth planet in the solar system?",
            "Which is the fifth planet in the solar system?",
            "Which is the sixth planet in the solar system?",
            "Which is the seventh planet in the solar system?",
            "Which is the eight planet in the solar system?",
            "Which is the ninth planet in the solar system?",
    };

    public String mAnswers[][] = {
            {"Mercury","Venus","Mars","Saturn"},
            {"Jupiter","Venus","Earth","Neptune"},
            {"Earth","Jupiter","Pluto","Venus"},
            {"Jupiter","Saturn","Mars","Earth"},
            {"Jupiter","Pluto","Mercury","Earth"},
            {"Uranus","Venus","Mars","Saturn"},
            {"Saturn","Pluto","Uranus","Earth"},
            {"Venus","Neptune","Pluto","Mars"},
            {"Mercury","Venus","Mars","Pluto"},

    };

    private String mCorrectAnswers[] =
            {"Mercury", "Venus", "Earth","Mars","Jupiter","Saturn","Uranus","Neptune",
           "Pluto"};

    public String getQuestion(int s){
        String question = mQuestions[s];
        return question;
    }

    public String getChoice1(int a){
        String choice = mAnswers[a][0];
        return choice;
    }

    public String getChoice2(int a){
        String choice = mAnswers[a][1];
        return choice;
    }

    public String getChoice3(int a){
        String choice = mAnswers[a][2];
        return choice;
    }

    public String getChoice4(int a){
        String choice = mAnswers[a][3];
        return choice;
    }

    public String getCorrectAnswer(int a){
        String answer = mCorrectAnswers[a];
        return answer;
    }


}
