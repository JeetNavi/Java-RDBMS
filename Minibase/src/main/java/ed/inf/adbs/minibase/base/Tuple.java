package ed.inf.adbs.minibase.base;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Tuple {

    private Constant[] values;

    public Tuple (Constant[] values) {this.values = values;}

    public Constant[] getValues() {
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple)) return false;
        Tuple tuple = (Tuple) o;
        return Arrays.equals(values, tuple.values);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(values);
    }

    @Override
    public String toString() {
        return "Tuple{" +
                "values=" + Arrays.toString(values) +
                '}';
    }
}
