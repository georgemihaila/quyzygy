using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;

namespace quyzygy_web.Controllers
{
    [Route("api/[controller]")]
    public class SampleDataController : Controller
    {
        private static string[] Summaries = new[]
        {
            "Freezing", "Bracing", "Chilly", "Cool", "Mild", "Warm", "Balmy", "Hot", "Sweltering", "Scorching"
        };

        /// <summary>
        /// Gets the SQL Express connection string.
        /// </summary>
        public string SqlExpressConnectionString => string.Format(@"Data Source=(local)\SQLExpress"
            +
            ";Initial Catalog=Quyzygy;Integrated Security=SSPI");

        //[HttpPost]
        [HttpGet("[action]")]
        public string Login(string username, string password)
        {
            using (SqlConnection connection = new SqlConnection(this.SqlExpressConnectionString))
            {
                connection.Open();
                using (SqlCommand command = new SqlCommand(string.Format("SELECT COUNT(*) FROM Users Where Username='{0}' and Password='{1}'", username, password)) { Connection = connection })
                {
                    var reader = command.ExecuteReader();
                    if (reader.Read())
                    {
                        if (reader[0].ToString() == "1")
                            return "ok";
                    }
                }
            }
            return "no";
        }

        [HttpGet("[action]")]
        public IEnumerable<WeatherForecast> WeatherForecasts()
        {
            var rng = new Random();
            return Enumerable.Range(1, 5).Select(index => new WeatherForecast
            {
                DateFormatted = DateTime.Now.AddDays(index).ToString("d"),
                TemperatureC = rng.Next(-20, 55),
                Summary = Summaries[rng.Next(Summaries.Length)]
            });
        }

        public class WeatherForecast
        {
            public string DateFormatted { get; set; }
            public int TemperatureC { get; set; }
            public string Summary { get; set; }

            public int TemperatureF
            {
                get
                {
                    return 32 + (int)(TemperatureC / 0.5556);
                }
            }
        }
    }
}
