package org.eclipse.yasson;

import static org.junit.jupiter.api.Assertions.*;

import java.util.function.Function;
import java.util.function.Supplier;

import javax.json.bind.JsonbException;

public class Assertions {
	
	/**
	 * Asserts that the given operation will fail with a JsonbException
	 * @param operation The operation that is expected to fail
	 */
	public static void shouldFail(Supplier<?> operation) {
		shouldFail(operation, JsonbException.class, msg -> true);
	}
	
	public static void shouldFail(Runnable operation) {
		shouldFail(() -> {
			operation.run();
			return null;
		});
	}
	
	/**
	 * Asserts that the given operation will fail with a JsonbException
	 * @param operation The operation that is expected to fail
	 * @param checkExceptionMessage Any checks that should be made on the exception message. For example, ensuring the exception
	 * includes a specific token. 
	 */
	public static void shouldFail(Supplier<?> operation, Function<String,Boolean> checkExceptionMessage) {
		shouldFail(operation, JsonbException.class, checkExceptionMessage);
	}

	/**
	 * Asserts that the given operation will fail
	 * @param operation The operation that is expected to fail
	 * @param expectedType The expected exception type to receive when evaluating the operation
	 * @param checkExceptionMessage Any checks that should be made on the exception message. For example, ensuring the exception
	 * includes a specific token. 
	 */
	public static void shouldFail(Supplier<?> operation, Class<? extends Throwable> expectedType, Function<String,Boolean> checkExceptionMessage) {
		try {
			operation.get();
			fail("The operation should have failed with a " + expectedType.getCanonicalName() + " but it succeeded.");
		} catch (Throwable t) {
			String fullErrorMessage = "";
			for (Throwable current = t; current != null && current.getCause() != current; current = current.getCause()) {
			    fullErrorMessage += current.getClass().getCanonicalName() + ": ";
				fullErrorMessage += current.getMessage() + "\n";
			}
			if (expectedType.isAssignableFrom(t.getClass())) {
				if (!checkExceptionMessage.apply(fullErrorMessage)) {
					t.printStackTrace();
					fail("Exception did not contain the proper content: " + fullErrorMessage);
				}
			} else {
				t.printStackTrace();
				fail("Expected to get an exception of " + expectedType + " but instead was " + t.getClass());
			}
		}
		
	}

}
