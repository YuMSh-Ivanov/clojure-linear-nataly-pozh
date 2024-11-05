package linear;

import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

public record OperatorOverload<T>(UnaryOperator<T> unary, BinaryOperator<T> binary) {
}
