package org.alixar.api.repositories;


import org.alixar.api.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario por su email de usuario.
     *
     * @param email el correo electrónico de usuario a buscar.
     * @return un Optional que contiene el usuario si se encuentra, o vacío si no existe.
     */
    Optional<User> findByEmail(String email);
}

