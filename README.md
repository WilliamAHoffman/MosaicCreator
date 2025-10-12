## Purpose
This program turns pixels from an input image into textures from a folder of provided 16x16 images. If an input image is too big the program will automatically resize it, please note that it won't resize images from the texture folder so make sure they are all 16x16.

## How to use
  
In order to use this program you must first download all the files, and then add a folder called "texturePack". This folder will hold all the 16x16 images which you would like to use for the conversion. When I tested this project, I used textures from the game *Minecraft*. I do not own these textures so I have not included them here. I have provided an example output image using these textures in the "output" folder. 

Once you have added all the textures you want into the folder, you will have to erase the example text in the file called "anti-Palette.txt". This file takes in names of textures (minus the file entension) and will not allow them to be used in the image. This is useful if you think some textures look bad but you don't want to remove them from the folder.

Next you must erase the example palette in the file called "Palette.txt". This file is the opposite of the "anti-Palette.txt" file. It only allows textures to be used which are included in the file. I have provided some example palettes for whiteList in "unusedPallet.txt". 

Finally, delete the example image I added in "placeImageHere" and put your own image in as input. Then run this program through IntelliJ or some other program and it will convert images quickly.

## How it works

This program first checks if an image is too big and resizes it. I recommend resizing the image on a professional website because the one Java provides leaves ugly artifacts. Make sure the image is no bigger than 150x150. After this step, the program will look through each pixel and finds the texture which has the closest average color of the image. It then creates a new image which will always be 16 times larger in width and height of the original image. 
