
package sales.feature.supplier.validation;

import sales.core.Supplier;

/**
 * A 'sum type' representing supplier data entered by the user.
 */
public sealed interface SupplierData {

    /**
     * Represents user-entered supplier data that has not yet
     * been validated, or is invalid in some way.
     */
    record Unvalidated(
            String companyName,
            String contactName,
            String contactTitle,
            String phone
    ) implements SupplierData {

        public static Unvalidated of(Supplier s) {
            return new Unvalidated(
                    s.companyName(),
                    s.contactName(),
                    s.contactTitle(),
                    s.phone()
            );
        }
    }

    /**
     * Represents user-entered supplier data that has been
     * validated and is safe to store in the database.
     */
    record Validated(
            String companyName,
            String contactName,
            String contactTitle,
            String phone
    ) implements SupplierData {}
}
