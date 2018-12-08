using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;
using RESTDemo.Entities;
using RESTDemo.Models;

namespace RESTDemo.Controllers
{
    public class HomeController : Controller
    {
        [Route("/Test")]
        public IActionResult Test()
        {
            return Content("Ok");
        }

        class LoginResult
        {
            public string UserType { get; set; }

            public string Key { get; set; }
        }

        [Route("/Login")]
        [HttpPost]
        public IActionResult Login(string username, string password)
        {
            Random random = new Random();
            string seed = "0123456789abcdef";
            if (SqlExpressHelper.GrabSingleColumn(string.Format("SELECT Count(*) FROM Users where Username='{0}' and PasswordHash='{1}'", username, password), 0) == "1")
            {
                string key = string.Empty;
                for (int i = 0; i < 16; i++)
                {
                    key += seed[random.Next(seed.Length)];
                }
                return Content(JsonConvert.SerializeObject(new LoginResult()
                {
                    Key = key,
                    UserType = SqlExpressHelper.GrabSingleColumn(string.Format("SELECT Usertype FROM Users where Username='{0}' and PasswordHash='{1}'", username, password), 0)
                }));
            }
            else return BadRequest();
        }

        [Route("/SignUp")]
        [HttpPost]
        public IActionResult SignUp(string username, string password, string userType)
        {
            SqlExpressHelper.ExecuteNonQuery(string.Format("INSERT INTO Users values ('{0}','{1}','{2}')", username, password, userType));
            return Content("Ok");
        }

        [ResponseCache(Duration = 0, Location = ResponseCacheLocation.None, NoStore = true)]
        public IActionResult Error()
        {
            return View(new ErrorViewModel { RequestId = Activity.Current?.Id ?? HttpContext.TraceIdentifier });
        }
    }
}
