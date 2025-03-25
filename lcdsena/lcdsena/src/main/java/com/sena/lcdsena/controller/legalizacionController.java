package com.sena.lcdsena.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sena.lcdsena.interfaces.iusuarioRepository;
import com.sena.lcdsena.interfaces.iviajeRepository;
import com.sena.lcdsena.iservice.ilegalizacionService;
import com.sena.lcdsena.model.legalizacion;
import com.sena.lcdsena.model.legalizacionRequest;
import com.sena.lcdsena.model.respuestaPdf;
import com.sena.lcdsena.model.usuario;
import com.sena.lcdsena.model.viaje;
import com.sena.lcdsena.service.legalizacionTokenService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RequestMapping("/api/v1/LCDSena/legalizacion")
@RestController
@RequiredArgsConstructor
public class legalizacionController {

    @Autowired
    private ilegalizacionService legalizacionService;

    @Autowired
    private iusuarioRepository iusuarioRepository;

    @Autowired
    private iviajeRepository iviajeRepository;

    private final legalizacionTokenService legalizacionTokenService;

    @PostMapping("/")
    public ResponseEntity<Object> save(@ModelAttribute legalizacionRequest request, 
                                    @RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return new ResponseEntity<>("El archivo es obligatorio", HttpStatus.BAD_REQUEST);
        }

        // Buscar el usuario en la BD
        Optional<usuario> usuarioOpt = iusuarioRepository.findById(request.getId_usuario());
        if (!usuarioOpt.isPresent()) {
            return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
        }

        // Buscar el viaje en la BD
        Optional<viaje> viajeOpt = iviajeRepository.findById(request.getId_viaje());
        if (!viajeOpt.isPresent()) {
            return new ResponseEntity<>("Viaje no encontrado", HttpStatus.NOT_FOUND);
        }

        // Convertir el archivo a Base64
        String pdfBase64 = Base64.getEncoder().encodeToString(file.getBytes());

        // Crear la legalización
        legalizacion nuevaLegalizacion = legalizacion.builder()
            .usuario(usuarioOpt.get())
            .viaje(viajeOpt.get())
            .pdf(pdfBase64)
            .build();

        
        // Guardar en la BD
        legalizacionService.save(nuevaLegalizacion);

        // Generar el token de legalización basado en el viaje
        String tokenLegalizacion = legalizacionTokenService.generarTokenLegalizacion(nuevaLegalizacion);

        return new ResponseEntity<>(Map.of(
            "legalizacion", nuevaLegalizacion,
            "token", tokenLegalizacion
        ), HttpStatus.OK);
    }

    @PostMapping("/pdf")
    public ResponseEntity<Object> guardarpdfJson(legalizacion legalizacion, @RequestParam("file") MultipartFile file)
            throws IOException {

        try {
            legalizacion.setPdf(Base64.getEncoder().encodeToString(file.getBytes()));

            int resultado = legalizacionService.guardarpdfJson(legalizacion);

            if (resultado == 0) {
                return new ResponseEntity<>(new respuestaPdf("Ok, se guardó correctamente"), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new respuestaPdf("Error al guardar"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (IOException e) {
            return new ResponseEntity<>("Error al guardar el pdf: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/listaLegalizaciones")
    public ResponseEntity<Object> findAll() {
        var litaLegalizaciones = legalizacionService.findAll();
        return new ResponseEntity<>(litaLegalizaciones, HttpStatus.OK);
    }

    @GetMapping("/{id_legalizacion}")
    public ResponseEntity<Object> findOne(@PathVariable String id_legalizacion) {
        var legalizacion = legalizacionService.findOne(id_legalizacion);
        return new ResponseEntity<>(legalizacion, HttpStatus.OK);
    }

    // @GetMapping("/busquedaFiltro")
    // public ResponseEntity<Object> findFiltro(@PathVariable String filtro) {
    //     var listaLegalizaciones = legalizacionService.filtroLegalizacion(filtro);
    //     return new ResponseEntity<>(listaLegalizaciones, HttpStatus.OK);
    // }

    //CONTADORES
    // @GetMapping("/vencidas")
    // public long contarVencidas() {
    //     return legalizacionService.contarVencidas();
    // }

    // @GetMapping("/pendientes")
    // public long contarPendientes() {
    //     return legalizacionService.contarPendientes();
    // }

    // @GetMapping("/completadas")
    // public long contarCompletadas() {
    //     return legalizacionService.contarCompletadas();
    // }

    @DeleteMapping("/deshabilitar/{id_legalizacion}")
    public ResponseEntity<Object> delete(@PathVariable String id_legalizacion) {
        legalizacionService.delete(id_legalizacion);
        return new ResponseEntity<>("Legalizacion deshabilitada", HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable String id, @RequestParam legalizacion legalizacionUpdate) {
        var legalizacion = legalizacionService.findOne(id).get();
        if (legalizacion != null) {
            legalizacion.setMoti_devolucion(legalizacionUpdate.getMoti_devolucion());
            legalizacion.setEstado_lega(legalizacionUpdate.getEstado_lega());

            legalizacionService.save(legalizacion);
            return new ResponseEntity<>(legalizacion, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("No se guardaron los cambios, por favor intentelo de nuevo.",
                    HttpStatus.BAD_REQUEST);
        }
    }

}
