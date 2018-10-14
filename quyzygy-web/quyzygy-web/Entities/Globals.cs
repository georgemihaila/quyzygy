using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.Linq;
using System.Threading.Tasks;

namespace Quyzygy.Entities
{
    /// <summary>
    /// Represents static global variables that can be accessed from anywhere.
    /// </summary>
    public static class Globals
    {
        /// <summary>
        /// Represents default Json responses that can be used when communicating with the REST API.
        /// </summary>
        public static class JsonResponses
        {
            /// <summary>
            /// Gets the default JSON success message.
            /// </summary>
            public static string Success => "{\"success\":\"true\"}";

            /// <summary>
            /// Gets the default JSON failure message.
            /// </summary>
            public static string Failure => "{\"success\":\"false\"}";
        }

        /// <summary>
        /// Represents a class that can be used for interacting with the local Sql Express database.
        /// </summary>
        public static class SqlExpressHelper
        {
            /// <summary>
            /// Initializes this instance.
            /// </summary>
            /// <param name="ConnectionString">The connection string.</param>
            public static void Initialize(string ConnectionString)
            {
                SqlExpressHelper.ConnectionString = ConnectionString;
                Connection = new SqlConnection(ConnectionString);
                Connection.Open();
            }

            /// <summary>
            /// Gets or sets the Sql connection.
            /// </summary>
            private static SqlConnection Connection { get; set; }

            /// <summary>
            /// Gets or sets the connection string.
            /// </summary>
            private static string ConnectionString { get; set; }

            /// <summary>
            /// An asynchronous version of System.Data.Common.DbCommand.ExecuteNonQuery, which executes a SQL statement against a connection object. Invokes System.Data.Common.DbCommand.ExecuteNonQueryAsync(System.Threading.CancellationToken) with CancellationToken.None.
            /// </summary>
            /// <param name="commandText">The command text.</param>
            /// <returns>The number of rows affected.</returns>
            public static async Task<int> ExecuteNonQueryAsync(string commandText)
            {
                SqlCommand command = new SqlCommand(commandText)
                {
                    Connection = SqlExpressHelper.Connection
                };
                return await command.ExecuteNonQueryAsync();
            }

            #region IDisposable Support
            private static bool disposedValue = false; // To detect redundant calls

            /// <summary>
            /// Releases unmanaged and - optionally - managed resources.
            /// </summary>
            /// <param name="disposing"><c>true</c> to release both managed and unmanaged resources; <c>false</c> to release only unmanaged resources.</param>
            public static void Dispose(bool disposing)
            {
                if (!disposedValue)
                {
                    if (disposing)
                    {
                        if (Connection != null)
                        {
                            if (Connection.State != System.Data.ConnectionState.Closed)
                                Connection.Close();
                        }
                    }
                    Connection.Dispose();
                    ConnectionString = null;
                    disposedValue = true;
                }
            }

            /// <summary>
            /// Performs application-defined tasks associated with freeing, releasing, or resetting unmanaged resources.
            /// </summary>
            public static void Dispose()
            {
                // Do not change this code. Put cleanup code in Dispose(bool disposing) above.
                Dispose(true);
                // TODO: uncomment the following line if the finalizer is overridden above.
                // GC.SuppressFinalize(this);
            }
            #endregion
        }
    }
}
