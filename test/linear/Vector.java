package linear;

import base.Annotated;
import clojure.lang.PersistentVector;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class Vector<T extends Number> implements Tensor<T> {
    private final List<? extends Tensor<T>> data;

    @SafeVarargs
    public Vector(final Tensor<T>... data) {
        this(Arrays.asList(data));
    }

    public Vector(final List<? extends Tensor<T>> data) {
        this.data = data;
    }

    public List<? extends Tensor<T>> data() {
        return Collections.unmodifiableList(data);
    }

    public int size() {
        return data.size();
    }

    public Tensor<T> get(int i) {
        return data.get(i);
    }

    @Override
    public Annotated<Object> toClojure() {
        final List<Annotated<Object>> annotatedData = data.stream().map(Tensor::toClojure).toList();
        return new Annotated<>(
                PersistentVector.create(annotatedData.stream().map(Annotated::value).toList()),
                annotatedData.stream().map(Annotated::context).collect(Collectors.joining(" ", "[", "]"))
        );
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof final Vector<?> vector) {
            return Objects.equals(data, vector.data);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(data);
    }

    @Override
    public String toString() {
        return data.stream().map(Objects::toString).collect(Collectors.joining(" ", "[", "]"));
    }
}