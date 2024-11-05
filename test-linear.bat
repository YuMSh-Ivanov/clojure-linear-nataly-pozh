@echo off

cd "%~dp0"

javac -cp "test;src;lib\clojure-1.12.0.jar;lib\core.specs.alpha-0.4.74.jar;lib\hamcrest-core-1.3.jar;lib\junit-4.13.2.jar;lib\spec.alpha-0.5.238.jar" -d "out" "test\linear\LinearTest.java"
if %errorlevel% neq 0 exit /b %errorlevel%
java -ea -cp "out;src;lib\clojure-1.12.0.jar;lib\core.specs.alpha-0.4.74.jar;lib\hamcrest-core-1.3.jar;lib\junit-4.13.2.jar;lib\spec.alpha-0.5.238.jar" org.junit.runner.JUnitCore linear.LinearTest
:: rd /q /s out
