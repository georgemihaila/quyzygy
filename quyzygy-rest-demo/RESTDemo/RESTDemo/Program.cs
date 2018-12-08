using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore;
using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;
using RESTDemo.Entities;

namespace RESTDemo
{
    public class Program
    {
        public static void Main(string[] args)
        {
            SqlExpressHelper.Initialize("Server=(local)\\SQLExpress;Database=Quyzygy;Trusted_Connection=True");
            CreateWebHostBuilder(args).Build().Run();
        }

        public static IWebHostBuilder CreateWebHostBuilder(string[] args) =>
            WebHost.CreateDefaultBuilder(args)
                .UseKestrel()
                .UseIISIntegration()
                .UseStartup<Startup>();
    }
}
