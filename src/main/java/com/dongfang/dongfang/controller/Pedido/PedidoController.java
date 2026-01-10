package com.dongfang.dongfang.controller.Pedido;

import com.dongfang.dongfang.model.DatosCliente;
import com.dongfang.dongfang.model.Etiqueta;
import com.dongfang.dongfang.model.PedidoRequest;
import com.dongfang.dongfang.model.Volante;
import com.dongfang.dongfang.service.ConsultaPrecioEtiqueta;
import com.dongfang.dongfang.service.ConsultaPrecioVolante;
import com.dongfang.dongfang.service.PedidoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/precio/pedido")
@CrossOrigin(origins = "${FRONT_URL}")
public class PedidoController {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PedidoService pedidoService;
    @Autowired
    private ConsultaPrecioEtiqueta consultaPrecioEtiqueta;
    @Autowired
    private ConsultaPrecioVolante consultaPrecioVolante;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<String> enviarPedido(
            @RequestPart("datos") PedidoRequest pedido
    ) {
        try {
            DatosCliente cliente = pedido.getCliente();

            System.out.println("Cliente recibido: " + cliente);

            switch (pedido.getTipoProducto()) {

                case "Etiqueta" -> {
                    Etiqueta etiqueta = objectMapper.treeToValue(
                            pedido.getProducto(), Etiqueta.class
                    );
                    String precio =consultaPrecioEtiqueta.obtenerPrecio(etiqueta);
                    pedidoService.guardarPedido(cliente.getNombreLocal(),
                            cliente.getDireccion(),
                            cliente.getContacto(),
                            pedido.getTipoProducto(),
                            String.valueOf(etiqueta.getLargo()),
                            String.valueOf(etiqueta.getAncho()),
                            "FULL",
                            String.valueOf(etiqueta.getCantidad()),
                            etiqueta.getTipo(),
                            precio
                            );
                    // 👇 MOSTRAR DATOS RECIBIDOS
                    System.out.println("Producto Etiqueta recibido:");
                    System.out.println(etiqueta);
                }

                case "Volante" -> {
                    Volante volante = objectMapper.treeToValue(
                            pedido.getProducto(), Volante.class
                    );

                    switch (volante.getTamanio()) {
                        case "20x28" -> {
                            volante.setLargo("20");
                            volante.setAncho("28");
                        }
                        case "40x28" -> {
                            volante.setLargo("40");
                            volante.setAncho("28");
                        }
                        case "40x56" -> {
                            volante.setLargo("40");
                            volante.setAncho("56");
                        }
                    }


                    String precio = consultaPrecioVolante.obtenerValorPorCantidadYColumnas(volante);
                    pedidoService.guardarPedido(cliente.getNombreLocal(),
                            cliente.getDireccion(),
                            cliente.getContacto(),
                            pedido.getTipoProducto(),
                            String.valueOf(volante.getLargo()),
                            String.valueOf(volante.getAncho()),
                            "FULL",
                            String.valueOf(volante.getCantidad()),
                            volante.getTipo(),
                            precio
                    );

                    // 👇 MOSTRAR DATOS RECIBIDOS
                    System.out.println("Producto Volante recibido:");
                    System.out.println(volante);
                }

                default -> {
                    return ResponseEntity.badRequest()
                            .body("Tipo de producto no soportado");
                }
            }


            return ResponseEntity.ok("Pedido recibido correctamente");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al procesar pedido");
        }
    }

}
