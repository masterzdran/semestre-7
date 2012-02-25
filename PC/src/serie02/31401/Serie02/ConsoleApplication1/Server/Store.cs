//[Nuno Cancelo] : Comentários apagados, porque tornavam dificil a leitura do código.
using System.Collections.Generic;
using System.Linq;
using System.Net;

namespace Server
{
    public sealed class Store
    {
        private static readonly Store _instance = null;
        static Store()
        {
            _instance = new Store();
        }

        public static Store Instance
        {
            get { return _instance; }
        }
        #region Instance members

        private readonly Dictionary<string, HashSet<IPEndPoint>> _store;
        private readonly IPEndPoint[] noLocations;
        private Store()
        {
            _store = new Dictionary<string, HashSet<IPEndPoint>>();
            noLocations = new IPEndPoint[0];
        }
        public bool ContainsKey(string key)
        {
            lock (_store)
            {
                return _store.ContainsKey(key);
            }
        }
        public void Register(string fileName, IPEndPoint client)
        {
            lock (_store)
            {
                if (!_store.ContainsKey(fileName))
                    _store[fileName] = new HashSet<IPEndPoint> { client };
                else
                    _store[fileName].Add(client);
            }
        }
        public bool Unregister(string fileName, IPEndPoint client)
        {
            bool result;
            lock (_store)
            {
                if (!_store.ContainsKey(fileName))
                    return false;
                HashSet<IPEndPoint> locations = _store[fileName];
                result = locations.Remove(client);
                if (result && locations.Count == 0)
                    _store.Remove(fileName);
            }
            return result;
        }
        public string[] GetTrackedFiles()
        {
            string[] rObject = null;
            lock (_store)
            {
                rObject = _store.Keys.ToArray();
            }
            return rObject;
        }
        public IPEndPoint[] GetFileLocations(string fileName)
        {
            IPEndPoint[] locations = noLocations;
            lock (_store[fileName])
            {
                if (_store.ContainsKey(fileName))
                    locations = _store[fileName].ToArray();
            }
            return locations;
        }

        #endregion
    }
}
