package iart.color_schemes;

import iart.JFXMain;
import iart.recorder.Recorder;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import org.clapper.util.classutil.*;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This class provides a static method to setup all the color schemes, and add them to the main window menu bar, so
 * the user can select them as desired.
 */
public class ColorSchemeSetup {

	/**
	 * Sets up all color schemes defined in ColorScheme.topLevelSchemes. Any subschemes (including superschemes that
	 * are subschemes of a top level superscheme) of those top level schemes will be added in automatically.
	 *
	 * @param menuBar MenuBar instance to add the Color Scheme menu to
	 */
	public static void setupColorSchemes(MenuBar menuBar) {
		// Setup color scheme menu
		Menu colorSchemeMenu = new Menu("Color Scheme");
		ToggleGroup tGroup = new ToggleGroup();

		ClassFinder finder = new ClassFinder();
		try {
			finder.add(new File(JFXMain.class.getProtectionDomain().getCodeSource().getLocation().toURI()));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		ClassFilter filter = new AndClassFilter(new SubclassClassFilter(ColorScheme.class));

		Collection<ClassInfo> colorSchemes = new ArrayList<>();
		finder.findClasses(colorSchemes, filter);

		for (ClassInfo classInfo : colorSchemes) {
			try {
				((ColorScheme) (Class.forName(classInfo.getClassName()).getConstructor().newInstance()))
						.registerSuperScheme();
			} catch (Exception e) {
				System.err.println("Error setting up: " + classInfo.getClassName());
			}
		}

		for (String superScheme : ColorScheme.topLevelSchemes)
			setupScheme(colorSchemeMenu, tGroup, superScheme);

		menuBar.getMenus().add(1, colorSchemeMenu);
	}

	/**
	 * Sets up an individual color scheme. If the scheme is a superscheme, it will create a menu under which to add
	 * its subschemes, and add them in recursively. Otherwise, it will add the scheme to the given menu.
	 *
	 * @param parentMenu  Menu into which to add the scheme being passed as a parameter
	 * @param toggleGroup Toggle group instance. Must be the same instance for all schemes
	 * @param scheme      Scheme to be set up. If the scheme is a superscheme, a menu will be created that goes
	 *                    inside the current menu, and its subschemes will be added into the submenu, and so on,
	 *                    creating as many menus as necessary, to reflect the hierarchy of schemes in the code
	 */
	private static void setupScheme(Menu parentMenu, ToggleGroup toggleGroup, String scheme) {
		ArrayList<String> subSchemes = ColorScheme.superSchemes.get(scheme);

		if (subSchemes == null || subSchemes.size() == 1) {
			try {
				if (subSchemes == null)
					ColorScheme.colorSchemes.put(
							scheme, (ColorScheme) Class.forName("iart.color_schemes." + scheme + "Scheme")
													   .getConstructor().newInstance()
					);
				else // if (subSchemes.size() == 1)
					ColorScheme.colorSchemes.putIfAbsent(
							scheme, (ColorScheme) Class.forName("iart.color_schemes." + subSchemes.get(0) + "Scheme")
													   .getConstructor().newInstance()
					);

				String[] schemeDisplayName = scheme.split("\\.");
				RadioMenuItem schemeItem = new RadioMenuItem(schemeDisplayName[schemeDisplayName.length - 1]);
				schemeItem.setToggleGroup(toggleGroup);
				schemeItem.setOnAction(event -> Recorder.colorScheme = swapColorScheme(scheme));

				// If scheme is the default scheme, set checkmark
				if (Recorder.colorScheme.getClass() == ColorScheme.colorSchemes.get(scheme).getClass())
					schemeItem.setSelected(true);
				parentMenu.getItems().add(schemeItem);
			} catch (Exception e) {
				System.err.println("Color scheme \"" + scheme + "\" could not be found.");
			}
		} else {
			Menu subMenu = new Menu(scheme);
			for (String subScheme : subSchemes)
				setupScheme(subMenu, toggleGroup, subScheme);
			parentMenu.getItems().add(subMenu);
		}
	}

	/**
	 * Allows the active color scheme to do some cleanup if necessary before being swapped.
	 *
	 * @param colorSchemeStr Name of the color scheme that is to replace the active one
	 * @return ColorScheme corresponding to the name passed as an argument
	 */
	private static ColorScheme swapColorScheme(String colorSchemeStr) {
		Recorder.colorScheme.stopColorScheme();

		ColorScheme newScheme = ColorScheme.colorSchemes.get(colorSchemeStr);
		newScheme.startColorScheme();
		return newScheme;
	}
}
