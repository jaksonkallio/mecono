/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mecono.ui;

import javafx.geometry.Insets;
import javafx.scene.text.Font;

/**
 *
 * @author sabreok
 */
public class UtilGUI {
	public static String formatPercentage(double percent){
		percent = (double) ((int) (percent*1000)) / 10;
		return percent + "%";
	}
	
	public static String getBooleanString(boolean bool){
		if(bool){
			return "true";
		}else{
			return "false";
		}
	}
	
	public static final Font TITLE_FONT = new Font("Arial", 16);
	public static final Insets STD_PADDING = new Insets(10, 10, 10, 10);
	public static final int STD_SPACING = 10;
}
