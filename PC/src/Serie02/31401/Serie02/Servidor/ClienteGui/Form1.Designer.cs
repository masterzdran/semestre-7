namespace ClienteGui
{
    partial class Form1
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.Register_btn = new System.Windows.Forms.Button();
            this.Unregister_btn = new System.Windows.Forms.Button();
            this.ListFiles_btn = new System.Windows.Forms.Button();
            this.ListLocations_btn = new System.Windows.Forms.Button();
            this.label1 = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.label3 = new System.Windows.Forms.Label();
            this.Ficheiros = new System.Windows.Forms.TextBox();
            this.Host = new System.Windows.Forms.TextBox();
            this.Porto = new System.Windows.Forms.TextBox();
            this.Consola = new System.Windows.Forms.RichTextBox();
            this.SuspendLayout();
            // 
            // Register
            // 
            this.Register_btn.Location = new System.Drawing.Point(12, 21);
            this.Register_btn.Name = "Register";
            this.Register_btn.Size = new System.Drawing.Size(96, 37);
            this.Register_btn.TabIndex = 0;
            this.Register_btn.Text = "Register";
            this.Register_btn.UseVisualStyleBackColor = true;
            this.Register_btn.Click += new System.EventHandler(this.Register_Click);
            // 
            // Unregister
            // 
            this.Unregister_btn.Location = new System.Drawing.Point(12, 96);
            this.Unregister_btn.Name = "Unregister";
            this.Unregister_btn.Size = new System.Drawing.Size(96, 37);
            this.Unregister_btn.TabIndex = 1;
            this.Unregister_btn.Text = "Unregister";
            this.Unregister_btn.UseVisualStyleBackColor = true;
            this.Unregister_btn.Click += new System.EventHandler(this.Unregister_Click);
            // 
            // ListFiles
            // 
            this.ListFiles_btn.Location = new System.Drawing.Point(12, 246);
            this.ListFiles_btn.Name = "ListFiles";
            this.ListFiles_btn.Size = new System.Drawing.Size(96, 36);
            this.ListFiles_btn.TabIndex = 2;
            this.ListFiles_btn.Text = "List Files";
            this.ListFiles_btn.UseVisualStyleBackColor = true;
            this.ListFiles_btn.Click += new System.EventHandler(this.ListFiles_Click);
            // 
            // ListLocations
            // 
            this.ListLocations_btn.Location = new System.Drawing.Point(12, 171);
            this.ListLocations_btn.Name = "ListLocations";
            this.ListLocations_btn.Size = new System.Drawing.Size(96, 37);
            this.ListLocations_btn.TabIndex = 3;
            this.ListLocations_btn.Text = "List Locations";
            this.ListLocations_btn.UseVisualStyleBackColor = true;
            this.ListLocations_btn.Click += new System.EventHandler(this.ListLocations_Click);
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(145, 4);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(44, 13);
            this.label1.TabIndex = 4;
            this.label1.Text = "Ficheiro";
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(145, 47);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(29, 13);
            this.label2.TabIndex = 5;
            this.label2.Text = "Host";
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(275, 47);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(26, 13);
            this.label3.TabIndex = 6;
            this.label3.Text = "Port";
            // 
            // Ficheiros
            // 
            this.Ficheiros.Location = new System.Drawing.Point(148, 21);
            this.Ficheiros.Name = "Ficheiros";
            this.Ficheiros.Size = new System.Drawing.Size(270, 20);
            this.Ficheiros.TabIndex = 7;
            // 
            // Host
            // 
            this.Host.Location = new System.Drawing.Point(148, 63);
            this.Host.Name = "Host";
            this.Host.Size = new System.Drawing.Size(100, 20);
            this.Host.TabIndex = 8;
            // 
            // Porto
            // 
            this.Porto.Location = new System.Drawing.Point(278, 63);
            this.Porto.Name = "Porto";
            this.Porto.Size = new System.Drawing.Size(44, 20);
            this.Porto.TabIndex = 9;
            // 
            // Consola
            // 
            this.Consola.AcceptsTab = true;
            this.Consola.BackColor = System.Drawing.SystemColors.ActiveCaption;
            this.Consola.ForeColor = System.Drawing.SystemColors.HotTrack;
            this.Consola.Location = new System.Drawing.Point(148, 90);
            this.Consola.Name = "Consola";
            this.Consola.ReadOnly = true;
            this.Consola.Size = new System.Drawing.Size(270, 193);
            this.Consola.TabIndex = 10;
            this.Consola.Text = "";
            // 
            // Form1
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(431, 304);
            this.Controls.Add(this.Consola);
            this.Controls.Add(this.Porto);
            this.Controls.Add(this.Host);
            this.Controls.Add(this.Ficheiros);
            this.Controls.Add(this.label3);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.ListLocations_btn);
            this.Controls.Add(this.ListFiles_btn);
            this.Controls.Add(this.Unregister_btn);
            this.Controls.Add(this.Register_btn);
            this.Name = "Form1";
            this.Text = "ClienteGui";
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Button Register_btn;
        private System.Windows.Forms.Button Unregister_btn;
        private System.Windows.Forms.Button ListFiles_btn;
        private System.Windows.Forms.Button ListLocations_btn;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.TextBox Ficheiros;
        private System.Windows.Forms.TextBox Host;
        private System.Windows.Forms.TextBox Porto;
        private System.Windows.Forms.RichTextBox Consola;
    }
}

