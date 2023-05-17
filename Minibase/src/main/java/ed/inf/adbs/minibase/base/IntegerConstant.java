package ed.inf.adbs.minibase.base;

import java.util.Objects;

public class IntegerConstant extends Constant {
    private Integer value;

    public IntegerConstant(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    /**
     * Overriding equals. IntegerConstant objects should be equal if they have the same value.
     * @param o The object to be compared with.
     * @return true if the object is an equivalent IntegerConstant object, else false.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntegerConstant)) return false;
        IntegerConstant that = (IntegerConstant) o;
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
        return value.toString();
    }
}