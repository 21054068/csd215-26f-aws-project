
package sales.feature.supplier;

import sales.core.Supplier;
import sales.feature.base.controller.BaseController;
import sales.feature.base.ui.MainWindow;
import sales.feature.supplier.ui.SuppliersView;

public class SupplierController extends BaseController {

    private final SupplierRepository repo;

    public SupplierController(MainWindow mainWindow, SupplierRepository repo) {
        super(mainWindow);
        this.repo = repo;
    }

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

    public void showSupplier(Supplier s) {
        System.out.println("Selected supplier: " + s.companyName());
    }
}
