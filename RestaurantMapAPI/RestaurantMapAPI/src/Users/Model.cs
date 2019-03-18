using System.Collections.Generic;

namespace RestaurantMapAPI
{
    public class DbUser
    {
        public int id { get; set; }
        public string username { get; set; }
        public string hashedPassword { get; set; }
        public bool isAdmin { get; set; }
        public IEnumerable<Session> sessions { get; set; }
    }

    public class User
    {
        public int id { get; set; }
        public string username { get; set; }
        public bool isAdmin { get; set; }
    }

    public class Session
    {
        public string accessToken { get; set; }
        public string refreshToken { get; set; }
        public long expiration { get; set; }
    }
}
