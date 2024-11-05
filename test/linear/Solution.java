package linear;

import java.util.List;
import java.util.stream.IntStream;

public final class Solution {
    private Solution() {
    }

    private static <T extends Number> Vector<T> compWise(final List<? extends Tensor<T>> tensors, final OperatorOverload<Tensor<T>> oper) {
        final Vector<T> first = (Vector<T>) tensors.getFirst();
        if (tensors.size() == 1) {
            return new Vector<>(IntStream.range(0, first.size())
                    .mapToObj(i -> oper.unary().apply(first.get(i)))
                    .toList());
        } else {
            return new Vector<>(IntStream.range(0, first.size())
                    .mapToObj(i ->
                            tensors.stream()
                                    .map(t -> (Vector<T>) t)
                                    .map(v -> v.get(i))
                                    .reduce(oper.binary())
                                    .get()
                    )
                    .toList());
        }
    }

    public static <T extends Number> Vector<T> vectorCompWise(final List<? extends Tensor<T>> tensors, final OperatorOverload<T> oper) {
        return compWise(
                tensors,
                new OperatorOverload<>(
                        t -> new Scalar<>(oper.unary().apply(((Scalar<T>) t).value())),
                        (t1, t2) -> new Scalar<>(oper.binary().apply(((Scalar<T>) t1).value(), ((Scalar<T>) t2).value()))
                )
        );
    }

    public static <T extends Number> Vector<T> matrixCompWise(final List<? extends Tensor<T>> tensors, final OperatorOverload<T> oper) {
        return compWise(
                tensors,
                new OperatorOverload<>(
                        t -> vectorCompWise(List.of(t), oper),
                        (t1, t2) -> vectorCompWise(List.of(t1, t2), oper)
                )
        );
    }

    public static <T extends Number> Scalar<T> inner(final List<? extends Tensor<T>> tensors, final OperatorOverload<T> add, final OperatorOverload<T> mul) {
        final Vector<T> in = vectorCompWise(tensors, mul);
        if (in.size() == 1) {
            return new Scalar<>(add.unary().apply(((Scalar<T>) in.get(0)).value()));
        } else {
            return new Scalar<>(in.data().stream().map(t -> (Scalar<T>) t).map(Scalar::value).reduce(add.binary()).get());
        }
    }

    public static <T extends Number> Vector<T> vectorScale(final List<? extends Tensor<T>> tensors, final OperatorOverload<T> oper) {
        final Vector<T> first = (Vector<T>) tensors.getFirst();
        if (tensors.size() == 1) {
            return first;
        }
        final T scaler = tensors.subList(1, tensors.size()).stream().map(t -> (Scalar<T>) t).map(Scalar::value).reduce(oper.binary()).get();
        return vectorCompWise(List.of(first), new OperatorOverload<>(t -> oper.binary().apply(t, scaler), null));
    }

    public static <T extends Number> Vector<T> matrixScale(final List<? extends Tensor<T>> tensors, final OperatorOverload<T> oper) {
        final Vector<T> first = (Vector<T>) tensors.getFirst();
        if (tensors.size() == 1) {
            return first;
        }
        final T scaler = tensors.subList(1, tensors.size()).stream().map(t -> (Scalar<T>) t).map(Scalar::value).reduce(oper.binary()).get();
        return matrixCompWise(List.of(first), new OperatorOverload<>(t -> oper.binary().apply(t, scaler), null));
    }

    private static <T extends Number> Vector<T> transposeImpl(final Vector<T> matrix) {
        final int resSize = ((Vector<T>) matrix.get(0)).size();
        return new Vector<>(
                IntStream.range(0, resSize)
                        .mapToObj(i -> new Vector<>(matrix.data()
                                .stream()
                                .map(t -> (Vector<T>) t)
                                .map(v -> v.get(i))
                                .toList()))
                        .toList()
        );
    }

    public static <T extends Number> Vector<T> transpose(final List<? extends Tensor<T>> tensors) {
        return transposeImpl((Vector<T>) tensors.getFirst());
    }

    private static <T extends Number> Scalar<T> dotImpl(final Tensor<T> lhs, final Tensor<T> rhs, final OperatorOverload<T> add, final OperatorOverload<T> mul) {
        return new Scalar<>(vectorCompWise(List.of(lhs, rhs), mul).data().stream().map(t -> (Scalar<T>) t).map(Scalar::value).reduce(add.binary()).get());
    }

    public static <T extends Number> Vector<T> transposeMultiplyV(final List<? extends Tensor<T>> tensors, final OperatorOverload<T> add, final OperatorOverload<T> mul) {
        final Vector<T> firstT = (Vector<T>) tensors.get(0);
        final Vector<T> second = (Vector<T>) tensors.get(1);
        return new Vector<>(firstT.data().stream().map(t -> dotImpl(t, second, add, mul)).toList());
    }

    private static <T extends Number> Vector<T> transposeMultiplyM(final Vector<T> first, final Vector<T> second, final OperatorOverload<T> add, final OperatorOverload<T> mul) {
        final Vector<T> secondT = transposeImpl(second);
        return new Vector<>(first.data().stream()
                .map(t1 -> new Vector<>(secondT.data().stream().map(t2 -> dotImpl(t1, t2, add, mul)).toList()))
                .toList());
    }

    public static <T extends Number> Vector<T> compose(final List<? extends Tensor<T>> tensors, final OperatorOverload<T> add, final OperatorOverload<T> mul) {
        return tensors.stream().map(t -> (Vector<T>) t).reduce((t1, t2) -> transposeMultiplyM(t1, t2, add, mul)).get();
    }
}
