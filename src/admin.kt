import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement
import java.util.Scanner

fun main() {
    val scanner = Scanner(System.`in`)
    val connection = connectToDatabase()

    while (true) {
        println("Selamat Datang Di Aplikasi Kami")
        println("==== Silahkan Memilih Menu ====")
        println("1. Tampilkan Daftar Buku")
        println("2. Pinjam Buku")
        println("3. Tampilkan Daftar Peminjam")
        println("4. Kembalikan Buku")
        println("5. Keluar")
        print("Pilih menu: ")
        val choice = scanner.nextInt()

        when (choice) {
            1 -> showBooks(connection)
            2 -> borrowBook(connection, scanner)
            3 -> showBorrowers(connection)
            4 -> returnBook(connection, scanner)
            5 -> {
                println("Terima kasih! Sampai jumpa.")
                connection.close() // Close the connection before exiting
                return
            }
            else -> println("Pilihan tidak valid.")
        }
    }
}

fun connectToDatabase(): Connection {
    val url = "jdbc:mysql://localhost:3306/perpustakaan"
    return DriverManager.getConnection(url, "root", "") // Ganti dengan username dan password Anda jika diperlukan
}

fun showBooks(connection: Connection) {
    val statement: Statement = connection.createStatement()
    val resultSet: ResultSet = statement.executeQuery("SELECT * FROM buku")

    println("Daftar Buku:")
    while (resultSet.next()) {
        println("ID: ${resultSet.getInt("id")}, Judul: ${resultSet.getString("judul")}, Penulis: ${resultSet.getString("penulis")}, Tersedia: ${resultSet.getBoolean("tersedia")}")
    }
}

fun borrowBook(connection: Connection, scanner: Scanner) {
    println("Masukkan nama peminjam:")
    val namaPeminjam = scanner.next()

    println("Masukkan ID buku yang ingin dipinjam:")
    val idBuku = scanner.nextInt()

    val statement: Statement = connection.createStatement()
    val checkBookQuery = "SELECT tersedia FROM buku WHERE id = $idBuku"
    val resultSet = statement.executeQuery(checkBookQuery)

    if (resultSet.next() && resultSet.getBoolean("tersedia")) {
        val sql = "INSERT INTO peminjam (nama, id_buku) VALUES ('$namaPeminjam', $idBuku)"
        statement.executeUpdate(sql)

        // Update status buku menjadi tidak tersedia
        val updateBookQuery = "UPDATE buku SET tersedia = FALSE WHERE id = $idBuku"
        statement.executeUpdate(updateBookQuery)

        println("Buku berhasil dipinjam oleh $namaPeminjam.")
    } else {
        println("Buku tidak tersedia untuk dipinjam.")
    }
}

fun showBorrowers(connection: Connection) {
    val statement: Statement = connection.createStatement()
    val resultSet: ResultSet = statement.executeQuery("SELECT * FROM peminjam")

    println("Daftar Peminjam:")
    while (resultSet.next()) {
        println("ID: ${resultSet.getInt("id")}, Nama: ${resultSet.getString("nama")}, ID Buku: ${resultSet.getInt("id_buku")}")
    }
}

fun returnBook(connection: Connection, scanner: Scanner) {
    println("Masukkan ID peminjam yang ingin mengembalikan buku:")
    val idPeminjam = scanner.nextInt()

    val statement: Statement = connection.createStatement()
    val checkBorrowerQuery = "SELECT id_buku FROM peminjam WHERE id = $idPeminjam"
    val resultSet = statement.executeQuery(checkBorrowerQuery)

    if (resultSet.next()) {
        val idBuku = resultSet.getInt("id_buku")

        // Update status buku menjadi tersedia
        val updateBookQuery = "UPDATE buku SET tersedia = TRUE WHERE id = $idBuku"
        statement.executeUpdate(updateBookQuery)

        // Hapus peminjam dari tabel peminjam
        val deleteBorrowerQuery = "DELETE FROM peminjam WHERE id = $idPeminjam"
        statement.executeUpdate(deleteBorrowerQuery)

        println("Buku berhasil dikembalikan.")
    }
}