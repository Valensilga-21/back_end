package com.sena.lcdsena.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sena.lcdsena.iservice.iviajeService;
import com.sena.lcdsena.model.viaje;
import com.sena.lcdsena.service.viajeTokenService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RequestMapping("/api/v1/LCDSena/viaje")
@RestController
@RequiredArgsConstructor
public class viajeController {

    @Autowired
    private iviajeService viajeService;

    private final viajeTokenService viajeTokenService;

    @PostMapping("/")
    public ResponseEntity<Object> save(@RequestBody viaje viaje) {

        if (viaje.getNum_comision() == 0) {
            return new ResponseEntity<>("El número de comisión es obligatorio", HttpStatus.BAD_REQUEST);
        }
        if (viaje.getFecha_inicio() == null) {
            return new ResponseEntity<>("La fecha de inicio del viaje es obligatoria", HttpStatus.BAD_REQUEST);
        }
        if (viaje.getFecha_fin() == null) {
            return new ResponseEntity<>("La fecha de fin del viaje es obligatoria", HttpStatus.BAD_REQUEST);
        }
        if (viaje.getRuta() == null || viaje.getRuta().isEmpty()) {
            return new ResponseEntity<>("La ruta del viaje es obligatoria", HttpStatus.BAD_REQUEST);
        }
        if (viaje.getEstado_viaje() == null) {
            return new ResponseEntity<>("El estado del viaje es obligatorio", HttpStatus.BAD_REQUEST);
        }
        

    // Guardar viaje en la base de datos
    viajeService.save(viaje);

    // Generar token para el viaje registrado
    String token = viajeTokenService.generarTokenViaje(viaje);

    // Respuesta con viaje y token
    return new ResponseEntity<>(Map.of(
        "viaje", viaje,
        "token", token
    ), HttpStatus.OK);
    }


    @GetMapping("/listaViajes")
    public ResponseEntity<Object> findAll() {
        var listaViajes = viajeService.findAll();
        return new ResponseEntity<>(listaViajes, HttpStatus.OK);
    }

    @GetMapping("/{id_viaje}")
    public ResponseEntity<Object> findOne(@PathVariable String id_viaje) {
        var viaje = viajeService.findOne(id_viaje);
        return new ResponseEntity<>(viaje, HttpStatus.OK);
    }

    @GetMapping("/busquedaFiltro/{filtro}")
    public ResponseEntity<Object> findFiltro(@PathVariable String filtro) {
        var listaViajes = viajeService.filtroViaje(filtro);
        return new ResponseEntity<>(listaViajes, HttpStatus.OK);
    }

    @DeleteMapping("/eliminar/{id_viaje}")
    public ResponseEntity<Object> delete(@PathVariable String id_viaje){
        viajeService.delete(id_viaje);
        return new ResponseEntity<>("Usuario deshabilitado", HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable String id, @RequestBody viaje viajeUpdate){
        var viaje=viajeService.findOne(id).get();
        if (viaje != null) {
            viaje.setNum_comision(viajeUpdate.getNum_comision());
            viaje.setFecha_inicio(viajeUpdate.getFecha_inicio());
            viaje.setFecha_fin(viajeUpdate.getFecha_fin());
            viaje.setRuta(viajeUpdate.getRuta());
            viaje.setEstado_viaje(viajeUpdate.getEstado_viaje());

            viajeService.save(viaje);
            return new ResponseEntity<>(viaje, HttpStatus.OK);
        }else{
            return new ResponseEntity<>("No se pudieron guardar los cambios, por favor intentelo de nuevo.", HttpStatus.BAD_REQUEST);
        }
    }
}
