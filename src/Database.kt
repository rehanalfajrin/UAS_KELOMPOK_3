import java.sql.Connection
import java.sql.DriverManager

fun getConnection(): Connection {
    try {
        // Register JDBC driver
        Class.forName("com.mysql.cj.jdbc.Driver")

        // URL untuk koneksi ke MySQL
        val url = "jdbc:mysql://localhost:3306/perpustakaan"

        // Kredensial MySQL (sesuaikan dengan database Anda)
        val user = "root" // Ganti dengan username MySQL Anda
        val password = "" // Ganti dengan password MySQL Anda

        // Mencoba koneksi
        return DriverManager.getConnection(url, user, password)
    } catch (e: Exception) {
        // Cetak error jika koneksi gagal
        e.printStackTrace()
        throw RuntimeException("Gagal menyambung ke database: ${e.message}")
    }
}

fun main() {
    try {
        // Membuka koneksi
        val connection = getConnection()
        println("Koneksi berhasil!")

        // Contoh query sederhana
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT DATABASE()")

        // Menampilkan hasil query
        if (resultSet.next()) {
            println("Database aktif: ${resultSet.getString(1)}")
        }

        // Menutup koneksi
        resultSet.close()
        statement.close()
        connection.close()
        println("Koneksi ditutup.")
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}