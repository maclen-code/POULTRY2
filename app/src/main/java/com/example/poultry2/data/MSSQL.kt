package com.example.poultry2.data

import android.annotation.SuppressLint
import android.os.StrictMode
import java.sql.*

class MSSQL {

    @SuppressLint("NewApi")
    fun conn(server: Data.Server): Connection? {
        try {
            val policy = StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)

            val ip = if (server.isLocal) server.localIp else server.publicIp

            Class.forName("net.sourceforge.jtds.jdbc.Driver")
            val connectionURL = ("jdbc:jtds:sqlserver://" + ip + ";"
                    + "databaseName=" + server.database + ";user=sa"
                    + ";password=" + server.password + ";")
            return DriverManager.getConnection(connectionURL)
        } catch (_: SQLException) {

        } catch (_: ClassNotFoundException) {

        } catch (_: Exception) {

        }
        return null
    }


}

