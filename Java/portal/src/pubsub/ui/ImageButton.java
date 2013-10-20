package pubsub.ui;

import java.awt.Color;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class ImageButton extends JButton {
	private String prefix = "images/", postfix = ".png", name;
	public ImageButton(String name) {
		super();
		setBorderPainted(false);
		setFocusPainted(false);
		setContentAreaFilled(false);
		setRolloverEnabled(true);
		this.name = name;
		ImageIcon defaultIcon = new ImageIcon(prefix + name + postfix);
//		setForeground(Color.WHITE);
		
		setIcon(defaultIcon);
		setSize(defaultIcon.getIconWidth(), defaultIcon.getIconHeight());
		setRolloverIcon(new ImageIcon(prefix + name + "-hover" + postfix));
		setPressedIcon(new ImageIcon(prefix + name + "-press" + postfix));
	}
	public void setLabelPainted(boolean p) {
		if (p && name != null) {
			setText(name);
			setMargin(new Insets(0,0,0,0));
			setIconTextGap(-getWidth()+10);
		} else {
			setText(null);
		}
	}
}
