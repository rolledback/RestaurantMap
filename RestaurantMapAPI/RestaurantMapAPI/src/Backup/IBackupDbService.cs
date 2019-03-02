using LiteDB;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace RestaurantMapAPI
{
    public interface IBackupDbService
    {
        Task Restore<T>(IBackupable<T> db);
        Task Backup<T>(IBackupable<T> db);
    }
}
