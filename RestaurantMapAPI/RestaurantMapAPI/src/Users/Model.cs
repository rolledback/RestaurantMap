using System.Collections.Generic;

namespace RestaurantMapAPI
{
    public class DbUser
    {

        public DbUser()
        {
        }

        public DbUser(string username, string hashedPassword)
        {
            this.username = username;
            this.hashedPassword = hashedPassword;
            this.sessions = new List<Session>();
            this.permissions = new List<Permission>();
        }

        public int id { get; set; }
        public string username { get; set; }
        public string hashedPassword { get; set; }
        public IEnumerable<Session> sessions { get; set; }
        public IEnumerable<Permission> permissions { get; set; }
    }

    public class Permission
    {
        public string permissionName { get; set; }
        public string associatedClaim { get; set; }
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
