package base;

import clojure.lang.ArraySeq;
import clojure.lang.IFn;
import org.junit.Assert;

public final class ClojureCall {
    private ClojureCall() {}

    public static Object nullable(final IFn f, final String context, final Object[] args) {
        try {
            return rawCall(f, args);
        } catch (Exception | StackOverflowError | AssertionError e) {
            throw new ClojureException("No error expected in " + context, e);
        }
    }

    public static Object nonNull(final IFn f, final String context, final Object[] args) {
        final Object result = nullable(f, context, args);
        if (result == null) {
            throw new ClojureException("Expected non-null in " + context);
        }
        return result;
    }

    public static <T> T nullableTyped(final IFn f, final String context, final Class<T> clazz, final Object[] args) {
        final Object result = nullable(f, context, args);
        if (result == null) {
            return null;
        }
        if (!clazz.isAssignableFrom(result.getClass())) {
            throw new ClojureException(String.format("Expected type %s, found %s of type %s in %s", clazz.getCanonicalName(), result, result.getClass().getCanonicalName(), context));
        }
        return clazz.cast(result);
    }

    public static <T> T nonNullTyped(final IFn f, final String context, final Class<T> clazz, final Object[] args) {
        final Object result = nullable(f, context, args);
        if (result == null) {
            throw new ClojureException(String.format("Expected non-null of type %s, found null in %s", clazz.getCanonicalName(), context));
        }
        if (!clazz.isAssignableFrom(result.getClass())) {
            throw new ClojureException(String.format("Expected non-null of type %s, found %s of type %s in %s", clazz.getCanonicalName(), result, result.getClass().getCanonicalName(), context));
        }
        return clazz.cast(result);
    }

    public static void expectThrow(final IFn f, final String context, final Class<? extends Throwable> errorClass, final Object[] args) {
        final Object result;
        try {
            result = rawCall(f, args);
        } catch (Exception | StackOverflowError | AssertionError e) {
            if (errorClass.isAssignableFrom(e.getClass())) {
                return;
            }
            throw new ClojureException(String.format("Expected error of type %s, found of type %s in %s", errorClass.getCanonicalName(), e.getClass().getCanonicalName(), context), e);
        }
        throw new ClojureException(String.format("Expected error of type %s, found %s in %s", errorClass.getCanonicalName(), result, context));
    }

    private static Object rawCall(final IFn f, final Object[] args) {
        return switch (args.length) {
            case 0 -> f.invoke();
            case 1 -> f.invoke(args[0]);
            case 2 -> f.invoke(args[0], args[1]);
            case 3 -> f.invoke(args[0], args[1], args[2]);
            case 4 -> f.invoke(args[0], args[1], args[2], args[3]);
            case 5 -> f.invoke(args[0], args[1], args[2], args[3], args[4]);
            case 6 -> f.invoke(args[0], args[1], args[2], args[3], args[4], args[5]);
            case 7 -> f.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
            case 8 -> f.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
            case 9 -> f.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]);
            case 10 ->
                    f.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9]);
            case 11 ->
                    f.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10]);
            case 12 ->
                    f.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11]);
            case 13 ->
                    f.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12]);
            case 14 ->
                    f.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13]);
            case 15 ->
                    f.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14]);
            case 16 ->
                    f.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15]);
            case 17 ->
                    f.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16]);
            case 18 ->
                    f.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17]);
            case 19 ->
                    f.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18]);
            case 20 ->
                    f.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18], args[19]);
            default -> f.applyTo(ArraySeq.create(args));
        };
    }
}
