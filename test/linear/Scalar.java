package linear;

import base.Annotated;

import java.util.Objects;

public final class Scalar<T extends Number> implements Tensor<T> {
    private final T value;

    public Scalar(final T value) {
        this.value = value;
    }

    public T value() {
        return value;
    }

    @Override
    public Annotated<Object> toClojure() {
        return new Annotated<>(value, value.toString());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof final Scalar<?> scalar) {
            return Objects.equals(value, scalar.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}