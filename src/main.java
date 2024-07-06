import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;
import java.util.*;

public class main {

    static int[] averageColor(File texture){
        try {
            int[] RGB = new int[3];
            int averageRed = 0;
            int averageGreen = 0;
            int averageBlue = 0;
            BufferedImage image = ImageIO.read(texture);
            int totalPix = image.getHeight()*image.getWidth();
            for(int x = 0; x < image.getHeight(); x++){
                for(int y = 0; y < image.getWidth(); y++){
                    int color = image.getRGB(x,y);
                    averageRed += findRed(color);
                    averageGreen += findGreen(color);
                    averageBlue += findBlue(color);
                }
            }
            averageRed = averageRed/totalPix;
            averageGreen = averageGreen/totalPix;
            averageBlue = averageBlue/totalPix;
            RGB[0] = averageRed;
            RGB[1] = averageGreen;
            RGB[2] = averageBlue;
            //System.out.println(texture + " success");
            return RGB;
        }
        catch (Exception IOException){
            System.out.println(texture + " failure");
//            texture.delete();
            return null;
        }
    }

    static int findGreen(int color){
        return (color & 0xff00) >> 8;
    }

    static int findRed(int color){
        return (color & 0xff0000) >> 16;
    }

    static int findBlue(int color){
        return color & 0xff;
    }

    static int colorDist(int[] RGB, int color){
        return Math.abs(RGB[0] - findRed(color)) + Math.abs(RGB[1] - findGreen(color)) + Math.abs(RGB[2] - findBlue(color));
    }

    static int closestColor(int color, ArrayList<int[]> RGB){
        int bestCD = colorDist(RGB.getFirst(), color);
        int bestIndex = 0;
        for(int i = 1; i < RGB.size(); i++){
            if(colorDist(RGB.get(i), color) < bestCD){
                bestCD = colorDist(RGB.get(i), color);
                bestIndex = i;
            }
        }
        return bestIndex;
    }

    static void createMosaic(ArrayList<File> textures, ArrayList<int[]> RGB, File picture){
        try {
            BufferedImage image = ImageIO.read(picture);
            BufferedImage output = new BufferedImage(image.getWidth() * 16, image.getHeight() * 16, BufferedImage.TYPE_INT_RGB);
            Graphics2D outputGraphic = output.createGraphics();
            HashMap<String, Integer> usedTextures = new HashMap<>();

            int outputX = -16;
            int outputY = -16;

            for(int x = 0; x < image.getWidth(); x++){
                outputX += 16;
                for(int y = 0; y < image.getHeight(); y++){
                    outputY += 16;
                    int color = image.getRGB(x, y);
                    if(color != 0) {
                        int index = closestColor(color, RGB);
                        BufferedImage bestImage = ImageIO.read(textures.get(index));
                        AffineTransform at = new AffineTransform();
                        outputGraphic.drawImage(bestImage, outputX, outputY, null);
                        if(usedTextures.containsKey(textures.get(index).getName())){
                            usedTextures.put(textures.get(index).getName(), usedTextures.get(textures.get(index).getName()) + 1);
                        }
                        else usedTextures.put(textures.get(index).getName(), 1);
                    }
                }
                outputY = -16;
            }

            File mosaic = new File("output/mosaic.png");
            ImageIO.write(output, "png", mosaic);

            File list = new File("output/usedTextures.txt");
            PrintWriter listWriter = new PrintWriter(list);

            for (String name : usedTextures.keySet()) {
                listWriter.println(name.replace(".png", "") + " = " + usedTextures.get(name));
            }

            listWriter.close();

        }
        catch (Exception IOException){
            System.out.println(picture + " failure");
        }
    }

    static ArrayList<String> readFile(String name){
        try {
            File textureFolder = new File(name);
            Scanner dictionary = new Scanner(textureFolder);
            ArrayList<String> whiteList = new ArrayList<>();
            while(dictionary.hasNext()){
                whiteList.add(dictionary.next());
            }
            dictionary.close();
            return whiteList;
        }
        catch (Exception IOException){
            System.out.println("whiteList error");
            return null;
        }
    }

    static ArrayList<File> textureStartUp(){
        File textureFolder = new File("texturePack");
        ArrayList<File> textures = new ArrayList<>();
        ArrayList<String> whiteList = readFile("whiteList.txt");
        ArrayList<String> blackList = readFile("blackList.txt");

        if(whiteList.size() == 0){
            for(File texture : textureFolder.listFiles()){
                if(!blackList.contains(texture.getName().replace(".png", ""))){
                    textures.add(texture);
                }
            }
        }
        else {
            for(String texture : whiteList){
                textures.add(new File("texturePack/" + texture + ".png"));
            }
        }

        return textures;
    }

    static ArrayList<int[]> findAllRGB(ArrayList<File> textures){
        ArrayList<int[]> RGB = new ArrayList<>();
        for (File texture : textures){
            RGB.add(averageColor(texture));
        }
        return RGB;
    }

    static File findFile(){
        File textureFolder = new File("placeImageHere");
        File pictureName = Objects.requireNonNull(textureFolder.listFiles())[0];
        return pictureName;
    }

    static File resizeImage(File image, int size){
        try{
            BufferedImage inputImage = ImageIO.read(image);
            double height = inputImage.getHeight();
            double width = inputImage.getWidth();
            if(Math.max(height, width) <= size) return image;

            double scaledWidth = 0;
            double scaledHeight = 0;
            if(width >= height){
                scaledWidth = size;
                scaledHeight = size * (height / width);
            }
            else{
                scaledWidth = size * (width / height);
                scaledHeight = size;
            }

            BufferedImage outputImage = new BufferedImage((int) scaledWidth, (int) scaledHeight,inputImage.getType());

            Graphics2D g2d = outputImage.createGraphics();
            g2d.drawImage(inputImage, 0, 0, (int) scaledWidth, (int) scaledHeight, null);
            g2d.dispose();

            File outputfile = new File("output/ResizedImage.png");
            ImageIO.write(outputImage, "png", outputfile);
            return outputfile;
        }
        catch (Exception IOException) {
            System.out.println(image + " failure");
            return null;
        }
    }

    public static void main(String[] args){
        File image = findFile();
        image = resizeImage(image, 150);
        ArrayList<File> textures = textureStartUp();
        ArrayList<int[]> RGB = findAllRGB(textures);
        createMosaic(textures, RGB, image);
    }
}
