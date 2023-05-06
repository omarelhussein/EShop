package shop.ui.cui.enums;

public enum AuswahlTyp {
    MITARBEITER, KUNDE, START;

    public String getKeyPrefix() {
        return switch (this) {
            case MITARBEITER -> "m";
            case KUNDE -> "k";
            case START -> "s";
        };
    }

}
