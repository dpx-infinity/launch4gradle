package example;

import com.google.common.base.Joiner;

/**
 * Date: 04.04.2014
 * Time: 9:39
 */
public class Main {
    public static void main(String[] args) {
        System.out.println(Joiner.on('-').join("Hello", "world"));
    }
}
