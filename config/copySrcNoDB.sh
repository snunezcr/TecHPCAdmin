#!/bin/bash
#NOTE: This file should be updated as we use the other dirs

originFolder="/home/grupo13/NetBeansProjects/Hpca"
originCodeFolder=$originFolder"/src/java"

targetFolder="/home/grupo13/HPCAdmin/TecHPCAdmin"
targetCodeFolder=$targetFolder"/src"

#Let's copy the configuration files
cp /etc/hpca/hpca.conf $targetCodeFolder/config/

#First we need to clean the target directories, in case we moved, removed or renamed any file/dir
#We will erase files only from folders that contain useful data
rm -r $targetCodeFolder/controller/
rm -r $targetCodeFolder/include/
rm -r $targetCodeFolder/model/
rm -r $targetCodeFolder/view/

#Now let's copy everything as needed

#Copying model
cp -r $originCodeFolder/ $targetCodeFolder/model/

#Copying view
cp -r $originFolder/web/ $targetCodeFolder/view/
cp -r $originFolder/include/ $targetCodeFolder/include/

#Copying controller
mkdir $targetCodeFolder/controller
mkdir $targetCodeFolder/controller/controller $targetCodeFolder/controller/servlets
cp -r $originCodeFolder/controller $targetCodeFolder/controller/
cp -r $originCodeFolder/servlets/ $targetCodeFolder/controller/

#We copied some files that we shouldn't so let's erase them
rm -r $targetCodeFolder/view/META-INF $targetCodeFolder/view/WEB-INF
rm -r $targetCodeFolder/model/controller $targetCodeFolder/model/servlets
