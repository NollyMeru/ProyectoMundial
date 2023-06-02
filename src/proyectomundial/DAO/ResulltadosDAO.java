package proyectomundial.DAO;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;
import java.util.Arrays;
import javax.swing.table.DefaultTableModel;
import proyectomundial.model.Resultados;
import proyectomundial.util.BasedeDatos;

public class ResulltadosDAO {

    public ResulltadosDAO() {
        BasedeDatos.conectar();
    }

    public boolean registrarResultados(Resultados resultado) {
        String sql = "INSERT INTO w_castro.partidos (grupo, local, visitante, continente_local,continente_visitante,goles_local,goles_visitante) values("
                + "'" + resultado.getGrupo() + "', "
                + "'" + resultado.getLocal() + "', "
                + "'" + resultado.getVisitante() + "', "
                + "'" + resultado.getContinente_local() + "', "
                + "'" + resultado.getContinente_visitante() + "', "
                + "'" + resultado.getGoles_locales() + "', "
                + "'" + resultado.getGoles_visitante() + "')";

        boolean registro = BasedeDatos.ejecutarActualizacionSQL(sql);

        return registro;
    }

    public List<Resultados> getSelecciones() {
        String sql = "SELECT grupo,local, visitante, continente_local,continente_visitante,goles_Local,goles_visitante FROM w_castro.partidos";
        List<Resultados> resultados = new ArrayList<Resultados>();

        try {
            ResultSet result = BasedeDatos.ejecutarSQL(sql);

            if (result != null) {

                while (result.next()) {
                    Resultados resultado = new Resultados(result.getString("grupo"), result.getString("local"), result.getString("visitante"),
                            result.getString("continente_local"), result.getString("continente_visitante"), result.getString("goles_local"), result.getString("goles_visitante"));
                    resultados.add(resultado);
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            System.out.println("Error consultando selecciones");
        }

        return resultados;
    }

    public int PartidosTotales() {
        int totalPartidos = 0;
        try {
            ResultSet result = BasedeDatos.ejecutarSQL("SELECT COUNT(*) AS total_partidos FROM w_castro.partidos");
            if (result.next()) {
                totalPartidos = result.getInt("total_partidos");
            }
        } catch (Exception e) {
        }
        return totalPartidos;
    }

    public double promedioGolesXPartidos() {
        double promediGoles = 0;
        try {
            ResultSet result = BasedeDatos.ejecutarSQL("SELECT AVG(goles_local + goles_visitante) AS promedio_goles_por_partido FROM w_castro.partidos");
            if (result.next()) {
                promediGoles = result.getInt("promedio_goles_por_partido");
            }
        } catch (Exception e) {
        }
        return promediGoles;
    }

    public int[] PartidosConEmpates() {
        int partidosEmpatados = 0;
        int partidosGanados = 0;
        try {
            ResultSet result = BasedeDatos.ejecutarSQL("SELECT SUM(CASE WHEN goles_local > goles_visitante THEN 1 ELSE 0 END) + "
                    + "SUM(CASE WHEN goles_local < goles_visitante THEN 1 ELSE 0 END) AS partidos_con_ganador, SUM(CASE WHEN goles_local = goles_visitante THEN 1 ELSE 0 END) AS partidos_con_empate FROM w_castro.partidos");
            if (result.next()) {
                partidosGanados = result.getInt("partidos_con_ganador");
                partidosEmpatados = result.getInt("partidos_con_empate");
            }
        } catch (Exception e) {
        }
        return new int[]{partidosGanados, partidosEmpatados};
    }

   public DefaultTableModel partidosConMasGoles() {
    String[] columnas = {"Equipo 1", "Equipo 2", "Goles Equipo 1", "Goles Equipo 2"};
    List<Object[]> filas = new ArrayList<>();

    try {
        ResultSet result = BasedeDatos.ejecutarSQL("SELECT local, visitante, goles_local, goles_visitante FROM w_castro.partidos WHERE goles_local + goles_visitante = (SELECT MAX(goles_local + goles_visitante) FROM w_castro.partidos)");

        while (result.next()) {
            String equipo1 = result.getString("local");
            String equipo2 = result.getString("visitante");
            int golesEquipo1 = result.getInt("goles_local");
            int golesEquipo2 = result.getInt("goles_visitante");
            Object[] fila = {equipo1, equipo2, golesEquipo1, golesEquipo2};
            filas.add(fila);
        }
    } catch (Exception e) {
    }

    Object[][] matrizResultados = filas.toArray(new Object[0][]);
    DefaultTableModel modeloTabla = new DefaultTableModel(matrizResultados, columnas);
    return modeloTabla;
}

public DefaultTableModel partidosConMenosGoles() {
    String[] columnas = {"Equipo 1", "Equipo 2", "Goles Equipo 1", "Goles Equipo 2"};
    List<Object[]> filas = new ArrayList<>();

    try {
        ResultSet result = BasedeDatos.ejecutarSQL("SELECT local, visitante, goles_local, goles_visitante FROM w_castro.partidos WHERE goles_local + goles_visitante = (SELECT MIN(goles_local + goles_visitante) FROM w_castro.partidos)");

        while (result.next()) {
            String equipo1 = result.getString("local");
            String equipo2 = result.getString("visitante");
            int golesEquipo1 = result.getInt("goles_local");
            int golesEquipo2 = result.getInt("goles_visitante");
            Object[] fila = {equipo1, equipo2, golesEquipo1, golesEquipo2};
            filas.add(fila);
        }
    } catch (Exception e) {
        // Manejo de excepciones
    }

    Object[][] matrizResultados = filas.toArray(new Object[0][]);
    DefaultTableModel modeloTabla = new DefaultTableModel(matrizResultados, columnas);
    return modeloTabla;
}


    public String[] seleccionesMasGoles() {
        List<String> selecciones = new ArrayList<>();
        try {
            ResultSet result = BasedeDatos.ejecutarSQL("SELECT seleccion FROM (SELECT local AS seleccion, SUM(goles_local) AS goles FROM w_castro.partidos GROUP BY local UNION SELECT visitante AS seleccion, SUM(goles_visitante) AS goles FROM w_castro.partidos GROUP BY visitante) t WHERE goles = (SELECT MAX(goles) FROM (SELECT SUM(goles_local) AS goles FROM w_castro.partidos GROUP BY local UNION SELECT SUM(goles_visitante) AS goles FROM w_castro.partidos GROUP BY visitante) s)");
            while (result.next()) {
                selecciones.add(result.getString("seleccion"));
            }
        } catch (Exception e) {
            //--------------------------------------
        }
        return selecciones.toArray(new String[0]);
    }

    public String[] seleccionesMenosGoles() {
        List<String> selecciones = new ArrayList<>();
        try {
            ResultSet result = BasedeDatos.ejecutarSQL("SELECT seleccion FROM (SELECT local AS seleccion, SUM(goles_local) AS goles FROM w_castro.partidos GROUP BY local UNION SELECT visitante AS seleccion, SUM(goles_visitante) AS goles FROM w_castro.partidos GROUP BY visitante) t WHERE goles = (SELECT MIN(goles) FROM (SELECT SUM(goles_local) AS goles FROM w_castro.partidos GROUP BY local UNION SELECT SUM(goles_visitante) AS goles FROM w_castro.partidos GROUP BY visitante) s)");
            while (result.next()) {
                selecciones.add(result.getString("seleccion"));
            }
        } catch (Exception e) {
            //--------------------------------------
        }
        return selecciones.toArray(new String[0]);
    }

   public String[] SeleconMayorPuntuacion() {
    try {
        String sql = "SELECT seleccion FROM (SELECT local AS seleccion, SUM(CASE WHEN goles_local > goles_visitante THEN 3 WHEN goles_local = goles_visitante THEN 1 ELSE 0 END) + SUM(CASE WHEN goles_local < goles_visitante THEN 0 ELSE 1 END) AS puntos FROM w_castro.partidos GROUP BY local UNION SELECT visitante AS seleccion, SUM(CASE WHEN goles_local < goles_visitante THEN 3 WHEN goles_local = goles_visitante THEN 1 ELSE 0 END) + SUM(CASE WHEN goles_local > goles_visitante THEN 0 ELSE 1 END) AS puntos FROM w_castro.partidos GROUP BY visitante) t WHERE puntos = (SELECT MAX(puntos) FROM (SELECT local AS seleccion, SUM(CASE WHEN goles_local > goles_visitante THEN 3 WHEN goles_local = goles_visitante THEN 1 ELSE 0 END) + SUM(CASE WHEN goles_local < goles_visitante THEN 0 ELSE 1 END) AS puntos FROM w_castro.partidos GROUP BY local UNION SELECT visitante AS seleccion, SUM(CASE WHEN goles_local < goles_visitante THEN 3 WHEN goles_local = goles_visitante THEN 1 ELSE 0 END) + SUM(CASE WHEN goles_local > goles_visitante THEN 0 ELSE 1 END) AS puntos FROM w_castro.partidos GROUP BY visitante) t)";
        ResultSet result = BasedeDatos.ejecutarSQL(sql);
        List<String> selecciones = new ArrayList<>();
        while (result.next()) {
            selecciones.add(result.getString("seleccion"));
        }
        return selecciones.toArray(new String[0]);
    } catch (Exception e) {
        e.printStackTrace();
    }
    return new String[0];
}


    public String[] SeleconMenorPuntuacion() {
    ArrayList<String> selecciones = new ArrayList<>();
    try {
        String sql = "SELECT seleccion FROM (SELECT local AS seleccion, SUM(CASE WHEN goles_local > goles_visitante THEN 3 WHEN goles_local = goles_visitante THEN 1 ELSE 0 END) + SUM(CASE WHEN goles_local < goles_visitante THEN 0 ELSE 1 END) AS puntos FROM w_castro.partidos GROUP BY local UNION SELECT visitante AS seleccion, SUM(CASE WHEN goles_local < goles_visitante THEN 3 WHEN goles_local = goles_visitante THEN 1 ELSE 0 END) + SUM(CASE WHEN goles_local > goles_visitante THEN 0 ELSE 1 END) AS puntos FROM w_castro.partidos GROUP BY visitante) t WHERE puntos = (SELECT MIN(puntos) FROM (SELECT local AS seleccion, SUM(CASE WHEN goles_local > goles_visitante THEN 3 WHEN goles_local = goles_visitante THEN 1 ELSE 0 END) + SUM(CASE WHEN goles_local < goles_visitante THEN 0 ELSE 1 END) AS puntos FROM w_castro.partidos GROUP BY local UNION SELECT visitante AS seleccion, SUM(CASE WHEN goles_local < goles_visitante THEN 3 WHEN goles_local = goles_visitante THEN 1 ELSE 0 END) + SUM(CASE WHEN goles_local > goles_visitante THEN 0 ELSE 1 END) AS puntos FROM w_castro.partidos GROUP BY visitante) t)";
        ResultSet result = BasedeDatos.ejecutarSQL(sql);
        while (result.next()) {
            selecciones.add(result.getString("seleccion"));
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return selecciones.toArray(new String[0]);
}

    public String[] continentesConMayorPuntos() {
        try {
            ResultSet result = BasedeDatos.ejecutarSQL("SELECT continente FROM ( SELECT continente_local AS continente, SUM(CASE WHEN goles_local > goles_visitante THEN 3 WHEN goles_local = goles_visitante THEN 1 ELSE 0 END) + SUM(CASE WHEN goles_local < goles_visitante THEN 0 ELSE 1 END) AS puntos FROM w_castro.partidos GROUP BY continente_local UNION SELECT continente_visitante AS continente, SUM(CASE WHEN goles_local < goles_visitante THEN 3 WHEN goles_local = goles_visitante THEN 1 ELSE 0 END) + SUM(CASE WHEN goles_local > goles_visitante THEN 0 ELSE 1 END) AS puntos FROM w_castro.partidos GROUP BY continente_visitante) t WHERE puntos = (SELECT MAX(puntos) FROM (SELECT continente_local AS continente, SUM(CASE WHEN goles_local > goles_visitante THEN 3 WHEN goles_local = goles_visitante THEN 1 ELSE 0 END) + SUM(CASE WHEN goles_local < goles_visitante THEN 0 ELSE 1 END) AS puntos FROM w_castro.partidos GROUP BY continente_local UNION SELECT continente_visitante AS continente, SUM(CASE WHEN goles_local < goles_visitante THEN 3 WHEN goles_local = goles_visitante THEN 1 ELSE 0 END) + SUM(CASE WHEN goles_local > goles_visitante THEN 0 ELSE 1 END) AS puntos FROM w_castro.partidos GROUP BY continente_visitante) t) ORDER BY puntos DESC;");
            List<String> continentes = new ArrayList<>();
            while (result.next()) {
                continentes.add(result.getString("continente"));
            }
            return continentes.toArray(new String[0]);
        } catch (Exception e) {
            e.printStackTrace(); 
        }return new String[0];
    }

    public String[] continentesConMenorPuntos() {
        try {
            ResultSet result = BasedeDatos.ejecutarSQL("SELECT continente FROM ( SELECT continente_local AS continente, SUM(CASE WHEN goles_local > goles_visitante THEN 3 WHEN goles_local = goles_visitante THEN 1 ELSE 0 END) + SUM(CASE WHEN goles_local < goles_visitante THEN 0 ELSE 1 END) AS puntos FROM w_castro.partidos GROUP BY continente_local UNION SELECT continente_visitante AS continente, SUM(CASE WHEN goles_local < goles_visitante THEN 3 WHEN goles_local = goles_visitante THEN 1 ELSE 0 END) + SUM(CASE WHEN goles_local > goles_visitante THEN 0 ELSE 1 END) AS puntos FROM w_castro.partidos GROUP BY continente_visitante) t WHERE puntos = (SELECT MIN(puntos) FROM (SELECT continente_local AS continente, SUM(CASE WHEN goles_local > goles_visitante THEN 3 WHEN goles_local = goles_visitante THEN 1 ELSE 0 END) + SUM(CASE WHEN goles_local < goles_visitante THEN 0 ELSE 1 END) AS puntos FROM w_castro.partidos GROUP BY continente_local UNION SELECT continente_visitante AS continente, SUM(CASE WHEN goles_local < goles_visitante THEN 3 WHEN goles_local = goles_visitante THEN 1 ELSE 0 END) + SUM(CASE WHEN goles_local > goles_visitante THEN 0 ELSE 1 END) AS puntos FROM w_castro.partidos GROUP BY continente_visitante) t) ORDER BY puntos ASC;");
            List<String> continentes = new ArrayList<>();
            while (result.next()) {
                continentes.add(result.getString("continente"));
            }
            return continentes.toArray(new String[0]);
        } catch (Exception e) {
            return new String[0];
        }//Segundo prub
    }

    public List<Resultados> getResultadosBusqueda(String nombreSeleccion) {
        String sql = "SELECT grupo, local, visitante, continente_local, continente_visitante, goles_local, goles_visitante FROM w_castro.partidos WHERE"
                + " local LIKE ? OR grupo LIKE ? OR visitante LIKE ? OR continente_local LIKE ? OR continente_visitante LIKE ?";
        List<Resultados> resultados = new ArrayList<Resultados>();
        try {
            if (BasedeDatos.conexion == null) {
                System.out.println("No hay conexión a la base de datos");
                return resultados;
            }
            PreparedStatement stmt = BasedeDatos.conexion.prepareStatement(sql);
            stmt.setString(1, "%" + nombreSeleccion + "%");
            stmt.setString(2, "%" + nombreSeleccion + "%");
            stmt.setString(3, "%" + nombreSeleccion + "%");
            stmt.setString(4, "%" + nombreSeleccion + "%");
            stmt.setString(5, "%" + nombreSeleccion + "%");

            ResultSet result = stmt.executeQuery();
            if (result != null) {
                while (result.next()) {
                    Resultados resultado = new Resultados(result.getString("grupo"),
                            result.getString("local"), result.getString("visitante"),
                            result.getString("continente_local"), result.getString("continente_visitante"),
                            result.getString("goles_local"), result.getString("goles_visitante"));
                    resultados.add(resultado);
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            System.out.println("Error consultando resultados");
        }

        return resultados;
    }

    public String[][] getResultadosMatriz() {
        String[][] matrizResultados = null;
        List<Resultados> resultados = getSelecciones();

        if (resultados != null && resultados.size() > 0) {

            matrizResultados = new String[resultados.size()][7];

            int x = 0;
            for (Resultados resultado : resultados) {

                matrizResultados[x][0] = resultado.getGrupo();
                matrizResultados[x][1] = resultado.getLocal();
                matrizResultados[x][2] = resultado.getVisitante();
                matrizResultados[x][3] = resultado.getContinente_local();
                matrizResultados[x][4] = resultado.getContinente_visitante();
                matrizResultados[x][5] = resultado.getGoles_locales();
                matrizResultados[x][6] = resultado.getGoles_visitante();
                x++;
            }
        }

        return matrizResultados;
    }

}
