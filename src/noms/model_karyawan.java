package noms;

public class model_karyawan {
    private String id_karyawan;
    private String nama;
    private String jabatan;
    private String telepon;
    private String email;
    private String alamat;

    public String getId_karyawan() { return id_karyawan; }
    public void setId_karyawan(String id_karyawan) { this.id_karyawan = id_karyawan; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getJabatan() { return jabatan; }
    public void setJabatan(String jabatan) { this.jabatan = jabatan; }

    public String getTelepon() { return telepon; }
    public void setTelepon(String telepon) { this.telepon = telepon; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }
}
