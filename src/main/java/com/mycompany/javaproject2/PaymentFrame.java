package com.mycompany.javaproject2;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.io.*;

class PaymentFrame extends JFrame {

    int customerId;
    int adults;
    int children;
    int infants;
    int tripId;
    String fareType;
    double unitPrice;
    ArrayList<String> selectedSeats;
    String tripType;
    String returnDate;
    int selectedReturnTripId;
    int outboundTripId = -1;
    ArrayList<String> outboundSeats = new ArrayList<>();

    JComboBox<String> method =
            new JComboBox<>(new String[]{"Mada", "Visa", "MasterCard", "Cash"});

    public PaymentFrame(int customerId, int tripId, String fareType, double unitPrice,
                    ArrayList<String> selectedSeats, String tripType, String returnDate,
                    int selectedReturnTripId, ArrayList<String> outboundSeats, int outboundTripId,
                    int adults, int children, int infants) {

           this.customerId = customerId;
           this.adults = adults;
           this.children = children;
           this.infants = infants;
           this.tripId = tripId;
           this.fareType = fareType;
           this.unitPrice = unitPrice;
           this.selectedSeats = selectedSeats;
           this.tripType = tripType;
           this.returnDate = returnDate;
           this.selectedReturnTripId = selectedReturnTripId;
           this.outboundSeats = outboundSeats;
           this.outboundTripId = outboundTripId;

        setTitle("Payment");
        setSize(500, 450);
        setLocationRelativeTo(null);
        setResizable(false);

        double total = unitPrice * selectedSeats.size();

        JPanel p = new JPanel(new GridLayout(9, 1, 6, 6));
        p.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        p.setBackground(UI.light);

        JLabel title = UI.title("Payment Summary");
        title.setHorizontalAlignment(SwingConstants.CENTER);

        p.add(title);
        p.add(new JLabel("Trip ID: " + tripId));
        p.add(new JLabel("Fare Type: " + fareType + " - " + tripType));
        p.add(new JLabel(tripType.equalsIgnoreCase("Round Trip") ? "Return Date: " + returnDate : "Return Date: -"));
        p.add(new JLabel(tripType.equalsIgnoreCase("Round Trip")
                ? "Outbound Seats: " + outboundSeats + " | Return Seats: " + selectedSeats
                : "Seats: " + selectedSeats));
        p.add(new JLabel("Price per passenger: " + unitPrice + " SAR"));
        p.add(new JLabel("Total Amount: " + total + " SAR"));
        p.add(method);

        JButton pay = UI.tealButton("Pay Now");
        pay.addActionListener(e -> payNow(total));
        p.add(pay);

        add(p);
        setVisible(true);
    }

  void payNow(double total) {

    Connection con = null;
     try {
        con = DBConnection.getConnection();
            if (con == null) return;

            con.setAutoCommit(false);
            String lockSql =
            "SELECT trip_id FROM trips " +
            "WHERE trip_id IN (?,?) " +
            "ORDER BY trip_id FOR UPDATE";

          try (PreparedStatement lockPs = con.prepareStatement(lockSql)) {
           if (tripType.equalsIgnoreCase("Round Trip")) {
            lockPs.setInt(1, outboundTripId);
            lockPs.setInt(2, selectedReturnTripId);
          } else {
              lockPs.setInt(1, tripId);
              lockPs.setInt(2, tripId);
    }

           try (ResultSet lockRs = lockPs.executeQuery()) {
            while (lockRs.next()) {
            // Lock selected trip records.
        }
     }
    }
         String checkSql =
        "SELECT ticket_id FROM tickets " +
        "WHERE trip_id=? AND seat_number=? " +
        "AND ticket_status='Booked' FOR UPDATE";

       try (PreparedStatement checkPs = con.prepareStatement(checkSql)) {

         if (tripType.equalsIgnoreCase("Round Trip")) {
            for (String seat : outboundSeats) {
              checkPs.setInt(1, outboundTripId);
               checkPs.setString(2, seat);

            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next()) {
                    throw new SQLException("Seat " + seat +
                            " is already booked.");
                }
            }
        }
    }

    for (String seat : selectedSeats) {
        checkPs.setInt(1,
                tripType.equalsIgnoreCase("Round Trip")
                ? selectedReturnTripId : tripId);

        checkPs.setString(2, seat);

        try (ResultSet rs = checkPs.executeQuery()) {
            if (rs.next()) {
                throw new SQLException("Seat " + seat +
                        " is already booked.");
            }
        }
    }
}
String bookingSql =
        "INSERT INTO bookings(customer_id, outbound_trip_id, return_trip_id) " +
        "VALUES(?,?,?)";

int bookingId;

try (PreparedStatement bookingPs = con.prepareStatement(
        bookingSql, Statement.RETURN_GENERATED_KEYS)) {

    bookingPs.setInt(1, customerId);

    if (tripType.equalsIgnoreCase("Round Trip")) {
        bookingPs.setInt(2, outboundTripId);
        bookingPs.setInt(3, selectedReturnTripId);
    } else {
        bookingPs.setInt(2, tripId);
        bookingPs.setNull(3, Types.INTEGER);
    }

    bookingPs.executeUpdate();

    try (ResultSet keys = bookingPs.getGeneratedKeys()) {
        if (!keys.next()) {
            throw new SQLException("Failed to create booking.");
        }
        bookingId = keys.getInt(1);
    }
}ArrayList<String> passengerCategories = new ArrayList<>();

for (int i = 0; i < adults; i++) {
    passengerCategories.add("Adult");
}

for (int i = 0; i < children; i++) {
    passengerCategories.add("Child");
}

for (int i = 0; i < infants; i++) {
    passengerCategories.add("Infant");
}

if (passengerCategories.size() != selectedSeats.size()) {
    throw new SQLException("Passenger count does not match selected seats.");
}

ArrayList<Integer> passengerIds = new ArrayList<>();

String passengerSql =
        "INSERT INTO passengers(booking_id, passenger_category) VALUES(?,?)";

try (PreparedStatement passengerPs = con.prepareStatement(
        passengerSql, Statement.RETURN_GENERATED_KEYS)) {

    for (String category : passengerCategories) {
        passengerPs.setInt(1, bookingId);
        passengerPs.setString(2, category);
        passengerPs.executeUpdate();

        try (ResultSet keys = passengerPs.getGeneratedKeys()) {
            if (!keys.next()) {
                throw new SQLException("Failed to save passenger.");
            }
            passengerIds.add(keys.getInt(1));
        }
    }
}
            ArrayList<Integer> ticketIds = new ArrayList<>();
int passengerIndex = 0;

String ticketSql =
        "INSERT INTO tickets(customer_id, trip_id, seat_number, ticket_status, booking_id, passenger_id, ticket_class) " +
        "VALUES(?,?,?,'Booked',?,?,?)";

String paySql =
        "INSERT INTO payments(ticket_id, amount, payment_method, payment_status) VALUES(?,?,?,'Paid')";

if (tripType.equalsIgnoreCase("Round Trip")) {

    for (String seat : outboundSeats) {
        PreparedStatement ps = con.prepareStatement(ticketSql, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, customerId);
        ps.setInt(2, outboundTripId);
        ps.setString(3, seat);
        ps.setInt(4, bookingId);
        ps.setInt(5, passengerIds.get(passengerIndex++));
        ps.setString(6, fareType);
        ps.executeUpdate();

        ResultSet keys = ps.getGeneratedKeys();
        keys.next();
        int ticketId = keys.getInt(1);
        ticketIds.add(ticketId);

        PreparedStatement pay = con.prepareStatement(paySql);
        pay.setInt(1, ticketId);
        pay.setDouble(2, unitPrice / 2);
        pay.setString(3, method.getSelectedItem().toString());
        pay.executeUpdate();
    }

    passengerIndex = 0;

    for (String seat : selectedSeats) {
        PreparedStatement ps = con.prepareStatement(ticketSql, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, customerId);
        ps.setInt(2, selectedReturnTripId);
        ps.setString(3, seat);
        ps.setInt(4, bookingId);
        ps.setInt(5, passengerIds.get(passengerIndex++));
        ps.setString(6, fareType);
        ps.executeUpdate();

        ResultSet keys = ps.getGeneratedKeys();
        keys.next();
        int ticketId = keys.getInt(1);
        ticketIds.add(ticketId);

        PreparedStatement pay = con.prepareStatement(paySql);
        pay.setInt(1, ticketId);
        pay.setDouble(2, unitPrice / 2);
        pay.setString(3, method.getSelectedItem().toString());
        pay.executeUpdate();
    }

} else {

    for (String seat : selectedSeats) {
        PreparedStatement ps = con.prepareStatement(ticketSql, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, customerId);
        ps.setInt(2, tripId);
        ps.setString(3, seat);
        ps.setInt(4, bookingId);
        ps.setInt(5, passengerIds.get(passengerIndex++));
        ps.setString(6, fareType);
        ps.executeUpdate();

        ResultSet keys = ps.getGeneratedKeys();
        keys.next();
        int ticketId = keys.getInt(1);
        ticketIds.add(ticketId);

        PreparedStatement pay = con.prepareStatement(paySql);
        pay.setInt(1, ticketId);
        pay.setDouble(2, unitPrice);
        pay.setString(3, method.getSelectedItem().toString());
        pay.executeUpdate();
    }
}
            con.commit();

            exportPaidTickets(ticketIds);

            JOptionPane.showMessageDialog(this,
                    "Booking completed successfully!\nTickets: " + ticketIds +
                            "\nTotal: " + total + " SAR\nPayment Status: Paid");

            dispose();

        } catch (Exception e) {
           if (con != null) {
             try {
                con.rollback();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    JOptionPane.showMessageDialog(this, e.getMessage());

} finally {
    if (con != null) {
        try {
            con.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
    }

    void exportPaidTickets(ArrayList<Integer> ticketIds) {
        try {
            File file = new File("Payments_Report.csv");
            boolean newFile = !file.exists() || file.length() == 0;

            FileWriter writer = new FileWriter(file, true);

            if (newFile) {
                writer.write("Payment ID,Ticket ID,Customer,Amount,Method,Status,Date\n");
            }

            Connection con = DBConnection.getConnection();

            String sql =
                    "SELECT p.payment_id, p.ticket_id, c.full_name, p.amount, " +
                    "p.payment_method, p.payment_status, p.payment_date " +
                    "FROM payments p " +
                    "JOIN tickets tk ON p.ticket_id = tk.ticket_id " +
                    "JOIN customers c ON tk.customer_id = c.customer_id " +
                    "WHERE p.ticket_id = ?";

            PreparedStatement ps = con.prepareStatement(sql);

            for (Integer ticketId : ticketIds) {
                ps.setInt(1, ticketId);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    writer.write(
                            rs.getInt("payment_id") + "," +
                            rs.getInt("ticket_id") + "," +
                            "\"" + rs.getString("full_name") + "\"," +
                            rs.getDouble("amount") + "," +
                            rs.getString("payment_method") + "," +
                            rs.getString("payment_status") + "," +
                            rs.getString("payment_date") + "\n"
                    );
                }
            }

            writer.close();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage());
        }
    }
}

class PaymentsFrame extends JFrame {

    DefaultTableModel model = new DefaultTableModel();
    JTable table = new JTable(model);

    public PaymentsFrame() {
        setTitle("Payments");
        setSize(850, 450);
        setLocationRelativeTo(null);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1200, 700));

        model.setColumnIdentifiers(new String[]{
                "Payment ID", "Ticket ID", "Customer", "Amount", "Method", "Status", "Date"
        });

        load();

      
        JButton importBtn = UI.button("Import Payments");
        importBtn.addActionListener(e -> importPayments());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
    
        bottom.add(importBtn);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        UI.maximizeWindow(this);
        setVisible(true);
    }

    void load() {
        model.setRowCount(0);

        try {
            Connection con = DBConnection.getConnection();
            if (con == null) return;

            String sql =
                    "SELECT p.payment_id, p.ticket_id, c.full_name, p.amount, " +
                    "p.payment_method, p.payment_status, p.payment_date " +
                    "FROM payments p " +
                    "JOIN tickets tk ON p.ticket_id = tk.ticket_id " +
                    "JOIN customers c ON tk.customer_id = c.customer_id";

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("payment_id"),
                        rs.getInt("ticket_id"),
                        rs.getString("full_name"),
                        rs.getDouble("amount"),
                        rs.getString("payment_method"),
                        rs.getString("payment_status"),
                        rs.getString("payment_date")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    void exportPayments() {
        try {
            FileWriter writer = new FileWriter("Payments_Report.csv");

            writer.write("Payment ID,Ticket ID,Customer,Amount,Method,Status,Date\n");

            for (int i = 0; i < model.getRowCount(); i++) {
                writer.write(
                        model.getValueAt(i, 0) + "," +
                        model.getValueAt(i, 1) + "," +
                        "\"" + model.getValueAt(i, 2) + "\"," +
                        model.getValueAt(i, 3) + "," +
                        model.getValueAt(i, 4) + "," +
                        model.getValueAt(i, 5) + "," +
                        model.getValueAt(i, 6) + "\n"
                );
            }

            writer.close();

            JOptionPane.showMessageDialog(this, "Payments exported successfully.");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Export failed: " + e.getMessage());
        }
    }

    void importPayments() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("Payments_Report.csv"));

            model.setRowCount(0);

            String line = reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

                if (data.length >= 7) {
                    model.addRow(new Object[]{
                            clean(data[0]),
                            clean(data[1]),
                            clean(data[2]),
                            clean(data[3]),
                            clean(data[4]),
                            clean(data[5]),
                            clean(data[6])
                    });
                }
            }

            reader.close();

            JOptionPane.showMessageDialog(this, "Payments imported successfully.");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Import failed: " + e.getMessage());
        }
    }

    String clean(String value) {
        return value.replace("\"", "").trim();
    }
}