using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.IO;
using System.Net.Sockets;
using System.Net;

namespace ClienteGui
{
    public partial class Form1 : Form
    {
        BackgroundWorker _worker=new BackgroundWorker();
        public Form1()
        {
            InitializeComponent();
            _worker.WorkerSupportsCancellation = true;
            _worker.WorkerReportsProgress = true;
        }
        #region METHODS
        private ushort _port = 1234;

        private void Register(IEnumerable<string> files, string adress, ushort port)
        {
            using (TcpClient client = new TcpClient())
            {
                client.Connect(IPAddress.Loopback, _port);

                StreamWriter output = new StreamWriter(client.GetStream());

                // Send request type line
                output.WriteLine("REGISTER");

                // Send message payload
                foreach (string file in files)
                    output.WriteLine(string.Format("{0}:{1}:{2}", file, adress, port));

                // Send message end mark
                output.WriteLine();

                output.Close();
                client.Close();
            }
        }

        private void Unregister(string file, string adress, ushort port)
        {
            using (TcpClient client = new TcpClient())
            {
                client.Connect(IPAddress.Loopback, _port);

                StreamWriter output = new StreamWriter(client.GetStream());

                // Send request type line
                output.WriteLine("UNREGISTER");
                // Send message payload
                output.WriteLine(string.Format("{0}:{1}:{2}", file, adress, port));
                // Send message end mark
                output.WriteLine();

                output.Close();
                client.Close();
            }
        }

        private void ListFiles()
        {
            using (TcpClient socket = new TcpClient())
            {
                socket.Connect(IPAddress.Loopback, _port);

                StreamWriter output = new StreamWriter(socket.GetStream());

                // Send request type line
                output.WriteLine("LIST_FILES");
                // Send message end mark and flush it
                output.WriteLine();
                output.Flush();

                // Read response
                string line;
                StreamReader input = new StreamReader(socket.GetStream());
                while ((line = input.ReadLine()) != null && line != string.Empty)
                    Console.WriteLine(line);

                output.Close();
                socket.Close();
            }
        }

        private void ListLocations(string fileName)
        {
            using (TcpClient socket = new TcpClient())
            {
                socket.Connect(IPAddress.Loopback, _port);

                StreamWriter output = new StreamWriter(socket.GetStream());

                // Send request type line
                output.WriteLine("LIST_LOCATIONS");
                // Send message payload
                output.WriteLine(fileName);
                // Send message end mark and flush it
                output.WriteLine();
                output.Flush();

                // Read response
                string line;
                StreamReader input = new StreamReader(socket.GetStream());
                while ((line = input.ReadLine()) != null && line != string.Empty)
                    Console.WriteLine(line);

                output.Close();
                socket.Close();
            }
        }

        #endregion

        #region WORKERS
        private void Register_work(Object o, DoWorkEventArgs args) 
        {
            String[] files = Ficheiros.Text.Split(',');
            _port = Convert.ToUInt16(Porto.Text);
            Register(files, Host.Text, _port);
        }
        private void Unregister_work(Object o, DoWorkEventArgs args) 
        {
            String[] files = Ficheiros.Text.Split(',');
            _port = Convert.ToUInt16(Porto.Text);
            foreach (String f in files)
                Unregister(f, Host.Text, _port);
        }
        private void ListFiles_work(Object o, DoWorkEventArgs args) 
        {
            ListFiles();
        }
        private void ListLocations_work(Object o, DoWorkEventArgs args) 
        {
            ListLocations(Consola.SelectedText.ToString());
        }
        #endregion

        #region BUTTON CLICKS
        private void Register_Click(object sender, EventArgs e)
        {
            _worker = new BackgroundWorker();
            _worker.DoWork += Register_work;
            _worker.RunWorkerAsync();
        }

        private void Unregister_Click(object sender, EventArgs e)
        {
            _worker = new BackgroundWorker();
            _worker.DoWork += Unregister_work;
            _worker.RunWorkerAsync();
        }

        private void ListLocations_Click(object sender, EventArgs e)
        {
            _worker = new BackgroundWorker();
            _worker.DoWork += ListLocations_work;
            _worker.RunWorkerAsync();
        }

        private void ListFiles_Click(object sender, EventArgs e)
        {
            _worker = new BackgroundWorker();
            _worker.DoWork += ListFiles_work;
            _worker.RunWorkerAsync();
        }
        #endregion

    }
}
