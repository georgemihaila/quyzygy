using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Quyzygy.Entities
{
    /// <summary>
    /// A class for initializing external application dependencies.
    /// </summary>
    public interface IInitializer
    {
        /// <summary>
        /// Initializes the external application dependencies.
        /// </summary>
        void Initialize();
    }
}
