/**
 * This class is designed outside any packages intentionally to test serialization/deserialization scenarios for such
 * cases
 *
 * Ehsan Zaery Moghaddam (zaerymoghaddam@gmail.com)
 */
public class PackagelessModel {
    private int intValue;
    private String stringValue;

    public PackagelessModel() {
    }

    public PackagelessModel(int intValue, String stringValue) {
        this.intValue = intValue;
        this.stringValue = stringValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }
}
