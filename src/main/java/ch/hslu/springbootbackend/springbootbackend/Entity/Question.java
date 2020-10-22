package ch.hslu.springbootbackend.springbootbackend.Entity;

import ch.hslu.springbootbackend.springbootbackend.Utils.QuestionType;

import java.util.LinkedList;
import java.util.List;
import javax.persistence.*;

@Entity
public class Question{

    public Question(){}

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(targetEntity = CategorySet.class, cascade = CascadeType.ALL)
    private CategorySet categorySet;

    @Column(length=1000000)
    private String questionphrase;

    @OneToMany(targetEntity = Answer.class, cascade = CascadeType.ALL)
    private List<Answer> possibleAnswers = new LinkedList<>();

    @OneToMany(targetEntity = Answer.class, cascade = CascadeType.ALL)
    private List<Answer> correctAnswers = new LinkedList<>();

    private QuestionType questionType;

    @OneToOne(targetEntity = Media.class, cascade = CascadeType.ALL)
    private Media questionImage;

    @OneToOne(targetEntity = Media.class, cascade = CascadeType.ALL)
    private Media answerImage;

    public List<Answer> getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(List<Answer> correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public Media getQuestionImage() {
        return questionImage;
    }

    public void setQuestionImage(Media questionImage) {
        this.questionImage = questionImage;
    }

    public Media getAnswerImage() {
        return answerImage;
    }

    public void setAnswerImage(Media answerImage) {
        this.answerImage = answerImage;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQuestionphrase() {
        return questionphrase;
    }

    public void setQuestionphrase(String questionphrase) {
        this.questionphrase = questionphrase;
    }

    public List<Answer> getPossibleAnswers() {
        return possibleAnswers;
    }

    public void setPossibleAnswers(List<Answer> possibleAnswers) {
        this.possibleAnswers = possibleAnswers;
    }

    public CategorySet getCategorySet() {
        return categorySet;
    }

    public void setCategorySet(CategorySet categorySet) {
        this.categorySet = categorySet;
    }

    public Question(String questionphrase, List<Answer> answers, List<Answer> correctAnswers, QuestionType questionType, CategorySet categorySet, Media questionImage, Media answerImage) {
        this.setQuestionphrase(questionphrase);
        this.setPossibleAnswers(answers);
        this.setCorrectAnswers(correctAnswers);
        this.setQuestionType(questionType);
        this.setCategorySet(categorySet);
        this.setQuestionImage(questionImage);
        this.setAnswerImage(answerImage);
    }
    public Question(String questionphrase, List<Answer> answers, List<Answer> correctAnswers, QuestionType questionType, CategorySet categorySet, int id, Media questionImage, Media answerImage) {
        this.setQuestionphrase(questionphrase);
        this.setPossibleAnswers(answers);
        this.setCorrectAnswers(correctAnswers);
        this.setQuestionType(questionType);
        this.setId(id);
        this.setCategorySet(categorySet);
        this.setQuestionImage(questionImage);
        this.setAnswerImage(answerImage);
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", categorySet=" + categorySet +
                ", questionphrase='" + questionphrase + '\'' +
                ", possibleAnswers=" + possibleAnswers +
                ", correctAnswers=" + correctAnswers +
                ", questionType=" + questionType +
                ", questionImage=" + questionImage +
                ", answerImage=" + answerImage +
                '}';
    }
}
