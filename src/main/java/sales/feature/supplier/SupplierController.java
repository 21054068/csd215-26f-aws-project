
package sales.feature.supplier;

import sales.core.Supplier;
import sales.feature.supplier.validation.SupplierValidator;
import sales.feature.base.validation.ValidationMessages;
import sales.feature.base.controller.BaseController;
import sales.feature.base.ui.MainWindow;
import sales.feature.supplier.ui.SuppliersView;
import sales.feature.supplier.ui.SupplierEditView;
import sales.feature.supplier.ui.SupplierNewView;

import sales.feature.supplier.validation.SupplierData;
import static sales.feature.supplier.validation.SupplierValidator.Result.*;

/**
 * Manages the supplier views and interactions.
 */
public class SupplierController extends BaseController {

    private final SupplierRepository repo;

    public SupplierController(MainWindow mainWindow, SupplierRepository repo) {
        super(mainWindow);
        this.repo = repo;
    }

    /**
     * Show the list of all suppliers.
     */
    public void showSuppliers() {
        mainWindow.setTitle("Suppliers");

        accessDataOrShowError(() -> {
            var viewModel = new SuppliersView.ViewModel(
                    repo.all(),
                    this::showSupplier
            );

            mainWindow.setMainScene(SuppliersView.createScene(viewModel));
        });
    }

    /**
     * Show the 'new supplier' view.
     */
    public void showNewSupplier() {
        mainWindow.setTitle("New Supplier");

        var viewModel = new SupplierNewView.ViewModel(
                "",
                "",
                "",
                "",
                ValidationMessages.none(),
                this::createSupplier
        );

        mainWindow.setMainScene(SupplierNewView.createScene(viewModel));
    }

    /**
     * Show the new supplier view with validation messages.
     */
    public void showNewSupplier(
            SupplierData.Unvalidated s,
            ValidationMessages messages) {

        mainWindow.setTitle("New Supplier");

        var viewModel = new SupplierNewView.ViewModel(
                s.companyName(),
                s.contactName(),
                s.contactTitle(),
                s.phone(),
                messages,
                this::createSupplier
        );

        mainWindow.setMainScene(SupplierNewView.createScene(viewModel));
    }

    /**
     * Show the edit view for a selected supplier.
     */
    public void showSupplier(Supplier s) {
        showSupplier(
                s.id(),
                SupplierData.Unvalidated.of(s),
                ValidationMessages.none()
        );
    }

    /**
     * Show the edit view with supplier data and validation messages.
     */
    public void showSupplier(
            int supplierId,
            SupplierData.Unvalidated s,
            ValidationMessages messages) {

        mainWindow.setTitle("Edit Supplier");

        accessDataOrShowError(() -> {
            var productsForSupplier =
                    repo.countProductsForSupplier(supplierId);

            var viewModel = new SupplierEditView.ViewModel(
                    supplierId,
                    s.companyName(),
                    s.contactName(),
                    s.contactTitle(),
                    s.phone(),
                    messages,
                    this::updateSupplier,
                    productsForSupplier > 0 ? null : this::deleteSupplier
            );

            mainWindow.setMainScene(
                    SupplierEditView.createScene(viewModel)
            );
        });
    }

    /**
     * Attempt to create a supplier from user-entered data.
     */
    public void createSupplier(SupplierData.Unvalidated s) {

        accessDataOrShowError(() -> {
            var validationResult = SupplierValidator.validate(s);

            switch (validationResult) {
                case Pass result -> {
                    repo.create(result.validatedSupplierData());
                    this.showSuppliers();
                }

                case Fail result ->
                        this.showNewSupplier(s, result.messages());
            }
        });
    }

    /**
     * Attempt to update an existing supplier.
     */
    public void updateSupplier(
            int supplierId,
            SupplierData.Unvalidated s) {

        accessDataOrShowError(() -> {
            var validationResult = SupplierValidator.validate(s);

            switch (validationResult) {
                case Pass result -> {
                    var data = result.validatedSupplierData();
                    var updatedSupplier = repo.update(supplierId, data);

                    this.showSupplier(updatedSupplier);
                }

                case Fail result ->
                        this.showSupplier(
                                supplierId,
                                s,
                                result.messages()
                        );
            }
        });
    }

    /**
     * Delete a supplier that is not associated with any products.
     */
    public void deleteSupplier(int supplierId) {

        accessDataOrShowError(() -> {

            if (repo.countProductsForSupplier(supplierId) > 0) {
                throw new RuntimeException(
                        "Can't delete suppliers that are being used."
                );
            }

            repo.delete(supplierId);
            this.showSuppliers();
        });
    }
}
