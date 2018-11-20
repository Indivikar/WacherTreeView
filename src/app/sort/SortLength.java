package app.sort;

import java.util.Comparator;

public class SortLength implements Comparator<String> {

    private int referenceLength;

    public SortLength(String reference) {
        super();
        this.referenceLength = reference.length();
    }

    public int compare(String s1, String s2) {
        int dist1 = Math.abs(s1.length() - referenceLength);
        int dist2 = Math.abs(s2.length() - referenceLength);

        return dist1 - dist2;
    }
}
