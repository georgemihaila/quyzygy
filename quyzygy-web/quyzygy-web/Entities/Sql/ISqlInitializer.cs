using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.Linq;
using System.Threading.Tasks;

namespace Quyzygy.Entities.Sql
{
    /// <summary>
    /// A class for initializing an Sql database such that the application can set its dependencies up in case it is runninng for the first time.
    /// </summary>
    public interface ISqlInitializer : IInitializer
    {
        /// <summary>
        /// Gets or sets the location of the folder containing the initialization instructions.
        /// </summary>
        string InitializationFolder { get; set; }

        /// <summary>
        /// Gets or sets the Sql database connection string.
        /// </summary>
        string ConnectionString { get; set; }

        /// <summary>
        /// Gets or sets the Sql connection.
        /// </summary>
        SqlConnection Connection { get; set; }
    }
}
