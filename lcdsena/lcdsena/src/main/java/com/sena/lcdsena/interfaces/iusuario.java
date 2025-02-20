package com.sena.lcdsena.interfaces;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sena.lcdsena.model.usuario;

@Repository
public interface iusuario extends JpaRepository<usuario, String> {
    
    Optional<usuario> findByUsername(String username);
}
