package ed.inf.adbs.minibase.base;

import ed.inf.adbs.minibase.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class RelationalAtom extends Atom {
    private String name;

    private List<Term> terms;

    public RelationalAtom(String name, List<Term> terms) {
        this.name = name;
        this.terms = terms;
    }

    public String getName() {
        return name;
    }

    public List<Term> getTerms() {
        return terms;
    }

    /**
     * Overriding equals. RelationalAtom objects should be equal if they have the same name and terms.
     * @param o The object to be compared with.
     * @return true if the object is an equivalent RelationalAtom object, else false.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RelationalAtom)) return false;
        RelationalAtom that = (RelationalAtom) o;
        return name.equals(that.name) && terms.equals(that.terms);
    }

    /**
     * Overriding hashcode since we overridden equals.
     * @return int hashcode.
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, terms);
    }

    @Override
    public String toString() {
        return name + "(" + Utils.join(terms, ", ") + ")";
    }

    /**
     * Method that maps the terms of the RelationalAtom according to a given mapping.
     * @param mapping A hashmap that maps terms to terms.
     * @return A newly made RelationalAtom object which is the result of the mapping.
     */
    public RelationalAtom mapTerms(HashMap<Term, Term> mapping) {
        List<Term> mappedTerms = new ArrayList<>();

        for (Term term : terms) {
            mappedTerms.add(mapping.getOrDefault(term, term));
        }

        return new RelationalAtom(name, mappedTerms);
    }
}
