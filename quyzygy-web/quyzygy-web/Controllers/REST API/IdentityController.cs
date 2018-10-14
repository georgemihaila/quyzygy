using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography;
using System.Text;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Quyzygy.Entities;

namespace Quyzygy.Controllers.API
{
    public class IdentityController : Controller
    {
        [AllowAnonymous]
        [HttpPost]
        [Route("/API/Identity/Authenticate")]
        public IActionResult Authenticate(string username, string passwordhash)
        {
            return Content("Is alive");
        }

        [AllowAnonymous]
        [HttpPost]
        [Route("/API/Identity/Register")]
        public IActionResult Register(string firstname, string lastname, string email, string password, string usertype)
        {
            if (firstname.Trim() == null ||
                  lastname.Trim() == null ||
                  email.Trim() == null ||
                  password.Trim() == null ||
                  usertype.Trim() == null)
                return BadRequest();
            if (firstname.Trim() == string.Empty ||
                lastname.Trim() == string.Empty ||
                email.Trim() == string.Empty ||
                password.Trim() == string.Empty ||
                usertype.Trim() == string.Empty)
                return BadRequest();
            if (!IsValidEmail(email))
                return BadRequest();
            if (password.Length < 8)
                return BadRequest();
            object userType_o = null;
            if (!Enum.TryParse(typeof(ApplicationUserType), usertype, out userType_o))
                return BadRequest();
            ApplicationUserType userType = (ApplicationUserType)userType_o;
            return Content("Is alive");
        }

        private async Task<IdentityResult> CreateAsync(ApplicationUser user)
        {
            string sql = $"INSERT INTO dbo.ApplicationUsers " +
                $"VALUES ('{user.Id}', @Email, @EmailConfirmed, @PasswordHash, @UserName)";

            int rows = await Globals.SqlExpressHelper.ExecuteNonQueryAsync(sql);

            if (rows > 0)
            {
                return IdentityResult.Success;
            }
            return IdentityResult.Failed(new IdentityError { Description = $"Could not insert user {user.Email}." });
        }

        /// <summary>
        /// Determines whether a string represents a valid email address.
        /// </summary>
        /// <param name="email">The email address as a string.</param>
        /// <returns>
        ///   <c>true</c> if [is valid email] [the specified email]; otherwise, <c>false</c>.
        /// </returns>
        private bool IsValidEmail(string email)
        {
            try
            {
                var addr = new System.Net.Mail.MailAddress(email);
                return addr.Address == email;
            }
            catch
            {
                return false;
            }
        }

        /// <summary>
        /// Hashes a string using the SHA-256 algorithm.
        /// </summary>
        /// <param name="randomString">The source string.</param>
        /// <returns>The hashed string.</returns>
        private string SHA256(string randomString)
        {
            var crypt = new SHA256Managed();
            string hash = String.Empty;
            byte[] crypto = crypt.ComputeHash(Encoding.ASCII.GetBytes(randomString));
            foreach (byte theByte in crypto)
            {
                hash += theByte.ToString("x2");
            }
            return hash;
        }
    }
}