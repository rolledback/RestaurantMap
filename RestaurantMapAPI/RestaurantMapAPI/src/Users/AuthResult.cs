using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace RestaurantMapAPI
{
    public class AuthResult
    {
        public User user { get; set; }
        public string accessToken { get; set; }
        public string refreshToken { get; set; }
    }
}
