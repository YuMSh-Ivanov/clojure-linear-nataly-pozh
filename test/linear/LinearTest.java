package linear;

import base.ClojureCall;
import base.ClojureNamespace;
import org.junit.Assert;
import org.junit.Test;

import clojure.java.api.Clojure;
import clojure.lang.IFn;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class LinearTest {
    private static final ClojureNamespace linearNs = ClojureNamespace.load("linear");
    private static final ClojureNamespace clojureCoreNs = ClojureNamespace.require("clojure.core");

    private static final OperatorOverload<Double> addOp = new OperatorOverload<>(a -> a, Double::sum);
    private static final OperatorOverload<Double> subtractOp = new OperatorOverload<>(a -> -a, (a, b) -> a - b);
    private static final OperatorOverload<Double> multiplyOp = new OperatorOverload<>(a -> a, (a, b) -> a * b);
    private static final OperatorOverload<Double> divideOp = new OperatorOverload<>(a -> 1 / a, (a, b) -> a / b);

    private static final FunctionTester<Double> vAdd = new FunctionTester<>(
            lst -> Solution.vectorCompWise(lst, addOp),
            linearNs.var("v+"), "v+"
    );
    private static final FunctionTester<Double> vSubtract = new FunctionTester<>(
            lst -> Solution.vectorCompWise(lst, subtractOp),
            linearNs.var("v-"), "v-"
    );
    private static final FunctionTester<Double> vMultiply = new FunctionTester<>(
            lst -> Solution.vectorCompWise(lst, multiplyOp),
            linearNs.var("v*"), "v*"
    );
    private static final FunctionTester<Double> vDivide = new FunctionTester<>(
            lst -> Solution.vectorCompWise(lst, divideOp),
            linearNs.var("vd"), "vd"
    );

    private static final FunctionTester<Double> mAdd = new FunctionTester<>(
            lst -> Solution.matrixCompWise(lst, addOp),
            linearNs.var("m+"), "m+"
    );
    private static final FunctionTester<Double> mSubtract = new FunctionTester<>(
            lst -> Solution.matrixCompWise(lst, subtractOp),
            linearNs.var("m-"), "m-"
    );
    private static final FunctionTester<Double> mMultiply = new FunctionTester<>(
            lst -> Solution.matrixCompWise(lst, multiplyOp),
            linearNs.var("m*"), "m*"
    );
    private static final FunctionTester<Double> mDivide = new FunctionTester<>(
            lst -> Solution.matrixCompWise(lst, divideOp),
            linearNs.var("md"), "md"
    );

    private static final FunctionTester<Double> inner = new FunctionTester<>(
            lst -> Solution.inner(lst, addOp, multiplyOp),
            linearNs.var("dot"), "dot"
    );

    private static final FunctionTester<Double> vTimesS = new FunctionTester<>(
            lst -> Solution.vectorScale(lst, multiplyOp),
            linearNs.var("v*s"), "v*s"
    );
    private static final FunctionTester<Double> mTimesS = new FunctionTester<>(
            lst -> Solution.matrixScale(lst, multiplyOp),
            linearNs.var("m*s"), "m*s"
    );
    private static final FunctionTester<Double> mTimesV = new FunctionTester<>(
            lst -> Solution.transposeMultiplyV(lst, addOp, multiplyOp),
            linearNs.var("m*v"), "m*v"
    );
    private static final FunctionTester<Double> mTimesM = new FunctionTester<>(
            lst -> Solution.compose(lst, addOp, multiplyOp),
            linearNs.var("m*m"), "m*m"
    );

    private static final FunctionTester<Double> transpose = new FunctionTester<>(
            Solution::transpose,
            linearNs.var("transpose"), "transpose"
    );

    private static final Random rng = new Random(1043751861432829521L);

    private static double generateDouble() {
        return rng.nextLong(-10_000, 10_000) / 100.0;
    }

    private static double generateNonZeroDouble() {
        double value;
        do {
            value = rng.nextLong(-10_000, 10_000) / 100.0;
        } while (value == 0);
        return value;
    }

    private static Supplier<Tensor<Double>> scalarGen() {
        return () -> Tensor.generate(LinearTest::generateDouble);
    }

    private static Supplier<Tensor<Double>> vectorGen(int dim) {
        return () -> Tensor.generate(LinearTest::generateDouble, dim);
    }

    private static Supplier<Tensor<Double>> vectorGenNonZero(int dim) {
        return () -> Tensor.generate(LinearTest::generateNonZeroDouble, dim);
    }

    private static Supplier<Tensor<Double>> matrixGen(int dim1, int dim2) {
        return () -> Tensor.generate(LinearTest::generateDouble, dim1, dim2);
    }

    private static Supplier<Tensor<Double>> matrixGenNonZero(int dim1, int dim2) {
        return () -> Tensor.generate(LinearTest::generateNonZeroDouble, dim1, dim2);
    }

    private static void testRandom(int count) {
        for (int dim = 1; dim <= 10; dim++) {
            vAdd.test(Stream.generate(vectorGen(dim)).limit(count).toList());
            vSubtract.test(Stream.generate(vectorGen(dim)).limit(count).toList());
            vMultiply.test(Stream.generate(vectorGen(dim)).limit(count).toList());
            vDivide.test(Stream.generate(vectorGenNonZero(dim)).limit(count).toList());
            inner.test(Stream.generate(vectorGen(dim)).limit(count).toList());
            vTimesS.test(Stream.concat(Stream.of(vectorGen(dim).get()), Stream.generate(scalarGen()).limit(count - 1)).toList());
        }

        for (int complexity = 1; complexity <= 10; complexity++) {
            for (int dim1 = 1; dim1 <= complexity; dim1++) {
                final int dim2 = 1 + complexity - dim1;
                mAdd.test(Stream.generate(matrixGen(dim1, dim2)).limit(count).toList());
                mSubtract.test(Stream.generate(matrixGen(dim1, dim2)).limit(count).toList());
                mMultiply.test(Stream.generate(matrixGen(dim1, dim2)).limit(count).toList());
                mDivide.test(Stream.generate(matrixGenNonZero(dim1, dim2)).limit(count).toList());
                mTimesS.test(Stream.concat(Stream.of(matrixGen(dim1, dim2).get()), Stream.generate(scalarGen()).limit(count - 1)).toList());
                mTimesV.test(List.of(matrixGen(dim1, dim2).get(), vectorGen(dim2).get()));
                transpose.test(List.of(matrixGen(dim1, dim2).get()));
            }
            final int maxDim = complexity;
            final int[] dims = IntStream.generate(() -> rng.nextInt(1, maxDim + 1)).limit(count + 1).toArray();
            mTimesM.test(IntStream.range(0, count).mapToObj(i -> matrixGen(dims[i], dims[i + 1]).get()).toList());
        }
    }

    @Test
    public void testValid() {
        testRandom(2);
        testRandom(1);
        Assert.assertEquals("(dot) is expected to be zero",
                ClojureCall.nonNullTyped(inner.actual(), "(dot)", Number.class, new Object[0]).doubleValue(),
                0.0,
                0.0
        );
        testRandom(3);
        testRandom(4);
        testRandom(5);
    }

    @Test
    public void testInvalid() {
        for (final FunctionTester<Double> tester : List.of(vAdd, vSubtract, vMultiply, vDivide, inner)) {
            tester.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble),
                    Tensor.generate(LinearTest::generateDouble)
            ));
            tester.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble),
                    Tensor.generate(LinearTest::generateDouble, 3)
            ));
            tester.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3),
                    Tensor.generate(LinearTest::generateDouble)
            ));

            tester.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3),
                    Tensor.generate(LinearTest::generateDouble, 3, 3)
            ));
            tester.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3, 3),
                    Tensor.generate(LinearTest::generateDouble, 3)
            ));
            tester.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3, 3),
                    Tensor.generate(LinearTest::generateDouble, 3, 3)
            ));

            tester.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 4),
                    Tensor.generate(LinearTest::generateDouble, 3)
            ));
            tester.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3),
                    Tensor.generate(LinearTest::generateDouble, 4)
            ));
            tester.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 2),
                    Tensor.generate(LinearTest::generateDouble, 3),
                    Tensor.generate(LinearTest::generateDouble, 3)
            ));
            tester.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3),
                    Tensor.generate(LinearTest::generateDouble, 2),
                    Tensor.generate(LinearTest::generateDouble, 3)
            ));
            tester.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3),
                    Tensor.generate(LinearTest::generateDouble, 3),
                    Tensor.generate(LinearTest::generateDouble, 2)
            ));
        }

        for (final FunctionTester<Double> tester : List.of(mAdd, mSubtract, mMultiply, mDivide)) {
            tester.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble),
                    Tensor.generate(LinearTest::generateDouble)
            ));
            tester.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble),
                    Tensor.generate(LinearTest::generateDouble, 3, 3)
            ));
            tester.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3, 3),
                    Tensor.generate(LinearTest::generateDouble)
            ));

            tester.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3),
                    Tensor.generate(LinearTest::generateDouble, 3)
            ));

            tester.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3),
                    Tensor.generate(LinearTest::generateDouble, 3, 3)
            ));
            tester.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3, 3),
                    Tensor.generate(LinearTest::generateDouble, 3)
            ));

            tester.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3, 3, 3),
                    Tensor.generate(LinearTest::generateDouble, 3, 3, 3)
            ));

            tester.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 4, 3),
                    Tensor.generate(LinearTest::generateDouble, 3, 3)
            ));
            tester.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3, 3),
                    Tensor.generate(LinearTest::generateDouble, 3, 4)
            ));
            tester.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 2, 3),
                    Tensor.generate(LinearTest::generateDouble, 3, 3),
                    Tensor.generate(LinearTest::generateDouble, 3, 3)
            ));
            tester.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3, 3),
                    Tensor.generate(LinearTest::generateDouble, 2, 3),
                    Tensor.generate(LinearTest::generateDouble, 3, 3)
            ));
            tester.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3, 3),
                    Tensor.generate(LinearTest::generateDouble, 3, 3),
                    Tensor.generate(LinearTest::generateDouble, 2, 3)
            ));
            tester.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3, 5),
                    Tensor.generate(LinearTest::generateDouble, 3, 3),
                    Tensor.generate(LinearTest::generateDouble, 3, 3)
            ));
            tester.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3, 3),
                    Tensor.generate(LinearTest::generateDouble, 3, 5),
                    Tensor.generate(LinearTest::generateDouble, 3, 3)
            ));
            tester.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3, 3),
                    Tensor.generate(LinearTest::generateDouble, 3, 3),
                    Tensor.generate(LinearTest::generateDouble, 3, 5)
            ));
        }

        {
            vTimesS.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble),
                    Tensor.generate(LinearTest::generateDouble)
            ));
            vTimesS.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3, 3),
                    Tensor.generate(LinearTest::generateDouble)
            ));
            vTimesS.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3),
                    Tensor.generate(LinearTest::generateDouble, 3)
            ));
            vTimesS.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3),
                    Tensor.generate(LinearTest::generateDouble),
                    Tensor.generate(LinearTest::generateDouble, 3)
            ));
            vTimesS.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble),
                    Tensor.generate(LinearTest::generateDouble, 3)
            ));
        }

        {
            mTimesS.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble),
                    Tensor.generate(LinearTest::generateDouble)
            ));
            mTimesS.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3),
                    Tensor.generate(LinearTest::generateDouble)
            ));
            mTimesS.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3, 3, 3),
                    Tensor.generate(LinearTest::generateDouble)
            ));
            mTimesS.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3, 3),
                    Tensor.generate(LinearTest::generateDouble, 3, 3)
            ));
            mTimesS.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3, 3),
                    Tensor.generate(LinearTest::generateDouble),
                    Tensor.generate(LinearTest::generateDouble, 3, 3)
            ));
            mTimesS.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble),
                    Tensor.generate(LinearTest::generateDouble, 3, 3)
            ));
            mTimesS.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3, 3),
                    Tensor.generate(LinearTest::generateDouble, 3)
            ));
            mTimesS.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3, 3),
                    Tensor.generate(LinearTest::generateDouble),
                    Tensor.generate(LinearTest::generateDouble, 3)
            ));
        }

        {
            mTimesV.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3),
                    Tensor.generate(LinearTest::generateDouble)
            ));
            mTimesV.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3, 3),
                    Tensor.generate(LinearTest::generateDouble)
            ));
            mTimesV.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3),
                    Tensor.generate(LinearTest::generateDouble, 3)
            ));
            mTimesV.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3),
                    Tensor.generate(LinearTest::generateDouble, 3, 3)
            ));
            mTimesV.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3, 3),
                    Tensor.generate(LinearTest::generateDouble)
            ));

            mTimesV.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3, 2),
                    Tensor.generate(LinearTest::generateDouble, 3)
            ));
            mTimesV.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3, 3),
                    Tensor.generate(LinearTest::generateDouble, 4)
            ));
        }

        {
            transpose.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble)
            ));
            transpose.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3)
            ));
            transpose.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3, 3, 3)
            ));
        }

        {
            mTimesM.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble),
                    Tensor.generate(LinearTest::generateDouble)
            ));
            mTimesM.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3),
                    Tensor.generate(LinearTest::generateDouble, 3)
            ));
            mTimesM.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3, 3),
                    Tensor.generate(LinearTest::generateDouble, 3)
            ));
            mTimesM.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3, 3),
                    Tensor.generate(LinearTest::generateDouble)
            ));
            mTimesM.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3),
                    Tensor.generate(LinearTest::generateDouble, 3, 3)
            ));
            mTimesM.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3, 4),
                    Tensor.generate(LinearTest::generateDouble, 3, 4)
            ));
            mTimesM.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3, 2),
                    Tensor.generate(LinearTest::generateDouble, 3, 3)
            ));
            mTimesM.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3, 2),
                    Tensor.generate(LinearTest::generateDouble, 4, 3),
                    Tensor.generate(LinearTest::generateDouble, 3, 3)
            ));
            mTimesM.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3, 3),
                    Tensor.generate(LinearTest::generateDouble, 3, 4),
                    Tensor.generate(LinearTest::generateDouble, 2, 3)
            ));
            mTimesM.expectThrow(List.of(
                    Tensor.generate(LinearTest::generateDouble, 3, 4),
                    Tensor.generate(LinearTest::generateDouble, 3, 3),
                    Tensor.generate(LinearTest::generateDouble, 4, 3)
            ));
        }
    }
}
