package ed.inf.adbs.minibase.base;

import java.util.Objects;

public class StringConstant extends Constant {
    private String value;

    public StringConstant(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Overriding equals. StringConstant objects should be equal if they have the same value.
     * @param o The object to be compared with.
     * @return true if the object is an equivalent StringConstant object, else false.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StringConstant)) return false;
        StringConstant that = (StringConstant) o;
        return value.equals(that.value);
    }

    /**
     * Overriding hashcode since we overridden equals.
     * @return int hashcode.
     */
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "'" + value + "'";
    }
}