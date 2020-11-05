package ch.hslu.springbootbackend.springbootbackend.Service.CsvService;

import ch.hslu.springbootbackend.springbootbackend.Entity.Answer;
import ch.hslu.springbootbackend.springbootbackend.Entity.CategorySet;
import ch.hslu.springbootbackend.springbootbackend.Entity.Media;
import ch.hslu.springbootbackend.springbootbackend.Entity.Question;
import ch.hslu.springbootbackend.springbootbackend.Repository.AnswerRepository;
import ch.hslu.springbootbackend.springbootbackend.Repository.CategorySetRepository;
import ch.hslu.springbootbackend.springbootbackend.Repository.MediaRepository;
import ch.hslu.springbootbackend.springbootbackend.Repository.QuestionRepository;
import ch.hslu.springbootbackend.springbootbackend.Utils.QuestionType;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class CsvQuestionService implements CsvService {

    private final Logger LOG = LoggerFactory.getLogger(CsvQuestionService.class);
    static String[] HEADER_QUESTION = {"chapterId", "QuestionPhrase", "Answer1", "Answer2", "Answer3", "Answer4", "Answer5", "CorrectAnswerAsLetter", "textIfCorrect", "textIfIncorrect", "questionImageId", "solutionImageId"};

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final CategorySetRepository categorySetRepository;
    private final MediaRepository mediaRepository;
    private List<Answer> currentCreatedAnswers;

    CsvQuestionService(QuestionRepository questionRepository, AnswerRepository answerRepository, CategorySetRepository categorySetRepository, MediaRepository mediaRepository) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.categorySetRepository = categorySetRepository;
        this.mediaRepository = mediaRepository;
    }

    public List<Question> saveNewEntities(MultipartFile file) {
        try {
            List<Question> questions = parseCsv(file.getInputStream());
            //List<Question> persistedQuestions = questionRepository.saveAll(questions);
            return questions;
        } catch (IOException e) {
            throw new RuntimeException("fail to store csv data: " + e.getMessage());
        }
    }

    public List<Question> parseCsv(InputStream is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

            List<Question> newQuestions = new ArrayList<>();

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                List<CategorySet> categorySet = new ArrayList<>();
                try {
                    CategorySet foundedCategorySet = this.getCategorySet(Integer.parseInt(csvRecord.get("chapterId")));
                    categorySet.add(foundedCategorySet);
                } catch (NumberFormatException ex) {
                    LOG.warn("Couldn't parse the founded Chapter ID :: " + csvRecord.get("chapterId") + " of the Data :: " + csvRecord.toString());
                    continue;
                }

                List<Answer> possibleAnswers = new ArrayList<>();
                for (int i = 1; i <= 5; i++) {
                    String answerPhrase = csvRecord.get("Answer" + i);
                    if (this.checkIfAnswerIsEmpty(answerPhrase)) {
                        continue;
                    }
                    String escapedAndEncodedString = this.escapeAndEncodeString(answerPhrase);
                    Optional<Answer> foundedAnswer = answerRepository.findByAnswerPhrase(escapedAndEncodedString);
                    if (foundedAnswer.isPresent()) {
                        possibleAnswers.add(foundedAnswer.get());
                    } else {
                        Answer newAnswerOfQuestion = new Answer(escapedAndEncodedString);
                        possibleAnswers.add(answerRepository.save(newAnswerOfQuestion));
                    }
                }

                List<Answer> correctAnswers = this.parseLettersToAnswers(csvRecord.get("CorrectAnswerAsLetter"), possibleAnswers);
                QuestionType questionType = QuestionType.fromString("Multiple Choice");
                Media questionImage = null;
                Media solutionImage = null;
                int pointsToAchieve = 2;
                if (this.checkIfMediaAvailableInCsv(csvRecord.get("questionImageId"))) {
                    questionImage = this.getMediaById(Integer.parseInt(csvRecord.get("questionImageId")));
                }
                if (this.checkIfMediaAvailableInCsv(csvRecord.get("solutionImageId"))) {
                    solutionImage = this.getMediaById(Integer.parseInt(csvRecord.get("solutionImageId")));
                }

                Question newQuestion = new Question(
                        this.escapeAndEncodeString(csvRecord.get("QuestionPhrase")),
                        possibleAnswers,
                        correctAnswers,
                        questionType,
                        null,
                        categorySet,
                        questionImage,
                        solutionImage,
                        pointsToAchieve
                );

                LOG.warn(newQuestion.toString());
                Question newPersistedQuestion = questionRepository.save(newQuestion);
                newQuestions.add(newPersistedQuestion);
            }
            return newQuestions;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }

    /**
     * Checks if an Answer-Object with this answerphrase exists inMemory or in Database.
     * If it exists it return the founded Answer. Else it creates a new one.
     *
     * @param answerPhrase
     * @return Answer
     */


    private boolean checkIfAnswerIsEmpty(String answerPhrase) {
        if (answerPhrase.equalsIgnoreCase("VOID") || answerPhrase.equalsIgnoreCase("")) {
            return true;
        }
        return false;
    }

    private CategorySet getCategorySet(final int categorySetId) {
        Optional<CategorySet> categorySet = categorySetRepository.findById(categorySetId);
        if (categorySet.isPresent()) {
            return categorySet.get();
        } else {
            throw new NoSuchElementException("The categorySet with the id :: " + categorySetId + " doesn't exists!");
        }

    }


    private List<Answer> parseLettersToAnswers(String answersAsLetters, List<Answer> possibleAnswers) {
        List<Answer> correctAnswers = new ArrayList<>();
        if (answersAsLetters.contains("a")) {
            correctAnswers.add(possibleAnswers.get(0));
        }
        if (answersAsLetters.contains("b")) {
            correctAnswers.add(possibleAnswers.get(1));
        }
        if (answersAsLetters.contains("c")) {
            correctAnswers.add(possibleAnswers.get(2));
        }
        if (answersAsLetters.contains("d")) {
            correctAnswers.add(possibleAnswers.get(3));
        }
        if (answersAsLetters.contains("e")) {
            correctAnswers.add(possibleAnswers.get(4));
        }
        return correctAnswers;
    }

    private String escapeAndEncodeString(final String string) {
        String escapedSpanTagInString = string.replaceAll("<(/|S|B)[^>]*>", "");
        String escapedAndEncodedHTMLEntitiesInString = StringEscapeUtils.unescapeHtml4(escapedSpanTagInString);
        return escapedAndEncodedHTMLEntitiesInString;
    }

    private Media getMediaById(final int mediaId) {
        Optional<Media> foundedMedia = mediaRepository.findById(mediaId);
        if (foundedMedia.isPresent()) {
            return foundedMedia.get();
        } else {
            throw new NoSuchElementException("The Media with the id :: " + mediaId + " doesn't exists in the database!");
        }
    }

    private boolean checkIfMediaAvailableInCsv(final String mediaId) {
        try {
            int idAsInteger = Integer.parseInt(mediaId);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

}
