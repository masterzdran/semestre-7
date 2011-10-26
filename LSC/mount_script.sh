sudo mkdir /mnt/lsc
sudo chown nuno.nuno /mnt/lsc
mkdir /mnt/lsc/hd0p1
mkdir /mnt/lsc/hd0p2

sudo mount /media/DATA/ISEL/Semestre7/LSC/dsk/lsc-hd63-flat.img /mnt/lsc/hd0p1 -o loop,offset=32256,sizelimit=22159872,nodev,noexec,rw,users,uid=1000,gid=1000
sudo mount /media/DATA/ISEL/Semestre7/LSC/dsk/lsc-hd63-flat.img /mnt/lsc/hd0p2 -o loop,offset=22192128,sizelimit=43868160,nodev,noexec,rw,users,uid=1000,gid=1000

sudo chown nuno.nuno /mnt/lsc/hd0p2
