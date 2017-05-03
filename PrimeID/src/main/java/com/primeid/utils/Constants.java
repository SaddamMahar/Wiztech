package com.primeid.utils;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Saddam Hussain
 */

public interface Constants{
    public enum Errors{
        ONE("Unknown Error"),
        TWO("Invalid IP"),
        THREE("Payload Format Error"),
        FOUR("Incorrect Account Credentials"),
        FIVE("Inactive Account"),
        SIX("Token Invalid"),
        SEVEN("Token Expired"),
        EIGHT("Case ID Invalid"),
        NINE("File ID Invalid"),
        TEN("Format Invalid"),
        ELEVEN("File Length Invalid"),
        TWELIVE("Artifact Key Invalid"),
        THIRTEEN("Invalid Jurisdiction"),
        FOURTEEN("Invalid Location");
        
        private String value;

        private Errors(String value) {

            this.value = value;
        }

        @Override
        public String toString() {

            return value;
        }
    }
}