
package sales.feature.supplier;

import sales.core.Supplier;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import sales.feature.base.data.BaseRepository;
import sales.feature.base.data.DataAccessException;
import sales.feature.supplier.validation.SupplierData;

public class SupplierRepository extends BaseRepository {

    private static final Logger logger = Logger.getLogger(SupplierRepository.class.getName());

    public SupplierRepository(Connection connection) {
        super(connection);
    }

    public List<Supplier> all() throws DataAccessException {

        try {
            var statement = connection.prepareStatement(
                    """
                            SELECT s.SupplierID, s.CompanyName,
                                   s.ContactName, s.ContactTitle, s.Phone
                            FROM Suppliers s
                            """
            );

            var resultSet = statement.executeQuery();
            var suppliers = new ArrayList<Supplier>();

            while (resultSet.next()) {
                var supplier = new Supplier(
                        resultSet.getInt("SupplierID"),
                        resultSet.getString("CompanyName"),
                        resultSet.getString("ContactName"),
                        resultSet.getString("ContactTitle"),
                        resultSet.getString("Phone")
                );

                suppliers.add(supplier);
            }

            return suppliers;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error getting all suppliers", e);
            throw new DataAccessException("Error getting all suppliers", e);
        }
    }

    /**
     * @param supplierId The id of the supplier to look up
     * @return The number of products associated with the given supplier id
     * @throws DataAccessException If an SQLException occurs
     */
    public int countProductsForSupplier(int supplierId) throws DataAccessException {

        try {
            var statement = connection.prepareStatement(
                    """
                        SELECT COUNT(*) AS ProductCount
                        FROM Products
                        WHERE SupplierID = ?
                        """
            );

            statement.setInt(1, supplierId);

            var resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("ProductCount");
            } else {
                return 0;
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error getting product count", e);
            throw new DataAccessException("Error getting product count", e);
        }
    }

    /**
     * Create a new supplier row for the given validated supplier data.
     * @param s The validated supplier data
     * @return The created Supplier with its new id
     * @throws DataAccessException If an SQLException occurs
     */
    public Supplier create(SupplierData.Validated s) throws DataAccessException {

        try {
            var statement = connection.prepareStatement(
                    """
                        INSERT INTO Suppliers(
                            CompanyName,
                            ContactName,
                            ContactTitle,
                            Phone
                        ) VALUES (?, ?, ?, ?)
                        """
            );

            statement.setString(1, s.companyName());
            statement.setString(2, s.contactName());
            statement.setString(3, s.contactTitle());
            statement.setString(4, s.phone());

            statement.execute();

            return new Supplier(
                    getLastInsertId(),
                    s.companyName(),
                    s.contactName(),
                    s.contactTitle(),
                    s.phone()
            );

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating supplier", e);
            throw new DataAccessException("Error creating supplier", e);
        }

    }

    /**
     * Updates a supplier with the given validated data.
     * @param supplierId The id of the supplier to update
     * @param s The validated supplier data
     * @return The updated Supplier data
     * @throws DataAccessException If an SQLException occurs
     */
    public Supplier update(int supplierId, SupplierData.Validated s)
            throws DataAccessException {

        try {
            var statement = connection.prepareStatement(
                    """
                        UPDATE Suppliers
                        SET CompanyName = ?,
                            ContactName = ?,
                            ContactTitle = ?,
                            Phone = ?
                        WHERE SupplierID = ?
                        """
            );

            statement.setString(1, s.companyName());
            statement.setString(2, s.contactName());
            statement.setString(3, s.contactTitle());
            statement.setString(4, s.phone());
            statement.setInt(5, supplierId);

            statement.executeUpdate();

            return new Supplier(
                    supplierId,
                    s.companyName(),
                    s.contactName(),
                    s.contactTitle(),
                    s.phone()
            );

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating supplier", e);
            throw new DataAccessException("Error updating supplier", e);
        }
    }

    /**
     * Delete a supplier.
     * @param supplierId The id of the supplier to delete
     * @throws DataAccessException If an SQLException occurs
     */
    public void delete(int supplierId) throws DataAccessException {

        try {
            var statement = connection.prepareStatement(
                    """
                        DELETE FROM Suppliers
                        WHERE SupplierID = ?
                        """
            );

            statement.setInt(1, supplierId);

            statement.executeUpdate();

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting supplier", e);
            throw new DataAccessException("Error deleting supplier", e);
        }
    }


}
