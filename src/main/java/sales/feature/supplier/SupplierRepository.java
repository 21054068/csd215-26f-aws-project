
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
}
