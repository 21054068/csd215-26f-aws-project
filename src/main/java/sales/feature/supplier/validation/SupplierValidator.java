
package sales.feature.supplier.validation;

import sales.feature.base.validation.ValidationMessages;

/**
 * A 'namespace' class for types and functions related
 * to validating Supplier data.
 */
public class SupplierValidator {

    /**
     * Represents the result of supplier validation,
     * either 'pass' or 'fail'.
     */
    public sealed interface Result {

        /**
         * If validation passes, the result includes
         * the validated supplier data.
         */
        record Pass(
                SupplierData.Validated validatedSupplierData
        ) implements Result {}

        /**
         * If validation fails, the result includes
         * the validation messages.
         */
        record Fail(
                ValidationMessages messages
        ) implements Result {}
    }

    /**
     * Validates user-entered supplier data.
     * @param s The unvalidated supplier data
     * @return Either a Pass or Fail result
     */
    public static Result validate(SupplierData.Unvalidated s) {

        var messages = new ValidationMessages();

        if (s.companyName() == null || s.companyName().isBlank()) {
            messages.add("companyName", "Company name is required.");
        }

        if (s.contactName() == null || s.contactName().isBlank()) {
            messages.add("contactName", "Contact name is required.");
        }

        if (s.phone() == null || s.phone().isBlank()) {
            messages.add("phone", "Phone number is required.");
        } else if (!s.phone().matches("[0-9.()+ -]+")) {
            messages.add("phone", "Phone number contains invalid characters.");
        }

        if (messages.isEmpty()) {
            return new Result.Pass(new SupplierData.Validated(
                    s.companyName(),
                    s.contactName(),
                    s.contactTitle(),
                    s.phone()
            ));
        } else {
            return new Result.Fail(messages);
        }
    }
}
