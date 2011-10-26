using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Internert_Programming
{
    public class User
    {
        private String _name;
        private String _username;
        private String _password;
        private String _category;

        public User()
        {}

        public String Name
        {
            set { _name = value; }
            get { return _name; }
        }
        public String UserName
        {
            set { _username = value; }
            get { return _username; }
        }
        public String Password 
        {
            set { _password = value; }
            get { return _password; }        
        }
        public String Category
        {
            set { _category = value; }
            get { return _category; }        
        }
        public override String ToString()
        {
            return String.Format("Name: {0}\nCategory: {1}\nUserName: {2}\nPassword: {3}\n", Name, Category, UserName, Password);
        }
    }
}
