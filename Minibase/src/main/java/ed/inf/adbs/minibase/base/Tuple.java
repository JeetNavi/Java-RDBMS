package ed.inf.adbs.minibase.base;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Tuple {

    private Constant[] values;
    private List<Variable> variables;

    public Tuple (Constant[] values, List<Variable> variables) {
        this.values = values;
        this.variables = variables;
    }

    public Constant[] getValues() {
        return values;
    }

    public Integer getPosOfVar(Variable variable) {
        return variables.indexOf(variable);
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public Constant getConstantFromVariable(Variable variable) {
        return values[variables.indexOf(variable)];
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