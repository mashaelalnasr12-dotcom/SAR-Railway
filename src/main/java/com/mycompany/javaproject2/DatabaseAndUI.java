package com.mycompany.javaproject2;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/railway_db";
    private static final String USER = "root";

    // Change this if your MySQL password is different.
    private static final String PASSWORD = "Narimanna2011";

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
            ensureExtraTables(con);
            return con;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage());
            return null;
        }
    }

    private static void ensureExtraTables(Connection con) {
        try {
            Statement st = con.createStatement();

            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS car_cargo_bookings (" +
                            "cargo_id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "customer_id INT, " +
                            "departure_station VARCHAR(100), " +
                            "arrival_station VARCHAR(100), " +
                            "travel_date VARCHAR(30), " +
                            "departure_time VARCHAR(20), " +
                            "arrival_time VARCHAR(20), " +
                            "car_plate VARCHAR(30), " +
                            "amount DOUBLE, " +
                            "status VARCHAR(30), " +
                            "booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP)"
            );

            try {
                st.executeUpdate("ALTER TABLE employees ADD COLUMN full_name VARCHAR(100)");
            } catch (Exception ignored) {}

            try {
                st.executeUpdate("ALTER TABLE employees ADD COLUMN role VARCHAR(50) DEFAULT 'Staff'");
            } catch (Exception ignored) {}

            try {
                st.executeUpdate("ALTER TABLE car_cargo_bookings ADD COLUMN departure_time VARCHAR(20)");
            } catch (Exception ignored) {}

            try {
                st.executeUpdate("ALTER TABLE car_cargo_bookings ADD COLUMN arrival_time VARCHAR(20)");
            } catch (Exception ignored) {}

            try {
                st.executeUpdate("ALTER TABLE car_cargo_bookings ADD COLUMN car_brand VARCHAR(50)");
            } catch (Exception ignored) {}

            try {
                st.executeUpdate("ALTER TABLE car_cargo_bookings ADD COLUMN car_model VARCHAR(50)");
            } catch (Exception ignored) {}

            try {
                st.executeUpdate("ALTER TABLE car_cargo_bookings ADD COLUMN owner_name VARCHAR(100)");
            } catch (Exception ignored) {}

            try {
                st.executeUpdate("UPDATE employees SET role='Admin' WHERE username='admin'");
            } catch (Exception ignored) {}

            try {
                st.executeUpdate("ALTER TABLE employees ADD COLUMN email VARCHAR(100) UNIQUE");
            } catch (Exception ignored) {}

            // Default admin and staff accounts: {username, name, email, role, old default names, old default emails}
            String[][] defaults = {
                    {"admin",  "Mada",    "mada@gmail.com",    "Admin", "System Admin,Abdullah Mohammed", "admin@sar.sa,admin@gmail.com"},
                    {"staff1", "Noran",   "noran@gmail.com",   "Staff", "SAR Employee,Fahad Khalid",      "fahad@sar.sa,fahad@gmail.com"},
                    {"staff2", "Seba",    "seba@gmail.com",    "Staff", "Sara Ahmed",                     "sara@sar.sa,sara@gmail.com"},
                    {"staff3", "Mashael", "mashael@gmail.com", "Staff", "Nora Saleh",                     "nora@sar.sa,nora@gmail.com"},
                    {"staff4", "Raghad",  "raghad@gmail.com",  "Staff", "",                               ""}
            };
            for (String[] d : defaults) {
                try {
                    PreparedStatement add = con.prepareStatement(
                            "INSERT IGNORE INTO employees (full_name, username, email, password, role) VALUES (?, ?, ?, '1234', ?)");
                    add.setString(1, d[1]);
                    add.setString(2, d[0]);
                    add.setString(3, d[2]);
                    add.setString(4, d[3]);
                    add.executeUpdate();

                    // Only replace names/emails that are still an old default (keeps edits made in the app)
                    PreparedStatement name = con.prepareStatement(
                            "UPDATE employees SET full_name=? WHERE username=? AND (full_name IS NULL OR FIND_IN_SET(full_name, ?) > 0)");
                    name.setString(1, d[1]);
                    name.setString(2, d[0]);
                    name.setString(3, d[4]);
                    name.executeUpdate();

                    PreparedStatement mail = con.prepareStatement(
                            "UPDATE employees SET email=? WHERE username=? AND (email IS NULL OR FIND_IN_SET(email, ?) > 0)");
                    mail.setString(1, d[2]);
                    mail.setString(2, d[0]);
                    mail.setString(3, d[5]);
                    mail.executeUpdate();
                } catch (Exception ignored) {}
            }

            // Any other old employee without an email logs in with their old username
            try {
                st.executeUpdate("UPDATE employees SET email=username WHERE email IS NULL");
            } catch (Exception ignored) {}

        } catch (Exception ignored) {}
    }
}


class UI {
    // ===== Palette =====
    static final Color NAVBAR     = new Color(18, 44, 64);
    static final Color PRIMARY    = new Color(31, 118, 135);
    static final Color PRIMARY_DK = new Color(21, 88, 104);
    static final Color ACCENT_BG  = new Color(230, 245, 248);
    static final Color SURFACE    = new Color(246, 248, 250);
    static final Color BORDER_C   = new Color(220, 226, 230);
    static final Color TEXT_MUTED = new Color(90, 105, 115);
    static final Color INACTIVE   = new Color(235, 238, 241);

    static final Color GOLD       = new Color(198, 153, 68);
    static final Color RED        = new Color(180, 58, 64);
    static final Color GREEN      = new Color(67, 155, 96);
    static final Color BUSINESS   = new Color(22, 65, 82);

    // أسماء قديمة محفوظة
    static Color navy = NAVBAR;
    static Color teal = PRIMARY;
    static Color tealDark = PRIMARY_DK;
    static Color blue = PRIMARY;
    static Color light = SURFACE;
    static Color paleBlue = ACCENT_BG;
    static Color gold = GOLD;
    static Color softGray = INACTIVE;
    static Color red = RED;
    static Color green = GREEN;
    static Color businessSeat = BUSINESS;
    static Color border = BORDER_C;
    static Color textMuted = TEXT_MUTED;

    static final Font FONT_BUTTON = new Font("Arial", Font.BOLD, 14);
    static final Font FONT_BODY   = new Font("Arial", Font.PLAIN, 14);
    static final Font FONT_TITLE  = new Font("Arial", Font.BOLD, 24);

    // ===== Look & Feel (استدعها مرة وحدة في أول main) =====
    static void setupLookAndFeel() {
        try {
            com.formdev.flatlaf.FlatLightLaf.setup();
            UIManager.put("Component.arrowType", "chevron");
            UIManager.put("Component.focusColor", PRIMARY);
            UIManager.put("Component.focusedBorderColor", PRIMARY);
            UIManager.put("Button.arc", 6);
            UIManager.put("Component.arc", 6);
            UIManager.put("TextComponent.arc", 6);
            UIManager.put("Spinner.buttonArrowColor", PRIMARY_DK);
            UIManager.put("ComboBox.buttonArrowColor", PRIMARY_DK);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== Helpers =====
    static Color darken(Color c, float factor) {
        return new Color(
                Math.max(0, (int) (c.getRed() * factor)),
                Math.max(0, (int) (c.getGreen() * factor)),
                Math.max(0, (int) (c.getBlue() * factor)));
    }

    private static JButton createButton(String text, Color bg, Color fg, Color hoverBg) {
        JButton b = new JButton(text);
        b.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFont(FONT_BUTTON);
        b.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (b.isEnabled()) b.setBackground(hoverBg);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(bg);
            }
        });
        return b;
    }

    // ===== Buttons =====
    static JButton button(String text) {
        return createButton(text, PRIMARY, Color.WHITE, PRIMARY_DK);
    }

    static JButton tealButton(String text) {
        return createButton(text, PRIMARY_DK, Color.WHITE, darken(PRIMARY_DK, 0.8f));
    }

    static JButton navyButton(String text) {
        return createButton(text, NAVBAR, Color.WHITE, darken(NAVBAR, 0.8f));
    }

    static JButton lightButton(String text) {
        JButton b = createButton(text, Color.WHITE, PRIMARY_DK, ACCENT_BG);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_C),
                BorderFactory.createEmptyBorder(7, 17, 7, 17)));
        return b;
    }

    static JButton navButton(String text) {
        return createButton(text, NAVBAR, Color.WHITE, PRIMARY_DK);
    }

    static JButton tab(String text, boolean active) {
        JButton b = new JButton(text);
        b.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setFont(new Font("Arial", Font.BOLD, 15));
        b.setBorder(BorderFactory.createEmptyBorder(12, 22, 12, 22));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setTabActive(b, active);
        return b;
    }

    static void setTabActive(JButton b, boolean active) {
        b.setBackground(active ? PRIMARY : INACTIVE);
        b.setForeground(active ? Color.WHITE : TEXT_MUTED);
    }

    // ===== Labels =====
    static JLabel title(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_TITLE);
        l.setForeground(NAVBAR);
        return l;
    }

    static JLabel subtitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_BODY);
        l.setForeground(TEXT_MUTED);
        return l;
    }

    static JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_BUTTON);
        l.setForeground(NAVBAR);
        return l;
    }

    // ===== Form fields =====
    static void styleCombo(JComboBox<?> c) {
        c.setFont(FONT_BODY);
        c.setBackground(Color.WHITE);
        c.setForeground(NAVBAR);
        c.setPreferredSize(new Dimension(c.getPreferredSize().width, 40));
    }

    static void styleSpinner(JSpinner s) {
        s.setFont(FONT_BODY);
        s.setPreferredSize(new Dimension(s.getPreferredSize().width, 40));
        JComponent editor = s.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
            tf.setBackground(Color.WHITE);
            tf.setForeground(NAVBAR);
            tf.setFont(FONT_BODY);
            tf.setHorizontalAlignment(JTextField.LEFT);
        }
    }

    static void styleField(JTextField f) {
        f.setFont(FONT_BODY);
        f.setForeground(NAVBAR);
        f.setBackground(Color.WHITE);
        f.setPreferredSize(new Dimension(f.getPreferredSize().width, 40));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_C),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));
    }

    // ===== Containers =====
    static JPanel navbarPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(NAVBAR);
        p.setPreferredSize(new Dimension(0, 55));
        p.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        return p;
    }

    static JPanel whiteCard() {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_C),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));
        return p;
    }

    // ===== Tables =====
    static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setForeground(NAVBAR);
        table.setRowHeight(30);
        table.setGridColor(BORDER_C);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(ACCENT_BG);
        table.setSelectionForeground(NAVBAR);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setPreferredSize(new Dimension(0, 34));
        table.getTableHeader().setFont(FONT_BUTTON);
        table.getTableHeader().setBackground(PRIMARY_DK);
        table.getTableHeader().setForeground(Color.WHITE);
    }

    static void styleScrollPane(JScrollPane sp) {
        sp.setBorder(BorderFactory.createLineBorder(BORDER_C));
        sp.getViewport().setBackground(Color.WHITE);
    }

    // ===== Images =====
    static JLabel imageBox(String imageName, int width, int height) {
        JLabel l = new JLabel() {
            Image image;

            {
                try {
                    java.net.URL imgURL = UI.class.getResource("/com/mycompany/javaproject2/images/" + imageName);
                    if (imgURL != null) {
                        image = new ImageIcon(imgURL).getImage();
                    }
                } catch (Exception e) {
                    image = null;
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (image != null) {
                    int panelW = getWidth();
                    int panelH = getHeight();
                    int imgW = image.getWidth(this);
                    int imgH = image.getHeight(this);

                    if (imgW > 0 && imgH > 0) {
                        double scale = Math.max((double) panelW / imgW, (double) panelH / imgH);
                        int newW = (int) Math.round(imgW * scale);
                        int newH = (int) Math.round(imgH * scale);
                        int x = (panelW - newW) / 2;
                        int y = (panelH - newH) / 2;

                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                        g2.drawImage(image, x, y, newW, newH, this);
                        g2.dispose();
                    }
                } else {
                    g.setColor(new Color(226, 235, 238));
                    g.fillRect(0, 0, getWidth(), getHeight());
                    g.setColor(NAVBAR);
                    g.setFont(FONT_BUTTON);
                    g.drawString("Image not found: " + imageName, 15, 25);
                }
            }
        };

        l.setPreferredSize(new Dimension(width, height));
        l.setMinimumSize(new Dimension(width, height));
        l.setOpaque(true);
        l.setBackground(new Color(226, 235, 238));
        l.setBorder(BorderFactory.createLineBorder(BORDER_C));
        return l;
    }

    // ===== Window =====
    static void maximizeWindow(JFrame frame) {
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setMinimumSize(new Dimension(1000, 650));
    }
}