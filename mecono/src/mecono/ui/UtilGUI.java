/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mecono.ui;

/**
 *
 * @author sabreok
 */
public class UtilGUI {
	public static String formatPercentage(double percent){
		percent = ((int) (percent * 1000)) / 10;
		return percent + "%";
	}
}
