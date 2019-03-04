using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;

namespace RestaurantMapAPI
{
    public class BackupDbTask : IScheduledTask
    {
        public string Schedule => "0 */12 * * *";
        private readonly IRestaurantRepository _RestaurantRepository;
        private readonly IUserRepository _UserRepository;
        private readonly IBackupDbService _BackupService;

        public BackupDbTask(IRestaurantRepository RestaurantRepository, IUserRepository UserRepository, IBackupDbService BackupDbService)
        {
            _RestaurantRepository = RestaurantRepository;
            _UserRepository = UserRepository;
            _BackupService = BackupDbService;
        }

        public async Task ExecuteAsync(CancellationToken cancellationToken)
        {
            await _BackupService.Backup(_UserRepository);
            await _BackupService.Backup(_RestaurantRepository);
        }
    }
}
