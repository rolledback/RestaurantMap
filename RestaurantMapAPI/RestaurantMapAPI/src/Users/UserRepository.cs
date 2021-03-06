﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace RestaurantMapAPI
{
    public class UserRepository : IUserRepository
    {
        private readonly UsersContext _context;

        public UserRepository()
        {
            _context = new UsersContext();
        }

        public async Task<IEnumerable<DbUser>> GetAllUsers()
        {
            try
            {
                return _context.Users
                        .Find(_ => true).ToList();
            }
            catch (Exception ex)
            {
                // log or manage the exception
                throw ex;
            }
        }

        public async Task AddUser(DbUser item)
        {
            try
            {
                _context.Users.Insert(item);
            }
            catch (Exception ex)
            {
                // log or manage the exception
                throw ex;
            }
        }

        public Task<IEnumerable<DbUser>> GetAllForBackup()
        {
            return GetAllUsers();
        }

        public async Task UpdateUser(DbUser user)
        {
            try
            {
                _context.Users.Update(user);
            }
            catch (Exception ex)
            {
                // log or manage the exception
                throw ex;
            }
        }

        public void RestoreFromBackup(IEnumerable<DbUser> backup)
        {
            _context.Users.Delete(_ => true);
            _context.Users.InsertBulk(backup);
        }

        public string GetBackupName()
        {
            return "users.json";
        }
    }
}
