package MyTestListener;

import org.testng.IExecutionListener;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class listener implements IInvokedMethodListener, ITestListener, IExecutionListener {

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        System.out.println("BeforeInvocation " + method.getTestMethod().getMethodName() + " Started");
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        System.out.println("AfterInvocation " + method.getTestMethod().getMethodName() + " Finished");
    }

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("TestStart " + result.getMethod().getMethodName() + " Started");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("Success " + result.getMethod().getMethodName() + " Success");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("Failure " + result.getMethod().getMethodName() + " Failed");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("Skipped " + result.getMethod().getMethodName() + " Skipped");
    }

    @Override
    public void onExecutionStart() {
        System.out.println("Execution Started");
    }

    @Override
    public void onExecutionFinish() {
        System.out.println("Execution Finished");
    }
}
