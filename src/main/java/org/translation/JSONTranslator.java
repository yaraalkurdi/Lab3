package org.translation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

/**
 * An implementation of the Translator interface which reads in the translation
 * data from a JSON file. The data is read in once each time an instance of this class is constructed.
 */
public class JSONTranslator implements Translator {
    private static final String ALPHA_2 = "alpha2";
    private static final String ALPHA_3 = "alpha3";
    private static final String ENGLISH_LANGUAGE_CODE = "en";
    private static final String ID_KEY = "id";
    private final Map<String, Map<String, String>> translations;

    /**
     * Constructs a JSONTranslator using data from the sample.json resources file.
     */
    public JSONTranslator() {
        this("sample.json");
    }

    /**
     * Constructs a JSONTranslator populated using data from the specified resources file.
     * @param filename the name of the file in resources to load the data from
     * @throws RuntimeException if the resource file can't be loaded properly
     */
    public JSONTranslator(String filename) {
        // read the file to get the data to populate things...
        try {

            String jsonString = Files.readString(Paths.get(getClass().getClassLoader().getResource(filename).toURI()));

            JSONArray jsonArray = new JSONArray(jsonString);

            translations = new HashMap<>();
            Map<String, String> countryNames = new HashMap<>();
            Map<String, String> languageNames = new HashMap<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                Map<String, String> countryData = new HashMap<>();
                for (String key : jsonArray.getJSONObject(i).keySet()) {
                    countryData.put(key, jsonArray.getJSONObject(i).getString(key));
                }

                translations.put(countryData.get(ALPHA_2), countryData);

                countryNames.put(countryData.get(ALPHA_2), countryData.get(ENGLISH_LANGUAGE_CODE));

                for (String langCode : countryData.keySet()) {
                    if (!langCode.equals(ID_KEY) && !langCode.equals(ALPHA_2)
                            && !langCode.equals(ALPHA_3) && !langCode.equals(ENGLISH_LANGUAGE_CODE)) {
                        languageNames.put(langCode, countryData.get(langCode));
                    }
                }
            }
        }
        catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<String> getCountryLanguages(String country) {
        List<String> languages = new ArrayList<>();
        for (String key : translations.get(country).keySet()) {
            if (!key.equals(ID_KEY) && !key.equals(ALPHA_2)
                    && !key.equals(ALPHA_3) && !key.equals(ENGLISH_LANGUAGE_CODE)) {
                languages.add(key);
            }
        }
        return languages;
    }

    @Override
    public List<String> getCountries() {
        List<String> countries = new ArrayList<>();
        countries.addAll(translations.keySet());
        return countries;
    }

    @Override
    public String translate(String country, String language) {
        return translations.get(country).get(language);
    }
}
