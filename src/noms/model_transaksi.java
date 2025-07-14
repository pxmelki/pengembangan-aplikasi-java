package noms;

import java.util.ArrayList;

public class model_transaksi {
    private String currentUser;

    public model_transaksi(String currentUser) {
        this.currentUser = currentUser;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    private String idTransaksi;
    private String tanggal;
    private double totalHarga;
    private double uangBayar;
    private double kembalian;

    private ArrayList<model_item> daftarItem = new ArrayList<>();

    public String getIdTransaksi() {
        return idTransaksi;
    }

    public void setIdTransaksi(String idTransaksi) {
        this.idTransaksi = idTransaksi;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public double getTotalHarga() {
        return totalHarga;
    }

    public void setTotalHarga(double totalHarga) {
        this.totalHarga = totalHarga;
    }

    public double getUangBayar() {
        return uangBayar;
    }

    public void setUangBayar(double uangBayar) {
        this.uangBayar = uangBayar;
    }

    public double getKembalian() {
        return kembalian;
    }

    public void setKembalian(double kembalian) {
        this.kembalian = kembalian;
    }

    public void tambahItem(model_item item) {
        daftarItem.add(item);
    }

    public ArrayList<model_item> getDaftarItem() {
        return daftarItem;
    }

    public class model_item {
        private String kodeMenu;
        private String namaMenu;
        private double harga;
        private int jumlah;

        public model_item(String kodeMenu, String namaMenu, double harga, int jumlah) {
            this.kodeMenu = kodeMenu;
            this.namaMenu = namaMenu;
            this.harga = harga;
            this.jumlah = jumlah;
        }

        public String getKodeMenu() {
            return kodeMenu;
        }

        public String getNamaMenu() {
            return namaMenu;
        }

        public double getHarga() {
            return harga;
        }

        public int getJumlah() {
            return jumlah;
        }

        public double getSubtotal() {
            return harga * jumlah;
        }
    }
}
