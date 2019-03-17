namespace RestaurantMapAPI
{
    public class DbUser
    {
        public int id { get; set; }
        public string username { get; set; }
        public string hashedPassword { get; set; }
        public bool isAdmin { get; set; }
    }

    public class User
    {
        public int id { get; set; }
        public string username { get; set; }
        public bool isAdmin { get; set; }
    }
}
