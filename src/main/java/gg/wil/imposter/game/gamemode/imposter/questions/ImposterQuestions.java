package gg.wil.imposter.game.gamemode.imposter.questions;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.*;

public class ImposterQuestions {

    private static final Logger logger = LoggerFactory.getLogger(ImposterQuestions.class);
    private static final List<QuestionPair> QUESTIONS = new ArrayList<>();

    private static void reloadQuestions() {
        QUESTIONS.clear();

        Gson gson = new Gson();
        try (Reader reader = new InputStreamReader(Objects.requireNonNull(ImposterQuestions.class.getResourceAsStream("/static/imposter-questions.json")))) {
            Type type = new TypeToken<Map<String, Map<String, String>>>(){}.getType();
            Map<String, Map<String, String>> data = gson.fromJson(reader, type);

            for (Map.Entry<String, Map<String, String>> entry : data.entrySet()) {
                int id = Integer.parseInt(entry.getKey());
                Map<String, String> values = entry.getValue();
                FilterType filterType = FilterType.valueOf(values.get("filter"));
                QUESTIONS.add(new QuestionPair(id, values.get("group"), values.get("imposter"), filterType));
            }

            logger.info("Loaded {} questions", QUESTIONS.size());
        } catch (IOException ex) {
            logger.error("Failed to load questions", ex);
        }
    }

    static {
        reloadQuestions();
    }

    public static QuestionPair getRandomQuestion() {
        return QUESTIONS.get(new Random().nextInt(QUESTIONS.size()));
    }

}
