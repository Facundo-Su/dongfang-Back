package com.dongfang.dongfang.controller.precio;

import com.dongfang.dongfang.model.Volante;
import com.dongfang.dongfang.service.ConsultaPrecioVolante;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/precio/Volante")
@CrossOrigin(origins = "http://localhost:5173/")
public class PrecioVolanteController {

    @Autowired
    private ConsultaPrecioVolante consultaPrecioVolante;

    @PostMapping
    public ResponseEntity<String> obtenerPrecio(@RequestBody Volante volante) {
        try {
            String resultado = consultaPrecioVolante.obtenerValorPorCantidadYColumnas(volante);
            if (resultado == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
