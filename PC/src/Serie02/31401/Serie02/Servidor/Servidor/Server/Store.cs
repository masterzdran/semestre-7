using System.Collections.Generic;
using System.Linq;
using System.Net;

namespace Tracker
{
    public class Store
    {
        private static readonly Store _instance = new Store();
        public static Store Instance
        {
            get { return _instance; }
        }

        #region Instance members

        private volatile Dictionary<string, HashSet<IPEndPoint>> _store;
        private readonly IPEndPoint[] noLocations;
        private Store()
        {
            _store = new Dictionary<string, HashSet<IPEndPoint>>();
            noLocations = new IPEndPoint[0];
        }
        public void Register(string fileName, IPEndPoint client)
        {
            HashSet<IPEndPoint> fileHosts = null;
            lock (_instance)
            {
                if (!_store.ContainsKey(fileName))
                    _store[fileName] = (fileHosts = new HashSet<IPEndPoint>());
                else
                    fileHosts = _store[fileName];
                fileHosts.Add(client);
            }
        }
        public bool Unregister(string fileName, IPEndPoint client)
        {
            lock (_instance)
            {
                if (!_store.ContainsKey(fileName))
                    return false;

                HashSet<IPEndPoint> locations = _store[fileName];
                bool result = locations.Remove(client);

                if (result && locations.Count == 0)
                    _store.Remove(fileName);

                return result;
            }
        }

        public string[] GetTrackedFiles()
        {
                return _store.Keys.ToArray();
        }

        public IPEndPoint[] GetFileLocations(string fileName)
        {
            lock (_instance)
            {
                return (_store.ContainsKey(fileName)) ? _store[fileName].ToArray() : noLocations.ToArray();
            }
        }

        #endregion
    }
}
