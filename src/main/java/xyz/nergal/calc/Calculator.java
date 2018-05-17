/*
sources:
- http://rosettacode.org/wiki/Parsing/Shunting-yard_algorithm#Java
- https://technologyconversations.com/2014/03/28/java-8-tutorial-through-katas-reverse-polish-notation-medium/
*/

package xyz.nergal.calc;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class Calculator {
    public double calc(String calculation) {
        String rpn = buildRPN(calculation);
        return evalRPN(rpn);
    }

    private List<String> buildTokens(String input) {
        List<String> tokens = new ArrayList<String>();
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            if(input.charAt(i) == ' '){
                continue;
            }
            if (input.charAt(i) >= '0' && input.charAt(i) <= '9') {
                token.append(input.charAt(i));
            } else {
                if (token.length() > 0) {
                    tokens.add(token.toString());
                    token.setLength(0);
                }
                token.append(input.charAt(i));
                tokens.add(token.toString());
                token.setLength(0);
            }
        }
        tokens.add(token.toString());
        return tokens;
    }

    private String buildRPN(String input) {
        final String ops = "-+/*";

        StringBuilder sb = new StringBuilder();
        Stack<Integer> s = new Stack<>();


        for (String token : buildTokens(input)) {
            if (token.isEmpty())
                continue;
            char c = token.charAt(0);
            int idx = ops.indexOf(c);

            // check for operator
            if (idx != -1) {
                if (s.isEmpty())
                    s.push(idx);

                else {
                    while (!s.isEmpty()) {
                        int prec2 = s.peek() / 2;
                        int prec1 = idx / 2;
                        if (prec2 > prec1 || (prec2 == prec1))
                            sb.append(ops.charAt(s.pop())).append(' ');
                        else break;
                    }
                    s.push(idx);
                }
            } else if (c == '(') {
                s.push(-2); // -2 stands for '('
            } else if (c == ')') {
                // until '(' on stack, pop operators.
                while (s.peek() != -2)
                    sb.append(ops.charAt(s.pop())).append(' ');
                s.pop();
            } else {
                sb.append(token).append(' ');
            }
        }
        while (!s.isEmpty())
            sb.append(ops.charAt(s.pop())).append(' ');
        return sb.toString();
    }

    private double evalRPN(String input) {
        Stack<Double> numbers = new Stack();
        String[] tokens = input.split("\\s+");

        Stream.of(tokens).forEach(token -> {
            if ("+".equals(token)) {
                calcSign(numbers, (n1, n2) -> n2 + n1);
            } else if ("-".equals(token)) {
                calcSign(numbers, (n1, n2) -> n2 - n1);
            } else if ("*".equals(token)) {
                calcSign(numbers, (n1, n2) -> n2 * n1);
            } else if ("/".equals(token)) {
                calcSign(numbers, (n1, n2) -> n2 / n1);
            } else {
                numbers.push(Double.parseDouble(token));
            }
        });
        return numbers.pop();
    }

    private Stack<Double> calcSign(Stack<Double> numbers, BiFunction<Double, Double, Double> operation) {
        numbers.push(operation.apply(numbers.pop(), numbers.pop()));
        return numbers;
    }

}
