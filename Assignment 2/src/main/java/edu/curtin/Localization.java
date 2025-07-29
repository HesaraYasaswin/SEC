package edu.curtin;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Localization {

    private Properties messages = new Properties();
    private Locale currentLocale;
    private static final Logger logger = Logger.getLogger(Localization.class.getName()); 

    public Localization() {
        this.currentLocale = Locale.getDefault();
        loadMessages();
    }

    public void setLocale(String languageTag) {        
        this.currentLocale = Locale.forLanguageTag(languageTag); // Locale.forLanguageTag() to set the locale based on user input
        loadMessages();
    }

    @SuppressWarnings("PMD.GuardLogStatement")
    private void loadMessages() {
        String fileName = "messages_" + currentLocale.getLanguage() + ".properties";

        try (InputStream input = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (input != null) {
                messages.load(input);
            } else {
                // If no file found, load default (English)
                System.err.println("Could not find localization file: " + fileName + ", falling back to English.");
                messages = new Properties(); // Clear any old messages
                try (InputStream defaultInput = getClass().getClassLoader().getResourceAsStream("messages_en.properties")) {
                    if (defaultInput != null) {
                        messages.load(defaultInput);
                    }
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error loading localization messages from file: " + fileName, e); // Log the exception
        }
    }

    public String getMessage(String key) {
        return messages.getProperty(key, key);
    }
}

