using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;

namespace RestaurantMapAPI
{
    public class BackupDbTask : IScheduledTask
    {
        public string Schedule => "*/1 * * * *";
        private readonly IRestaurantRepository _RestaurantRepository;

        public BackupDbTask(IRestaurantRepository RestaurantRepository)
        {
            _RestaurantRepository = RestaurantRepository;
        }

        public async Task ExecuteAsync(CancellationToken cancellationToken)
        {
            var x = await _RestaurantRepository.GetAllRestaurants();
            return;
        }
    }
}
