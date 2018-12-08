using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.Linq;
using System.Threading.Tasks;

namespace RESTDemo.Entities
{
    public static class SqlExpressHelper
    {
        #region Properties

        /// <summary>
        /// Gets a value indicating whether this instance is initialized.
        /// </summary>
        public static bool IsInitialized => (SqlConnection != null && ConnectionString != null);

        /// <summary>
        /// Gets or sets the SQL connection instance.
        /// </summary>
        private static SqlConnection SqlConnection { get; set; }

        /// <summary>
        /// Gets or sets the connection string.
        /// </summary>
        private static string ConnectionString { get; set; }

        #endregion

        #region Initialization

        /// <summary>
        /// Initializes the instance asynchronously.
        /// </summary>
        /// <param name="ConnectionString">The connection string.</param>
        /// <returns></returns>
        public static async Task InitializeAsync(string ConnectionString)
        {
            if (!IsInitialized)
            {
                SqlExpressHelper.ConnectionString = ConnectionString;
                SqlConnection = SqlConnection ?? new SqlConnection(ConnectionString);
                await SqlConnection.OpenAsync();
            }
        }

        /// <summary>
        /// Initializes the instance.
        /// </summary>
        /// <param name="ConnectionString">The connection string.</param>
        public static void Initialize(string ConnectionString)
        {
            if (!IsInitialized)
            {
                SqlExpressHelper.ConnectionString = ConnectionString;
                SqlConnection = SqlConnection ?? new SqlConnection(ConnectionString);
                SqlConnection.Open();
            }
        }

        #endregion

        #region Methods

        #region Non-Query

        /// <summary>
        ///An asynchronous version of System.Data.Common.DbCommand.ExecuteNonQuery, which executes a SQL statement against a connection object.Invokes Sstem.Data.Common.DbCommand.ExecuteNonQueryAsync(System.Threading.CancellationToken) with CancellationToken.None.
        /// </summary>
        /// <param name="command">The command text.</param>
        /// <returns></returns>
        public static async Task<int> ExecuteNonQueryAsync(string commandText)
        {
            using (SqlCommand sqlCommand = new SqlCommand(commandText) { Connection = SqlExpressHelper.SqlConnection })
            {
                return await sqlCommand.ExecuteNonQueryAsync();
            }
        }

        /// <summary>
        ///Executes a Transact-SQL statement against the connection and returns the number of rows affected.
        /// </summary>
        /// <param name="commandText">The command text.</param>
        /// <returns>The number of rows affected.</returns>
        public static int ExecuteNonQuery(string commandText)
        {
            using (SqlCommand sqlCommand = new SqlCommand(commandText) { Connection = SqlExpressHelper.SqlConnection })
            {
                return sqlCommand.ExecuteNonQuery();
            }
        }

        #endregion

        #region Grab columns

        /// <summary>
        /// Executes an Sql command and returns a specific column asynchronously.
        /// </summary>
        /// <param name="commandText">The command text.</param>
        /// <param name="column">The column index.</param>
        /// <returns></returns>
        /// <exception cref="ArgumentException">Invalid command</exception>
        public static async Task<string> GrabSingleColumnAsync(string commandText, int column)
        {
            using (SqlCommand sqlCommand = new SqlCommand(commandText) { Connection = SqlExpressHelper.SqlConnection })
            {
                using (SqlDataReader reader = await sqlCommand.ExecuteReaderAsync())
                {
                    if (!reader.Read())
                        throw new ArgumentException(nameof(commandText));
                    return reader[column].ToString();
                }
            }
        }

        /// <summary>
        /// Executes an Sql command and returns a specific column.
        /// </summary>
        /// <param name="commandText">The command text.</param>
        /// <param name="column">The column index.</param>
        /// <returns></returns>
        /// <exception cref="ArgumentException">Invalid command</exception>
        public static string GrabSingleColumn(string commandText, int column)
        {
            using (SqlCommand sqlCommand = new SqlCommand(commandText) { Connection = SqlConnection })
            {
                using (SqlDataReader reader = sqlCommand.ExecuteReader())
                {
                    if (!reader.Read())
                        throw new ArgumentException(nameof(commandText));
                    return reader[column].ToString();
                }
            }
        }

        #endregion

        #region Grab columns as JSON

        /// <summary>
        /// Executes a select query and returns the results as a Json array asynchoronously.
        /// </summary>
        public static async Task<string[]> GrabRowsAsJsonAsync(string commandText)
        {
            List<string> list = new List<string>();
            using (SqlCommand command = new SqlCommand(commandText) { Connection = SqlExpressHelper.SqlConnection })
            {
                using (SqlDataReader reader = await command.ExecuteReaderAsync())
                {
                    while (reader.Read())
                    {
                        string json = "{";
                        for (int i = 0; i < reader.FieldCount; i++)
                        {
                            json += string.Format("\"{0}\":\"{1}\"", reader.GetName(i), reader[i].ToString());
                            if (i != reader.FieldCount - 1)
                                json += ",";
                        }
                        json += "}";
                        list.Add(json);
                    }
                }
            }
            return list.ToArray();
        }

        /// <summary>
        /// Executes a select query and returns the results as a Json array synchronously.
        /// </summary>
        public static string[] GrabRowsAsJson(string commandText)
        {
            List<string> list = new List<string>();
            using (SqlCommand command = new SqlCommand(commandText) { Connection = SqlExpressHelper.SqlConnection })
            {
                using (SqlDataReader reader = command.ExecuteReader())
                {
                    while (reader.Read())
                    {
                        string json = "{";
                        for (int i = 0; i < reader.FieldCount; i++)
                        {
                            json += string.Format("\"{0}\":\"{1}\"", reader.GetName(i), reader[i].ToString());
                            if (i != reader.FieldCount - 1)
                                json += ",";
                        }
                        json += "}";
                        list.Add(json);
                    }
                }
            }
            return list.ToArray();
        }

        #endregion

        #region Deserialization

        /// <summary>
        /// Executes a select query and returns the results as a list of deserialized Json objects asynchoronously.
        /// </summary>
        public static async Task<List<object>> GrabRowsAsObjectsAsync(string commandText)
        {
            List<object> o = new List<object>();
            var serializedObjects = await GrabRowsAsJsonAsync(commandText);
            foreach (var obj in serializedObjects)
                o.Add(Newtonsoft.Json.JsonConvert.DeserializeObject(obj));
            return o;
        }

        /// <summary>
        /// Executes a select query and returns the results as a list of deserialized Json objects synchoronously.
        /// </summary>
        public static List<object> GrabRowsAsObjects(string commandText)
        {
            List<object> o = new List<object>();
            var serializedObjects = GrabRowsAsJson(commandText);
            foreach (var obj in serializedObjects)
                o.Add(JsonConvert.DeserializeObject(obj));
            return o;
        }

        #endregion

        #endregion

        #region IDisposable Support
        private static bool _disposedValue = false; // To detect redundant calls

        /// <summary>
        /// Releases unmanaged and - optionally - managed resources.
        /// </summary>
        /// <param name="disposing"><c>true</c> to release both managed and unmanaged resources; <c>false</c> to release only unmanaged resources.</param>
        public static void Dispose(bool disposing)
        {
            if (_disposedValue) return;
            if (disposing)
            {
                if (SqlConnection != null)
                {
                    if (SqlConnection.State != System.Data.ConnectionState.Closed)
                        SqlConnection.Close();
                }
            }
            SqlConnection?.Dispose();
            if (ConnectionString != null)
                ConnectionString = null;
            _disposedValue = true;
        }

        /// <summary>
        /// Releases unmanaged and - optionally - managed resources.
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
