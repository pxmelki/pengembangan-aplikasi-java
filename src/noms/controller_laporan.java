package noms;

import java.sql.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.JFileChooser;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.Session;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import java.util.Properties;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class controller_laporan {
    private view_laporan view;
    private model_laporan model;

    public controller_laporan(view_laporan v) {
        this.view = v;
        model = new model_laporan();
        initAction();
    }

    private void initAction() {
        view.getBtnFilter().addActionListener(e -> filterData());
        view.getBtnReset().addActionListener(e -> resetData());
        view.getBtnPDF().addActionListener(e -> exportPDF());
        view.getBtnEmail().addActionListener(e -> handleKirimEmail());
    }

    private void filterData() {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Calendar calAwal = Calendar.getInstance();
            calAwal.setTime(view.getTanggalAwal());
            calAwal.set(Calendar.HOUR_OF_DAY, 0);
            calAwal.set(Calendar.MINUTE, 0);
            calAwal.set(Calendar.SECOND, 0);

            Calendar calAkhir = Calendar.getInstance();
            calAkhir.setTime(view.getTanggalAkhir());
            calAkhir.set(Calendar.HOUR_OF_DAY, 23);
            calAkhir.set(Calendar.MINUTE, 59);
            calAkhir.set(Calendar.SECOND, 59);

            String tglAwal = format.format(calAwal.getTime());
            String tglAkhir = format.format(calAkhir.getTime());

            System.out.println("Filter tanggal: " + tglAwal + " - " + tglAkhir);

            ResultSet rs = model.getLaporanByTanggal(tglAwal, tglAkhir);

            DefaultTableModel tbl = new DefaultTableModel();
            tbl.setColumnIdentifiers(new String[] {
                "Tanggal", "ID Transaksi", "Menu", "Kategori", "Subtotal", "Total", "Kasir", "Pelanggan"
            });

            while (rs.next()) {
                tbl.addRow(new Object[] {
                    rs.getString("Tanggal"),
                    rs.getString("ID_Transaksi"),
                    rs.getString("Menu"),
                    rs.getString("Kategori"),
                    rs.getDouble("Subtotal"),
                    rs.getDouble("Total"),
                    rs.getString("Kasir"),
                    rs.getString("Pelanggan")
                });
            }

            view.getTblLaporan().setModel(tbl);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal memuat data: " + e.getMessage());
        }
    }

    private void resetData() {
        view.getTblLaporan().setModel(new DefaultTableModel());
    }

    private void exportPDF() {
        try {
            if (view.getTblLaporan().getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "Tabel kosong, tidak bisa export PDF.");
                return;
            }

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Simpan PDF");
            chooser.setSelectedFile(new File("laporan_transaksi.pdf"));

            int result = chooser.showSaveDialog(null);
            if (result != JFileChooser.APPROVE_OPTION) return;

            String path = chooser.getSelectedFile().getAbsolutePath();
            if (!path.endsWith(".pdf")) {
                path += ".pdf"; 
            }

            Document doc = new Document(PageSize.A4.rotate());
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(path));
            doc.open();

            Font headerFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Font subFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);

            doc.add(new Paragraph("KAKOKOPI SALATIGA", headerFont));
            doc.add(new Paragraph("Jl. Patimura No. 62, Salatiga", subFont));
            doc.add(new Paragraph("Telp: 089601402523", subFont));
            doc.add(Chunk.NEWLINE);

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String tglAwal = sdf.format(view.getSpinnerTanggalAwal().getValue());
            String tglAkhir = sdf.format(view.getSpinnerTanggalAkhir().getValue());

            doc.add(new Paragraph("LAPORAN TRANSAKSI", headerFont));
            doc.add(new Paragraph("Tanggal: " + tglAwal + " s.d " + tglAkhir, subFont));
            doc.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(view.getTblLaporan().getColumnCount());
            table.setWidthPercentage(100);

            for (int i = 0; i < view.getTblLaporan().getColumnCount(); i++) {
                PdfPCell cell = new PdfPCell(new Phrase(view.getTblLaporan().getColumnName(i)));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);
            }

            for (int i = 0; i < view.getTblLaporan().getRowCount(); i++) {
                for (int j = 0; j < view.getTblLaporan().getColumnCount(); j++) {
                    Object value = view.getTblLaporan().getValueAt(i, j);
                    table.addCell(value != null ? value.toString() : "");
                }
            }

            doc.add(table);
            doc.close();
            writer.close();

            JOptionPane.showMessageDialog(null, "PDF berhasil disimpan di:\n" + path);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Export PDF gagal: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void handleKirimEmail() {
        if (view.getTblLaporan().getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Data laporan kosong. Harap export PDF terlebih dahulu.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Pilih File PDF Laporan yang Akan Dikirim");
        chooser.setSelectedFile(new File("laporan_transaksi.pdf"));

        int result = chooser.showOpenDialog(null);
        if (result != JFileChooser.APPROVE_OPTION) return;

        String filePath = chooser.getSelectedFile().getAbsolutePath();
        String emailTujuan = JOptionPane.showInputDialog(null, "Masukkan email tujuan:");

        if (emailTujuan == null || emailTujuan.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Email tidak boleh kosong.");
            return;
        }

        try {
            final String fromEmail = "melkiparkes@gmail.com";
            final String password = "phmx luxz rzoc eztk"; 

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, password);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailTujuan));
            message.setSubject("Laporan PDF Transaksi");

            BodyPart bodyPart = new MimeBodyPart();
            bodyPart.setText("Berikut laporan PDF Anda.");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(bodyPart);

            MimeBodyPart attachment = new MimeBodyPart();
            attachment.attachFile(new File(filePath));
            multipart.addBodyPart(attachment);

            message.setContent(multipart);
            Transport.send(message);

            JOptionPane.showMessageDialog(null, "Email berhasil dikirim ke " + emailTujuan);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal mengirim email: " + e.getMessage());
        }
    }
}
