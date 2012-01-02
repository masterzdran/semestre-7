#!/bin/sh
sudo mount ./lsc-hd63-flat.img /mnt/lsc/hd0p1 -o loop,offset=32256,sizelimit=22159872,nodev,noexec,rw,users,uid=1000,gid=1000
sudo mount ./lsc-hd63-flat.img /mnt/lsc/hd0p2 -o loop,offset=22192128,sizelimit=43868160,nodev,noexec,rw,users,uid=1000,gid=1000
