package org.opendoors.gemini.common;

import org.opendoors.gemini.Gemini;

import java.util.Scanner;

/**
 * Contributors:
 * yourilefers
 *
 * @since v1.0
 */
public class Menu {

    /** The gemini instance */
    private Gemini gemini;

    /**
     * Constructor for the menu.
     * @param gemini
     */
    public Menu(Gemini gemini) {
        this.gemini = gemini;
    }

    /**
     * Route the action.
     * @param action
     */
    private void route(int action) {
        switch(action) {
            case 1:
                gemini.startServer();
                return;
            case 2:
                gemini.actionIndexTypes();
                break;
            case 3:
                exit();
                return;
        }

        // Go to home
        showMenu();
    }

    /**
     * Show the main menu.
     */
    public void showMenu() {
        try {

            // Print the menu
            System.out.println("\nGEMINI HOME MENU\n");
            System.out.println("1 | Start server | NOTE: the server may be closed by pressing ctrl + c.");
            System.out.println("2 | Show types");
            System.out.println("3 | Exit");
            System.out.println("");

            // Get input
            System.out.print("Action: ");
            Scanner in = new Scanner(System.in);
            int value = Integer.parseInt(in.next());

            // Route the value
            route(value);

        } catch(Exception e) {

            // Oops
            System.out.println("Invalid input! Please give the number of the action.");
            showMenu();

        }
    }

    /**
     * Exit the program.
     */
    public void exit() {

        // Exit
        Config.getInstance().setExiting();
        System.out.println("Gemini closing. Goodbye.");
        gemini.exit();

    }

}
