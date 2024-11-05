package linear;

import base.Annotated;
import base.ClojureCall;
import clojure.lang.IFn;
import org.junit.Assert;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public record FunctionTester<T extends Number>(Function<List<? extends Tensor<T>>, ? extends Tensor<T>> expected, IFn actual, String name) {
    public void test(final List<Tensor<T>> objects) {
        final Tensor<T> expected = this.expected.apply(objects);
        final List<Annotated<Object>> clojureAnnotations = objects.stream().map(Tensor::toClojure).toList();
        final String context = clojureAnnotations.stream().map(Annotated::context).collect(Collectors.joining(" ", "(" + name + " ", ")"));
        final Object[] clojureObjects = clojureAnnotations.stream().map(Annotated::value).toArray();
        final Tensor<?> actual = Tensor.fromClojure(ClojureCall.nullable(this.actual, context, clojureObjects), context);
        Assert.assertEquals(context, expected, actual);
    }

    public void expectThrow(final List<Tensor<T>> objects) {
        final List<Annotated<Object>> clojureAnnotations = objects.stream().map(Tensor::toClojure).toList();
        final String context = clojureAnnotations.stream().map(Annotated::context).collect(Collectors.joining(" ", "(" + name + " ", ")"));
        final Object[] clojureObjects = clojureAnnotations.stream().map(Annotated::value).toArray();
        ClojureCall.expectThrow(this.actual, context, AssertionError.class, clojureObjects);
    }
}
