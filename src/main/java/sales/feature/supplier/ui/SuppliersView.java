
package sales.feature.supplier.ui;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import sales.core.Supplier;

import java.util.List;
import java.util.function.Consumer;

import static sales.feature.base.ui.helpers.JavaFXUtils.createColumn;
import static sales.feature.base.ui.helpers.JavaFXUtils.setOnDoubleClick;

/**
 * A 'namespace' class for types and functions related to displaying
 * the list of suppliers in the UI.
 */
public class SuppliersView {

    /**
     * The view model containing the data needed by the view
     * @param suppliers The set of suppliers to show
     * @param onSupplierSelected The callback for when the user
     * double-clicks a specific supplier
     */
    public record ViewModel(
            List<Supplier> suppliers,
            Consumer<Supplier> onSupplierSelected
    ) {}

    /**
     * @param viewModel The data required by the view
     * @return The root Node of the scene for showing suppliers
     */
    public static Node createScene(ViewModel viewModel) {
        var observableSuppliers = FXCollections.observableList(viewModel.suppliers());
        TableView<Supplier> table = new TableView<>(observableSuppliers);

        var companyNameCol = createColumn("Supplier Name", Supplier::companyName);
        var contactNameCol = createColumn("Contact Name", Supplier::contactName);
        var contactTitleCol = createColumn("Contact Title", Supplier::contactTitle);
        var phoneCol = createColumn("Phone Number", Supplier::phone);

        setOnDoubleClick(table, viewModel.onSupplierSelected());

        table.getColumns().addAll(List.of(
                companyNameCol,
                contactNameCol,
                contactTitleCol,
                phoneCol
        ));

        var pane = new VBox(table);

        // Make the table fill the vbox vertically
        VBox.setVgrow(table, javafx.scene.layout.Priority.ALWAYS);

        return pane;
    }
}
