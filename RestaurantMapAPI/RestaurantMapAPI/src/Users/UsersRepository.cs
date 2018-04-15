using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.Extensions.Options;
using MongoDB.Bson;
using MongoDB.Driver;

namespace RestaurantMapAPI
{
    public class UsersRepository : IUsersRepository
    {
        private readonly UsersContext _context = null;

        public UsersRepository(IOptions<MongoDbSettings> settings)
        {
            _context = new UsersContext(settings);
        }

        public async Task<IEnumerable<User>> GetAllUsers()
        {
            try
            {
                return await _context.Users
                        .Find(_ => true).ToListAsync();
            }
            catch (Exception ex)
            {
                // log or manage the exception
                throw ex;
            }
        }
    }
}
