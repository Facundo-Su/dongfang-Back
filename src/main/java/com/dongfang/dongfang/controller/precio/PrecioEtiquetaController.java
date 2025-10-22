package com.dongfang.dongfang.controller.precio;

import com.dongfang.dongfang.model.Etiqueta;
import com.dongfang.dongfang.model.Volante;
import com.dongfang.dongfang.service.ConsultaPrecioEtiqueta;
import com.dongfang.dongfang.service.ConsultaPrecioVolante;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/precio/Etiqueta")
@CrossOrigin(origins = "${FRONT_URL}")
public class PrecioEtiquetaController {

    @Autowired
    private ConsultaPrecioEtiqueta consultaPrecioEtiqueta;

    @PostMapping
    public ResponseEntity<String> obtenerPrecio(@RequestBody Etiqueta etiqueta) {
        try {
            String resultado = consultaPrecioEtiqueta.obtenerPrecio(etiqueta);
            if (resultado == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
