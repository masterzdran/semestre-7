#home="/mnt/hgfs/D/ISEL/Semestre7"
#username=isel
home="/home/masterzdran/WorkingArea/Isel/semestre-7" 
username=masterzdran

if [ ! -d /mnt/lsc ];then
  sudo mkdir /mnt/lsc
  sudo chown $username.$username /mnt/lsc
fi
if [ ! -d /mnt/lsc/hd0p1 ];then
 mkdir /mnt/lsc/hd0p1
fi
if [ ! -d /mnt/lsc/hd0p2 ];then
 mkdir /mnt/lsc/hd0p2
fi
sudo mount $home/LSC/dsk/lsc-hd63-flat.img /mnt/lsc/hd0p1 -o loop,offset=32256,sizelimit=22159872,nodev,noexec,rw,users,uid=1000,gid=1000
sudo mount $home/LSC/dsk/lsc-hd63-flat.img /mnt/lsc/hd0p2 -o loop,offset=22192128,sizelimit=43868160,nodev,noexec,rw,users,uid=1000,gid=1000

sudo chown $username.$username /mnt/lsc/hd0p2
