using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.Xml;


namespace Internert_Programming
{
    public class UserLoad
    {
        private Dictionary<String, User> _users;
        private DateTime _lastTouched;
        private readonly String _filePath = "./config/users.xml";

        public UserLoad()
        {
            if (_users != null) return;
            _users = new Dictionary<string,User>();
            XmlTextReader  xmlFile = new XmlTextReader (_filePath);
             _lastTouched = File.GetLastWriteTimeUtc(_filePath);

             xmlFile.Read();
             while (xmlFile.Read())
             {
                 xmlFile.MoveToElement();
                 if (xmlFile.NodeType == XmlNodeType.Element && xmlFile.Name.Equals("user")){
                     User user = new User();
                     user.Name = xmlFile.GetAttribute("name");
                     user.Password = xmlFile.GetAttribute("password");
                     user.UserName = xmlFile.GetAttribute("username");
                     user.Category = xmlFile.GetAttribute("category");
                     _users.Add(xmlFile.GetAttribute("username"), user);
                 }

             }
        }

        public Boolean isAuthorized(String username, String password)
        {
            return _users[username].Password.Equals(password);
        }
        public Boolean isValid(String username, String password) 
        {
            return _users.ContainsKey(username);
        }


        public void printAllUsersInfo()
        {
            foreach ( KeyValuePair<String, User> u in _users)
            {
                Console.WriteLine(u.Value.ToString());
            }
        }
        public static void Main()
        {
            UserLoad u = new UserLoad();
            u.printAllUsersInfo();
            Console.ReadKey();
        }
    }

}
