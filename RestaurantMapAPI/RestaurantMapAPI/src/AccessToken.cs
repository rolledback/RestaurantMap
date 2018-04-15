using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace RestaurantMapAPI
{
    public class AccessToken
    {
        public AccessToken(string username)
        {
            this.token = Guid.NewGuid().ToString();
            this.username = username;
        }

        public string token { get; }
        public string username { get; }
    }
}
