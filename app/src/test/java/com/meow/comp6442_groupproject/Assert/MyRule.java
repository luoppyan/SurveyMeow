package com.meow.comp6442_groupproject.Assert;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class MyRule implements TestRule {

    @Override
    public Statement apply(final Statement base, final Description description) {

        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                // call before test method execute
                String methodName = description.getMethodName();
                System.out.println(methodName + "Test Start！");

                base.evaluate();

                // call after test method execute
                System.out.println(methodName + "Test Finished！");
            }
        };
    }
}