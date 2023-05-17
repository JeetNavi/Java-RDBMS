package ed.inf.adbs.minibase.base;

import java.util.Objects;

public class Variable extends Term {
    private String name;

    public Variable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Method to change the name of the variables.
     * Used to make rewriting queries easier.
     * @param name New name.
     */
    public void changeName(String name) {
        this.name = name;
    }

    /**
     * Overriding equals. Variable objects should be equal if they have the same name.
     * @param o The object to be compared with.
     * @return true if the object is an equivalent Variable object, else false.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Variable)) return false;
        Variable variable = (Variable) o;
        return name.equals(variable.name);
    }

    /**
     * Overriding hashcode since we overridden equals.
     * @return int hashcode.
     */
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}