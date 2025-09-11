package com.comerzzia.iskaypet.pos.util.formatter;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;

public class IskaypetFormatter {

    public static TextFormatter<Integer> getIntegerFormat() {
        return new TextFormatter<>(new IntegerStringConverter() {
            @Override
            public Integer fromString(String string) {
                return (string == null || string.isEmpty()) ? 1 : super.fromString(string);
            }
        }, 1, change -> change.getControlNewText().matches("\\d*") ? change : null);
    }

    public static TextFormatter<Integer> getIntegerFormat(Integer defaultValue) {
        return new TextFormatter<>(new IntegerStringConverter() {
            @Override
            public Integer fromString(String string) {
                return (string == null || string.isEmpty()) ? defaultValue : super.fromString(string);
            }
        }, defaultValue, change -> change.getControlNewText().matches("\\d*") ? change : null);
    }

    public static TextFormatter<Double> getDoubleFormat(int integerPart, int decimalPart, Double valorPorDefecto) {
        return new TextFormatter<>(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                return object != null ? String.format("%." + decimalPart + "f", object).replace('.', ',') : "";
            }

            @Override
            public Double fromString(String string) {
                return string != null && !string.isEmpty() ? Double.parseDouble(string.replace(',', '.')) : valorPorDefecto;
            }
        }, valorPorDefecto, change -> change.getControlNewText().matches("\\d{0," + integerPart + "}(,\\d{0," + decimalPart + "})?") ? change : null);
    }

    public static void setUpperCaseFormatter(TextField textField) {
        textField.setTextFormatter(new TextFormatter<>(change -> {
            change.setText(change.getText().toUpperCase());
            return change;
        }));
    }

    public static TextFormatter<String> getNumberWithOptionalCommaFormat() {
        return new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty() || newText.matches("\\d*(,\\d*)?")) {
                return change;
            }
            return null;
        });
    }

    public static TextFormatter<Double> getDoubleFormatNullable(int integerPart, int decimalPart) {
        return new TextFormatter<>(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                return object != null ? String.format("%." + decimalPart + "f", object).replace('.', ',') : "";
            }

            @Override
            public Double fromString(String string) {
                return string != null && !string.isEmpty() ? Double.parseDouble(string.replace(',', '.')) : null;
            }
        }, null, change -> change.getControlNewText().matches("\\d{0," + integerPart + "}(,\\d{0," + decimalPart + "})?") ? change : null);
    }

    public static TextFormatter<String> getIntegerFormatWithLimitNullable(int maxNum) {
        return new TextFormatter<>(new StringConverter<String>() {
            @Override
            public String toString(String object) {
                return object != null ? object : "";
            }

            @Override
            public String fromString(String string) {
                return string != null && !string.isEmpty() ? string : null;
            }
        }, "", change -> change.getControlNewText().matches("\\d{0," + maxNum + "}") ? change : null);
    }

}
