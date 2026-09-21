package sv.edu.utec.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import sv.edu.utec.modelo.Producto;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class ProveedorAPI {

    private final HttpClient clienteHttp;
    private final ObjectMapper mapper;

    public ProveedorAPI() {
        this.clienteHttp = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
    }

    public List<Producto> obtenerProductos(int limite) throws IOException, InterruptedException {
        String url = "https://dummyjson.com/products?limit=" + limite + "&select=title,stock";

        HttpRequest solicitud = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> respuesta = clienteHttp.send(solicitud, HttpResponse.BodyHandlers.ofString());

        // Validar código de estado 200
        if (respuesta.statusCode() != 200) {
            throw new IOException("Error al consumir API externa. Codigo de estado HTTP recibido: " + respuesta.statusCode());
        }

        // Deserializar el JSON a RespuestaProductos
        RespuestaProductos contenedor = mapper.readValue(respuesta.body(), RespuestaProductos.class);

        // Convertir cada ProductoApi a Producto de dominio
        List<Producto> productos = new ArrayList<>();
        if (contenedor != null && contenedor.getProducts() != null) {
            for (ProductoApi apiItem : contenedor.getProducts()) {
                productos.add(apiItem.aProducto());
            }
        }

        return productos;
    }
}