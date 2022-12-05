package com.example.median_cut;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class MedianCutController {
    @FXML
    Pane c1,c2,c3,c4,c5,c6,c7,c8,c9,c10;

    @FXML
    javafx.scene.control.Label l1,l2,l3,l4,l5,l6,l7,l8,l9,l10;


    @FXML
    ImageView imageView;

    BufferedImage bufferedImage;
    int r,g,b;
    int height,width;
    int id = 0;
    ArrayList<int[]> rgbList = new ArrayList<int[]>();
    ArrayList<int[]> rgbListSorted = new ArrayList<int[]>();
    ArrayList<int[]> noColors = new ArrayList<int[]>();


    private String imagePath;

    @FXML
    public void initialize(){
        addNoColors();
    }

    private void addNoColors(){
        noColors.add(new int[]{0,0,0});
        noColors.add(new int[]{255,255,255});
    }

    private boolean ifColorInNoColors(int[] color){
        for(int[] noColor : noColors){
            if(Arrays.equals(noColor,color)){
                return true;
            }
        }
        return false;
    }

    public  static String fileChooser(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                //new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif")
                //new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"),
                //new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
//            welcomeText.setText(selectedFile.getAbsolutePath());
            System.out.println(selectedFile.getAbsolutePath());
            return selectedFile.getAbsolutePath();
            //copyFileUsingApacheCommonsIO(selectedFile.getAbsolutePath(), "C:\\Users\\Public\\Pictures\\Sample Pictures\\test.jpg");
        }
        return null;
    }


    public void getImagePath(){
        imagePath = fileChooser();
        if(imagePath == null){
            return;
        }

        Image image = new Image("file:"+imagePath);
        imageView.setImage(image);
        long start =System.currentTimeMillis();
        find();
        System.out.println(System.currentTimeMillis() - start + " ms");
    }


    public void find(){

        try {
            bufferedImage = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        height = bufferedImage.getHeight();
        width = bufferedImage.getWidth();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                id++;
                Color color = new Color(bufferedImage.getRGB(x,y));
                r = color.getRed();
                g = color.getGreen();
                b = color.getBlue();
                int[] rgb =new int[] {r,g,b};
                // if rgb in noColors continue
                if(ifColorInNoColors(rgb)){
//                    System.out.println("yes it contains noColor");
                    continue;
                }
                rgbList.add(rgb);
            }
        }
        int index =  getMaxColorRangeIndex();
        sortRgbListByMaxRange(index);
        getTenColors();
    }

    public int  getMaxColorRangeIndex(){
        int[] rangeRGB = new int[3];

        int maxR = 0;
        int maxG = 0;
        int maxB = 0;

        int minR = 255;
        int minG = 255;
        int minB = 255;


        for (int[] ints : rgbList) {
            if (ints[0] > maxR) {
                maxR = ints[0];
            }
            if (ints[1] > maxG) {
                maxG = ints[1];
            }
            if (ints[2] > maxB) {
                maxB = ints[2];
            }

            if (ints[0] < minR) {
                minR = ints[0];
            }

            if (ints[1] < minG) {
                minG = ints[1];
            }

            if (ints[2] < minB) {
                minB = ints[2];
            }
        }

        rangeRGB[0] = maxR - minR;
        rangeRGB[1] = maxG - minG;
        rangeRGB[2] = maxB - minB;

        // get max range
        int maxRange = 0;
        int index = 0;
        for (int i : rangeRGB) {
            if (i > maxRange) {
                maxRange = i;
            }
        }
        for (int i = 0; i < rangeRGB.length; i++) {
            if (rangeRGB[i] == maxRange) {
                index = i;
                break;
            }
        }

        //System.out.println( "maxRange = " + maxRange + " index = " + index);

        // if rangeR = rangeG = rangeB
        // then the image is gray
        //System.out.println("rangeR = "+rangeRGB[0]+", rangeG = "+rangeRGB[1]+", rangeB = "+rangeRGB[2]);

        return  index;
    }

    private void sortRgbListByMaxRange(int index) {
        // copy rgbList to rgbListSorted
        rgbListSorted = (ArrayList<int[]>) rgbList.clone();

        // from small to big (0,0,0 to 255,255,255)
        rgbListSorted.sort((o1, o2) -> {
            if (o1[index] < o2[index]) {
                return -1;
            } else if (o1[index] > o2[index]) {
                return 1;
            } else {
                return 0;
            }
        });

        //System.out.println(rgbListSorted.get(0)[0] + " " + rgbListSorted.get(0)[1] + " " + rgbListSorted.get(0)[2]);
        int size = rgbListSorted.size();
        //System.out.println(rgbListSorted.get(size - 1)[0] + " " + rgbListSorted.get(size - 1)[1] + " " + rgbListSorted.get(size - 1)[2]);
    }


    private void  getTenColors(){
        int size = rgbListSorted.size();
        int step = size / 10;
        ArrayList<int[]> tenColors = new ArrayList<int[]>();
        for (int i = 0; i < 10; i++) {
            int[] rgb = rgbListSorted.get(i * step);
            tenColors.add(rgb);
            //System.out.println(rgb[0] + " " + rgb[1] + " " + rgb[2]);
        }

        Pane[] panes = new Pane[]{c1,c2,c3,c4,c5,c6,c7,c8,c9,c10};
        javafx.scene.control.Label[] labels = new Label[]{l1,l2,l3,l4,l5,l6,l7,l8,l9,l10};

        for (int i = 0; i < 10; i++) {
            int[] rgb = tenColors.get(i);
            String hex = String.format("#%02x%02x%02x", rgb[0], rgb[1], rgb[2]);
            try{
                panes[i].setStyle("-fx-background-color: rgb(" + rgb[0] + "," + rgb[1] + "," + rgb[2] + ")");
                labels[i].setText(hex);
            }catch (Exception e){
                //System.out.println("i = "+i);
                e.printStackTrace();
            }
        }



    }

    private String getHex(int r, int g, int b){
        String hex = "#";
        hex += Integer.toHexString(r);
        hex += Integer.toHexString(g);
        hex += Integer.toHexString(b);
        return hex;
    }
}
