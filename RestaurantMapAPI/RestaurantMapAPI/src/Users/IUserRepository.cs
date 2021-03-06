﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

// Shamelessly taken from: http://www.qappdesign.com/using-mongodb-with-net-core-webapi/

namespace RestaurantMapAPI
{
    public interface IUserRepository : IBackupable<DbUser>
    {
        Task<IEnumerable<DbUser>> GetAllUsers();
        Task UpdateUser(DbUser user);
        Task AddUser(DbUser user);
    }
}
