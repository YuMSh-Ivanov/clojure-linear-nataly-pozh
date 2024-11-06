#!/bin/bash

set -e

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
cd "$SCRIPT_DIR"

javac -cp "test:src:lib/clojure-1.12.0.jar:lib/core.specs.alpha-0.4.74.jar:lib/hamcrest-core-1.3.jar:lib/junit-4.13.2.jar:lib/spec.alpha-0.5.238.jar" -d "out" "test/linear/LinearTest.java"
java -cp "out:src:lib/clojure-1.12.0.jar:lib/core.specs.alpha-0.4.74.jar:lib/hamcrest-core-1.3.jar:lib/junit-4.13.2.jar:lib/spec.alpha-0.5.238.jar" org.junit.runner.JUnitCore linear.LinearTest
