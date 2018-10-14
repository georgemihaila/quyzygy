using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.IO;
using System.Linq;
using System.Threading.Tasks;

namespace Quyzygy.Entities.Sql
{
    /// <summary>
    /// A class for initializing an Sql database such that the application can set its dependencies up in case it is runninng for the first time.
    /// </summary>
    /// <seealso cref="quyzygy.Entities.Sql.ISqlInitializer" />
    public class SqlInitializer : Quyzygy.Entities.Sql.ISqlInitializer, IDisposable
    {
        /// <summary>
        /// Gets or sets the location of the folder containing the initialization instructions.
        /// </summary>
        public string InitializationFolder { get; set; }

        /// <summary>
        /// Gets or sets the Sql database connection string.
        /// </summary>
        public string ConnectionString { get; set; }

        /// <summary>
        /// Gets or sets the Sql connection.
        /// </summary>
        public SqlConnection Connection { get; set; }

        /// <summary>
        /// Initializes a new instance of the <see cref="SqlInitializer"/> class.
        /// </summary>
        /// <param name="InitializationFolder">The location of the folder containing the initialization instructions..</param>
        public SqlInitializer(string InitializationFolder, string ConnectionString)
        {
            this.InitializationFolder = InitializationFolder;
            this.ConnectionString = ConnectionString;
        }

        /// <summary>
        /// Initializes the external Sql application dependencies.
        /// </summary>
        /// <exception cref="ArgumentNullException"></exception>
        public void Initialize()
        {
            string[] filenames = Directory.GetFiles(InitializationFolder);
            if (filenames == null || filenames.Length == 0)
                throw new ArgumentNullException(string.Format("The initialization folder (\"{0}\") is empty.", InitializationFolder));
            string[] commands = filenames.ToList().Select(o => File.ReadAllText(o)).ToArray();
            if (commands != null || commands.Length > 0)
            {
                Connection = Connection ?? new SqlConnection(ConnectionString);
                if (Connection.State != System.Data.ConnectionState.Open)
                    Connection.Open();
                foreach (var command in commands)
                {
                    SqlCommand comm = new SqlCommand(command)
                    {
                        Connection = this.Connection
                    };
                    comm.ExecuteNonQuery();
                }
                Connection.Close();
            }
        }

        #region IDisposable Support
        private bool disposedValue = false; // To detect redundant calls

        /// <summary>
        /// Releases unmanaged and - optionally - managed resources.
        /// </summary>
        /// <param name="disposing"><c>true</c> to release both managed and unmanaged resources; <c>false</c> to release only unmanaged resources.</param>
        protected virtual void Dispose(bool disposing)
        {
            if (!disposedValue)
            {
                if (disposing)
                {
                    if (Connection != null)
                    {
                        if (Connection.State != System.Data.ConnectionState.Closed)
                            Connection.Close();
                        Connection.Dispose();
                    }
                }
                ConnectionString = null;
                InitializationFolder = null;
                disposedValue = true;
            }
        }
        /// <summary>
        /// Performs application-defined tasks associated with freeing, releasing, or resetting unmanaged resources.
        /// </summary>
        public void Dispose()
        {
            // Do not change this code. Put cleanup code in Dispose(bool disposing) above.
            Dispose(true);
            // TODO: uncomment the following line if the finalizer is overridden above.
            // GC.SuppressFinalize(this);
        }
        #endregion
    }
}
