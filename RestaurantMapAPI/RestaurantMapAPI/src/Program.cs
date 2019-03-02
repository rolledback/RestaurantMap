using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore;
using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;

namespace RestaurantMapAPI
{
    public class Program
    {
        public static async Task Main(string[] args)
        {
            IWebHost webHost = BuildWebHost(args);

            IRestaurantRepository restaurantRepository = (IRestaurantRepository)webHost.Services.GetService(typeof(IRestaurantRepository));
            IUserRepository userRepository = (IUserRepository)webHost.Services.GetService(typeof(IUserRepository));
            IBackupDbService backupService = (IBackupDbService)webHost.Services.GetService(typeof(IBackupDbService));

            await backupService.Restore(restaurantRepository);
            await backupService.Restore(userRepository);

            BuildWebHost(args).Run();
        }


        public static IWebHost BuildWebHost(string[] args) =>
            WebHost.CreateDefaultBuilder(args)
                .UseApplicationInsights()
                .UseStartup<Startup>()
                .Build();
    }
}
