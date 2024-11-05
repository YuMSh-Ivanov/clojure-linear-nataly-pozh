package linear;

import base.Annotated;
import clojure.lang.IPersistentVector;
import clojure.lang.PersistentVector;
import org.junit.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public sealed interface Tensor<T extends Number> permits Scalar, Vector {
    Annotated<Object> toClojure();

    static Tensor<?> fromClojure(final Object o, final String context) {
        if (o instanceof final Number num) {
            return new Scalar<>(num);
        } else if (o instanceof final IPersistentVector vector) {
            return new Vector<>(IntStream.range(0, vector.length()).mapToObj(vector::nth).map(t -> fromClojure(t, context)).toList());
        }

        if (o == null) {
            Assert.fail("Returned null object from " + context);
        } else {
            Assert.fail("Returned object of type " + o.getClass().getCanonicalName() + " [expected number or vector] from " + context);
        }
        return null;
    }

    static <T extends Number> Tensor<T> generate(final Supplier<T> rng, final int... shape) {
        return generate(rng, Arrays.stream(shape).boxed().toList());
    }

    private static <T extends Number> Tensor<T> generate(final Supplier<T> rng, final List<Integer> shape) {
        if (shape.isEmpty()) {
            return new Scalar<>(rng.get());
        } else {
            final List<Integer> shapeRest = shape.subList(1, shape.size());
            return new Vector<>(IntStream.range(0, shape.getFirst()).mapToObj(i -> generate(rng, shapeRest)).toList());
        }
    }
}
