package buzz.blaize;

public enum BotType {
    ADAPTIVE("adaptive"),

    ALLIN("allin"),

    BAYESIAN("bayesian"),

    BLUFFER("bluffer"),

    CALLSTATION("callstation"),

    FLOAT("float"),

    GEARSHIFT("gearshift"),

    LIMPER("limper"),

    MINRAISER("minraiser"),

    POSAWARE("posaware"),

    POTODDS("potodds"),

    RANDOM("random"),

    SHORTSTACK("shortstack"),

    TAG("tag"),

    THREEBET("threebet"),

    TRAPPER("trapper");

    String value;
    BotType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
