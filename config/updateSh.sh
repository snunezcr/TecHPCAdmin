#!/bin/bash
#NOTE: This file should be updated as we use the other dirs

originFolder="/home/grupo13/HPCAdmin/TecHPCAdmin/src"

targetFolder="/home/grupo13/NetBeansProjects/Hpca"
targetCodeFolder=$targetFolder"/src/java"
targetWebFolder=$targetFolder"/web"

#Let's copy the configuration files
sudo cp $originFolder/config/hpca.conf /etc/hpca/hpca.conf

#First we need to clean the target directories, in case we moved, removed or renamed any file/dir
#We will erase files only from folders that contain useful data
rm -r $targetCodeFolder/
rm -r $targetWebFolder/administrator
rm -r $targetWebFolder/images
rm -r $targetWebFolder/normal
rm -r $targetWebFolder/styles
rm -r $targetWebFolder/*.*

#Now let's copy everything as needed

#Copying model
cp -r $originFolder/model $targetCodeFolder
cp -r $originFolder/controller/controller $targetCodeFolder
cp -r $originFolder/controller/servlets $targetCodeFolder

#Copying view
cp -r $originFolder/view/administrator $targetWebFolder
cp -r $originFolder/view/images $targetWebFolder
cp -r $originFolder/view/normal $targetWebFolder
cp -r $originFolder/view/styles $targetWebFolder
cp -r $originFolder/view/*.* $targetWebFolder

#Copying libraries
cp -r $originFolder/include/ $targetFolder
