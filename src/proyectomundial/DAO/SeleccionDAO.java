package proyectomundial.DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import proyectomundial.model.Resultados;
import proyectomundial.model.Seleccion;
import proyectomundial.util.BasedeDatos;
import static proyectomundial.util.BasedeDatos.ejecutarSQL;

/**
 *
 * @author miguelropero
 */
public class SeleccionDAO {

    public SeleccionDAO() {
        BasedeDatos.conectar();
    }

    public boolean registrarSeleccion(Seleccion seleccion) {

        String sql = "INSERT INTO w_castro.seleccion (nombres, continente, dt, nacionalidad) values("
                + "'" + seleccion.getNombre() + "', "
                + "'" + seleccion.getContinente() + "', "
                + "'" + seleccion.getDt() + "', "
                + "'" + seleccion.getNacionalidad() + "')";

        //BasedeDatos.conectar();
        boolean registro = BasedeDatos.ejecutarActualizacionSQL(sql);
        //BasedeDatos.desconectar();
        return registro;
    }

    public List<Seleccion> getSelecciones() {

        String sql = "SELECT nombres, continente, dt, nacionalidad FROM w_castro.seleccion";
        List<Seleccion> selecciones = new ArrayList<Seleccion>();

        try {
            ResultSet result = BasedeDatos.ejecutarSQL(sql);

            if (result != null) {

                while (result.next()) {
                    Seleccion seleccion = new Seleccion(result.getString("nombres"), result.getString("continente"), result.getString("dt"), result.getString("nacionalidad"));
                    selecciones.add(seleccion);
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            System.out.println("Error consultando selecciones");
        }

        return selecciones;
    }

    ///////////////////////
    public int getTotalSelecciones() {
        String sql = "SELECT COUNT(*) AS total FROM w_castro.seleccion";
        int totalPartidos = 0;

        try {
            ResultSet result = BasedeDatos.ejecutarSQL(sql);

            if (result.next()) {
                totalPartidos = result.getInt("total");
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            System.out.println("Error TotalSele");
        }

        return totalPartidos;
    }

    public Map<String, Integer> getSeleccionesPorContinente() {
        String sql = "SELECT continente, COUNT(*) AS total FROM w_castro.seleccion GROUP BY continente";
        Map<String, Integer> seleccionesPorContinente = new HashMap<>();

        try {
            ResultSet result = BasedeDatos.ejecutarSQL(sql);

            while (result.next()) {
                String continente = result.getString("continente");
                int totalSelecciones = result.getInt("total");
                seleccionesPorContinente.put(continente, totalSelecciones);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            System.out.println("Error obteniendo selecciones por continente");
        }

        return seleccionesPorContinente;
    }

    public int getCantidadNacionalidadesDT() {
        String sql = "SELECT COUNT(DISTINCT nacionalidad) AS cantidad FROM w_castro.seleccion WHERE dt IS NOT NULL";
        int cantidadNacionalidades = 0;

        try {
            ResultSet result = BasedeDatos.ejecutarSQL(sql);

            if (result.next()) {
                cantidadNacionalidades = result.getInt("cantidad");
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            System.out.println("Error obteniendo cantidad de nacionalidades de los directores técnicos");
        }

        return cantidadNacionalidades;
    }

    public Map<String, Integer> getRankingNacionalidadesDT() {
        String sql = "SELECT nacionalidad, COUNT(*) AS cantidad FROM w_castro.seleccion WHERE dt IS NOT NULL GROUP BY nacionalidad ORDER BY cantidad DESC";
        Map<String, Integer> rankingNacionalidades = new LinkedHashMap<>();

        try {
            ResultSet result = BasedeDatos.ejecutarSQL(sql);

            while (result.next()) {
                String nacionalidad = result.getString("nacionalidad");
                int cantidad = result.getInt("cantidad");
                rankingNacionalidades.put(nacionalidad, cantidad);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            System.out.println("Error obteniendo ranking de nacionalidades de los directores técnicos");
        }

        return rankingNacionalidades;
    }
    
/////////////////////////////////
    public List<Seleccion> getSeleccionesBusqueda(String nombreSeleccion) {
        System.out.println("Limite hasta GetSeleccionesBusqueda");
        String sql = "SELECT nombres, continente, dt, nacionalidad FROM w_castro.seleccion WHERE nombres LIKE ?";
        List<Seleccion> selecciones = new ArrayList<Seleccion>();

        try {
            if (BasedeDatos.conexion == null) {
                // Mostrar un mensaje de error o lanzar una excepción
                System.out.println("No hay conexión a la base de datos");
                return selecciones;
            }
            // Preparar la consulta SQL y establecer el parámetro
            PreparedStatement stmt = BasedeDatos.conexion.prepareStatement(sql);
            stmt.setString(1, "%" + nombreSeleccion + "%");

            // Ejecutar la consulta y obtener el resultado
            ResultSet result = stmt.executeQuery();

            if (result != null) {
                while (result.next()) {
                    Seleccion seleccion = new Seleccion(result.getString("nombres"), result.getString("continente"), result.getString("dt"), result.getString("nacionalidad"));
                    selecciones.add(seleccion);
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            System.out.println("Error consultando selecciones");
        }

        return selecciones;
    }

    public String[][] getSeleccionesMatriz() {

        String[][] matrizSelecciones = null;
        List<Seleccion> selecciones = getSelecciones();

        if (selecciones != null && selecciones.size() > 0) {

            matrizSelecciones = new String[selecciones.size()][4];

            int x = 0;
            for (Seleccion seleccion : selecciones) {

                matrizSelecciones[x][0] = seleccion.getNombre();
                matrizSelecciones[x][1] = seleccion.getContinente();
                matrizSelecciones[x][2] = seleccion.getDt();
                matrizSelecciones[x][3] = seleccion.getNacionalidad();
                x++;
            }
        }

        return matrizSelecciones;
    }

}
